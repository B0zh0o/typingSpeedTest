package com.typinggame;

import java.io.FileWriter;
import java.io.IOException;

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
        List<History> historyList = loadAllHistries();
        historyList.add(newHistory);

        try (FileWriter writer = new FileWriter(FILE_NAME)) {
            gson.toJson(historyList, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<History> loadAllHistories() {
        try (fileReader reader = new fileReader(FILE_NAME)) {
            Type listType = new TypeToken<ArrayList<History>>() {}.getType();
            List<History> histories = gson.fromJson(reader, listType);
            return histories != null ? histories : new ArrayList<>();
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }
}
