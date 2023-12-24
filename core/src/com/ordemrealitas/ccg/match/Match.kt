package com.ordemrealitas.ccg.match

import com.ordemrealitas.ccg.Card
import com.ordemrealitas.ccg.CardEntity
import com.ordemrealitas.ccg.gameevents.EventHandler
import com.ordemrealitas.ccg.gameevents.MatchEvent
import com.ordemrealitas.ccg.gameevents.defaultevents.*
import com.ordemrealitas.ccg.gameevents.eventcauses.EventCause
import com.ordemrealitas.ccg.gameevents.eventcauses.EventCauseType
import com.ordemrealitas.ccg.graphical.GraphicalPlayer
import com.ordemrealitas.ccg.mock.MockPlayer
import java.util.concurrent.Semaphore
import java.util.function.BiConsumer
import kotlin.math.abs

class Match(private val player1: Player, private val player2: Player) : EventCause {
    companion object {
        var registeredCards: List<Card>? = null
    }

    private var round: Int = 0
        private set

    val stateChangeSemaphore: Semaphore = Semaphore(1)

    var isGameOver: Boolean = false
        get() = field || (round >= 20)

    var strongestPlayer: Player = player1
        private set

    var weakestPlayer: Player = player2
        private set

    var eventHandler: EventHandler = EventHandler(this)
    var winner: Player? = null
    val emergencyModePlayer: Player?
        get() = if (abs(strongestPlayer.totalPower - weakestPlayer.totalPower) >= 20)
            minOf(strongestPlayer, weakestPlayer, compareBy { it.totalPower })
        else    if (abs(strongestPlayer.health - weakestPlayer.health) >= 40)
            minOf(strongestPlayer, weakestPlayer, compareBy { it.health })
        else
            null

    fun getOpponent(player: Player): Player {
        return if (player == strongestPlayer)
            weakestPlayer
        else
            strongestPlayer
    }

    private fun startMatch() {
        eventHandler.callEvent(MatchStartEvent(player1, player2, this))
    }

    private fun startRound() {
        round++
        eventHandler.callEvent(RoundStartEvent(this))

        strongestPlayer = maxOf(weakestPlayer, strongestPlayer, compareBy { it.totalPower }).also {
            weakestPlayer = minOf(strongestPlayer, weakestPlayer, compareBy { it.totalPower })
        }
    }

    private fun computePlayedCards() {
        val hpPlayerCards = strongestPlayer.playedCards
        val lpPlayerCards = weakestPlayer.playedCards
        var hpSpentEnergy = 0
        var lpSpentEnergy = 0
        for (cardEntity in hpPlayerCards) {
            hpSpentEnergy += cardEntity.cost
            if (hpSpentEnergy > strongestPlayer.energy)
                break

            eventHandler.callEvent(AddCardToBoardEvent(cardEntity, this, strongestPlayer).apply {
                cause = strongestPlayer
            })
        }
        for (cardEntity in lpPlayerCards) {
            lpSpentEnergy += cardEntity.cost
            if (lpSpentEnergy > weakestPlayer.energy)
                break

            eventHandler.callEvent(AddCardToBoardEvent(cardEntity, this, weakestPlayer).apply { cause = weakestPlayer })
        }
    }

    private fun endRound() {
        eventHandler.callEvent(RoundEndEvent(this))
    }

    fun mainLoop() {
        startMatch()
        while (!isGameOver) {
            startRound()
            computePlayedCards()
            endRound()
        }
    }

    override val eventCauseType: EventCauseType = EventCauseType.GAME
}

fun registerCards(match: Match) {
    class ImprovisedCard(
        override val id: String,
        override val name: String,
        override val basePower: Int = 0,
        override val baseCost: Int = 0,
        override val description: String = "",
        val action: BiConsumer<MatchEvent, CardEntity>? = null
    ) : Card {
        override val hasEffect: Boolean = action != null
        override fun onGameEvent(gameEvent: MatchEvent?, cardEntity: CardEntity) {
            gameEvent?.run { action?.accept(this, cardEntity) }
        }
    }

    val kian = ImprovisedCard("kian", "Kian", 2, 7, action = { event, cardEntity ->
        run {
            if (event is RoundEndEvent && cardEntity.location == CardEntity.Location.BOARD)
                cardEntity.player.hand.forEach { it.cost-- }
        }
    })
    val verissimo = ImprovisedCard(
        "sr-verissimo",
        "Sr. Veríssimo",
        2,
        7,
        action = { event, cardEntity ->
            run {
                if (event is CardRevealEvent) {
                    if (cardEntity != event.cardEntity)
                        return@run
                    cardEntity.player.board.filterNot { buffedCardEntity -> buffedCardEntity == cardEntity }
                        .forEach { buffedCardEntity ->
                            match.eventHandler.callEvent(
                                ChangeCardEntityEvent(
                                    buffedCardEntity,
                                    match,
                                    powerDelta = 3
                                )
                            )
                        }
                } else if (event is RemoveCardFromBoardEvent) {
                    if (event.cardEntity.player != cardEntity.player)
                        return@run
                    else if ((cardEntity.location != CardEntity.Location.DECK) && (cardEntity.location != CardEntity.Location.HAND))
                        return@run
                    val addToBoardEvent = AddCardToBoardEvent(cardEntity, match)
                    addToBoardEvent.cause = cardEntity
                    match.eventHandler.callEvent(addToBoardEvent)
                    event.isCancelled = true
                }
            }
        },
        description = "Quando sou invocado, dou +2 de força para todos os aliados. Se um aliado seria destruído e eu estiver no deck ou na mão, me invoco impedindo."
    )
    val kaiser = ImprovisedCard("kaiser", "Kaiser", 1, 1)
    val joui = ImprovisedCard("joui", "Joui Jouki", 3, 5, action = { event, cardEntity ->
        run {
            if (event !is RoundStartEvent)
                return@run
            if (cardEntity.location != CardEntity.Location.BOARD)
                return@run
            val buffCardEvent =
                ChangeCardEntityEvent(cardEntity, match, (cardEntity.player.board.size - 1).coerceAtMost(3), 0)
            buffCardEvent.cause = cardEntity
            match.eventHandler.callEvent(buffCardEvent)
        }
    }, description = "Ao fim de cada rodada, para cada aliado (até o máximo de 3), eu ganho +1 de força")
    val arthur = ImprovisedCard("arthur", "Arthur Cervero", 2, 5, action = { event, cardEntity ->
        run {
            if (event !is AddCardToBoardEvent)
                return@run
            if (event.cardEntity != cardEntity)
                return@run
            val player = cardEntity.player
            if (player.deck.isEmpty())
                return@run
            val addCardFromDeckEvent = AddCardToBoardEvent(player.deck[0], match, player)
            addCardFromDeckEvent.cause = cardEntity
            match.eventHandler.callEvent(addCardFromDeckEvent)
        }
    }, description = "Ao início de cada rodada, eu jogo a carta no topo de seu deck")
    val dante = ImprovisedCard("dante", "Dante", 1, 3, action = { event, cardEntity ->
        run {
            if (event !is RoundEndEvent)
                return@run
            if (cardEntity.location != CardEntity.Location.BOARD)
                return@run

            match.getOpponent(cardEntity.player).board.maxByOrNull { it.power }?.let { it.power-- }
        }
    })
    val thiago = ImprovisedCard("thiago", "Thiago Fritz", 2, 1)
    val elizabeth = ImprovisedCard("elizabeth", "Elizabeth", 3, 2, action = { event, cardEntity ->
        run {
            if ((event is RoundStartEvent) && cardEntity.hasEffect && cardEntity.location == CardEntity.Location.BOARD)
                cardEntity.run {
                    match.eventHandler.callEvent(PlayerDrawEvent(match, player).apply {
                        cause = cardEntity
                    }); hasEffect = false
                }
        }
    }, description = "Ao me revelar, compra uma carta no início do próximo turno")
    val gal = ImprovisedCard("gal", "Gal", 1, 4, action = { event, cardEntity ->
        run {
            if (event !is RoundStartEvent)
                return@run
            if (cardEntity.location != CardEntity.Location.BOARD)
                return@run
            val player = cardEntity.player
            if (player.hand.isEmpty())
                return@run
            player.hand.filter { cardInHand -> cardInHand.cost == (player.hand.maxByOrNull { it.cost }!!.cost) }
                .forEach {
                    it.cost--
                }
        }
    }, description = "Ao início de cada turno, reduz o custo da(s) carta(s) mais cara(s) em sua mão em 1")
    val godOfDeath = ImprovisedCard("deus-da-morte", "Deus da Morte", 2, 7, action = { event, cardEntity ->
        run {
            if (event !is CardRevealEvent)
                return@run
            if (event.cause !is AddCardToBoardEvent)
                return@run
            if ((event.cause as AddCardToBoardEvent).cause !is Player)
                return@run
            if (event.cardEntity.player != cardEntity.player)
                return@run
            if (cardEntity.location != CardEntity.Location.BOARD)
                return@run
            if (cardEntity == event.cardEntity)
                return@run
            val removeFromBoardEvent = RemoveCardFromBoardEvent(event.cardEntity, match)
            removeFromBoardEvent.cause = cardEntity
            match.eventHandler.callEvent(removeFromBoardEvent)
            val buffGodOfDeathEvent = ChangeCardEntityEvent(cardEntity, match, event.cardEntity.power, 0)
            buffGodOfDeathEvent.cause = cardEntity
            match.eventHandler.callEvent(buffGodOfDeathEvent)
        }
    }, description = "Quando você jogar uma carta, eu a destruo e adiciono a sua força a mim")
    val theHost = ImprovisedCard("anfitriao", "O Anfitrião", 2, 7, action = { event, cardEntity ->
        run {
            if (event !is AddCardToBoardEvent)
                return@run
            if ((cardEntity.location != CardEntity.Location.BOARD) or (event.cardEntity.card == cardEntity.card))
                return@run
            if (event.cause !is Player)
                return@run
            if (cardEntity.player != event.cardEntity.player)
                return@run
            if (event.cardEntity.cost > 3)
                return@run
            val addCopyCardEvent = AddCardToBoardEvent(event.cardEntity.clone(), match)
            addCopyCardEvent.cause = cardEntity
            event.match.eventHandler.callEvent(addCopyCardEvent)
        }
    }, description = "Quando você jogar uma carta que custe 3 ou menos, eu jogo uma cópia dela")
    val agatha = ImprovisedCard("agatha", "Agatha", 1, 3)
    val henri = ImprovisedCard("henri", "Henri", 7, 2, action = { event, cardEntity ->
        run {
            if (event !is RoundEndEvent)
                return@run
            if (cardEntity.location != CardEntity.Location.BOARD)
                return@run
            cardEntity.player.board.filter { it != cardEntity }.forEach {
                val debuffAllyEvent = ChangeCardEntityEvent(it, match, -1, 0)
                debuffAllyEvent.cause = cardEntity
                match.eventHandler.callEvent(debuffAllyEvent)
            }
        }
    })
    val cristopher = ImprovisedCard("cristopher", "Cristopher Cohen", 6, 8, action = { event, cardEntity ->
        run {
            if (event !is DamagePlayerEvent)
                return@run
            if (cardEntity.location != CardEntity.Location.BOARD)
                return@run
            if (cardEntity.player != event.player)
                return@run
            event.damage -= cardEntity.power
        }
    }, description = "Reduz o dano que você toma em valor equivalente a minha força")
    val mia = ImprovisedCard("mia", "Mia", 2, 2, action = { event, cardEntity ->
        run {
            if (event !is AddCardToBoardEvent)
                return@run
            if (event.cardEntity != cardEntity)
                return@run
            val player = cardEntity.player
            if (player.deck.isEmpty())
                return@run
            val drawCardFromDeckEvent = PlayerDrawEvent(match, player)
            drawCardFromDeckEvent.cause = cardEntity
            match.eventHandler.callEvent(drawCardFromDeckEvent)
        }
    })
    val enpap = ImprovisedCard("enpap", "Enpap X", 10, 4, action = { event, cardEntity ->
        run {
            if (event !is RoundEndEvent)
                return@run
            if (cardEntity.location != CardEntity.Location.BOARD)
                return@run
            if (cardEntity.player.board.size <= 1)
                return@run
            cardEntity.player.board.filterNot { killedCardEntity -> killedCardEntity == cardEntity }
                .minByOrNull { it.power }!!.apply {
                    val destroyEvent = RemoveCardFromBoardEvent(this, match, cardEntity.player)
                    destroyEvent.cause = cardEntity
                    match.eventHandler.callEvent(destroyEvent)
                }
        }
    })

    val cards = listOf(kaiser, thiago, agatha, elizabeth, mia, enpap, verissimo, cristopher, henri)
    Match.registeredCards = cards
}

fun initDecks(match: Match) {
    match.stateChangeSemaphore.acquire()
    match.strongestPlayer.deck.addAll(Match.registeredCards!!.map { CardEntity(it, match.strongestPlayer) })
    match.weakestPlayer.deck.addAll(Match.registeredCards!!.map { CardEntity(it, match.weakestPlayer) })
    match.stateChangeSemaphore.release()
}

fun main() {
    val player1: Player = GraphicalPlayer()
    val player2: Player = MockPlayer()

    val match = Match(player1, player2)

    registerCards(match)
    initDecks(match)

    match.mainLoop()
}
