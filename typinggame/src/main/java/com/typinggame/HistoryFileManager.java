package com.typinggame;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

public class HistoryFileManager {
    private static final String FILE_NAME = "typinggame/history.json";
    private final Gson gson;

    public HistoryFileManager() {
        this.gson = new GsonBuilder().setPrettyPrinting().create();
    }

    public void saveHistory(History newHistory) {
        List<History> historyList = loadAllHistories();

        int nextId = historyList.size() + 1;
        newHistory.id = nextId;
        newHistory.sessionId = nextId;

        historyList.add(newHistory);

        try (FileWriter writer = new FileWriter(FILE_NAME)) {
            gson.toJson(historyList, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<History> loadAllHistories() {
        try (FileReader reader = new FileReader(FILE_NAME)) {
            Type listType = new TypeToken<ArrayList<History>>() {}.getType();
            List<History> histories = gson.fromJson(reader, listType);
            return histories != null ? histories : new ArrayList<>();
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }
}
