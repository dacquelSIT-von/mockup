package com.example.mockup_mobapp

import kotlin.test.Test
import kotlin.test.assertEquals
import com.example.mockup_mobapp.data.model.UserRole
import com.example.mockup_mobapp.data.model.normalizeUserRole

class SharedCommonTest {

    @Test
    fun example() {
        assertEquals(3, 1 + 2)
    }

    @Test
    fun normalizesKnownProfileRolesAndRejectsUnknownValues() {
        assertEquals(UserRole.STUDENT, normalizeUserRole("STUDENT"))
        assertEquals(UserRole.STUDENT, normalizeUserRole("Student"))
        assertEquals(UserRole.STAFF, normalizeUserRole(" staff "))
        assertEquals(UserRole.UNKNOWN, normalizeUserRole("administrator"))
    }

    @Test
    fun staffClaimsNavigationIsOnlyAvailableToStaff() {
        assertEquals(false, Screen.STAFF_CLAIMS in navigationScreensForRole(UserRole.STUDENT))
        assertEquals(true, Screen.STAFF_CLAIMS in navigationScreensForRole(UserRole.STAFF))
        assertEquals(false, Screen.STAFF_CLAIMS in navigationScreensForRole(UserRole.UNKNOWN))
    }

    @Test
    fun studentNavigationRemainsAvailableForEveryRole() {
        listOf(UserRole.STUDENT, UserRole.STAFF, UserRole.UNKNOWN).forEach { role ->
            assertEquals(true, Screen.HOME in navigationScreensForRole(role))
            assertEquals(true, Screen.FIND_ITEM in navigationScreensForRole(role))
            assertEquals(true, Screen.REPORT_ITEM in navigationScreensForRole(role))
            assertEquals(true, Screen.MY_ACTIVITY in navigationScreensForRole(role))
            assertEquals(true, Screen.MY_ACCOUNT in navigationScreensForRole(role))
        }
    }

    @Test
    fun staffClaimsScreenRequiresStaffRole() {
        assertEquals(false, isScreenAuthorized(Screen.STAFF_CLAIMS, UserRole.STUDENT))
        assertEquals(false, isScreenAuthorized(Screen.STAFF_CLAIMS, UserRole.UNKNOWN))
        assertEquals(true, isScreenAuthorized(Screen.STAFF_CLAIMS, UserRole.STAFF))
        assertEquals(true, isScreenAuthorized(Screen.HOME, UserRole.STUDENT))
    }
}
