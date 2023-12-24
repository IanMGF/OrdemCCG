package com.ordemrealitas.ccg.gameevents.defaultevents

import com.ordemrealitas.ccg.match.Match

class RoundEndEvent(match: Match) : DefaultEvent(match){
    override fun onExecute() {
        match.eventHandler.callEvent(DamagePlayerEvent(match.weakestPlayer,   match, match.strongestPlayer.totalPower).apply{ cause = this@RoundEndEvent })
        match.eventHandler.callEvent(DamagePlayerEvent(match.strongestPlayer, match, match.weakestPlayer  .totalPower).apply{ cause = this@RoundEndEvent })
    }
}