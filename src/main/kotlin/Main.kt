package converter

import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.pow


fun main() {
    var lastCommand: String

    while (true) {
        print("Enter two numbers in format: {source base} {target base} (To quit type /exit) ")
        lastCommand = readln()

        if (lastCommand == "/exit") {
            return
        } else {
            val (sourceBase, targetBase) = lastCommand.split(' ').map { it.toInt() }
            while (true) {
                print("Enter number in base $sourceBase to convert to base $targetBase (To go back type /back) ")
                lastCommand = readln()

                if (lastCommand == "/back") {
                    break
                } else {
                    val inDecimal = toDecimal(sourceBase, lastCommand)
                    val inTargetBase = fromDecimal(lastCommand.indexOf('.') != -1, targetBase, inDecimal)
                    println("Conversion result: $inTargetBase")
                }
            }
        }
    }
}

fun fromDecimal(isDouble: Boolean, targetBase: Int, number: BigDecimal): String {
    var lastPart = number % BigDecimal.ONE
    var firstPart = number.setScale(0, RoundingMode.FLOOR)
    val targetBase = BigDecimal.valueOf(targetBase.toLong())
    val tenInBigDecimal = BigDecimal("10")
    var numberInBase = ""
    var baseChar: Char

    while (firstPart >= BigDecimal.ONE) {
        baseChar = if (firstPart % targetBase >= tenInBigDecimal)
            'A' + (firstPart % targetBase - tenInBigDecimal).toInt()
        else '0' + (firstPart % targetBase).toInt()
        numberInBase = "$baseChar$numberInBase"
        firstPart = firstPart.divide(targetBase, 0, RoundingMode.DOWN)
    }

    if (isDouble || lastPart > BigDecimal.ZERO) {
        numberInBase += "."
        var i = 0
        while (i++ < 5) {
            lastPart *= targetBase
            baseChar = if (targetBase > tenInBigDecimal) {
                if (lastPart.divide(BigDecimal.ONE, 0, RoundingMode.FLOOR) >= BigDecimal.TEN)
                    'A' + (lastPart.divide(BigDecimal.ONE, 0, RoundingMode.FLOOR) - tenInBigDecimal).toInt()
                else '0' + (lastPart.divide(BigDecimal.ONE, 0, RoundingMode.FLOOR)).toInt()
            } else {
                val a = (lastPart.divide(BigDecimal.ONE, 0, RoundingMode.FLOOR)).toInt()
                '0' + a
            }
            numberInBase += baseChar
            lastPart %= 1.toBigDecimal()
        }
    }
    return numberInBase
}

fun toDecimal(sourceBase: Int, number: String): BigDecimal {
    var numberInBase = BigDecimal.ZERO
    var baseDigit: Int
    var firstPart: String = number
    var lastPart = ""

    val dotIndex = number.indexOf('.')
    if (dotIndex != -1) {
        firstPart = number.substring(0, dotIndex)
        lastPart = number.substring(dotIndex + 1, number.lastIndex + 1)
    }

    for (i in firstPart.indices) {
        baseDigit = getSymbolValue(firstPart[i])
        numberInBase += BigDecimal.valueOf(baseDigit * sourceBase.toDouble().pow((firstPart.length - 1) - i))
    }

    if (dotIndex != -1) {
        for (i in lastPart.indices) {
            baseDigit = getSymbolValue(lastPart[i])
            numberInBase += BigDecimal.valueOf(baseDigit * sourceBase.toDouble().pow(-i - 1.0))
        }
    }
    return numberInBase.divide(BigDecimal.ONE, 5, RoundingMode.CEILING)
}

fun getSymbolValue(symbol: Char): Int {
    if (symbol.isDigit()) return symbol.toString().toInt()
    else return 10 + (symbol.uppercase().first() - 'A')
}