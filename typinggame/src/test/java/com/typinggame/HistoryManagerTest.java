package com.typinggame;

import java.io.File;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import com.typinggame.logic.TypingSession;
import com.typinggame.model.History;
import com.typinggame.service.HistoryFileManager;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

class HistoryManagerTest {
    private static final String TEST_FILE = "mockHistoryDb.json";

    @AfterEach 
    void TearDown() {
        File file = new File(TEST_FILE);
        if (file.exists()) {
            file.delete();
        }
    }

    @Test 
    void testingHistoryCreationAndFileManagement() {
        TypingSession session = new TypingSession("test text");
        session.handleTypedCharacter('t');
        session.handleTypedCharacter('e');

        History history = new History(session, 60);

        assertNotNull(history, "History object should not be null.");
        assertEquals(100, history.accuracy, "Accuracy should be 100% for correct start");

        HistoryFileManager historyFileManager = new HistoryFileManager(TEST_FILE);

        historyFileManager.saveHistory(history);

        List<History> allHistories = historyFileManager.loadAllHistories();

        assertFalse(allHistories.isEmpty(), "History list should contain saved sessions.");

        History lateHistory = allHistories.get(allHistories.size() - 1);

        assertTrue(lateHistory.id > 0, "Saved history should have a valid generated ID.");
        assertEquals(lateHistory.id, lateHistory.sessionId, "SessionId and Id should match.");
        assertEquals(history.accuracy, lateHistory.accuracy, "Accuracy should be preserved.");
    }
}
