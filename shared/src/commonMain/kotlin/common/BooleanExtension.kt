package common
fun Boolean.toLong(): Long {
    return if (this) 1L else 0L
}