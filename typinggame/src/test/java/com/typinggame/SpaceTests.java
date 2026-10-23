package com.typinggame;



import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SpaceTests {

    @Test 
    public void pressingSpaceCausesMissingCharacters() {
        TypingSession session = new TypingSession("Hello World");

        session.handleTypedCharacter('H');
        session.handleTypedCharacter('e');
        session.handleTypedCharacter('l');
        session.handleTypedCharacter(' ');

        long missingCharacters = session.getErrorTracker()
            .getErrors()
            .stream()
            .filter(error -> 
                error.getType()
                    == ErrorType.MISSING_CHARACTER)
            .count();
        
        assertEquals(2, missingCharacters);

    }

    @Test
    public void missingSpaceCausesError() {
        TypingSession session = new TypingSession("Hello World");

        session.handleTypedCharacter('H');
        session.handleTypedCharacter('e');
        session.handleTypedCharacter('l');
        session.handleTypedCharacter('l');
        session.handleTypedCharacter('o');
        session.handleTypedCharacter('W');

        assertEquals(ErrorType.MISSING_SPACE, session.getErrorTracker().getErrors().get(0).getType());
    }


    @Test 
    public void correctSpaceMovesToNextWord() {
        TypingSession session = new TypingSession("Hello World");

        session.handleTypedCharacter('H');
        session.handleTypedCharacter('e');
        session.handleTypedCharacter('l');
        session.handleTypedCharacter('l');
        session.handleTypedCharacter('o');
        session.handleTypedCharacter(' ');

        assertEquals(6, session.getCurrentPosition());
    }

     @Test 
     public void secondSpaceIsIgnored() {
        TypingSession session = new TypingSession("a a");
        session.handleTypedCharacter('a');
        session.handleTypedCharacter(' ');
        TypingUpdate update = session.handleTypedCharacter(' ');
        
        assertTrue(update.isIgnored());
     }

     @Test
     public void spaceBackspaceSpecialDynamic() {
        TypingSession session = new TypingSession("abc abc");

        session.handleTypedCharacter('a');
        session.handleTypedCharacter(' ');
        assertEquals(4, session.getCurrentPosition());

        session.handleBackspace();
        assertEquals(1, session.getCurrentPosition());
     }

}
