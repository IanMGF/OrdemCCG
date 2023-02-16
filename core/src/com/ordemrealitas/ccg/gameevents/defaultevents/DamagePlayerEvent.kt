package com.ordemrealitas.ccg.gameevents.defaultevents

import com.ordemrealitas.ccg.match.Match
import com.ordemrealitas.ccg.match.Player

class DamagePlayerEvent(val player: Player, match: Match, damage: Int) : DefaultEvent(match) {
    var damage: Int = damage
        set(value) { field = value.coerceAtLeast(0) }

    override fun onExecute() { player.health -= damage }
}