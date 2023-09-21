package org.humbleshuttler.plugins

import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.humbleshuttler.parking.Address
import org.humbleshuttler.parking.Lot
import kotlin.math.log

fun Application.configureRouting() {
    val lot = Lot(Address("2116 jollay st", "", "Durham", "NC", "USA", "27703"), 10)
    routing {
        get("/") {
            call.respondText("Available lots: $lot")
        }
        post("/new") {
            val receipt = lot.parkVehicle()
            call.respondText(receipt.toString())
        }
        post ("/exit") {
            val receiptId = call.receive<String>()
            val updatedReceipt = lot.exitVehicle(receiptId)
            call.respondText(updatedReceipt.toString())
        }
    }
}
