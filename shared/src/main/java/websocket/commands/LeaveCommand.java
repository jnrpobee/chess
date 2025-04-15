package websocket.commands;

public class LeaveCommand extends UserGameCommand {

    public LeaveCommand(String authData, int gameID) {
        super(CommandType.LEAVE, authData, gameID);
    }
}
