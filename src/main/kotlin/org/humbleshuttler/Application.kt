package org.humbleshuttler

import io.ktor.server.application.*
import io.ktor.server.netty.*
import org.humbleshuttler.plugins.configureMonitoring
import org.humbleshuttler.plugins.configureRouting
import org.humbleshuttler.plugins.configureSerialization

fun main(args: Array<String>): Unit = EngineMain.main(args)

fun Application.module() {
    configureSerialization()
    configureMonitoring()
    configureRouting()
}
