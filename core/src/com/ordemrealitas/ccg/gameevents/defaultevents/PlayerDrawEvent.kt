package com.ordemrealitas.ccg.gameevents.defaultevents

import com.ordemrealitas.ccg.CardEntity
import com.ordemrealitas.ccg.match.Match
import com.ordemrealitas.ccg.match.Player

class PlayerDrawEvent(match: Match, private val player: Player, private var cardEntity: CardEntity? = null): DefaultEvent(match) {
    override fun onExecute() {
        cardEntity = cardEntity?: player.deck.getOrNull(0)

        match.stateChangeSemaphore.acquire()
        cardEntity?.location = CardEntity.Location.HAND
        match.stateChangeSemaphore.release()
    }
}