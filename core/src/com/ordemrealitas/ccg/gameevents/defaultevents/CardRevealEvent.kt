package com.ordemrealitas.ccg.gameevents.defaultevents

import com.ordemrealitas.ccg.CardEntity
import com.ordemrealitas.ccg.match.Match

class CardRevealEvent(val cardEntity: CardEntity, match: Match): DefaultEvent(match) {
    override fun onExecute(){
    }
}