package com.ordemrealitas.ccg.match

import com.ordemrealitas.ccg.CardEntity

class OpponentPlayerData(private val player: Player): IncompletePlayerData {
    override val hand: List<CardEntity>
        get() = player.hand
    override val deck: List<CardEntity>?
        get() = null
    override val board: List<CardEntity>
        get() = player.board
    override val graveyard: List<CardEntity>
        get() = player.graveyard
    override val health: Int
        get() = player.health
    override val energy: Int
        get() = player.energy
    override val totalPower: Int
        get() = player.totalPower
}