package com.ordemrealitas.ccg

import com.ordemrealitas.ccg.gameevents.MatchEvent

interface Card {
    val name: String
    val id: String
    val basePower: Int
    val baseCost: Int
    val description: String
    val hasEffect: Boolean

    fun onGameEvent(gameEvent: MatchEvent?, cardEntity: CardEntity) {}
}