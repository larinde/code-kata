package com.koweg.kata

import java.math.BigDecimal
import java.time.LocalDateTime

enum class Result {
    COMPLETED, FAILED
}

class Transaction(val amount: BigDecimal, val date: LocalDateTime, val accountNumber: String, val kycVerified: Boolean)

interface Rule<C, R> {
    fun execute(): R
}

interface Action<Input, Output> {
    fun execute(data: Input): Output
}

class AmountExceededAction : Action<Transaction, Result> {
    override fun execute(trans: Transaction): Result {
        val limit = BigDecimal(10_000)
        return if (trans.amount.compareTo(limit) > 1) Result.FAILED else Result.COMPLETED
    }
}

class TransactionAmountLimitRule(val trans: Transaction, val nextRule: Rule<Transaction, Result>) : Rule<Transaction, Result> {
    override fun execute(): Result {
        val limit = BigDecimal(10_000)
        return if (limit.compareTo(trans.amount) >= 0) nextRule.execute() else return Result.FAILED
    }
}

class KYCVerificationRule(val trans: Transaction, val nextRule: Rule<Transaction, Result>) : Rule<Transaction, Result> {
    override fun execute(): Result {
        return if (trans.kycVerified) nextRule.execute() else Result.FAILED
    }
}

class TransactionExpiryDateValidationRule(val trans: Transaction, val nextRule: Rule<Transaction, Result>) : Rule<Transaction, Result> {
    override fun execute(): Result {
        val dateLimit = LocalDateTime.now().minusDays(7)
        return if (trans.date.isAfter(dateLimit)) nextRule.execute() else Result.FAILED
    }
}

class CompletionRule : Rule<Transaction, Result> {
    override fun execute(): Result {
        return Result.COMPLETED
    }
}
