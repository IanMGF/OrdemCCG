package com.ordemrealitas.ccg

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.ScreenUtils
import com.ordemrealitas.ccg.graphical.KeyboardInput
import com.ordemrealitas.ccg.stages.MatchStage

class OrdemCardGame : ApplicationAdapter() {
    private lateinit var stage: Stage

    override fun create() {
        Gdx.graphics.setFullscreenMode(Gdx.graphics.displayMode)
        Gdx.input.inputProcessor = KeyboardInput()

        stage = MatchStage()
    }

    override fun resize(width: Int, height: Int) {
        stage.viewport.update(width, height, true)
    }

    override fun render() {
        ScreenUtils.clear(0f, 0f, 0f, 1f)

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        stage.draw()
    }

    override fun dispose() {
        stage.dispose()
    }
}