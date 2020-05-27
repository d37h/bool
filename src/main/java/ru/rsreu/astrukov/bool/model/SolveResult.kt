package ru.rsreu.astrukov.bool.model

data class SolveResult (
    val truthyFunction: String,
    val falsyFunction: String,

    val excludedVariable: String,
    val variables: List<String>
)
