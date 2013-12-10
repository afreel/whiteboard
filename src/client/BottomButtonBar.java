package client;

import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.Border;

public class BottomButtonBar extends JPanel {
    
    public JTextField inputName;
    public JTextField inputIP;
    public JTextField inputPort;
    
    public JLabel name;
    public JLabel ip;
    public JLabel port;
    
    public JMenuBar boardMenuBar;
    public JMenu boardMenu;
    public JMenuItem board1;
    public JMenuItem board2;
    
    public JButton connect;
    public JButton joinBoard;
    
    public BottomButtonBar() {
    	inputName = new JTextField(8);
    	inputIP = new JTextField(8);
    	inputPort = new JTextField(4);
    	
    	name = new JLabel("Username:");
        ip = new JLabel("IP:");
        port = new JLabel("Port:");
        
        //BOARD MENU
        boardMenuBar = new JMenuBar();
        boardMenu = new JMenu("Whiteboard Menu");
        boardMenuBar.add(boardMenu);
        
        board1 = new JMenuItem("Board 1");
        board2 = new JMenuItem("Board 2");
        boardMenu.add(board1);
        boardMenu.add(board2);
        
        boardMenuBar.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        
        connect = new JButton("Connect");
        joinBoard = new JButton("Join Board");
        
        this.add(ip);
        this.add(inputIP);
        
        this.add(port);
        this.add(inputPort);
        
        this.add(connect);
        
        this.setBackground(new Color(220,220,220));
        this.setBorder(BorderFactory.createRaisedBevelBorder());
    }

}
