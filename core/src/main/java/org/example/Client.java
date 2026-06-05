package org.example;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.net.InetAddress;
import java.net.ServerSocket;

public class Client extends ApplicationAdapter {
    private boolean isAndroid;
    public Client(boolean isAndroid) {
        this.isAndroid = isAndroid;
    }

    CarPysyc carPysyc;
    Assets assets;
    CarPysyc.E34NormalEditionPysyc e34NormalEditionPysyc;
    InputManifest inputManifest;

    // ТОЧНЫЕ КОРДЫ КНОПОК (внутри виртуального экрана 1270x720)
    Rectangle btn1 = new Rectangle(40, 30, 256, 256);
    Rectangle btn2 = new Rectangle(160, 30, 256, 256);
    Rectangle btn3 = new Rectangle(290, 30, 256, 256);
    Rectangle btn4 = new Rectangle(420, 30, 256, 256);
    Rectangle btn5 = new Rectangle(543, 30, 256, 256);
    Rectangle btnW = new Rectangle(1000, 30, 256, 256);

    Texture drag;
    Texture e34NormanlEdition;
    Texture win;
    Texture winText;
    public Texture AndroidButton1;
    public Texture AndroidButton2;
    public Texture AndroidButton3;
    public Texture AndroidButton4;
    public Texture AndroidButton5;
    public Texture AndroidButtonW;

    Renderer renderer;
    SpriteBatch batch;

    private java.net.Socket socket;
    private java.io.DataOutputStream out;
    private java.io.DataInputStream in;

    private float enemyTrackX;
    private float enemyWinX;

    private volatile boolean isRunning = true;
    private Thread senderThread;

    // СЕНСОРНЫЙ ДВИЖОК: Камера, полноэкранный адаптер и вектор кликов
    private OrthographicCamera camera;
    private Viewport viewport;
    private Vector3 touchVector;

    @Override
    public void create() {
        assets = new Assets();
        carPysyc = new CarPysyc();
        renderer = new Renderer();
        inputManifest = new InputManifest();
        e34NormalEditionPysyc = new CarPysyc.E34NormalEditionPysyc();
        batch = new SpriteBatch();

        // НАСТРОЙКА ПОЛНОЭКРАННОЙ КАМЕРЫ ПОД 1270x720
        camera = new OrthographicCamera();
        viewport = new StretchViewport(1270, 720, camera);
        viewport.apply();
        camera.position.set(1270 / 2f, 720 / 2f, 0);
        touchVector = new Vector3();

        // Загрузка текстур
        drag = new Texture("drag.png");
        assets.drag = this.drag;
        e34NormanlEdition = new Texture("e34.png");
        assets.e34normalEdition = this.e34NormanlEdition;
        win = new Texture("win.png");
        assets.win = this.win;
        winText = new Texture("winText.png");
        assets.winText = this.winText;
        AndroidButton1 = new Texture("button1.png");
        assets.AndroidButton1 = this.AndroidButton1;
        AndroidButton2 = new Texture("button2.png");
        assets.AndroidButton2 = this.AndroidButton2;
        AndroidButton3 = new Texture("button3.png");
        assets.AndroidButton3 = this.AndroidButton3;
        AndroidButton4 = new Texture("button4.png");
        assets.AndroidButton4 = this.AndroidButton4;
        AndroidButton5 = new Texture("button5.png");
        assets.AndroidButton5 = this.AndroidButton5;
        AndroidButtonW = new Texture("buttonW.png");
        assets.AndroidButtonW = this.AndroidButtonW;

        // Поток сети
        new Thread(() -> {
            try {
                // ИСПРАВЛЕНО: Если это ПК, даем окну 500 миллисекунд, чтобы полностью открыться
                if (!isAndroid) {
                    Thread.sleep(500);
                }

                String targetIp;
                if (isAndroid) {
                    targetIp = "192.168.0.106"; // Ваш Wi-Fi IP для телефона
                } else {
                    targetIp = "127.0.0.1";     // Чистые цифры для ПК
                }

                System.out.println("Подключаюсь к серверу по адресу: " + targetIp);

                socket = new java.net.Socket();
                socket.connect(new java.net.InetSocketAddress(targetIp, 12345), 2000);
                socket.setSoTimeout(5000);

                out = new java.io.DataOutputStream(socket.getOutputStream());
                in = new java.io.DataInputStream(socket.getInputStream());

                // Запускаем фоновый поток отправки
                startSenderThread();

                // Цикл чтения ответов сервера
                while (isRunning && !Thread.currentThread().isInterrupted() && socket != null && !socket.isClosed()) {
                    try {
                        float track = in.readFloat();
                        float winVal = in.readFloat();
                        Gdx.app.postRunnable(() -> {
                            enemyTrackX = track;
                            enemyWinX = winVal;
                        });
                    } catch (java.io.IOException e) {
                        break;
                    }
                }
            } catch (Exception e) {
                Gdx.app.log("Network", "Ошибка подключения: " + e.getMessage());
            }
        }).start();
    }

    private void startSenderThread() {
        senderThread = new Thread(() -> {
            try {
                while (isRunning && out != null && socket != null && !socket.isClosed()) {
                    float currentTrackX = e34NormalEditionPysyc.getTrackX();
                    float currentWinX = e34NormalEditionPysyc.getWinX();
                    try {
                        out.writeFloat(currentTrackX);
                        out.writeFloat(currentWinX);
                        out.flush();
                    } catch (java.io.IOException e) {
                        break;
                    }
                    Thread.sleep(16);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                closeConnections();
            }
        });
        senderThread.start();
    }

    // ИСПРАВЛЕННЫЙ СЕНСОР: Переводит пиксели любого телефона в ваши корды кнопок
    private void checkInput() {
        if (Gdx.input.justTouched()) {
            touchVector.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            viewport.unproject(touchVector); // Умная магия LibGDX

            float touchX = touchVector.x;
            float touchY = touchVector.y;

            if (btn1.contains(touchX, touchY)) {
                e34NormalEditionPysyc.setGear(1, 20);
            } else if (btn2.contains(touchX, touchY)) {
                e34NormalEditionPysyc.setGear(2, 40);
            } else if (btn3.contains(touchX, touchY)) {
                e34NormalEditionPysyc.setGear(3, 70);
            } else if (btn4.contains(touchX, touchY)) {
                e34NormalEditionPysyc.setGear(4, 110);
            } else if (btn5.contains(touchX, touchY)) {
                e34NormalEditionPysyc.setGear(5, 160);
            } else if (btnW.contains(touchX, touchY)) {
                e34NormalEditionPysyc.pysyc();
            }
        }
    }

    @Override
    public void render() {
        if (isAndroid) checkInput();
        e34NormalEditionPysyc.gearBox();
        e34NormalEditionPysyc.pysyc();
        inputManifest.escape();

        // Привязываем матрицу камеры к отрисовщику
        camera.update();
        batch.setProjectionMatrix(camera.combined);

        batch.begin();
        renderer.render(batch, assets, e34NormalEditionPysyc, enemyTrackX, enemyWinX, this);
        batch.end();
    }

    // Автоматическое растягивание графики без черных полос
    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
        camera.position.set(1270 / 2f, 720 / 2f, 0);
    }

    @Override
    public void dispose() {
        isRunning = false;
        if (batch != null) batch.dispose();
        if (drag != null) drag.dispose();
        if (e34NormanlEdition != null) e34NormanlEdition.dispose();
        if (win != null) win.dispose();
        if (winText != null) winText.dispose();
        if (AndroidButton1 != null) AndroidButton1.dispose();
        if (AndroidButton2 != null) AndroidButton2.dispose();
        if (AndroidButton3 != null) AndroidButton3.dispose();
        if (AndroidButton4 != null) AndroidButton4.dispose();
        if (AndroidButton5 != null) AndroidButton5.dispose();
        if (AndroidButtonW != null) AndroidButtonW.dispose();
        closeConnections();
    }

    private void closeConnections() {
        isRunning = false;
        try { if (out != null) out.close(); } catch (Exception ignored) {}
        try { if (in != null) in.close(); } catch (Exception ignored) {}
        try { if (socket != null) socket.close(); } catch (Exception ignored) {}
        out = null;
        in = null;
        socket = null;
    }
}
