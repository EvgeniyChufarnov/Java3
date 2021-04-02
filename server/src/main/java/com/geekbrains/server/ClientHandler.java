package com.geekbrains.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientHandler implements Runnable {
    private String nickname;
    private Server server;
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private final DataBaseSingleton dataBaseSingleton;
    private final Logger logger;

    private Boolean isAuthorised = false;
    private Boolean isStopped = false;

    public String getNickname() {
        return nickname;
    }
    public ClientHandler(Server server, Socket socket, DataBaseSingleton dataBaseSingleton) {
        this.dataBaseSingleton = dataBaseSingleton;
        this.logger = Server.getLOGGER();
        init(server, socket);
    }

    private void init(Server server, Socket socket) {
        try {
            this.server = server;
            this.socket = socket;
            this.in = new DataInputStream(socket.getInputStream());
            this.out = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            logExceptionInfo(e);
        }
    }

    @Override
    public void run() {
        try {
            while (!isAuthorised) {
                tryToAuthorize();
            }
            while (!isStopped) {
                receiveMessage();
            }
        } catch (IOException e) {
            logExceptionInfo(e);
        } finally {
            ClientHandler.this.disconnect();
        }
    }

    private void tryToAuthorize() throws IOException {
        String msg = in.readUTF();
        // /auth login1 pass1
        if (msg.startsWith("/auth ")) {
            String[] tokens = msg.split("\\s");
            String nick = dataBaseSingleton.getNicknameOrNull(tokens[1], tokens[2]);
            if (nick != null && !server.isNickBusy(nick)) {
                sendMsg("/authok " + nick);
                nickname = nick;
                server.subscribe(this);
                isAuthorised = true;
            }
        }
    }

    private void receiveMessage() throws IOException {
        String msg = in.readUTF();
        if(msg.startsWith("/")) {
            logger.log(Level.FINE, String.format("Клиент %s прислал команду: %s", nickname, msg));
            if (msg.equals("/end")) {
                sendMsg("/end");
                isStopped = true;
            }
            if(msg.startsWith("/w ")) {
                String[] tokens = msg.split("\\s", 3);
                server.privateMsg(this, tokens[1], tokens[2]);
            }
            if(msg.startsWith("/change ")) {
                String[] tokens = msg.split("\\s", 3);
                boolean success = dataBaseSingleton.updateNickname(nickname, tokens[1]);
                if (success) {
                    server.broadcastMsg("user " + nickname + " changed nickname to " + tokens[1]);
                    nickname = tokens[1];
                }
            }
        } else {
            logger.log(Level.FINE, String.format("Клиент %s прислал сообщение: %s", nickname, msg));
            server.broadcastMsg(nickname + ": " + msg);
        }
    }

    public void sendMsg(String msg) {
        try {
            out.writeUTF(msg);
        } catch (IOException e) {
            logExceptionInfo(e);
        }
    }

    public void disconnect() {
        server.unsubscribe(this);
        try {
            in.close();
        } catch (IOException e) {
            logExceptionInfo(e);
        }
        try {
            out.close();
        } catch (IOException e) {
            logExceptionInfo(e);
        }
        try {
            socket.close();
        } catch (IOException e) {
            logExceptionInfo(e);
        }
    }

    private void logExceptionInfo(Exception e) {
        logger.log(Level.SEVERE, String.format("Клиент %s: Ошибка: %s", nickname, e.getMessage()));
    }
}
