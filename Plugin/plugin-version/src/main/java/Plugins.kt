/**
 *
 *  @Author LiABao
 *  @Since 2022/2/15
 *
 */
object Plugins {
    const val AAA = 0
    val ext = hashMapOf<String, String>().apply {
        this["11"] = "222"
    }

    @JvmStatic
    fun extra(name: String): String? {
        return ext[name]
    }
}