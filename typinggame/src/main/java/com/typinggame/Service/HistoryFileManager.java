package com.typinggame.service;

//* A class responsible for logging the history in a text file */

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.typinggame.model.History;

public class HistoryFileManager {
    private final String fileName;
    private final Gson gson;

    public HistoryFileManager(String fileName) {
        this.fileName = fileName;
        this.gson = new GsonBuilder().setPrettyPrinting().create();
    }

    public void saveHistory(History newHistory) {
        List<History> historyList = loadAllHistories();

        int nextId = historyList.size() + 1;
        newHistory.id = nextId;
        newHistory.sessionId = nextId;

        historyList.add(newHistory);

        try (FileWriter writer = new FileWriter(fileName)) {
            gson.toJson(historyList, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<History> loadAllHistories() {
        try (FileReader reader = new FileReader(fileName)) {
            Type listType = new TypeToken<ArrayList<History>>() {}.getType();
            List<History> histories = gson.fromJson(reader, listType);
            return histories != null ? histories : new ArrayList<>();
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }
}
