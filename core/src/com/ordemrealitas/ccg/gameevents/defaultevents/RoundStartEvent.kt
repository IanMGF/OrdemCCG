package com.ordemrealitas.ccg.gameevents.defaultevents

import com.ordemrealitas.ccg.match.Match

class RoundStartEvent(match: Match) : DefaultEvent(match){
    override fun onExecute() {
        match.stateChangeSemaphore.acquire()
        match.eventHandler.callEvent(PlayerDrawEvent(match, match.strongestPlayer))
        match.eventHandler.callEvent(PlayerDrawEvent(match, match.weakestPlayer  ))

        // Emergency Mode player draws Relic card
        match.emergencyModePlayer?.run { match.eventHandler.callEvent(PlayerDrawEvent(match, this)) }

        match.strongestPlayer.energy++
        match.weakestPlayer  .energy++
        match.stateChangeSemaphore.release()
    }
}