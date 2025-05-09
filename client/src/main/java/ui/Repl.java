package ui;

import com.google.gson.Gson;
import result.LoginRequest;
import websocket.NotificationHandler;
import websocket.messages.*;

import java.util.Scanner;

import static ui.EscapeSequences.*;

public class Repl implements NotificationHandler {
    private final PreLogin preLogin;
    private final PostLogin postLogin;
    private final GamePlay gamePlay;

    public Repl(String serverUrl) {

        preLogin = new PreLogin(serverUrl);
        gamePlay = new GamePlay(serverUrl, preLogin.getAuth(), this);
        postLogin = new PostLogin(serverUrl, preLogin.getAuth(), this, gamePlay);
    }

    public void run() {
        System.out.println(SET_TEXT_BOLD +
                "♕ Welcome to 240 Chess Game. Type Help to get started ♕");
        System.out.print(preLogin.help());

        Scanner scanner = new Scanner(System.in);
        String state = "preLogin"; // Start in pre-login state
        String result = "";

        while (!result.equals("quit")) {
            postLogin.authData = preLogin.getAuth();
            printPrompt();
            String line = scanner.nextLine();

            try {
                if (state.equals("preLogin")) {
                    result = preLogin.eval(line);
                    System.out.print(SET_TEXT_COLOR_BLUE + result);
                    // Transition to postLogin if login is successful
                    if (result.startsWith(String.format("You signed in as %s.", preLogin.getUsername())) ||
                            result.startsWith(String.format("Registered user %s", preLogin.getUsername()))) {
                        state = "postLogin";
                        System.out.println("\nEntering post-login phase...");
                        System.out.print(postLogin.help());
                    } else if (result.equals("Logged out successfully")) {
                        state = "preLogin";
                        System.out.print(preLogin.help());
                    }
                } else if (state.equals("postLogin")) {
                    result = postLogin.eval(line);
                    System.out.print(SET_TEXT_COLOR_BLUE + result);
                    if (result.equals("Logged out successfully")) {
                        state = "preLogin";
                        System.out.print(preLogin.help());
                    }
                    // Transition to gamePlay if game starts
                    else if (result.matches("Observing Game: \\w+") || result.matches("Joined Game: \\d+ as \\w+")) {
                        gamePlay.state = 2;
                        state = "gamePlay";
                        System.out.println("\nEntering gameplay phase...");
                        System.out.print(gamePlay.help());
                    }
                } else {
                    result = gamePlay.eval(line);
                    System.out.print(SET_TEXT_COLOR_BLUE + result);
                    // Optionally return to postLogin or quit
                    if (result.equals("Left the game")) {
                        state = "postLogin";
//                        postLogin.state = 1; // this line of code is added to return to postLogin state
//                        gamePlay.state = 0; // Reset gamePlay state
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
                //System.out.println(EscapeSequences.SET_BG_COLOR_BLACK);
                printPrompt();
                break;
            }
        }
    }
}
