package ru.rsreu.astrukov.bool.helper

import ru.rsreu.astrukov.bool.model.BoolFunction

object VariablesHelper {
    fun inverseVariable(variable: String): String = if (variable.contains("!")) {
        variable.replace("!", "")
    } else {
        "!$variable"
    }

    fun toVariableWithoutInverse(variable: String): String = if (variable.contains("!")) {
        variable.replace("!", "")
    } else {
        variable
    }

    fun replaceVariableWithBoolean(boolFunction: BoolFunction, variable: String, value: Boolean): BoolFunction {
        if ("(!x4 && x10) || (x6 && !x5 && !x4)" == boolFunction.toString()) {
            val a = 1
        }
        //убираем те группы, где переменная выродилась в 0 из-за нуля в конъюнкции
        val excludedFalsyGroups = boolFunction.varGroups.minus(
                boolFunction.varGroups.filter {
                    it.variables.contains(if (value) inverseVariable(variable) else variable)
                }
        ).toList()

        val excludedTruthyOccurences = excludedFalsyGroups.map {
            it.variables.minus(listOf((if (value) variable else inverseVariable(variable))))
        }.filter { it.isNotEmpty() }.map {
            BoolFunction.VariableGroup(it)
        }

        return BoolFunction(varGroups = excludedTruthyOccurences)


    }
}