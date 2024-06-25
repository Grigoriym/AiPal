package com.grappim.aipal.android.nav

sealed interface NavDestinations {
    val route: String

    data object Chat : NavDestinations {
        override val route: String = "chat_route"
    }

    data object Settings : NavDestinations {
        override val route: String = "settings_route"
    }

    data object Prompts : NavDestinations {
        override val route: String = "prompts_route"
    }
}
