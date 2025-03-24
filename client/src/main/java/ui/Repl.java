package ui;

import java.util.Scanner;

import static ui.EscapeSequences.SET_TEXT_COLOR_BLUE;

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
        System.out.println("♕ Welcome to 240 Chess Game. Type Help to get started ♕");
        System.out.print(client.help());

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            printPrompt();
            String line = scanner.nextLine();

            try {
                result = client.eval(line);
                System.out.print(SET_TEXT_COLOR_BLUE + result);
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }
        }
        System.out.println();
    }


}
