package com.geekbrains.client;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

public class LocalHistoryHandler {
    private static LocalHistoryHandler localHistoryHandler = null;

    private Path path;

    private LocalHistoryHandler() {}

    public static LocalHistoryHandler getLocalHistoryHandler() {
        if (localHistoryHandler == null) {
            localHistoryHandler = new LocalHistoryHandler();
        }

        return localHistoryHandler;
    }

    public void setLocalPath(String login) {
        path = Paths.get("history/" + login+".txt");

        try {
            if (!Files.exists(Paths.get("history"))) {
                Files.createDirectory(Paths.get("history"));
            }
            if (!Files.exists(path)) {
                Files.createFile(path);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeLine(String line) {
        try {
            Files.write(path, (line + "\n").getBytes(StandardCharsets.UTF_8), StandardOpenOption.APPEND);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<String> getHistory() {
        List<String> result = new ArrayList<>();

        try {
            result = Files.readAllLines(path);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }
}