package com.ordemrealitas.ccg.graphical

import com.ordemrealitas.ccg.CardEntity
import com.ordemrealitas.ccg.match.Player
import java.util.ArrayList
import java.util.concurrent.Semaphore

class GraphicalPlayer : Player {
    companion object {
        val semaphore = Semaphore(0)
        var asyncPlayedCards: List<CardEntity> = emptyList()
    }

    override var deck: ArrayList<CardEntity> = ArrayList()
    override var hand: ArrayList<CardEntity> = ArrayList()
    override var board: ArrayList<CardEntity> = ArrayList()
    override var graveyard: ArrayList<CardEntity> = ArrayList()
    override var energy: Int = 0
    override var health: Int = 100

    override val playedCards: List<CardEntity>
        get() {
            semaphore.acquire()
            return asyncPlayedCards
        }
}