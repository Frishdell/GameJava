package org.example;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.HashMap;

public class CarPysyc {
    static class E34NormalEditionPysyc {
        private int currentGearBox = 0;
        private int currentGearBoxSpeed = 0;
        // Давай зададим стартовую позицию, например 10
        private float finalX = 10;
        private float trackX = 0;
        private float winX = 300;

        // Убираем из параметров x и speed, они тут больше не нужны!
        void pysyc() {
            if (Gdx.input.isKeyPressed(Input.Keys.W)) {
                int speed = (currentGearBox > 0) ? currentGearBoxSpeed : 0;
                if ( currentGearBox == 1) winX += 0.01f;
                if ( currentGearBox == 2) winX += 0.03f;
                if ( currentGearBox == 3) winX += 0.08f;
                if ( currentGearBox == 4) winX += 0.1f;
                if ( currentGearBox == 5) winX += 0.4f;
                // Увеличиваем смещение трассы (умножаем на дельту для плавности)
                trackX += speed * Gdx.graphics.getDeltaTime() * 10;
            }
        }

        void gearBox() {
            if (Gdx.input.isKeyPressed(Input.Keys.NUM_1)) {
                currentGearBox = 1;
                currentGearBoxSpeed = 20;
            }
            if (Gdx.input.isKeyPressed(Input.Keys.NUM_2)) {
                currentGearBox = 2;
                currentGearBoxSpeed = 40; // Сделаем шаг побольше для драйва!
            }
            if (Gdx.input.isKeyPressed(Input.Keys.NUM_3)) {
                currentGearBox = 3;
                currentGearBoxSpeed = 70;
            }
            if (Gdx.input.isKeyPressed(Input.Keys.NUM_4)) {
                currentGearBox = 4;
                currentGearBoxSpeed = 110;
            }
            if (Gdx.input.isKeyPressed(Input.Keys.NUM_5)) {
                currentGearBox = 5;
                currentGearBoxSpeed = 160;
            }
        }
        void setGear(int gear, int speed) {
            currentGearBox = gear;
            currentGearBoxSpeed = speed;
        }

        void winAnim(SpriteBatch batch, Assets assets) {
            if ( winX >= 860) {
                batch.draw(assets.winText, 600, 360, 400,400);
            }
        }
        public float getWinX() {
            return winX;
        }
        public float getTrackX() {
            return trackX;
        }
    }
}
