import com.typinggame.TypingSession;

public class History {
    public int sessionId;
    public int sessionWpm;

    public History(TypingSession session) {
        this.sessionId = session.id;
        this.sessionWpm = session.wpm;
    }
}
