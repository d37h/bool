package ru.rsreu.astrukov.bool.model.element.ext

import ru.rsreu.astrukov.bool.helper.VariablesHelper
import ru.rsreu.astrukov.bool.model.BoolFunction


fun BoolFunction.toMatrix2() = this.varGroups.map {
    val allvars = this.allVariables().toTypedArray()
    val map = it.toMap()
    allvars.map {variable -> map[variable]}.toTypedArray()
}.toTypedArray()

fun BoolFunction.VariableGroup.toMap() = this.variables.map {
    VariablesHelper.toVariableWithoutInverse(it) to !it.contains("!")
}.toMap()