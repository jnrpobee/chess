package websocket.commands;

public class ResignCommand extends UserGameCommand {

    public ResignCommand(String authData, int gameID) {
        super(CommandType.RESIGN, authData, gameID);
        //this.commandType = CommandType.RESIGN;
    }
}
