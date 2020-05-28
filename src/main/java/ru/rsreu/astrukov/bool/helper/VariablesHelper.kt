package ru.rsreu.astrukov.bool.helper

object VariablesHelper {
    fun inverseVariable(variable: String): String = if (variable.contains("!")) {
        variable.replace("!","")
    } else {
        "!$variable"
    }

    fun toVariableWithoutInverse(variable: String): String = if (variable.contains("!")) {
        variable.replace("!","")
    } else {
        variable
    }
}