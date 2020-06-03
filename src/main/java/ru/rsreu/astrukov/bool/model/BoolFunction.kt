package ru.rsreu.astrukov.bool.model

import ru.rsreu.astrukov.bool.helper.VariablesHelper.toVariableWithoutInverse

data class BoolFunction(
        val varGroups: List<VariableGroup>
) {
    data class VariableGroup(
            val variables: List<String>
    ) {

        fun getVariablesToExclude(group: VariableGroup): String? {

            val vars = this.variables
            val groupVars = group.variables

            if (vars.size != groupVars.size) {
                return null
            }


            val u1 = vars.minus(groupVars)
            val u2 = groupVars.minus(vars)

            if (u1.size != u2.size || u1.size != 1) {
                return null
            }

            //если каждой недуплицированной переменной есть противоположность
            //возвращаем список переменных без отрицаний
            return toVariableWithoutInverse(u1[0])
        }

    }

    override fun toString() = varGroups.joinToString(separator = " || ") {
        it.variables.joinToString(separator = " && ", prefix = "(", postfix = ")")
    }


    fun allVariables(): Set<String> = varGroups.flatMap { it.variables }
            .map { it.replace("!", "") }
            .distinct().sorted().toSet()
}

