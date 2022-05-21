package com.skyd.imomoe.util.compare

/**
 * 比较集数名称(title)的字典序，数字按照从大到小，例如90小于100
 * 例：第10集<第11集<第11.5集<第90集<第100集
 */
object EpisodeTitleCompareUtil {

    var asc: Boolean = true

    // 计算出数字的结尾下标，最多可以包括一个小数点。不能只包含小数点
    private fun findDigitEndIndex(arrChar: String, at: Int): Int {
        var k = at
        var c: Char
        var hasDot = false
        var hasNumber = false
        while (k < arrChar.length) {
            c = arrChar[k]
            if (c == '.' && !hasDot) hasDot = true
            else if (c > '9' || c < '0') break
            else if ((c <= '9' || c >= '0') && !hasNumber) hasNumber = true
            k++
        }
        return if (hasNumber) k else at
    }

    fun compare(a: String, b: String): Int {
        var aIndex = 0
        var bIndex = 0
        var aComparedUnitEndIndex: Int
        var bComparedUnitEndIndex: Int
        while (aIndex < a.length && bIndex < b.length) {
            // 找a串的数字结束下标+1
            aComparedUnitEndIndex = findDigitEndIndex(a, aIndex)
            // 找b串的数字结束下标+1
            bComparedUnitEndIndex = findDigitEndIndex(b, bIndex)
            // 如果a和b数字的结束下标都增加了，则说明之前开始找的地方是数字，开始比较数字
            if (aComparedUnitEndIndex > aIndex && bComparedUnitEndIndex > bIndex) {
                // 用BigDecimal比较，防止浮点数出现精度问题
                val aDigit = a.substring(aIndex, aComparedUnitEndIndex).toBigDecimal()  // a数
                val bDigit = b.substring(bIndex, bComparedUnitEndIndex).toBigDecimal()  // b数
                // 如果a数!=b数，则返回其差值
                aDigit.compareTo(bDigit).let { if (it != 0) return asc(it) }
                // 如果a数==b数，则继续比较
                aIndex = aComparedUnitEndIndex
                bIndex = bComparedUnitEndIndex
            } else {
                if (a[aIndex] != b[bIndex]) return asc(a[aIndex] - b[bIndex])
                aIndex++
                bIndex++
            }
        }
        return asc(a.length - b.length)
    }

    // 如果是升序，则返回自身，否则返回相反数
    private fun asc(a: Int): Int = if (asc) a else -a
}
