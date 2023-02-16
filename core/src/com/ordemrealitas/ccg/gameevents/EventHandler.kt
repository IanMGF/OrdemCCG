package com.ordemrealitas.ccg.gameevents

import com.ordemrealitas.ccg.match.Match
import java.util.*

class EventHandler(private val match: Match) {
    private val queuedEvents: LinkedList<MatchEvent> = LinkedList()
    private var runningEvent = false
    private fun runEvent(matchEvent: MatchEvent){
        runningEvent = true
        match.strongestPlayer.board.forEach { it.card.onGameEvent(matchEvent, it) }
        match.weakestPlayer  .board.forEach { it.card.onGameEvent(matchEvent, it) }
        match.strongestPlayer.hand .forEach { it.card.onGameEvent(matchEvent, it) }
        match.weakestPlayer  .hand .forEach { it.card.onGameEvent(matchEvent, it) }
        match.strongestPlayer.deck .forEach { it.card.onGameEvent(matchEvent, it) }
        match.weakestPlayer  .deck .forEach { it.card.onGameEvent(matchEvent, it) }

        if(!matchEvent.isCancelled)
            matchEvent.onExecute()

        if(queuedEvents.isNotEmpty())
            runEvent(queuedEvents.removeAt(0))
        runningEvent = false
    }

    fun callEvent(matchEvent: MatchEvent){
        if(queuedEvents.isEmpty() and !runningEvent)
            runEvent(matchEvent)
        else
            queuedEvents.add(matchEvent)
    }
}