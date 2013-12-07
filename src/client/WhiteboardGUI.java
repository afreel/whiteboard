package client;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;


/**
 * Canvas represents a drawing surface that allows the user to draw
 * on it freehand, with the mouse.
 */
public class WhiteboardGUI extends JPanel {
	
    // image where the user's drawing is stored
    private Image drawingBuffer;
    private JToggleButton eraser;
    private JToggleButton accessPalette;
    private JColorChooser palette;
    
    private JMenuBar eraseMenuBar;
    private JMenu eraseMenu;
    private JMenuItem eraseItemSmall;
    private JMenuItem eraseItemMedium;
    private JMenuItem eraseItemLarge;
    
    private JButton bmp;
    private JButton revert;
    
    private final int SMALL = 10;
    private final int MEDIUM = 25;
    private final int LARGE = 50;
    
    private int eraserSize = MEDIUM;
    
    private WhiteboardModel model;
    /**
     * Make a canvas.
     * @param width width in pixels
     * @param height height in pixels
     */
    public WhiteboardGUI(int width, int height, String host, int port, String username, String whiteboard) {
    	
    	this.setPreferredSize(new Dimension(width, height));
        addDrawingController();
        eraser = new JToggleButton("Eraser");
        accessPalette = new JToggleButton("Choose Color");
        palette = new JColorChooser();
        palette.setColor(Color.BLACK);
        this.add(eraser);
        this.add(accessPalette);
        
        //ERASER MENU TESTING
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
        
        // note: we can't call makeDrawingBuffer here, because it only
        // works *after* this canvas has been added to a window.  Have to
        // wait until paintComponent() is first called.
        
    	accessPalette.addActionListener(new ActionListener() {
    		public void actionPerformed(ActionEvent event) {
    			togglePalette();
    		}
    	});
    	
    	eraseItemSmall.addActionListener(new ActionListener() {
    		public void actionPerformed(ActionEvent event) {
    			eraserSize = SMALL;
    			eraseMenu.setText(eraseItemSmall.getText());
    		}
    	});
    	
    	eraseItemMedium.addActionListener(new ActionListener() {
    		public void actionPerformed(ActionEvent event) {
    			eraserSize = MEDIUM;
    			eraseMenu.setText(eraseItemMedium.getText());
    		}
    	});
    	
    	eraseItemLarge.addActionListener(new ActionListener() {
    		public void actionPerformed(ActionEvent event) {
    			eraserSize = LARGE;
    			eraseMenu.setText(eraseItemLarge.getText());
    		}
    	});
    	
    	bmp.addActionListener(new ActionListener() {
    		public void actionPerformed(ActionEvent event) {
    			saveBMP();
    		}
    	});
    	
    	revert.addActionListener(new ActionListener() {
    		public void actionPerformed(ActionEvent event) {
    			revertToLastBMP();
    		}
    	});  	
    	System.out.println("Made it to gui initialization");
    	this.model = new WhiteboardModel(host, port, username, whiteboard, this);

    }
    
    public void revertToLastBMP() {
    	try {
			drawingBuffer = ImageIO.read(new File("./././savedImages/CanvasImage.BMP"));
			this.repaint();
			System.out.println("reverted");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("could not revert");
		}
    }
    
    public void saveBMP() {
    	BufferedImage bi = (BufferedImage) drawingBuffer;
    	try {
			ImageIO.write(bi, "BMP", new File("./././savedImages/CanvasImage.BMP"));
			System.out.println("saved");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("not saved");
		}
    }
    
    public void togglePalette() {
    	
    	if (accessPalette.isSelected()) {
    		this.add(palette);
    	}
    	else{
    		this.remove(palette);
    	}
    	
    	this.revalidate();
    	this.repaint();
    }
    
    /**
     * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
     */
    @Override
    public void paintComponent(Graphics g) {
        // If this is the first time paintComponent() is being called,
        // make our drawing buffer.
        if (drawingBuffer == null) {
        	System.out.println("About to call mdb");
        	makeDrawingBuffer();
        }
        
        // Copy the drawing buffer to the screen.
        g.drawImage(drawingBuffer, 0, 0, null);
    }
    
    /*
     * Make the drawing buffer and draw some starting content for it.
     */
    private void makeDrawingBuffer() {
        drawingBuffer = createImage(getWidth(), getHeight());
        System.out.println("Made drawingBuffer");
        fillWithWhite();
        drawSmile();
    }
    
    /*
     * Make the drawing buffer entirely white.
     */
    private void fillWithWhite() {
        Graphics2D g = (Graphics2D) drawingBuffer.getGraphics();

        g.setColor(Color.WHITE);
        g.fillRect(0,  0,  getWidth(), getHeight());
        
        // IMPORTANT!  every time we draw on the internal drawing buffer, we
        // have to notify Swing to repaint this component on the screen.
        this.repaint();
    }
    
    /*
     * Draw a happy smile on the drawing buffer.
     */
    private void drawSmile() {
        final Graphics2D g = (Graphics2D) drawingBuffer.getGraphics();

        // all positions and sizes below are in pixels
        final Rectangle smileBox = new Rectangle(20, 20, 100, 100); // x, y, width, height
        final Point smileCenter = new Point(smileBox.x + smileBox.width/2, smileBox.y + smileBox.height/2);
        final int smileStrokeWidth = 3;
        final Dimension eyeSize = new Dimension(9, 9);
        final Dimension eyeOffset = new Dimension(smileBox.width/6, smileBox.height/6);
        
        g.setColor(Color.BLACK);
        g.setStroke(new BasicStroke(smileStrokeWidth));
        
        // draw the smile -- an arc inscribed in smileBox, starting at -30 degrees (southeast)
        // and covering 120 degrees
        g.drawArc(smileBox.x, smileBox.y, smileBox.width, smileBox.height, -30, -120);
        
        // draw some eyes to make it look like a smile rather than an arc
        for (int side: new int[] { -1, 1 }) {
            g.fillOval(smileCenter.x + side * eyeOffset.width - eyeSize.width/2,
                       smileCenter.y - eyeOffset.height - eyeSize.width/2,
                       eyeSize.width,
                       eyeSize.height);
        }
        
        // IMPORTANT!  every time we draw on the internal drawing buffer, we
        // have to notify Swing to repaint this component on the screen.
        this.repaint();
    }
    
    /*
     * Draw a line between two points (x1, y1) and (x2, y2), specified in
     * pixels relative to the upper-left corner of the drawing buffer.
     */
    private void drawLocalLineSegment(int x1, int y1, int x2, int y2) {        
        Color color = palette.getColor();
        int drawWidth = 5; //TODO: Implement changeable width
        model.drawLineOnServer(x1, y1, x2, y2, drawWidth, color.getRed(), color.getGreen(), color.getBlue());
    }
    
    public void drawLineOnGUI(String strx1, String stry1, String strx2, String stry2, String strwidth, String strr, String strg, String strb) {
        if (drawingBuffer == null) {
        	System.out.println("Making drawing buffer within lineongui");
        	makeDrawingBuffer();
        }
        
    	Graphics2D g = (Graphics2D) drawingBuffer.getGraphics();
        
        
        g.setColor(new Color(Integer.parseInt(strr), Integer.parseInt(strg), Integer.parseInt(strb)));
        g.setStroke(new BasicStroke(Integer.parseInt(strwidth)));
        g.drawLine(Integer.parseInt(strx1), Integer.parseInt(stry1), Integer.parseInt(strx2), Integer.parseInt(stry2));
        
        this.repaint();
    }
    /*
     * ERASER
     */
    private void erase(int x1, int y1, int x2, int y2) {
        Graphics2D g = (Graphics2D) drawingBuffer.getGraphics();
        
        g.setColor(Color.WHITE);
        g.setStroke(new BasicStroke(eraserSize));
        g.drawLine(x1, y1, x2, y2);
        
        // IMPORTANT!  every time we draw on the internal drawing buffer, we
        // have to notify Swing to repaint this component on the screen.
        this.repaint();
    }
    
    
    /*
     * Add the mouse listener that supports the user's freehand drawing.
     */
    private void addDrawingController() {
        DrawingController controller = new DrawingController();
        addMouseListener(controller);
        addMouseMotionListener(controller);
    }
    
    /*
     * DrawingController handles the user's freehand drawing.
     */
    private class DrawingController implements MouseListener, MouseMotionListener {
        // store the coordinates of the last mouse event, so we can
        // draw a line segment from that last point to the point of the next mouse event.
        private int lastX, lastY; 

        /*
         * When mouse button is pressed down, start drawing.
         */
        public void mousePressed(MouseEvent e) {
            System.out.println(e.paramString());
        	lastX = e.getX();
            lastY = e.getY();
        }

        /*
         * When mouse moves while a button is pressed down,
         * draw a line segment.
         */
        public void mouseDragged(MouseEvent e) {
            int x = e.getX();
            int y = e.getY();
            if (eraser.isSelected()){
            	erase(lastX, lastY, x, y);
            }
            else{
            	drawLocalLineSegment(lastX, lastY, x, y);
            }
            lastX = x;
            lastY = y;
        }

        // Ignore all these other mouse events.
        public void mouseMoved(MouseEvent e) { }
        public void mouseClicked(MouseEvent e) { }
        public void mouseReleased(MouseEvent e) { }
        public void mouseEntered(MouseEvent e) { }
        public void mouseExited(MouseEvent e) { }
    }

    /*
     * Main program. Make a window containing a Canvas.
     */
    public static void main(final String[] args) {
        // set up the UI (on the event-handling thread)
        SwingUtilities.invokeLater(new Runnable() {
            
        	public void run() {
                JFrame window = new JFrame("Freehand Canvas");
                window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                window.setLayout(new BorderLayout());
                WhiteboardGUI canvas = new WhiteboardGUI(800, 600, "localhost", 4444, "norheim", "1");
                window.add(canvas, BorderLayout.CENTER);
                window.pack();
                window.setVisible(true);
            }
        });
    }
    
    
}
