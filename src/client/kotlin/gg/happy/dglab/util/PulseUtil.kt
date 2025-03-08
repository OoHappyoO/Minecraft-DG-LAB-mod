package gg.happy.dglab.util

object PulseUtil
{
    /**
     * Convent frequency to Dg-Lab format
     *
     * @param frequency frequency
     * @return Dg-Lab format number
     */
    private fun convert(frequency: Int): Int =
        when (frequency)
        {
            in 10..100 -> frequency
            in 101..600 -> (frequency - 100) / 5 + 100
            in 601..1000 -> (frequency - 600) / 10 + 200
            else -> throw IllegalArgumentException("frequency must be between 0 and 1000")
        }

    /**
     * input frequency and strength list, return the pulse string
     *
     * @param frequencies frequency list
     * @param percent     percent list
     * @return the pulse strings
     */
    fun pulse(frequencies: List<Int>, percent: List<Int>): List<String> =
        mutableListOf<String>().apply {
            val size = percent.size and frequencies.size
            for (i in 0 until size step 4)
            {
                add(buildString {
                    for (j in 0 until 4)
                        append(String.format("%02X", convert(frequencies.getOrElse(i + j) { 10 })))
                    for (j in 0 until 4)
                        append(String.format("%02X", percent.getOrElse(i + j) { 0 }))
                })
            }
        }

    /**
     * input frequency and strength list, return the pulse string
     *
     * @param frequency frequency
     * @param percent   strength list
     * @return the pulse strings
     */
    fun pulse(frequency: Int, percent: List<Int>): List<String> =
        mutableListOf<String>().apply {
            val size = percent.size
            val frequencyHex = String.format("%02X", convert(frequency))
            val frequencyString = buildString {
                repeat(4) { append(frequencyHex) }
            }
            for (i in 0 until size step 4)
            {
                add(buildString {
                    append(frequencyString)
                    for (j in 0 until 4)
                        append(String.format("%02X", percent.getOrElse(i + j) { 0 }))
                })
            }
        }
}

fun toArrayString(array: List<String>): String =
    buildString {
        append('[')
        array.forEachIndexed { index, s ->
            if (index > 0)
                append(',')
            append('"')
            append(s)
            append('"')
        }
        append(']')
    }