package gg.happy.dglab.module.api

class Strength(
    var aCurrentStrength: Int = 0,
    var bCurrentStrength: Int = 0,
    var aMaxStrength: Int = 0,
    var bMaxStrength: Int = 0
)
{
    fun byString(str: String): Strength
    {
        str.substringAfter("strength-").split('+').map { it.toInt() }.let {
            aCurrentStrength = it[0]
            bCurrentStrength = it[1]
            aMaxStrength = it[2]
            bMaxStrength = it[3]
        }
        return this
    }
}