TYPING GAME

A Java Typing game developed as a part of the CBL project. The program allows the user to generate or load a target text while tracking typing speed, accuracy, and typing errors.

1. Building The Project:
 - The program uses Maven as a build system.

 REQUIREMENTS:
    JDK 17
    Apache Maven 3.10.0
    Any IDE that can run Java Projects

 - Java and Maven need to be recognized in the PATH

 - To Build the program run "mvn clean package" through the mvn dropdown on VS Code or terminal (If you have the extension). This compiles the source code and runs the JUnit tests.

 - To Run the tests, use "mvn test" through the mvn dropdown on VS code or terminal (If you have the extension).

2. Running The Program:
 
    The Main Class is com.typinggame.Main

    Visual Studio Code:
        1. Open the project folder on VS Code.
        2. Open src/main/java/com/typinggame/Main.java.
        3. Click Run above the main method.
        4. The typing game window should open.

3. Using The Program:
    
    Load Text: 
        Press the load text button to select a file from your computer, make sure the file is a text file. If the file is empty an error message will pop up.
    Generate Text:
        Simply press the Generate button for a random session.
    Reset:
        Press the reset button to end the current typing session.
    History:
        Press the history button to check previous tests.

4. Required Setup and Dependencies:

Java:
    The project requires JDK 17.
    The Java version is configured in pom.xml.
    Apache Maven is required to download the project dependencies and build and test the project.
    Maven bin directory should be added to the system PATH.

Dependencies:
    The project uses:
        Gson - used for JSON serialization and file storage.
        JUnit Jupiter - used for automatic unit testing.




