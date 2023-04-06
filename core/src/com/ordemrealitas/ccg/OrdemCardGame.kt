package com.ordemrealitas.ccg

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.ScreenUtils
import com.ordemrealitas.ccg.graphical.GraphicalPlayer
import com.ordemrealitas.ccg.graphical.KeyboardInput
import com.ordemrealitas.ccg.graphical.SelfHostedClientPlayerData
import com.ordemrealitas.ccg.match.*
import com.ordemrealitas.ccg.mock.MockPlayer
import com.ordemrealitas.ccg.stages.MatchStage
import java.util.concurrent.Executors
import java.util.concurrent.Semaphore

class OrdemCardGame : ApplicationAdapter() {
    //private var scene: Scene2d =

    private lateinit var batch: SpriteBatch
    private lateinit var shapeRenderer: ShapeRenderer
    private lateinit var stage: Stage
    private var semaphore = Semaphore(0)
    private var clientData: SelfHostedClientPlayerData? = null
    private var opponentData: OpponentPlayerData? = null
    var board: Texture? = null


    override fun create() {
        batch = SpriteBatch()
        shapeRenderer = ShapeRenderer()
        board = Texture("board.png")

        Gdx.graphics.setFullscreenMode(Gdx.graphics.displayMode)
        Gdx.input.inputProcessor = KeyboardInput()

        lateinit var match: Match
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

        stage = MatchStage(match, clientData!!, opponentData!!)
    }

    override fun resize(width: Int, height: Int) {
        stage.viewport.update(width, height, true)
    }

    override fun render() {
        ScreenUtils.clear(0f, 0f, 0f, 1f)

        val delta = Gdx.graphics.deltaTime
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        batch.begin()
        batch.draw(board, 0f, 0f, 1366f, 768f)
        batch.end()

        stage.act(delta)
        stage.draw()
    }

    override fun dispose() {
        shapeRenderer.dispose()
        batch.dispose()
        stage.dispose()
        board!!.dispose()
    }
}