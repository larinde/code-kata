package com.koweg.kata

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.TestInstance
import java.math.BigDecimal
import java.time.LocalDateTime

//@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BusinessRulesTest {

    //   fun `rules should return a COMPLETED state when fulfilled`()  {
    @Test
    fun test_business_rule_should_pass_unexpired_transactions() {
        val trans = Transaction(BigDecimal(3000), LocalDateTime.now().minusDays(3), "7301", true)
        val transactionExpiryRule = TransactionExpiryDateValidationRule(trans, CompletionRule())
        val result = transactionExpiryRule.execute()
        assertThat(result).isEqualTo(Result.COMPLETED)
    }

    //    fun `rules should return a FAILED state when not fulfilled`() {
    @Test
    fun test_business_rule_should_fail_expired_transactions() {
        val trans = Transaction(BigDecimal(3000), LocalDateTime.now().minusDays(30), "7301", true)
        val transactionExpiryRule = TransactionExpiryDateValidationRule(trans, CompletionRule())
        val result = transactionExpiryRule.execute()

        assertThat(result).isEqualTo(Result.FAILED)
    }

    @Test
    fun test_business_rule_should_pass_when_kyc_can_be_verified() {
        val expired = LocalDateTime.now()
        val trans = Transaction(BigDecimal(3_000), expired, "7301", true)
        val kycRule = KYCVerificationRule(trans, CompletionRule())

        assertThat(kycRule.execute()).isEqualTo(Result.COMPLETED)
    }

    @Test
    fun test_business_should_fail_when_kyc_cannot_be_verified() {
        val expired = LocalDateTime.now()
        val trans = Transaction(BigDecimal(3_000), expired, "7301", false)
        val kycRule = KYCVerificationRule(trans, CompletionRule())
        assertThat(kycRule.execute()).isEqualTo(Result.FAILED)
    }

    @Test
    fun test_business_should_fail_when_transaction_amount_limit_is_exceeded() {
        val expired = LocalDateTime.now()
        val trans = Transaction(BigDecimal(3_000_000), expired, "7301", true)
        val amountLimitRule = TransactionAmountLimitRule(trans, CompletionRule())
        assertThat(amountLimitRule.execute()).isEqualTo(Result.FAILED)
    }

    @Test
    fun test_business_rule_should_pass_when_transaction_amount_is_within_limits() {
        val expired = LocalDateTime.now()
        val trans = Transaction(BigDecimal(10_000), expired, "7301", true)
        val amountLimitRule = TransactionAmountLimitRule(trans, CompletionRule())
        assertThat(amountLimitRule.execute()).isEqualTo(Result.COMPLETED)
    }

    @Test
    fun test_business_rules_can_be_chained_to_execute__in_any_order_till_they_complete_successfully() {
        //chain: check kyc -> check expiry date -> check amount limit
        val trans = Transaction(BigDecimal(3_000), LocalDateTime.now().minusDays(3), "7301", true)
        val amountLimitRule = TransactionAmountLimitRule(trans, CompletionRule())
        val transactionExpiryRule = TransactionExpiryDateValidationRule(trans, amountLimitRule)
        val kycRule = KYCVerificationRule(trans, transactionExpiryRule)
        val result = kycRule.execute()
        assertThat(result).isEqualTo(Result.COMPLETED)

        var rules = TransactionAmountLimitRule(trans, TransactionExpiryDateValidationRule(trans, KYCVerificationRule(trans, CompletionRule())))
        assertThat(rules.execute()).isEqualTo(Result.COMPLETED)

        var rules2 = TransactionExpiryDateValidationRule(trans, TransactionAmountLimitRule(trans, KYCVerificationRule(trans, CompletionRule())))
        assertThat(rules2.execute()).isEqualTo(Result.COMPLETED)
    }

    @Test
    fun test_business_rules_can_be_chained_to_execute_and_fail_before_completion_when_a_rule_is_violated() {
        //chain: check kyc -> check expiry date -> check amount limit
        val expired = LocalDateTime.now().minusDays(30)
        val trans = Transaction(BigDecimal(3_000), expired, "7301", true)
        val kycRule = KYCVerificationRule(trans, TransactionExpiryDateValidationRule(trans, TransactionAmountLimitRule(trans, CompletionRule())))
        assertThat(kycRule.execute()).isEqualTo(Result.FAILED)
    }

}

