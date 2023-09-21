package org.humbleshuttler.parking

import org.humbleshuttler.parking.exception.InvalidReceiptException
import org.humbleshuttler.parking.exception.ParkingLotFullException
import java.lang.StringBuilder
import java.time.Instant
import kotlin.random.Random

class Lot(val address: Address, private val parkingCount: Int) {

    private var lotId: String = generateId()
    private var emptySpaceCount = parkingCount
    private val history = hashMapOf<String, Receipt>()


    override fun toString(): String {
        return "LotId: $lotId\n" + "Address: $address" + getHistory()
    }

    fun isFull(): Boolean {
        return emptySpaceCount == 0
    }

    fun parkVehicle(): Receipt {
        if (isFull()) {
            throw ParkingLotFullException()
        }
        val receipt = Receipt.createOnDemand()
        history[receipt.id] = receipt
        this.emptySpaceCount -= 1
        return receipt
    }

    fun exitVehicle(receiptId: String): Receipt {
        val receipt = history[receiptId] ?: throw InvalidReceiptException(receiptId)
        receipt.close()
        // perform payment
        this.emptySpaceCount += 1
        return receipt
    }

    fun bookSlot(startTime: Instant): Receipt {
        val receipt = Receipt.createPrepaid(startTime)
        // take payment info
        return receipt
    }

    private fun getHistory(): String {
        val builder = StringBuilder()
        builder.append("===== History of parking lot =====")
        builder.append("ReceiptId Receipt")
        builder.append("\n")
        for (item in history) {
            builder.append(item.key + " " + item.value)
            builder.append("\n")
        }
        builder.append("===== End of History =====")
        builder.append("\n")
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
        builder.append(line2 + "\n")
        builder.append("city: $city\n")
        builder.append("state: $state\n")
        builder.append("country: $country\n")
        builder.append("zip code: $zip\n")
        return builder.toString()
    }
}