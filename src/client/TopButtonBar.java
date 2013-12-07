package client;

import java.awt.Color;

import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

public class TopButtonBar extends JPanel{
	
	public JToggleButton eraser;
    public JToggleButton accessPalette;
    private JColorChooser palette;
    
    public JMenuBar eraseMenuBar;
    public JMenu eraseMenu;
    public JMenuItem eraseItemSmall;
    public JMenuItem eraseItemMedium;
    public JMenuItem eraseItemLarge;
    
    public JButton bmp;
    public JButton revert;
	
	public TopButtonBar() {
		
		eraser = new JToggleButton("Eraser");
        accessPalette = new JToggleButton("Choose Color");
        this.add(eraser);
        this.add(accessPalette);
        
        //ERASER MENU
        eraseMenuBar = new JMenuBar();
        eraseMenu = new JMenu("Erase Menu");
        eraseMenuBar.add(eraseMenu);
        
        eraseItemSmall = new JMenuItem("Small");
        eraseItemMedium = new JMenuItem("Medium");
        eraseItemLarge = new JMenuItem("Large");
        eraseMenu.add(eraseItemSmall);
        eraseMenu.add(eraseItemMedium);
        eraseMenu.add(eraseItemLarge);
        
        this.add(eraseMenuBar);
        
        bmp = new JButton("BMP");
        this.add(bmp);
        
        revert = new JButton("Revert");
        this.add(revert);
	}

}
