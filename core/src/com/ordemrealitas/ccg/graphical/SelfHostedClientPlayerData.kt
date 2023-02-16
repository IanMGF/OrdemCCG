package com.ordemrealitas.ccg.graphical

import com.ordemrealitas.ccg.CardEntity
import com.ordemrealitas.ccg.match.IncompletePlayerData
import com.ordemrealitas.ccg.match.Player

/*
Implementation of IncompletePlayerData for self-hosted clients
(Single player campaigns and multiplayer when one of the players is also a host)
 */
class SelfHostedClientPlayerData(private val player: Player): IncompletePlayerData {
    override val hand: List<CardEntity>?
        get() {
            val playerHandClone = player.hand.clone()
            if(playerHandClone as? List<CardEntity> != null)
                return playerHandClone
            return null
        }
    override val deck: List<CardEntity>? = null
    override val board: List<CardEntity>
        get() = player.board
    override val graveyard: List<CardEntity> = player.graveyard
    override val health: Int
        get() = player.health
    override val energy: Int
        get() = player.energy
    override val totalPower: Int
        get() = player.totalPower
}