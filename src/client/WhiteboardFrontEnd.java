package client;

import java.util.List;

public interface WhiteboardFrontEnd {

    public void drawLineOnGUI(String strx1, String stry1, String strx2,
            String stry2, String strwidth, String strr, String strg,
            String strb, String user);
    
    public void fillWithWhite();
    public void addNewUser(String user);
    public void removeUser(String user);

    public void loadGuiUsers(List<String> usersList);
    
}
