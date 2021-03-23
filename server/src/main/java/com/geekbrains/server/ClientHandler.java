package com.geekbrains.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private String nickname;
    private Server server;
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private final DataBaseSingleton dataBaseSingleton;

    private Boolean isAuthorised = false;
    private Boolean isStopped = false;

    public String getNickname() {
        return nickname;
    }
    public ClientHandler(Server server, Socket socket, DataBaseSingleton dataBaseSingleton) {
        this.dataBaseSingleton = dataBaseSingleton;
        init(server, socket);
    }

    private void init(Server server, Socket socket) {
        try {
            this.server = server;
            this.socket = socket;
            this.in = new DataInputStream(socket.getInputStream());
            this.out = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
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
            e.printStackTrace();
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
            server.broadcastMsg(nickname + ": " + msg);
        }
    }

    public void sendMsg(String msg) {
        try {
            out.writeUTF(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void disconnect() {
        server.unsubscribe(this);
        try {
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
