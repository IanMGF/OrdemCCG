package com.ordemrealitas.ccg.stages

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.scenes.scene2d.Actor
import com.ordemrealitas.ccg.Card
import com.ordemrealitas.ccg.graphical.GraphicalPlayer
import com.ordemrealitas.ccg.graphical.SelfHostedClientPlayerData
import com.ordemrealitas.ccg.match.*
import com.ordemrealitas.ccg.mock.MockPlayer

import com.ordemrealitas.ccg.stages.MatchStage.Companion.cardOverlay
import com.ordemrealitas.ccg.stages.MatchStage.Companion.cardTextures
import com.ordemrealitas.ccg.stages.MatchStage.Companion.font
import java.util.concurrent.Executors
import java.util.concurrent.Semaphore

class MatchStage(
    private val player1: Player = GraphicalPlayer(),
    private val player2: Player = MockPlayer()
) : BaseStage() {

    private lateinit var clientData: SelfHostedClientPlayerData
    private lateinit var opponentData: OpponentPlayerData
    private var executor = Executors.newSingleThreadExecutor()
    lateinit var match: Match
    private var semaphore = Semaphore(0)
    companion object {
        var cardOverlay = Texture("card_overlay.png")
        var cardBack = Texture("card_back.png")

        var cardTextures: MutableMap<Card, Texture> = mutableMapOf()
        var font = BitmapFont()
    }

    init {
        val matchRunnable = Runnable {
            match = Match(player1, player2)

            registerCards(match)
            initDecks(match)

            clientData = SelfHostedClientPlayerData(player1)
            opponentData = OpponentPlayerData(player2)

            semaphore.release()
            match.mainLoop()
        }
        executor = Executors.newSingleThreadExecutor()

        executor.execute(matchRunnable)
        if(Match.registeredCards == null)
            semaphore.acquire()

        Match.registeredCards!!.forEach {
            cardTextures[it] = Texture("${it.id}.png")
        }

        addActor(Background())
        addActor(PlayerBoard(clientData))
        addActor(OpponentBoard(opponentData))
    }

    override fun draw() {
        val delta = Gdx.graphics.deltaTime

        match.stateChangeSemaphore.acquire()
        super.draw()
        batch.begin()
        font.draw(batch, "FPS: ${(1 / delta).toInt()}", 10f, 10f)
        batch.end()
        match.stateChangeSemaphore.release()
    }
}

class PlayerBoard(private val clientData: IncompletePlayerData): Actor() {
    override fun draw(batch: Batch, parentAlpha: Float) {
        val yOffset = -60f

        clientData.hand?.forEach {
            val xOffset = 700f + (it!!.player.hand.indexOf(it) * 150f) - ((it.player.hand.size-1) * 87.5f)

            batch.draw(cardTextures[it.card], xOffset-75f, yOffset, 150f, 210f)
            batch.draw(cardOverlay, (xOffset-75f) - 11.25f, yOffset)
            font.draw(batch, it.cost.toString(), ((xOffset-75f) - 7.25f) + 12.5f, yOffset+206)
            font.draw(batch, it.power.toString(), ((xOffset-75f) - 7.25f) + 140.875f, yOffset+206)
        }

        font.draw(batch, clientData.health.toString(), 1210f, 130f)
    }
}

class OpponentBoard(private val clientData: IncompletePlayerData): Actor() {
    override fun draw(batch: Batch, parentAlpha: Float) {
        clientData.hand?.forEach {
            val xOffset = 700f + (it!!.player.hand.indexOf(it) * 150f) - ((it.player.hand.size-1) * 87.5f)
            val yOffset = 680f
            batch.draw(MatchStage.cardBack, xOffset-75f, yOffset, 75f, 105f, 150f, 210f, 1f, 1f, 180f, 0, 0, 400, 560, false, false)
        }

        clientData.board.forEach {
            val xOffset = 683f + (it.player.board.indexOf(it) * 170f) - ((it.player.board.size-1) * 87.5f)
            val yOffset = 400f
            batch.draw(cardTextures[it.card], xOffset-75f, yOffset, 75f, 105f, 150f, 210f, 1f, 1f, 0f, 0, 0, 400, 560, false, false)

            batch.draw(cardOverlay, (xOffset-75f) - 11.25f, yOffset)
            font.draw(batch, it.cost.toString(), ((xOffset-75f) - 7.25f) + 12.5f, yOffset+206)
            font.draw(batch, it.power.toString(), ((xOffset-75f) - 7.25f) + 140.875f, yOffset+206)
        }

        font.draw(batch, clientData.health.toString(), 1210f, 530f)
    }
}

class Background(val board: Texture = Texture("board.png")): Actor() {
    override fun draw(batch: Batch, parentAlpha: Float) {
        batch.draw(board, 0f, 0f, 1366f, 768f)
    }
}