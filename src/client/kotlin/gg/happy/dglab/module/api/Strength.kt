package gg.happy.dglab.module.api

import java.util.regex.Matcher

class Strength(
    var aCurrentStrength: Int = 0,
    var bCurrentStrength: Int = 0,
    var aMaxStrength: Int = 0,
    var bMaxStrength: Int = 0
)
{
    companion object
    {
        fun byMatcher(matcher: Matcher) =
            Strength(
                aCurrentStrength = matcher.group(1).toInt(),
                bCurrentStrength = matcher.group(2).toInt(),
                aMaxStrength = matcher.group(3).toInt(),
                bMaxStrength = matcher.group(4).toInt()
            )
    }

    fun byMatcher(matcher: Matcher): Strength
    {
        aCurrentStrength = matcher.group(1).toInt()
        bCurrentStrength = matcher.group(2).toInt()
        aMaxStrength = matcher.group(3).toInt()
        bMaxStrength = matcher.group(4).toInt()
        return this
    }
}