package com.sahara.dgsproject.repositories.user

import com.example.generated.tables.pojos.Users

interface UserRepository {
    fun create(user: Users): Users

    fun findById(id: Long): Users
}