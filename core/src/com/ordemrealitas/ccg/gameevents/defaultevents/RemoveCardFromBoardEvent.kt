package com.ordemrealitas.ccg.gameevents.defaultevents

import com.ordemrealitas.ccg.CardEntity
import com.ordemrealitas.ccg.match.Match
import com.ordemrealitas.ccg.match.Player

class RemoveCardFromBoardEvent(val cardEntity: CardEntity, match: Match, val player: Player = cardEntity.player): DefaultEvent(match) {
    override fun onExecute(){
        match.stateChangeSemaphore.acquire()
        cardEntity.location = CardEntity.Location.GRAVEYARD
        match.stateChangeSemaphore.release()
    }
}