package com.ordemrealitas.ccg.graphical

import com.badlogic.gdx.Input
import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.InputProcessor

class KeyboardInput: InputAdapter() {
    override fun keyDown(keycode: Int): Boolean {
        when(keycode){
            Input.Keys.Q -> {
                synchronized(GraphicalPlayer.semaphore){
                    GraphicalPlayer.semaphore.release()
                }
            }
        }
        return true
    }
}