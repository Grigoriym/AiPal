package com.grappim.aipal

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform