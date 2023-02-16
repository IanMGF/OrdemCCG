package com.ordemrealitas.ccg

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.utils.ScreenUtils
import com.ordemrealitas.ccg.graphical.GraphicalPlayer
import com.ordemrealitas.ccg.graphical.KeyboardInput
import com.ordemrealitas.ccg.graphical.SelfHostedClientPlayerData
import com.ordemrealitas.ccg.match.*
import com.ordemrealitas.ccg.mock.MockPlayer
import java.util.concurrent.Executors
import java.util.concurrent.Semaphore

class OrdemCardGame : ApplicationAdapter() {
    lateinit var batch: SpriteBatch
    lateinit var shapeRenderer: ShapeRenderer
    var board: Texture? = null
    var cardOverlay: Texture? = null
    var cardTextures: MutableMap<Card, Texture> = mutableMapOf()
    var semaphore = Semaphore(0)
    var clientData: SelfHostedClientPlayerData? = null
    var cardBack: Texture? = null
    var opponentData: OpponentPlayerData? = null
    var font: BitmapFont? = null

    override fun create() {
        batch = SpriteBatch()
        shapeRenderer = ShapeRenderer()
        cardOverlay = Texture("card_overlay.png")
        cardBack = Texture("card_back.png")
        board = Texture("board.png")
        font = BitmapFont()

        Gdx.graphics.setFullscreenMode(Gdx.graphics.displayMode)
        Gdx.input.inputProcessor = KeyboardInput()

        var match: Match
        val matchRunnable = Runnable {
            val player1: Player = GraphicalPlayer()
            val player2: Player = MockPlayer()

            match = Match(player1, player2)

            registerCards(match)
            initDecks(match)

            clientData = SelfHostedClientPlayerData(player1)
            opponentData = OpponentPlayerData(player2)
            semaphore.release()
            match.mainLoop()
        }
        val executor = Executors.newSingleThreadExecutor()

        executor.execute(matchRunnable)
        if(Match.registeredCards == null)
            semaphore.acquire()

        Match.registeredCards!!.forEach {
            cardTextures[it] = Texture("${it.id}.png")
        }
    }

    override fun render() {
        ScreenUtils.clear(0f, 0f, 0f, 1f)

        batch.begin()
        batch.draw(board, 0f, 0f, 1366f, 768f)

        clientData?.hand?.forEach {
            val xOffset = 683f + (it.player.hand.indexOf(it) * 175f) - ((it.player.hand.size-1) * 87.5f)
            val yOffset = -60f

            batch.draw(cardTextures[it.card], xOffset-75f, yOffset, 150f, 210f)
            batch.draw(cardOverlay, (xOffset-75f) - 11.25f, yOffset)
            font!!.draw(batch, it.cost.toString(), ((xOffset-75f) - 7.25f) + 12.5f, yOffset+206)
            font!!.draw(batch, it.power.toString(), ((xOffset-75f) - 7.25f) + 140.875f, yOffset+206)
        }

        opponentData?.hand?.forEach {
            val xOffset = 683f + (it.player.hand.indexOf(it) * 175f) - ((it.player.hand.size-1) * 87.5f)
            val yOffset = 680f
            batch.draw(cardBack, xOffset-75f, yOffset, 75f, 105f, 150f, 210f, 1f, 1f, 180f, 0, 0, 400, 560, false, false)
        }

        batch.end()
    }

    override fun dispose() {
        shapeRenderer.dispose()
        batch.dispose()
        board!!.dispose()
    }
}