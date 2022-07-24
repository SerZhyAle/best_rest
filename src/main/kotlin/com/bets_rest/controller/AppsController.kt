package com.bets_rest.controller

import org.springframework.hateoas.MediaTypes
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import java.io.IOException
import java.sql.CallableStatement
import java.sql.Connection
import java.sql.DriverManager
import java.sql.Timestamp
import java.time.LocalDateTime

@CrossOrigin(maxAge = 3600)
@RestController
class AppsController(
) {
    @GetMapping
    fun indexAnswer(): String = "REST Usage: <IP>:8080/total_amount/{player_id}/{date_time_from}/{date_time_to} <br>" +
            "for example: http://localhost:8080/total_amount/872de53a900f3250ae5649ea19e5c381/2019-10-22T00:00:00/2019-10-22T23:00:00"
    @GetMapping(
        value = ["/total_amount/{player_id}/{date_from}/{date_to}"],
        produces = [MediaTypes.HAL_JSON_VALUE, MediaType.APPLICATION_JSON_VALUE]
//    [MediaType.TEXT_EVENT_STREAM_VALUE])
    )
    fun gettotalamont(
        @PathVariable player_id: String,
        @PathVariable date_from: String,
        @PathVariable date_to: String
    ): TotalAmount {

        val startTime: String? = firstNChars(date_from.replace("T"," "), 13) //data hour
        val startTimestamp:Timestamp = Timestamp.valueOf(date_from.replace("T"," "))
        val endTime: String? = firstNChars(date_to.replace("T"," "),13)
        val endTimestamp:Timestamp = Timestamp.valueOf(date_to.replace("T"," "))

        //return Flux.interval(Duration.ofSeconds(1)).map{TotalAmount(player_id,startTimestamp,endTimestamp)}

        var aggregates = mutableListOf<ResultItem>()
        var resultitem:ResultItem

        val bUrl: String= "jdbc:postgresql://localhost:12306/STAGING"
        val dbUser: String = "admin"
        val dbPass: String = "ThisPasswordIsSafe"
        Class.forName("org.postgresql.Driver")
        try {
            println(bUrl)
            val conn: Connection = DriverManager . getConnection (bUrl, dbUser, dbPass)
            val callableStatement: CallableStatement = conn . prepareCall ("{ call public.forest_user_period (?, ?, ?) }")
            callableStatement.setString(1, player_id)
            callableStatement.setString(2, startTime)
            callableStatement.setString(3, endTime)
            if (callableStatement.execute()) {
                val resultSet = callableStatement.resultSet
                while (resultSet.next()) {
                    resultitem= ResultItem(resultSet.getString("game_id"), resultSet.getString("sum_amount_bet"), resultSet.getString("sum_amount_win"))
                    aggregates.add(resultitem)

       //             System.out.println(": " + resultSet.getString("game_id") + ", " + resultSet.getString("sum_amount_bet") + ", " + resultSet.getString("sum_amount_win"));
                }
            }
        } catch (e: IOException) {
            System.out.println("Something went wrong: " + e.message);
        }

        return TotalAmount(player_id,startTimestamp,endTimestamp,aggregates)
    }

    // yes I noticed it looks like timestamp, but we're going to return everything aggregated for hours
    data class TotalAmount(
        var player_id: String,
        var startTimestamp: Timestamp,
        var endTimestamp: Timestamp,
        var aggregates: MutableList<ResultItem>)

    data class ResultItem(
        var gameId: String,
        var TotalBetAmount: String,
        var TotalWinAmount: String
    )

    fun firstNChars(str: String?, n: Int): String? {
        if (str == null) {
            return null
        }
        return if (str.length < n) str else str.substring(0, n)
    }}