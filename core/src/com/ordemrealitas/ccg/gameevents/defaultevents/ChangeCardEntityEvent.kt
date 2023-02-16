package com.ordemrealitas.ccg.gameevents.defaultevents

import com.ordemrealitas.ccg.CardEntity
import com.ordemrealitas.ccg.match.Match

class ChangeCardEntityEvent(val targetCard: CardEntity, match: Match, var powerDelta: Int = 0, var costDelta: Int = 0) : DefaultEvent(match) {
    override fun onExecute() {
        targetCard.power += powerDelta
        targetCard.cost  += costDelta
    }
}