package com.geekbrains.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server {
    private static final Logger LOGGER = Logger.getLogger("");

    private Vector<ClientHandler> clients;
    private DataBaseSingleton dataBaseSingleton;
    private ExecutorService executorService;

    public DataBaseSingleton getDataBaseSingleton() {
        return dataBaseSingleton;
    }

    public static Logger getLOGGER() {
        return LOGGER;
    }

    public Server() {
        clients = new Vector<>();
        executorService = Executors.newCachedThreadPool();
        setFileHandler();

        try (DataBaseSingleton dataBaseSingleton = DataBaseSingleton.getDataBaseSingleton();
        ServerSocket serverSocket = new ServerSocket(8189)) {
            LOGGER.log(Level.INFO, "Сервер запущен на порту 8189");
            while (true) {
                Socket socket = serverSocket.accept();
                ClientHandler client = new ClientHandler(this, socket, dataBaseSingleton);
                executorService.execute(client);
                LOGGER.log(Level.INFO, "Подключился новый клиент");
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage());
        }
        executorService.shutdown();
        LOGGER.log(Level.INFO, "Сервер завершил свою работу");
    }

    private void setFileHandler() {
        try {
            Handler fileHandler = new FileHandler("log_%g.txt", 1024, 10, true);
            LOGGER.addHandler(fileHandler);
            LOGGER.setLevel(Level.INFO);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void broadcastMsg(String msg) {
        for (ClientHandler o : clients) {
            o.sendMsg(msg);
        }
    }

    public void privateMsg(ClientHandler sender, String receiverNick, String msg) {
        if (sender.getNickname().equals(receiverNick)) {
            sender.sendMsg("заметка для себя: " + msg);
            return;
        }
        for (ClientHandler o : clients) {
            if (o.getNickname().equals(receiverNick)) {
                o.sendMsg("от " + sender.getNickname() + ": " + msg);
                sender.sendMsg("для " + receiverNick + ": " + msg);
                return;
            }
        }
        sender.sendMsg("Клиент " + receiverNick + " не найден");
    }

    public void subscribe(ClientHandler clientHandler) {
        clients.add(clientHandler);
        broadcastClientsList();
    }

    public void unsubscribe(ClientHandler clientHandler) {
        clients.remove(clientHandler);
        broadcastClientsList();
    }

    public boolean isNickBusy(String nickname) {
        for (ClientHandler o : clients) {
            if (o.getNickname().equals(nickname)) {
                return true;
            }
        }
        return false;
    }

    public void broadcastClientsList() {
        StringBuilder sb = new StringBuilder(15 * clients.size());
        sb.append("/clients ");
        // '/clients '
        for (ClientHandler o : clients) {
            sb.append(o.getNickname()).append(" ");
        }
        // '/clients nick1 nick2 nick3 '
        sb.setLength(sb.length() - 1);
        // '/clients nick1 nick2 nick3'
        String out = sb.toString();
        for (ClientHandler o : clients) {
            o.sendMsg(out);
        }
    }
}
