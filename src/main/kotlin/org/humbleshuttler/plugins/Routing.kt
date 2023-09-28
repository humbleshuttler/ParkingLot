package org.humbleshuttler.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.humbleshuttler.parking.Address
import org.humbleshuttler.parking.Lot
import org.humbleshuttler.parking.exception.ParkingLotFullException
import kotlin.math.log

fun Application.configureRouting() {
    val lot = Lot(Address("2116 jollay st", "", "Durham", "NC", "USA", "27703"), 10000)
    routing {
        get("/") {
            call.respondText("Available lots: $lot")
        }
        post("/new") {
            try {
                val receipt = lot.parkVehicle()
                call.respondText(receipt.toString())
            } catch (e: ParkingLotFullException) {
                call.respondText(status = HttpStatusCode.Forbidden) {"Parking lot is full"}
            }
        }
        post ("/exit") {
            val receiptId = call.receive<String>()
            val updatedReceipt = lot.exitVehicle(receiptId)
            call.respondText(updatedReceipt.toString())
        }
    }
}
