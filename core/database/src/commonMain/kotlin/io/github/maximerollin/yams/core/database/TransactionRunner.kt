package io.github.maximerollin.yams.core.database

import androidx.room.immediateTransaction
import androidx.room.useWriterConnection

public interface TransactionRunner {
    public suspend operator fun <T> invoke(block: suspend () -> T): T
}

internal class DefaultTransactionRunner(
    private val db: AppDatabase
) : TransactionRunner {
    override suspend fun <T> invoke(block: suspend () -> T): T {
        return db.useWriterConnection { transactor ->
            transactor.immediateTransaction {
                block()
            }
        }
    }
} 
