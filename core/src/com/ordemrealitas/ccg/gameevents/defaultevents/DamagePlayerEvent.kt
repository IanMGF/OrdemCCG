package com.ordemrealitas.ccg.gameevents.defaultevents

import com.ordemrealitas.ccg.match.Match
import com.ordemrealitas.ccg.match.Player

class DamagePlayerEvent(val player: Player, match: Match, damage: Int) : DefaultEvent(match) {
    var damage: Int = damage
        set(value) { field = value.coerceAtLeast(0) }

    override fun onExecute() {
        player.health -= damage

        match.isGameOver = match.isGameOver or (player.health <= 0)
        if(match.isGameOver)
            match.winner =  if(match.weakestPlayer.health == match.strongestPlayer.health) null
                            else maxOf(match.weakestPlayer, match.strongestPlayer, compareBy{ it.health })
    }
}