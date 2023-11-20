import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;

public interface service extends Remote {
    // 1. Get the current game board
    String getGameBoard() throws RemoteException;

    // 2. Select a category and point value
    server.Question selectQuestion(int categoryIndex, int questionIndex) throws RemoteException;

    // 3. Submit an answer
    boolean submitAnswer(String answer) throws RemoteException;

    // 4. Get player scores
    Map<String, Integer> getPlayerScores() throws RemoteException;

    // 6. Get the current game board in Java
    String getGameBoardJava(int i) throws RemoteException;

    
    // 7. Get the correct answer for the last question
    
    String getCorrectAnswer() throws RemoteException;
}
