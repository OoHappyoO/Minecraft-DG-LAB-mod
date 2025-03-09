package gg.happy.dglab.util

import java.net.Inet4Address
import java.net.NetworkInterface
import java.util.*

fun getAddressAutoly(): String?
{
    var result: String? = null
    NetworkInterface.getNetworkInterfaces().forEach {
        if (it.isLoopback || !it.isUp)
            return@forEach
        it.inetAddresses.forEach { address ->
            if (address is Inet4Address)
            {
                if (result != null)
                    return null
                result = address.hostAddress
            }
        }
    }
    return result
}

inline fun <T> Enumeration<T>.forEach(action: (T) -> Unit)
{
    while (hasMoreElements())
        action(nextElement())
}