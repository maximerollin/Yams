package com.maximerollin.yams

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform