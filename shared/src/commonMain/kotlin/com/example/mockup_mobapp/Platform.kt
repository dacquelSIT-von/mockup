package com.example.mockup_mobapp

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform