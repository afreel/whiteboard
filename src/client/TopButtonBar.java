package client;

import java.awt.Color;
import java.util.Hashtable;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JToggleButton;

public class TopButtonBar extends JPanel{
	
	public ImageIcon drawIcon;
	public ImageIcon eraseIcon;
	public JLabel imageLabel;
	
	public JToggleButton eraser;
    public JToggleButton accessPalette;
    
    public JSlider strokeSlider;
    private final int STROKE_MIN = 1;
    private final int STROKE_MAX = 41;
    private final int STROKE_DEFAULT = 21;
    
    public JButton bmp;
	
	public TopButtonBar() {
		
		drawIcon = new ImageIcon("././images/pencil.png");
		eraseIcon = new ImageIcon("././images/eraser.gif");
		imageLabel = new JLabel(drawIcon);
		this.add(imageLabel);
		
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
        
        bmp = new JButton("Save Image");
        this.add(bmp);
        
        this.setBackground(new Color(220,220,220));
        this.setBorder(BorderFactory.createRaisedBevelBorder());
	}

}
