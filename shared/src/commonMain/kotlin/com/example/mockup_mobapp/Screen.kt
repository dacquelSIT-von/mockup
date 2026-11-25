package com.example.mockup_mobapp

import com.example.mockup_mobapp.data.model.UserRole

internal fun isScreenAuthorized(screen: Screen, role: UserRole): Boolean =
    screen != Screen.STAFF_CLAIMS || role == UserRole.STAFF

enum class Screen {
    HOME,
    FIND_ITEM,
    ITEM_DETAILS,
    REPORT_ITEM,
    MY_ACTIVITY,
    MY_ACCOUNT,
    REPORT_PROBLEM,
    STAFF_CLAIMS
}

/*Screen represents our application's navigation destinations.
Keeping it separate makes it reusable across different parts of
the application.*/
