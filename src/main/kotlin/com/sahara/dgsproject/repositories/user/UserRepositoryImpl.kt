package com.sahara.dgsproject.repositories.user

import com.example.generated.tables.pojos.Users
import com.example.generated.tables.references.USERS
import org.jooq.DSLContext
import org.springframework.stereotype.Repository

@Repository
class UserRepositoryImpl(
    private val dslContext: DSLContext
) : UserRepository {
    override fun create(user: Users): Users {
        return dslContext
            .insertInto(
                USERS,
                USERS.USERNAME,
                USERS.PASSWORD
            ).values(
                user.username,
                user.password
            ).returningResult(USERS)
            .fetchOneInto(Users::class.java)
            ?: throw IllegalArgumentException("user not found")
    }

    override fun findById(id: Long): Users {
        return dslContext
            .selectOne()
            .from(USERS)
            .where(USERS.ID.eq(id))
            .fetchOneInto(Users::class.java)
            ?: throw IllegalArgumentException("user not found")
    }
}