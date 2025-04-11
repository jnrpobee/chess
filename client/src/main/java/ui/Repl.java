package ui;

import com.google.gson.Gson;
import websocket.NotificationHandler;
import websocket.messages.*;

import java.util.Scanner;

import static ui.EscapeSequences.*;

public class Repl implements NotificationHandler {
    private final PreLogin preLogin;
    private final PostLogin postLogin;
    private final GamePlay gamePlay;
    //private int state;


    public Repl(String serverUrl) {

        preLogin = new PreLogin(serverUrl);
        postLogin = new PostLogin(serverUrl, preLogin.getAuth(), this);
        gamePlay = new GamePlay(serverUrl, this.toString(), this);
    }

    public void run() {
        System.out.println(SET_TEXT_BOLD +
                "♕ Welcome to 240 Chess Game. Type Help to get started ♕");
        System.out.print(preLogin.help());

        Scanner scanner = new Scanner(System.in);
        String state = "preLogin"; // Start in pre-login state
        String result = "";

        while (!result.equals("quit")) {
            if (preLogin.state == 1 && !state.equals("postLogin")) {
                state = "postLogin";
                System.out.println("\nTransitioning to postLogin phase...");
                System.out.print(postLogin.help());
            } else if (postLogin.state == 2 && !state.equals("gamePlay")) {
                state = "gamePlay";
                System.out.println("\nTransitioning to gameplay phase...");
                System.out.print(gamePlay.help());
            }

            if (state.equals("gamePlay") && result.equals("Exited Gameplay")) {
                postLogin.state = 1; // Return to postLogin state
                state = "postLogin";
            } else if (state.equals("postLogin") && result.equals("Logged out successfully")) {
                postLogin.state = 0; // Return to preLogin state
                preLogin.state = 0;
                System.out.print(preLogin.help());
                state = "preLogin";
            }

            postLogin.authData = preLogin.getAuth();
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
                    } else if (result.equals("logout")) {
                        state = "preLogin";
                        System.out.print(preLogin.help());
                    }
                } else if (state.equals("postLogin")) {
                    result = postLogin.eval(line);
                    System.out.print(SET_TEXT_COLOR_BLUE + result);
                    // Transition to gamePlay if game starts
                    if (result.equals("observe")) {
                        state = "gamePlay";
                        System.out.println("\nEntering gameplay phase...");
                        System.out.print(gamePlay.help());
                    }
                } else {
                    result = gamePlay.eval(line);
                    System.out.print(SET_TEXT_COLOR_BLUE + result);
                    // Optionally return to postLogin or quit
                    if (result.equals("Exited Gameplay")) {
                        state = "postLogin";
                        postLogin.state = 1; // this line of code is added to return to postLogin state
                        gamePlay.state = 0; // Reset gamePlay state
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


    @Override
    public void handle(String message) {
        ServerMessage sm = new Gson().fromJson(message, ServerMessage.class);
        switch (sm.getServerMessageType()) {
            case ERROR: {
                ErrorMessage errorMessage = new Gson().fromJson(message, ErrorMessage.class);
                System.out.println(errorMessage.getErrorMessage());
                break;
            }
            case NOTIFICATION: {
                NotificationMessage notification = new Gson().fromJson(message, NotificationMessage.class);
                System.out.println("\n" + EscapeSequences.SET_TEXT_COLOR_BLUE + notification.getMessage());
                printPrompt();
                break;
            }

            case LOAD_GAME: {
                LoadGameMessage loadGameMessage = new Gson().fromJson(message, LoadGameMessage.class);
                System.out.println(EscapeSequences.SET_BG_COLOR_WHITE +
                        EscapeSequences.SET_TEXT_COLOR_BLACK + gamePlay.drawBoard(loadGameMessage.getGame()));
                System.out.println(EscapeSequences.SET_BG_COLOR_BLACK);
                printPrompt();
                break;
            }
        }
    }

}
