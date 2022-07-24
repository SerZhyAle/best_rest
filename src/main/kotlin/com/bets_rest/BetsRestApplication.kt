package com.bets_rest

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import java.io.IOException
import java.sql.*
import java.sql.DriverManager
import java.sql.Connection
//import org.postgresql.util.PSQLException

@SpringBootApplication
class BetsRestApplication
    fun main(args: Array<String>) {

        runApplication<BetsRestApplication>(*args)
    }
