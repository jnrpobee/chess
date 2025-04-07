package websocket.commands;

public class LeaveCommand extends UserGameCommand {

    public LeaveCommand(String authData, int gameID) {
        super(CommandType.LEAVE, authData, gameID);
    }

    // @Override
    // public String toString() {
    //     return "LeaveCommand{" +
    //             "authData='" + authData + '\'' +
    //             ", gameID=" + gameID +
    //             ", commandType=" + commandType +
    //             '}';
    // }
}
