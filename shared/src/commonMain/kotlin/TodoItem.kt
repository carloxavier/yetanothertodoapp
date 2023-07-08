import kotlinx.datetime.Clock

data class TodoItem(
    val id: Long? = null,
    val text: String = "",
    val done: Boolean = false,
    val timestamp: Long = Clock.System.now().toEpochMilliseconds()
)