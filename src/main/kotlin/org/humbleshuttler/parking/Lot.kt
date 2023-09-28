package org.humbleshuttler.parking

import org.humbleshuttler.parking.exception.InvalidReceiptException
import org.humbleshuttler.parking.exception.ParkingLotFullException
import java.lang.StringBuilder
import java.time.Instant
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger
import kotlin.random.Random

class Lot(val address: Address, private val parkingCount: Int) {

    private var lotId: String = generateId()
    private var emptySpaceCount: AtomicInteger = AtomicInteger(parkingCount)
    private val history = ConcurrentHashMap<String, Receipt>()


    override fun toString(): String {
        return "LotId: $lotId\n" + "Address: $address\n" + "Available spots: ${emptySpaceCount.get()}" + getHistory()
    }

    fun isFull(): Boolean {
        return emptySpaceCount.get() == 0
    }

    fun parkVehicle(): Receipt {
        synchronized(history) {
            if (isFull()) {
                throw ParkingLotFullException()
            }
            val receipt = Receipt.createOnDemand()
            history[receipt.id] = receipt
            this.emptySpaceCount.decrementAndGet()
            return receipt
        }

    }

    fun exitVehicle(receiptId: String): Receipt {
        val receipt = history[receiptId] ?: throw InvalidReceiptException(receiptId)
        receipt.close()
        // perform payment
        this.emptySpaceCount.incrementAndGet()
        return receipt
    }

    fun bookSlot(startTime: Instant): Receipt {
        val receipt = Receipt.createPrepaid(startTime)
        // take payment info
        return receipt
    }

    private fun getHistory(): String {
        val builder = StringBuilder()
        builder.append("===== History of parking lot =====\n")
        builder.append("ReceiptId Receipt\n")
        for (item in history) {
            builder.append(item.key + " " + item.value)
            builder.append("\n")
        }
        builder.append("===== End of History =====\n")
        return builder.toString()
    }

    companion object {
        private val charPool: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')

        private fun generateId(): String {
            return (1..8)
                .map { Random.nextInt(0, charPool.size).let { charPool[it] } }
                .joinToString("")
        }
    }
}

data class Address(
    val line1: String,
    val line2: String,
    val city: String,
    val state: String,
    val country: String,
    val zip: String
) {
    override fun toString(): String {
        val builder = StringBuilder(line1 + "\n")
        if (line2.isNotEmpty()) {
            builder.append(line2 + "\n")
        }
        builder.append("city: $city\n")
        builder.append("state: $state\n")
        builder.append("country: $country\n")
        builder.append("zip code: $zip\n")
        return builder.toString()
    }
}