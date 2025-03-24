package ui;

import java.util.Scanner;

import static ui.EscapeSequences.*;

public class Repl {
    private final PreLogin preLogin;
    private final PostLogin postLogin;
    private final GamePlay gamePlay;


    public Repl(String serverUrl) {

        preLogin = new PreLogin(serverUrl, this);
        postLogin = new PostLogin(serverUrl, this);
        gamePlay = new GamePlay(serverUrl, this);
    }

    public void run() {
        System.out.println(SET_TEXT_BOLD + SET_BG_COLOR_DARK_GREEN +
                "♕ Welcome to 240 Chess Game. Type Help to get started ♕");
        System.out.print(preLogin.help());

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            printPrompt();
            String line = scanner.nextLine();

            try {
                result = preLogin.eval(line);
                System.out.print(SET_TEXT_COLOR_BLUE + result);
            } catch (Throwable e) {
                System.out.print(e.getMessage());
            }
        }
        System.out.println();
    }


}
