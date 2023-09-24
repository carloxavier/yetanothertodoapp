package view.navigation

sealed class Screen {
    object TodoList : Screen()
    object TodoDetails : Screen()
}