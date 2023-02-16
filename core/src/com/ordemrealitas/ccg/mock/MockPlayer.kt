package com.ordemrealitas.ccg.mock

import com.ordemrealitas.ccg.CardEntity
import com.ordemrealitas.ccg.match.Player
import java.util.ArrayList

class MockPlayer: Player {
    override var deck: ArrayList<CardEntity> = ArrayList()
    override var hand: ArrayList<CardEntity> = ArrayList()
    override var board: ArrayList<CardEntity> = ArrayList()
    override var graveyard: ArrayList<CardEntity> = ArrayList()
    override var energy: Int = 0
    override var health: Int = 100
    override val playedCards: List<CardEntity>
        get() =
            if(hand.isNotEmpty()){
                val possibleCards = mutableListOf<CardEntity>()
                var playedCardIndex = 0
                while((hand.size > ++playedCardIndex) && (hand[playedCardIndex].cost + possibleCards.sumBy { it.cost } <= energy))
                    possibleCards.add(hand[playedCardIndex])
                possibleCards
            }
            else
                emptyList()
}