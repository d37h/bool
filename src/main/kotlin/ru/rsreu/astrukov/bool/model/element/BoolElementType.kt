package ru.rsreu.astrukov.bool.model.element


enum class BoolElementType(val stringValue: String) {
    FALSE("0000"),
    NOT_OR("0001"),
    FIRST_BIGGER("0010"), //инверсия прямой импл
    NOT_SECOND("0011"),

    FIRST_LESS("0100"), //инверсия обратной импл
    NOT_FIRST("0101"),
    XOR("0110"),
    NAND("0111"),

    AND("1000"),
    EQUAL("1001"),
    FIRST("1010"),
    FIRST_NOT_LESS("1011"), //обратная импл

    SECOND("1100"),
    FIRST_NOT_BIGGER("1101"), //прямая импл
    OR("1110"),
    TRUE("1111");

    fun isTerminal() = this == TRUE || this == FALSE || this == FIRST || this == SECOND || this == NOT_FIRST || this == NOT_SECOND
}