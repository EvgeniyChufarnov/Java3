package com.geekbrains.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Iterator;
import java.util.List;

public class Network {
    private static Socket socket;
    private static DataInputStream in;
    private static DataOutputStream out;

    private static Callback callOnMsgReceived;
    private static Callback callOnAuthenticated;
    private static Callback callOnException;
    private static Callback callOnCloseConnection;

    private static LocalHistoryHandler localHistoryHandler;
    private static String lastLogin;

    static {
        Callback empty = args -> { };
        callOnMsgReceived = empty;
        callOnAuthenticated = empty;
        callOnException = empty;
        callOnCloseConnection = empty;
    }

    public static void setCallOnMsgReceived(Callback callOnMsgReceived) {
        Network.callOnMsgReceived = callOnMsgReceived;
    }

    public static void setCallOnAuthenticated(Callback callOnAuthenticated) {
        Network.callOnAuthenticated = callOnAuthenticated;
    }

    public static void setCallOnException(Callback callOnException) {
        Network.callOnException = callOnException;
    }

    public static void setCallOnCloseConnection(Callback callOnCloseConnection) {
        Network.callOnCloseConnection = callOnCloseConnection;
    }

    public static void sendAuth(String login, String password) {
        localHistoryHandler = LocalHistoryHandler.getLocalHistoryHandler();
        try {
            if (socket == null || socket.isClosed()) {
                connect();
            }
            out.writeUTF("/auth " + login + " " + password);
            lastLogin = login;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void connect() {
        try {
            socket = new Socket("localhost", 8189);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
            Thread clientListenerThread = new Thread(() -> {
                try {
                    while (true) {
                        String msg = in.readUTF();
                        if (msg.startsWith("/authok ")) {
                            callOnAuthenticated.callback(msg.split("\\s")[1]);
                            localHistoryHandler.setLocalPath(lastLogin);
                            break;
                        }
                    }
                    getLocalHistory();
                    while (true) {
                        String msg = in.readUTF();
                        if (msg.equals("/end")) {
                            break;
                        }
                        callOnMsgReceived.callback(msg);
                        if (!msg.startsWith("/clients")) {
                            localHistoryHandler.writeLine(msg);
                        }
                    }
                } catch (IOException e) {
                    callOnException.callback("Соединение с сервером разорвано");
                } finally {
                    closeConnection();
                }
            });
            clientListenerThread.setDaemon(true);
            clientListenerThread.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void getLocalHistory() {
        List<String> history = localHistoryHandler.getHistory();
        Iterator<String> iterator = history.iterator();

        int i = 0;

        while (iterator.hasNext() && i++ < 100) {
            callOnMsgReceived.callback(iterator.next());
        }
    }

    public static boolean sendMsg(String msg) {
        try {
            out.writeUTF(msg);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void closeConnection() {
        callOnCloseConnection.callback();
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
