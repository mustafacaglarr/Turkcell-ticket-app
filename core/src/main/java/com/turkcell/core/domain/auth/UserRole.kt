package com.turkcell.core.domain.auth

enum class UserRole {
    USER, STAFF, ADMIN;

    companion object {
        fun fromApi(value: String?): UserRole = when (value?.uppercase()) {
            "ADMIN" -> ADMIN
            "STAFF" -> STAFF
            else -> USER
        }
    }
}
