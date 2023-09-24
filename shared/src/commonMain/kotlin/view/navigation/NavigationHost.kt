package view.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState

@Composable
fun NavigationHost(
    screens: Map<Screen, @Composable (Any?) -> Unit>
) {
    val navigationAction = Navigator.currentNavigationAction.collectAsState().value
    Navigator.processNavigation(navigationAction, screens)
}