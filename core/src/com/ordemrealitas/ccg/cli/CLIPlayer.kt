package com.ordemrealitas.ccg.cli

import com.ordemrealitas.ccg.CardEntity
import com.ordemrealitas.ccg.match.Match
import com.ordemrealitas.ccg.match.Player
import java.util.ArrayList

class CLIPlayer: Player {
    override var deck: ArrayList<CardEntity> = ArrayList()
    override var hand: ArrayList<CardEntity> = ArrayList()
    override var board: ArrayList<CardEntity> = ArrayList()
    override var graveyard: ArrayList<CardEntity> = ArrayList()
    override var energy: Int = 0
    override var health: Int = 100

    override val playedCards: List<CardEntity>
        get() = cliPlayerTurn()

    private fun cliPlayerTurn(): List<CardEntity> {
        print("Sua mesa: ")
        println(board.map { "${it.card.name}[${it.cost}|${it.power}]${
            if (it.card.hasEffect) "*"
            else ""
        }" })
        print("Sua mão:  ")
        print(hand.map { "${it.card.name}[${it.cost}|${it.power}]${
            if (it.hasEffect) "*"
            else ""
        }" })
        println(" HP: $health | Energia: $energy")
        print("[Jogar / Ler / Pular]: ")
        val input = readLine() ?: return emptyList()
        return if(input.startsWith("Jogar"))
            input.removePrefix("Jogar ").split(' ').map { hand[it.toInt()] }
        else if(input.startsWith("Ler ")) {
            val cardName = input.removePrefix("Ler ")
            val card = Match.registeredCards?.first { it.name.equals(cardName, ignoreCase = true) }
            if(card != null) {
                println("${card.name}[${card.baseCost}|${card.basePower}]: ${card.description}\n")
            } else {
                println("Carta não encontrada: $cardName")
            }
            cliPlayerTurn()
        } else if(input.startsWith("Pular")) {
            return emptyList()
        }else {
            print("Invalido.")
            cliPlayerTurn()
        }
    }
}