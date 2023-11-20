import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class server implements service {

    private Map<String, Integer> playerScores;
    private Map<Integer, Map<Integer, Question>> gameBoard;
    private Question currentQuestion;

    public server() {
        playerScores = new HashMap<>();
        gameBoard = new HashMap<>();
        readMovieQuestions();
    }

    private void readMovieQuestions() {
        try (BufferedReader reader = new BufferedReader(new FileReader("questions.txt"))) {
            String line;
            int categoryIndex = 1;

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    String questionText = parts[0].trim();
                    String answer = parts[1].trim();

                    Question movieQuestion = new Question(questionText, answer);
                    gameBoard.computeIfAbsent(categoryIndex, k -> new HashMap<>()).put(gameBoard.size() + 1, movieQuestion);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading movie questions: " + e.getMessage());
            e.printStackTrace();
        }
    }

@Override
public String getGameBoard(int categoryIndex) throws RemoteException {
    StringBuilder gameBoardString = new StringBuilder();

    for (Map.Entry<Integer, Map<Integer, Question>> categoryEntry : gameBoard.entrySet()) {
        int currentCategoryIndex = categoryEntry.getKey();
        Map<Integer, Question> questions = categoryEntry.getValue();

        if (currentCategoryIndex == categoryIndex) {
            gameBoardString.append("Category ").append(currentCategoryIndex).append(":\n");

            for (Map.Entry<Integer, Question> questionEntry : questions.entrySet()) {
                int questionIndex = questionEntry.getKey();
                Question question = questionEntry.getValue();

                gameBoardString.append("  ").append(questionIndex).append(". ").append(question.getQuestionText());
                gameBoardString.append(" (").append(getPointValue(questionIndex)).append(" points)\n");
            }
            gameBoardString.append("\n");
        }
    }

    return gameBoardString.toString();
}


    private int getPointValue(int questionIndex) {
        return 50;
    }

    @Override
    public Map<String, Integer> getPlayerScores() throws RemoteException {
        return playerScores;
    }

    @Override
    public Question selectQuestion(int categoryIndex, int questionIndex) throws RemoteException {
        Map<Integer, Question> category = gameBoard.get(categoryIndex);
        if (category != null) {
            Question selectedQuestion = category.remove(questionIndex);
            if (selectedQuestion != null) {
                currentQuestion = selectedQuestion;
                return selectedQuestion;
            } else {
                throw new RemoteException("Invalid question index");
            }
        } else {
            throw new RemoteException("Invalid category index");
        }
    }

    @Override
    public boolean submitAnswer(String answer) throws RemoteException {
        if (currentQuestion != null && answer.equals(currentQuestion.getAnswer())) {
            String currentPlayer = "Player1";
            int currentScore = playerScores.getOrDefault(currentPlayer, 0);
            playerScores.put(currentPlayer, currentScore + 1);
            return true;
        }
        return false;
    }

    public static void main(String[] args) {
        try {
            service jeopardyservice = new server();

            // Export the remote object
            service stub = (service) UnicastRemoteObject.exportObject(jeopardyservice, 0);

            // Bind the remote object
            Registry registry = LocateRegistry.createRegistry(8080);
            registry.rebind("Jeopardyservice", stub);

            System.out.println("Jeopardy server is ready.");
        } catch (Exception e) {
            System.err.println("Jeopardy server exception: " + e.toString());
            e.printStackTrace();
        }
    }

    public static class Question implements Serializable {
        private String questionText;
        private String answer;

        public Question(String questionText, String answer) {
            this.questionText = questionText;
            this.answer = answer;
        }

        public String getQuestionText() {
            return questionText;
        }

        public String getAnswer() {
            return answer;
        }
    }

    @Override
    public String getCorrectAnswer() throws RemoteException {
        if (currentQuestion != null) {
            return currentQuestion.getAnswer();
        } else {
            throw new RemoteException("No question has been selected yet");
        }
    }

    // @Override
    // public void startNewGame() throws RemoteException {
    //     playerScores.clear();
    //     gameBoard.clear();
    //     readMovieQuestions();
    //     System.out.println("Game Board:\n" + getGameBoardJava(1)); // Assuming category 1 is the movie category
    //     System.out.println("A new game has started.");
    // }

    @Override
    public String getGameBoardJava(int i) throws RemoteException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getGameBoardJava'");
    }

    @Override
    public String getGameBoard() throws RemoteException {
        throw new UnsupportedOperationException("Unimplemented method 'getGameBoard'");
    }
}
