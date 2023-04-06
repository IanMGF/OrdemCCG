package com.ordemrealitas.ccg.gameevents.defaultevents

import com.ordemrealitas.ccg.CardEntity
import com.ordemrealitas.ccg.match.Match
import com.ordemrealitas.ccg.match.Player

class AddCardToBoardEvent(val cardEntity: CardEntity, match: Match, val player: Player = cardEntity.player): DefaultEvent(match) {
    override fun onExecute(){
        match.stateChangeSemaphore.acquire()
        cardEntity.location = CardEntity.Location.BOARD
        match.eventHandler.callEvent(CardRevealEvent(cardEntity, match).apply{ cause = this@AddCardToBoardEvent })
        match.stateChangeSemaphore.release()
    }
}