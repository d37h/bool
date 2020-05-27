package ru.rsreu.astrukov.bool.service

import org.codehaus.janino.ExpressionEvaluator
import ru.rsreu.astrukov.bool.model.BoolElement
import ru.rsreu.astrukov.bool.model.BoolElementBlock
import ru.rsreu.astrukov.bool.model.BoolElementType
import ru.rsreu.astrukov.bool.model.BoolElementVariable
import java.util.*

class EquationSolverKt {

    //сюда должны подаваться сама функция


    fun solve(function: String, variables: List<String>): BoolElement {

        return when {
            variables.size > 2 -> {
                val excludedVariable = getVariableToExclude(function, variables)
                val vars = variables.minus(excludedVariable)

                val truthyFunction = function.replace(excludedVariable, "true")
                val falsyFunction = function.replace(excludedVariable, "false")

                BoolElementBlock(
                    firstChild = solve(truthyFunction, vars),
                    secondChild = solve(falsyFunction, vars),
                    variables = vars,
                    excludedVariable = excludedVariable,
                    function = function,
                    type = BoolElementType.BIP
            )}
            variables.size == 2 -> BoolElementBlock(
                    firstChild = null,
                    secondChild = null,
                    variables = variables,
                    function = simplifyTwoArgsFunction(function, variables),
                    type = BoolElementType.FUNCTION
            )
            else -> BoolElementVariable(variable = variables[0])
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
            function: String, variables: List<String>
    ): String {

        val expressionEvaluator = ExpressionEvaluator()
        val classParameters = Array(variables.size) { Boolean::class.javaPrimitiveType }

        expressionEvaluator.setParameters(variables.toTypedArray(), classParameters)
        expressionEvaluator.setExpressionType(Boolean::class.javaPrimitiveType)
        expressionEvaluator.cook(function)

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