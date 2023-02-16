package com.ordemrealitas.ccg

import com.ordemrealitas.ccg.gameevents.eventcauses.EventCause
import com.ordemrealitas.ccg.gameevents.eventcauses.EventCauseType
import com.ordemrealitas.ccg.match.Player
import com.ordemrealitas.ccg.mock.MockPlayer

class CardEntity (val card: Card, val player: Player): EventCause{
    var cost: Int = card.baseCost
        set(value) { field = value.coerceAtLeast(0) }
    var power: Int = card.basePower
        set(value) { field = value.coerceAtLeast(0) }
    var location: Location
        get() =  if(player.deck .contains(this)) Location.DECK
            else if(player.hand .contains(this)) Location.HAND
            else if(player.board.contains(this)) Location.BOARD
            else                                 Location.GRAVEYARD
        set(value) {
            locationList.remove(this)
            when(value){
                Location.BOARD      -> { player.board       .add(this); }
                Location.HAND       -> { player.hand        .add(this); }
                Location.DECK       -> { player.deck        .add(this); }
                Location.GRAVEYARD  -> { player.graveyard   .add(this); }
            }
        }

    private val locationList: MutableList<CardEntity>
        get(){
            return when(location){
                Location.DECK       -> player.deck
                Location.BOARD      -> player.board
                Location.HAND       -> player.hand
                Location.GRAVEYARD  -> player.graveyard
            }
        }
    var hasEffect: Boolean = card.hasEffect

    override val eventCauseType: EventCauseType
        get() = EventCauseType.CARD_ENTITY

    enum class Location {
        BOARD, HAND, DECK, GRAVEYARD
    }

    override fun toString(): String {
        return "${card.name} [$cost, $power]"
    }

    fun clone(): CardEntity {
        return CardEntity(card, player).apply{ this.cost = this@CardEntity.cost; this.power = this@CardEntity.power }
    }
}