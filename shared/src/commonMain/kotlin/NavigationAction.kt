sealed class NavigationAction {
    object Back : NavigationAction()
    data class Navigate(val screen: Screen, val param: Any? = null) : NavigationAction()
}