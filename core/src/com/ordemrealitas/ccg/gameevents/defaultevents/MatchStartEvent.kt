package com.ordemrealitas.ccg.gameevents.defaultevents

import com.ordemrealitas.ccg.match.Match
import com.ordemrealitas.ccg.match.Player


class MatchStartEvent(private val player1: Player, private val player2: Player, match: Match) : DefaultEvent(match){
    override fun onExecute() {
        match.stateChangeSemaphore.acquire()
        player1.deck.shuffle()
        player2.deck.shuffle()
        match.stateChangeSemaphore.release()

        for (i in 1..3)
            match.eventHandler.run {
                callEvent(PlayerDrawEvent(match, player1).apply { cause = this@MatchStartEvent })
                callEvent(PlayerDrawEvent(match, player2).apply { cause = this@MatchStartEvent })
            }
    }
}