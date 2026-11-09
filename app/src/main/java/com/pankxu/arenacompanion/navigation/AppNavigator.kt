package com.pankxu.arenacompanion.navigation

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Navigation manager. Tracks current route and handles navigation events.
 * Integrated with Compose Navigation in MainActivity.
 */
class AppNavigator {

    private val _currentRoute = MutableStateFlow(NavigationRoutes.BROWSER)
    val currentRoute: StateFlow<String> = _currentRoute

    private val _backStack = MutableStateFlow<List<String>>(emptyList())
    val backStack: StateFlow<List<String>> = _backStack

    fun navigate(route: String) {
        if (route != _currentRoute.value) {
            _backStack.value = _backStack.value + _currentRoute.value
            _currentRoute.value = route
        }
    }

    fun navigateBack() {
        if (_backStack.value.isNotEmpty()) {
            _currentRoute.value = _backStack.value.last()
            _backStack.value = _backStack.value.dropLast(1)
        }
    }

    fun resetTo(route: String = NavigationRoutes.BROWSER) {
        _backStack.value = emptyList()
        _currentRoute.value = route
    }

    fun currentRoute(): String = _currentRoute.value
}
