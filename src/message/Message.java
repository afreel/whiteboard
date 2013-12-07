package message;

public class Message {
    private final String[] message;

    public Message(String message){
        this.message = message.split(" ");
    }
    
    public boolean isWhiteboardConnectMsg(){
        if(message.length == 3){
            if (message[0] == "whiteboard" && message[2] == "username"){
                return true;
            }
        }
        return false;
    }
    
    public boolean isLineMsg(){
        if(message.length == 9){
            if (message[0] == "line"){
                try{
                    Integer.parseInt(message[1]);
                    return true;
                }catch (NumberFormatException e){
                  return false;  
                }
            }
            }
        return false;
    }
    
    public boolean isUpdateUsersMsg(){
        if(!(isWhiteboardConnectMsg() || isLineMsg())  && message.length > 1){
            if(message[0] == "users"){
                return true;
            }
        }
        return false;
    }
    
}
