import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.InputMismatchException;
import java.util.Scanner;

public class client {
    public static void main(String[] args) {
        try {
            // Get the reference to the remote object
            Registry registry = LocateRegistry.getRegistry("localhost", 8080);
            service service = (service) registry.lookup("Jeopardyservice");

            // Initialize scanner for user input
            Scanner scanner = new Scanner(System.in);

            // Main menu loop
            while (true) {
                displayMainMenu();
                int[] choice = getUserChoice(scanner);

                switch (choice[0]) {
                    case 1:
                        // Display game board for the movie category
                        System.out.println("Movie Category Game Board:\n" + service.getGameBoardJava(1));
                        break;
                    case 2:
                        // Select a question
                        try {
                            System.out.println("Enter the question index:");
                            int[] questionIndex = getUserChoice(scanner);
                            server.Question selectedQuestion = service.selectQuestion(1, questionIndex[0]);
                            if (selectedQuestion != null) {
                                System.out.println("Selected Question: " + selectedQuestion.getQuestionText());
                            } else {
                                System.out.println("Invalid question index.");
                            }
                        } catch (InputMismatchException e) {
                            System.out.println("Invalid input. Please enter a numeric value for the question index.");
                            scanner.nextLine(); // Consume the newline character
                        } catch (Exception e) {
                            System.err.println("Error: " + e.getMessage());
                        }
                        break;
                    case 3:
                        // Submit an answer
                        handleUserAnswer(scanner, service);
                        break;
                    case 4:
                        // Get player scores
                        System.out.println("Player Scores: " + service.getPlayerScores());
                        break;
                    case 5:
                        // Exit the client
                        System.out.println("Exiting Jeopardy Client.");
                        System.exit(0);
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            }

        } catch (Exception e) {
            System.err.println("Jeopardy Client exception: " + e.toString());
            e.printStackTrace();
        }
    }

    private static void displayMainMenu() {
        System.out.println("\n=== Game Menu ===");
        System.out.println("1. Display Movie Category Game Board");
        System.out.println("2. Select a Question");
        System.out.println("3. Submit an Answer");
        System.out.println("4. Get Player Scores");
        System.out.println("5. Exit");
        System.out.print("Enter your choice: ");
    }

    private static int[] getUserChoice(Scanner scanner) {
        int[] choices = new int[1];
        try {
            String input = scanner.nextLine();
            choices[0] = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a numeric choice.");
            return getUserChoice(scanner); // Recursively call to get valid choices
        }
        return choices;
    }

    // Use this method to handle user answer input and display updated points
    private static void handleUserAnswer(Scanner scanner, service service) {
        System.out.println("Enter your answer:");
        String answer = scanner.nextLine();

        try {
            boolean isCorrect = service.submitAnswer(answer);
            if (isCorrect) {
                System.out.println("Correct! Your updated points: " + service.getPlayerScores());
            } else {
                System.out.println("Incorrect! The correct answer was: " + service.getCorrectAnswer());
            }
        } catch (Exception e) {
            System.err.println("Error submitting answer: " + e.getMessage());
        }
    }
}
