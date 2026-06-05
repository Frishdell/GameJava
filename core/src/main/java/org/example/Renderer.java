package org.example;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Renderer {

    void render(SpriteBatch batch, Assets assets, CarPysyc.E34NormalEditionPysyc carsE34,
                float enemyTrackX, float enemyWinX, Client client) {

        int screenWidth = 1270;

        // Защита от NullPointerException на старте
        if (assets == null || carsE34 == null || assets.drag == null || assets.e34normalEdition == null) {
            return;
        }

        float x = carsE34.getTrackX() % screenWidth;

        // Рисуем задний фон (трассу)
        batch.draw(assets.drag, -x, 0, screenWidth, 720);
        batch.draw(assets.drag, screenWidth - x, 0, screenWidth, 720);

        // 1. Своя машина
        batch.draw(assets.e34normalEdition, 200, 120, 400, 350);

        // 2. Машина оппонента из сети (с защитой от диких координат на ПК)
        float enemyVisualX = 200 + (enemyTrackX - carsE34.getTrackX());
        // Ограничиваем, чтобы машина не улетала в бесконечность и не вешала буфер видеокарты Linux
        if (enemyVisualX > -1000 && enemyVisualX < 5000) {
            batch.draw(assets.e34normalEdition, enemyVisualX, 120, 400, 350);
        }

        // Рисуем мини-карту (финиш)
        if (assets.win != null) {
            batch.draw(assets.win, 300, 560, 600, 64);
        }

        // Точки машин на мини-карте
        batch.draw(assets.e34normalEdition, carsE34.getWinX(), 565, 50, 50);
        batch.draw(assets.e34normalEdition, enemyWinX, 565, 50, 50);

        // Анимация победы
        carsE34.winAnim(batch, assets);

        // ИСПРАВЛЕНО: Жесткая проверка на null для Android-кнопок при запуске на ПК!
        if (Gdx.app.getType() == Application.ApplicationType.Android) {
            if (assets.AndroidButton1 != null) batch.draw(assets.AndroidButton1, 40, 30, 256, 256);
            if (assets.AndroidButton2 != null) batch.draw(assets.AndroidButton2, 160, 30, 256, 256);
            if (assets.AndroidButton3 != null) batch.draw(assets.AndroidButton3, 290, 30, 256, 256);
            if (assets.AndroidButton4 != null) batch.draw(assets.AndroidButton4, 420, 30, 256, 256);
            if (assets.AndroidButton5 != null) batch.draw(assets.AndroidButton5, 543, 30, 256, 256);
            if (assets.AndroidButtonW != null) batch.draw(assets.AndroidButtonW, 1000, 30, 256, 256);
        }
    }
}
