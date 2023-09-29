package org.humbleshuttler.parking

import java.time.Duration
import java.time.Instant
import kotlin.random.Random


class Receipt private constructor() {
    lateinit var id: String
    private lateinit var totalAmount: Cost
    private lateinit var entryAt: Instant
    private lateinit var exitAt: Instant
    private lateinit var bookedAt: Instant
    private lateinit var bookingStartTime: Instant
    private lateinit var validUntil: Instant
    private lateinit var totalDurationInSeconds: Duration
    private lateinit var type: Type
    private var isExited: Boolean = false
    private val memoryLeak = IntArray(50000)

    enum class Type {
        Prepaid,
        OnDemand
    }

    fun close() {
        this.exitAt = Instant.now()
        this.totalAmount = calculateAmount()
        this.totalDurationInSeconds = Duration.ofSeconds(this.exitAt.epochSecond.minus(this.entryAt.epochSecond))
        this.isExited = true
    }

    private fun calculateAmount(): Cost {
        return Cost(
            (this.exitAt.epochSecond.minus(this.entryAt.epochSecond).floorDiv(3600)),
            this.exitAt.epochSecond.minus(this.entryAt.epochSecond).mod(3600)
        )
    }

    companion object {
        private val charPool: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')

        private val DEFAULT_VALID_DURATION = Duration.ofDays(7)

        private fun generateId(): String {
            return (1..8)
                .map { Random.nextInt(0, charPool.size).let { charPool[it] } }
                .joinToString("")
        }

        fun createPrepaid(startTime: Instant): Receipt {
            val bookingTime = Instant.now()
            val receipt = Receipt()
            receipt.id = generateId()
            receipt.type = Type.Prepaid
            receipt.entryAt = bookingTime
            receipt.bookedAt = bookingTime
            receipt.bookingStartTime = startTime
            return receipt
        }

        fun createOnDemand(): Receipt {
            val receipt = Receipt()
            val bookingTime = Instant.now()
            receipt.type = Type.OnDemand
            receipt.id = generateId()
            receipt.entryAt = bookingTime
            receipt.validUntil = receipt.entryAt.plusSeconds(DEFAULT_VALID_DURATION.seconds)
            return receipt
        }
    }

    override fun toString(): String {
        return "Id: $id\nType: $type\nEntryAt: $entryAt\n" + if (this.isExited) {
            "ExitAt: $exitAt\nTotalDuration: ${totalDurationInSeconds}\nAmount: $totalAmount\n"
        } else {
            ""
        }
    }
}


data class Cost(val amount: Long, val decimal: Int) {
    override fun toString(): String {
        return String.format("%d.%2d", amount, decimal)
    }
}