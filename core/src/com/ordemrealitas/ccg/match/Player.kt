package com.ordemrealitas.ccg.match

import com.ordemrealitas.ccg.CardEntity
import com.ordemrealitas.ccg.gameevents.eventcauses.EventCause
import com.ordemrealitas.ccg.gameevents.eventcauses.EventCauseType
import java.util.ArrayList

interface Player: EventCause {
    var deck: ArrayList<CardEntity>
    var hand: ArrayList<CardEntity>
    var board: ArrayList<CardEntity>
    var graveyard: ArrayList<CardEntity>
    var energy: Int
    var health: Int

    val totalPower: Int
        get() = board.sumBy { cardEntity -> cardEntity.power }

    val playedCards: List<CardEntity>

    override val eventCauseType: EventCauseType
        get() = EventCauseType.PLAYER
}