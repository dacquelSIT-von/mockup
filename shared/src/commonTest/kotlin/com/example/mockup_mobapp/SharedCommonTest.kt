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
}
