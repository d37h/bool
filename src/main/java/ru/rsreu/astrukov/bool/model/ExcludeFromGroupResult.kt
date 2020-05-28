package ru.rsreu.astrukov.bool.model

data class ExcludeFromGroupResult (
    val variables: List<BoolFunction.VariableGroup>,
    val simplifiedGroup: BoolFunction.VariableGroup
)
