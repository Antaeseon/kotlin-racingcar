package study

enum class Operator(
    val symbol: Char,
    val calculate: (Int, Int) -> Int
) {
    PLUS(
        symbol = '+',
        calculate = { a, b -> a + b }
    ),
    MINUS(
        symbol = '-',
        calculate = { a, b -> a - b }
    ),
    MULTIPLY(
        symbol = '*',
        calculate = { a, b -> a * b }
    ),
    DIVIDE(
        symbol = '/',
        calculate = { a, b ->
            if (b != 0) a / b
            else throw IllegalArgumentException("0으로 나눌 수 없습니다.")
        }
    );

    companion object {
        fun of(char: Char) = values()
            .find { it.symbol == char }
            ?: throw IllegalArgumentException("사칙연산 기호가 아닙니다.")
    }
}

class StringCalculator(
    private val separator: String
) {
    fun calculate(input: String?): Int {
        require(!input.isNullOrBlank()) { "input이 null이거나 공백입니다." }
        val (numbers, symbols) = input
            .split(separator)
            .separateByIndex { it.isEven }

        val operands = numbers.parseToOperands()
        val operators = symbols.parseToOperators()

        return calculate(operators, operands)
    }

    private fun calculate(operators: List<Operator>, operands: List<Int>) = operands
        .drop(1) // initial value drop
        .foldIndexed(operands.first()) { index, acc, number ->
            operators[index].calculate(acc, number)
        }

    private fun List<IndexedValue<String>>.parseToOperands(): List<Int> =
        map { it.value.toIntOrNull() ?: throw IllegalArgumentException("숫자가 아닌 값이 숫자자리에 입력되었습니다.") }
            .takeIf { it.size > 1 }
            ?: throw IllegalArgumentException("하나 이상의 숫자가 입력되어야합니다.")

    private fun List<IndexedValue<String>>.parseToOperators(): List<Operator> =
        map { it.value.first().let(Operator::of) }

    private fun List<String>.separateByIndex(
        predicate: (index: Int) -> Boolean
    ) = withIndex()
        .partition { predicate(it.index) }

    private val Int.isEven: Boolean get() = this % 2 == 0
}