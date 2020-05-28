package ru.rsreu.astrukov.bool.service

import org.codehaus.janino.ExpressionEvaluator
import ru.rsreu.astrukov.bool.helper.VariablesHelper.replaceVariableWithBoolean
import ru.rsreu.astrukov.bool.model.*
import java.util.*

class EquationSolverKt {

    private val variableService = VariableService()

    fun solve(function: BoolFunction): BoolElement {

        val variables = function.simpleVariables().toList()

        return when {
            variables.size > 2 -> {
                val excludedVariable = getVariableToExclude(function)
                val vars = variables.minus(excludedVariable)

                val truthyFunction = replaceVariableWithBoolean(function, excludedVariable, true)
                val falsyFunction = replaceVariableWithBoolean(function, excludedVariable, false)

                val firstChild = if (truthyFunction.varGroups.isEmpty()) {
                    simplifyTwoArgsFunction(function, excludedVariable, true)
                } else {
                    solve(truthyFunction)
                }

                val secondChild = if (falsyFunction.varGroups.isEmpty()) {
                    simplifyTwoArgsFunction(function, excludedVariable, true)
                } else {
                    solve(falsyFunction)
                }

                BoolElementBlock(
                        firstChild = firstChild,
                        secondChild = secondChild,
                        variables = vars,
                        excludedVariable = excludedVariable,
                        function = function,
                        type = BoolElementType.BIP
                )
            }
            variables.size == 2 -> simplifyTwoArgsFunction(function)
            else -> BoolElementVariable(variable = variables[0])
        }

    }

    fun simplifyTwoArgsFunction(function: BoolFunction, excludedVariable: String? = null, value: Boolean? = null) : BoolElementFunction {

            var expression = function.toStringFunction()

            val expressionEvaluator = ExpressionEvaluator()
            val classParameters = arrayOf(Boolean::class.javaPrimitiveType, Boolean::class.javaPrimitiveType)

            if (excludedVariable != null && value != null) {
                expression = expression.replace("!$excludedVariable", (!value).toString())
                expression = expression.replace(excludedVariable, value.toString())
            }

            val variableParameters = excludedVariable?.let {function.simpleVariables().minus(it)} ?: function.simpleVariables()

            expressionEvaluator.setParameters(variableParameters.toTypedArray() , classParameters)
            expressionEvaluator.setExpressionType(Boolean::class.javaPrimitiveType)
            expressionEvaluator.cook(expression)

            var resultString = ""

            val sets = listOf(listOf(false, false), listOf(true, false), listOf(false, true), listOf(true, true))

            for (set in sets) {

                val result = expressionEvaluator.evaluate(set.toTypedArray()) as Boolean

                resultString += if (result) "1" else "0"

            }

            return BoolElementFunction(
                    type = resultString.reversed(),
                    firstChild = BoolElementVariable(variable = variableParameters.toTypedArray().first()),
                    secondChild = BoolElementVariable(variable = variableParameters.toTypedArray().last())
            )

    }

    fun simplify(function: BoolFunction): BoolFunction {
        //группируем выражения по участвующим переменным
        val varGroupsMap = function.varGroups.groupBy { it.variables.map { v -> v.replace("!", "") }.asSequence().toSet() }
        val newMap = LinkedHashMap<Set<String>, List<BoolFunction.VariableGroup>>()

        var simplified = false
        varGroupsMap.forEach {

            if (it.value.size < 2) {
                newMap[it.key] = newMap[it.key]?.plus(it.value) ?: it.value
                return@forEach
            }

            val excludeResult = variableService.excludeVariablesFromGroup(it.value)

            if (excludeResult == null) {
                newMap[it.key] = newMap[it.key]?.plus(it.value) ?: it.value
                return@forEach
            }

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

    fun simplifyTwoArgsFunction(function: String, variables: List<String>): String {

        val expressionEvaluator = ExpressionEvaluator()
        val classParameters = Array(variables.size) { Boolean::class.javaPrimitiveType }

        expressionEvaluator.setParameters(variables.toTypedArray(), classParameters)
        expressionEvaluator.setExpressionType(Boolean::class.javaPrimitiveType)
        expressionEvaluator.cook(function)

        var resultString = ""

        val sets = listOf(listOf(false, false), listOf(true, false), listOf(false, true), listOf(true, true))

        for (set in sets) {

            val result = expressionEvaluator.evaluate(set.toTypedArray()) as Boolean

            resultString += if (result) "1" else "0"

        }

        return resultString.reversed()
    }


    fun getVariableToExclude(
            function: BoolFunction
    ): String {


        val variables = function.simpleVariables().toList()

        val expressionEvaluator = ExpressionEvaluator()
        val classParameters = Array(variables.size) { Boolean::class.javaPrimitiveType }

        expressionEvaluator.setParameters(variables.toTypedArray(), classParameters)
        expressionEvaluator.setExpressionType(Boolean::class.javaPrimitiveType)
        expressionEvaluator.cook(function.toStringFunction())

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


    private fun toBinarySets(binaryLength: Int, fixedNumIndex: Int, fixedNumValue: Boolean): List<List<Boolean>> {

        val binarySetsCount = Math.pow(2.0, binaryLength.toDouble()).toInt()
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