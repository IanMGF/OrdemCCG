package com.ordemrealitas.ccg.graphical

import com.badlogic.gdx.Input.Buttons
import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.InputProcessor

class MouseInput: InputAdapter() {
    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        when(button){
            Buttons.LEFT -> {

            }
        }
        return true
    }
}