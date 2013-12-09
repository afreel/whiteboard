package client;

import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class BottomButtonBar extends JPanel {
    
    public JTextField inputName;
    
    public JMenuBar boardMenuBar;
    public JMenu boardMenu;
    public JMenuItem board1;
    public JMenuItem board2;
    public JButton connect;
    
    public BottomButtonBar() {
    	inputName = new JTextField("[guest]");
        
        //BOARD MENU
        boardMenuBar = new JMenuBar();
        boardMenu = new JMenu("Whiteboard Menu");
        boardMenuBar.add(boardMenu);
        
        board1 = new JMenuItem("Board 1");
        board2 = new JMenuItem("Board 2");
        boardMenu.add(board1);
        boardMenu.add(board2);
        
        connect = new JButton("Connect!");
        
        this.add(boardMenuBar);
        this.add(inputName);
        this.add(connect);
        
        this.setBackground(new Color(220,220,220));
        this.setBorder(BorderFactory.createRaisedBevelBorder());
    }

}
