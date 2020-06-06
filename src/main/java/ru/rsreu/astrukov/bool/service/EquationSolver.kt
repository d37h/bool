package ru.rsreu.astrukov.bool.service

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.codehaus.janino.ExpressionEvaluator
import ru.rsreu.astrukov.bool.helper.VariablesHelper.inverseVariable
import ru.rsreu.astrukov.bool.helper.VariablesHelper.replaceVariableWithBoolean
import ru.rsreu.astrukov.bool.model.BoolFunction
import ru.rsreu.astrukov.bool.model.element.*
import ru.rsreu.astrukov.bool.model.element.ext.toMatrix2
import java.util.*
import kotlin.math.pow

enum class SolveMode {
    STANDART, OPENCL
}

class EquationSolver {

    private val variableService = VariableService()
    private val openClService = OpenClService()

    fun solve(function: BoolFunction, mode:SolveMode): BoolElement {

        val variables = function.allVariables().toList()

        return when {
            variables.size > 2 -> {

                val excludedVariable: String = when (mode) {
                    SolveMode.STANDART -> getVariableToExclude(function)
                    SolveMode.OPENCL -> getVariableToExcludeCL(function)
                }
                val vars = variables.minus(excludedVariable)

                val truthyFunction = replaceVariableWithBoolean(function, excludedVariable, true)
                val falsyFunction = replaceVariableWithBoolean(function, excludedVariable, false)

                val firstChild = if (truthyFunction.varGroups.isEmpty()) {
                    simplifyTwoArgsFunction(function, excludedVariable, true)
                } else {
                    solve(truthyFunction, mode)
                }

                val secondChild = if (falsyFunction.varGroups.isEmpty()) {
                    simplifyTwoArgsFunction(function, excludedVariable, true)
                } else {
                    solve(falsyFunction, mode)
                }

                BoolElementBlock(
                        firstChild = firstChild,
                        secondChild = secondChild,
                        variables = vars,
                        excludedVariable = excludedVariable,
                        function = function
                )
            }
            variables.size == 2 -> simplifyTwoArgsFunction(function)
            else -> BoolElementVariable(variable = variables[0])
        }

    }

    fun simplifyTwoArgsFunction(function: BoolFunction, excludedVariable: String? = null, value: Boolean? = null): BoolElement {

        var expression = function.toString()

        val expressionEvaluator = ExpressionEvaluator()
        val classParameters = arrayOf(Boolean::class.javaPrimitiveType, Boolean::class.javaPrimitiveType)

        if (excludedVariable != null && value != null) {
            expression = expression.replace("!$excludedVariable", (!value).toString())
            expression = expression.replace(excludedVariable, value.toString())
        }

        val variableParameters = excludedVariable?.let { function.allVariables().minus(it) } ?: function.allVariables()

        expressionEvaluator.setParameters(variableParameters.toTypedArray(), classParameters)
        expressionEvaluator.setExpressionType(Boolean::class.javaPrimitiveType)
        expressionEvaluator.cook(expression)

        var resultString = ""

        val sets = listOf(listOf(false, false), listOf(true, false), listOf(false, true), listOf(true, true))

        for (set in sets) {

            val result = expressionEvaluator.evaluate(set.toTypedArray()) as Boolean

            resultString += if (result) "1" else "0"

        }

        val type = BoolElementType.values().find { it.stringValue == resultString.reversed() }
                ?: throw RuntimeException("no BoolElementTypeValue for ${resultString.reversed()}")

        return getElementByType(type, variableParameters.toList())

    }

    fun getElementByType(type: BoolElementType, variables: List<String>): BoolElement {
        return when (type) {
            BoolElementType.FALSE -> BoolElementVariable(BoolElementVariable.falsyValue)
            BoolElementType.TRUE -> BoolElementVariable(BoolElementVariable.truthyValue)
            BoolElementType.FIRST -> BoolElementVariable(variables.first())
            BoolElementType.SECOND -> BoolElementVariable(variables.last())
            BoolElementType.NOT_FIRST -> BoolElementVariable(inverseVariable(variables.first()))
            BoolElementType.NOT_SECOND -> BoolElementVariable(inverseVariable(variables.last()))
            else -> BoolElementFunction(
                    type = type,
                    firstChild = BoolElementVariable(variable = variables.first()),
                    secondChild = BoolElementVariable(variable = variables.last())
            )
        }
    }

    fun simplify(function: BoolFunction): BoolFunction {
        //группируем выражения по участвующим переменным
        val varGroupsMap = function.varGroups.groupBy {
            it.variables.map { v -> v.replace("!", "") }.asSequence().toSet()
        }
        val newMap = LinkedHashMap<Set<String>, List<BoolFunction.VariableGroup>>()

        var simplified = false
        varGroupsMap.forEach {

            //не пытаемся упростить функции от двух переменных
            if (it.value.size < 2) {
                newMap[it.key] = newMap[it.key]?.plus(it.value) ?: it.value
                return@forEach
            }

            val excludeResult = variableService.excludeVariablesFromGroup(it.value)

            if (excludeResult == null) {
                newMap[it.key] = newMap[it.key]?.plus(it.value) ?: it.value
                return@forEach
            }

            //убираем старые значения, добавляем новые
            newMap[it.key] = newMap[it.key]?.plus(excludeResult.variables) ?: excludeResult.variables
            val excludedVarsKey = excludeResult.simplifiedGroup.variables.map { variable ->
                variable.replace("!", "")
            }.toSet()

            newMap[excludedVarsKey] = newMap[excludedVarsKey]?.plus(excludeResult.simplifiedGroup)
                    ?: listOf(excludeResult.simplifiedGroup)

            simplified = true

        }

        return if (simplified) {
            simplify(BoolFunction(varGroups = newMap.values.flatten()))
        } else {
            function
        }

    }

    private fun getVariableToExclude(function: BoolFunction): String {
        val variables = function.allVariables().toList()

        val expressionEvaluator = ExpressionEvaluator()
        val classParameters = Array(variables.size) { Boolean::class.javaPrimitiveType }

        expressionEvaluator.setParameters(variables.toTypedArray(), classParameters)
        expressionEvaluator.setExpressionType(Boolean::class.javaPrimitiveType)
        expressionEvaluator.cook(function.toString())

        val weightList = ArrayList<Int>()

        for (variableIndex in variables.indices) {

            var weight = 0

            val falsySets = ArrayList(toBinarySets(variables.size, variableIndex, false))
            val truthySets = ArrayList(toBinarySets(variables.size, variableIndex, true))

            for (setIndex in falsySets.indices) {

                val truthySet = truthySets[setIndex].toTypedArray()
                val falsySet = falsySets[setIndex].toTypedArray()

                val truthyResult = expressionEvaluator.evaluate(truthySet) as Boolean
                val falsyResult = expressionEvaluator.evaluate(falsySet) as Boolean

                if (falsyResult xor truthyResult) {
                    weight++
                }
            }

            weightList.add(weight)

        }

        val maxWeight = Collections.max(weightList)
        val variableToExcludeIndex = weightList.indexOf(maxWeight)

        println(variableToExcludeIndex)

        //fixme: actually returns 2x weight - duplicate iterations
        return variables[variableToExcludeIndex]
    }


    private fun getVariableToExcludeCL(function: BoolFunction): String {
        val variablesNullable = function.toMatrix2()

        val variables = variablesNullable.map { it.map { variable -> variable ?: false }.toBooleanArray() }.toTypedArray()
        val weightList = ArrayList<Int>()

        function.allVariables().forEachIndexed { variableIndex, _ ->
            val falsySets = toBinarySets(function.allVariables().size, variableIndex, false).map {
                it.toBooleanArray()
            }.toTypedArray()

            val truthySets = toBinarySets(function.allVariables().size, variableIndex, true).map {
                it.toBooleanArray()
            }.toTypedArray()

            val contain = variablesNullable.map { it.map { variable -> variable != null }.toBooleanArray() }.toTypedArray()

            val w = openClService.calcWeight(
                    variables, contain, truthySets = truthySets, falsySets = falsySets
            )

            weightList.add(w)
        }

        val maxWeight = Collections.max(weightList)
        val variableToExcludeIndex = weightList.indexOf(maxWeight)

        println(variableToExcludeIndex)

        //fixme: actually returns 2x weight - duplicate iterations
        return function.allVariables().toTypedArray()[variableToExcludeIndex]
    }


//    private fun getVariableToExcludeCoroutine(function: BoolFunction): String {
//        val variables = function.allVariables().toList()
//
//
//        val weightList = ArrayList<Int>()
//
//        for (variableIndex in variables.indices) {
//
//            val falsySets = ArrayList(toBinarySets(variables.size, variableIndex, false))
//            val truthySets = ArrayList(toBinarySets(variables.size, variableIndex, true))
//
//            val deferredWeights = (0..falsySets.size).map {
//                GlobalScope.async {
//                    val expressionEvaluator = ExpressionEvaluator()
//                    val classParameters = Array(variables.size) { Boolean::class.javaPrimitiveType }
//
//                    expressionEvaluator.setParameters(variables.toTypedArray(), classParameters)
//                    expressionEvaluator.setExpressionType(Boolean::class.javaPrimitiveType)
//                    expressionEvaluator.cook(function.toString())
//
//                    val truthySet = truthySets[it].toTypedArray()
//                    val falsySet = falsySets[it].toTypedArray()
//
//                    val truthyResult = expressionEvaluator.evaluate(truthySet) as Boolean
//                    val falsyResult = expressionEvaluator.evaluate(falsySet) as Boolean
//
//                    if (falsyResult xor truthyResult) 1 else 0
//                }
//            }
//
//            weightList.add(runBlocking {
//                deferredWeights.map { it.await() }.sum()
//            })
//
//        }
//
//        val maxWeight = Collections.max(weightList)
//        val variableToExcludeIndex = weightList.indexOf(maxWeight)
//
//        println(variableToExcludeIndex)
//
//        //fixme: actually returns 2x weight - duplicate iterations
//        return variables[variableToExcludeIndex]
//    }

    private fun toBinarySets(binaryLength: Int, fixedNumIndex: Int, fixedNumValue: Boolean): List<List<Boolean>> {

        val binarySetsCount = 2.0.pow(binaryLength).toInt()
        val list = ArrayList<List<Boolean>>()

        for (number in 0 until binarySetsCount) {
            val numberSet: ArrayList<Boolean> = ArrayList()

            for (i in 0 until binaryLength) {
                if (i == fixedNumIndex) {
                    numberSet.add(fixedNumValue)
                } else {
                    numberSet.add(1 shl i and number != 0)
                }

            }

            list.add(numberSet)
        }

        return list.distinct()
    }

}