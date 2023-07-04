package me.oyb.demo.nio

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class SimpleNioServerApplication

fun main() {
    runApplication<SimpleNioServerApplication>()
}