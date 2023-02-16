package com.ordemrealitas.ccg.match

import com.ordemrealitas.ccg.CardEntity

/*
 Interface that the client-side will use to refer to player data.
 As the name suggests, it is incomplete, so it cannot be used to access information
 the player should not have by using the game rules (such as opponent's hand and deck)
 */
interface IncompletePlayerData {
    val hand: List<CardEntity?>?
    val deck: List<CardEntity?>?
    val board: List<CardEntity>
    val graveyard: List<CardEntity>

    val health: Int
    val energy: Int
    val totalPower: Int
}
