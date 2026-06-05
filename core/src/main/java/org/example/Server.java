package org.example;

import java.io.*;
import java.net.*;
import java.util.*;

public class Server {
    private ServerSocket serverSocket;
    private final List<Socket> players = new ArrayList<>();

    public void start(int port) {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Сервер запущен, жду игроков...");

            // Ждем ровно двух игроков
            while (players.size() < 2) {
                Socket client = serverSocket.accept();
                players.add(client);
                System.out.println("Игрок " + players.size() + " подключился!");
            }

            System.out.println("Оба игрока на месте. Запускаю мост обмена данными...");

            // Создаем потоки обмена
            Thread t1 = new Thread(() -> forwardData(players.get(0), players.get(1)));
            Thread t2 = new Thread(() -> forwardData(players.get(1), players.get(0)));

            // Запускаем их
            t1.start();
            t2.start();

            // ИСПРАВЛЕНО: Заставляем сервер ЖДАТЬ эти потоки, чтобы он не закрывался сам!
            t1.join();
            t2.join();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void forwardData(Socket from, Socket to) {
        try (DataInputStream in = new DataInputStream(from.getInputStream());
             DataOutputStream out = new DataOutputStream(to.getOutputStream())) {

            while (!from.isClosed() && !to.isClosed()) {
                float x = in.readFloat();
                float y = in.readFloat();

                if (!to.isClosed()) {
                    out.writeFloat(x);
                    out.writeFloat(y);
                    out.flush();
                }
            }
        } catch (IOException e) {
            System.out.println("Связь прервана: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        new Server().start(12345);
    }
}
