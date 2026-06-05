package org.example;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector3;

public class InputManifest {
    void escape() {
        if ( Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) Gdx.app.exit();
    }
}
