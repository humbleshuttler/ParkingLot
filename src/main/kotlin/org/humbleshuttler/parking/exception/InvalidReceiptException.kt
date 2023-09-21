package org.humbleshuttler.parking.exception

import org.humbleshuttler.parking.Receipt

class InvalidReceiptException(receiptId: String) : Error("Receipt: $receiptId is not valid!") {
}