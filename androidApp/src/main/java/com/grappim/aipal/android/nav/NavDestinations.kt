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

    data object ApiKeys : NavDestinations {
        override val route: String = "api_key_route"
    }

    data object AiSettings : NavDestinations {
        override val route: String = "ai_settings_route"
    }

    data object SstSettings : NavDestinations {
        override val route: String = "sst_settings_route"
    }
}
