package ui;

import java.util.Scanner;

import static ui.EscapeSequences.*;

public class Repl {
    private final PreLogin preLogin;
    private final PostLogin postLogin;
    private final GamePlay gamePlay;


    public Repl(String serverUrl) {

        preLogin = new PreLogin(serverUrl);
        postLogin = new PostLogin(serverUrl, this);
        gamePlay = new GamePlay(serverUrl, this);
    }

    public void run() {
        System.out.println(SET_TEXT_BOLD + SET_BG_COLOR_DARK_GREEN +
                "♕ Welcome to 240 Chess Game. Type Help to get started ♕");
        System.out.print(preLogin.help());

        Scanner scanner = new Scanner(System.in);
        String state = "preLogin"; // Start in pre-login state
        String result = "";

        while (!result.equals("quit")) {
            printPrompt();
            String line = scanner.nextLine();

            try {
                if (state.equals("preLogin")) {
                    result = preLogin.eval(line);
                    System.out.print(SET_TEXT_COLOR_BLUE + result);
                    // Transition to postLogin if login is successful
                    if (result.equals("login")) {
                        state = "postLogin";
                        System.out.println("\nEntering post-login phase...");
                        System.out.print(postLogin.help());
                    }
                } else if (state.equals("postLogin")) {
                    result = postLogin.eval(line);
                    System.out.print(SET_TEXT_COLOR_BLUE + result);
                    // Transition to gamePlay if game starts
                    if (result.equals("start")) {
                        state = "gamePlay";
                        System.out.println("\nEntering gameplay phase...");
                        System.out.print(gamePlay.help());
                    }
                } else {
                    result = gamePlay.eval(line);
                    System.out.print(SET_TEXT_COLOR_BLUE + result);
                    // Optionally return to postLogin or quit
                    if (result.equals("end")) {
                        state = "postLogin";
                        System.out.println("\nReturning to post-login phase...");
                        System.out.print(postLogin.help());
                    }
                }
            } catch (Throwable e) {
                System.out.print(e.getMessage());
            }
        }
        System.out.println("\nGoodbye!");
        scanner.close();
    }

    private void printPrompt() {
        System.out.print(SET_TEXT_BOLD + SET_TEXT_COLOR_YELLOW + "> ");
    }
//
//}
//


}
