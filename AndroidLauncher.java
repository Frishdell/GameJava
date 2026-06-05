package org.example.android;

import android.os.Bundle;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import org.example.Client; // Убедись, что импорт верный!

public class AndroidLauncher extends AndroidApplication {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
        config.useImmersiveMode = true;
        config.useGL30 = false;           // ОБЯЗАТЕЛЬНО для телефонов
        config.useAccelerometer = false;
        config.useCompass = false;

        // ЗАПУСКАЕМ ТВОЙ КЛАСС CLIENT
        initialize(new Client(true), config);
    }
}
