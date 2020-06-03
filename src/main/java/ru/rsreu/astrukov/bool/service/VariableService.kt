package ru.rsreu.astrukov.bool.service

import ru.rsreu.astrukov.bool.helper.VariablesHelper.inverseVariable
import ru.rsreu.astrukov.bool.model.BoolFunction
import ru.rsreu.astrukov.bool.model.ExcludeFromGroupResult

class VariableService {

    fun excludeVariablesFromGroup(variables: List<BoolFunction.VariableGroup>): ExcludeFromGroupResult? {

        variables.forEach { firstGroup -> variables.forEach { secondGroup ->
                if (firstGroup.variables.toSet() != secondGroup.variables.toSet()) {
                    val varToExclude = firstGroup.getVariablesToExclude(secondGroup)

                    if (varToExclude != null) {
                        var newVariables = variables.toList()

                        newVariables = newVariables.minus(firstGroup)
                        newVariables = newVariables.minus(secondGroup)

                        //fixme: change param list -> to string
                        val excludedVarGroup = excludeVariablesFromGroup(firstGroup, listOf(varToExclude))

                        return ExcludeFromGroupResult(newVariables, excludedVarGroup)
                    }
                }
            }}

        return null

    }

    private fun excludeVariablesFromGroup(group: BoolFunction.VariableGroup, variablesToExclude: List<String>) : BoolFunction.VariableGroup {
        var newGroup = group.variables.toList()

        variablesToExclude.forEach {
            newGroup = newGroup.minus(it)
            newGroup = newGroup.minus(inverseVariable(it))
        }

        return BoolFunction.VariableGroup(variables = newGroup)
    }

}