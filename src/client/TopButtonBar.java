package client;

import java.awt.Color;
import java.util.Hashtable;

import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JToggleButton;

public class TopButtonBar extends JPanel{
	
	public JToggleButton eraser;
    public JToggleButton accessPalette;
    private JColorChooser palette;
    
    public JSlider strokeSlider;
    private final int STROKE_MIN = 1;
    private final int STROKE_MAX = 41;
    private final int STROKE_DEFAULT = 21;
    
    public JButton bmp;
    public JButton revert;
	
	public TopButtonBar() {
		
		eraser = new JToggleButton("Eraser");
        accessPalette = new JToggleButton("Choose Color");
        this.add(eraser);
        this.add(accessPalette);
        
        strokeSlider = new JSlider(STROKE_MIN, STROKE_MAX, STROKE_DEFAULT);
        strokeSlider.setMinorTickSpacing(2);
        strokeSlider.setMajorTickSpacing(10);
        strokeSlider.setPaintTicks(true);
        this.add(strokeSlider);
        
        Hashtable<Integer, JLabel> labelTable = new Hashtable<Integer, JLabel>();
        labelTable.put( new Integer(STROKE_MIN), new JLabel("Small") );
        labelTable.put( new Integer(STROKE_MAX), new JLabel("Large") );
        strokeSlider.setLabelTable(labelTable);

        strokeSlider.setPaintLabels(true);
        
        bmp = new JButton("BMP");
        this.add(bmp);
        
        revert = new JButton("Revert");
        this.add(revert);
	}

}
