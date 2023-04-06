package com.ordemrealitas.ccg.gameevents.defaultevents

import com.ordemrealitas.ccg.match.Match

class RoundEndEvent(match: Match) : DefaultEvent(match){
    override fun onExecute() {
        match.stateChangeSemaphore.acquire()
        match.eventHandler.callEvent(DamagePlayerEvent(match.weakestPlayer,   match, match.strongestPlayer.totalPower).apply{ cause = this@RoundEndEvent })
        match.eventHandler.callEvent(DamagePlayerEvent(match.strongestPlayer, match, match.weakestPlayer  .totalPower).apply{ cause = this@RoundEndEvent })

        match.isGameOver = (match.weakestPlayer.health <= 0) or (match.strongestPlayer.health <= 0)
        if(match.isGameOver)
            match.winner =  if(match.weakestPlayer.health == match.strongestPlayer.health) null
                            else maxOf(match.weakestPlayer, match.strongestPlayer, compareBy{ it.health })
        match.stateChangeSemaphore.release()
    }
}