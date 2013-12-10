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
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JColorChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;


/**
 * Canvas represents a drawing surface that allows the user to draw
 * on it freehand, with the mouse.
 */
public class WhiteboardGUI extends JPanel {
	
    // image where the user's drawing is stored
    private Image drawingBuffer;
    
    private boolean drawing = true; //used to track if user is currently in draw or erase mode
    
    private JColorChooser palette;
    
	private String username = "[guest]"; //default username to "guest"
    private String whiteboard = "1"; //default to whiteboard 1
    private String ip = "localhost"; //default IP address to be localhost
    private int port = 4444; //default Port # is 4444
    
    boolean connectedToServer = false; //initially not connected to server
    
    private TopButtonBar topbar;
    private BottomButtonBar bottombar;
    private UsersBar usersbar;
    
    private WhiteboardModel model;
    /**
     * Make a canvas.
     * @param width width in pixels
     * @param height height in pixels
     */
    public WhiteboardGUI(TopButtonBar TBB, BottomButtonBar BBB, UsersBar UB, int width, int height) {
    	
    	this.setPreferredSize(new Dimension(width, height));
        addDrawingController();
        
        topbar = TBB;
        bottombar = BBB;
        usersbar = UB;
        
        palette = new JColorChooser();
        palette.setColor(Color.BLACK);
        
        // note: we can't call makeDrawingBuffer here, because it only
        // works *after* this canvas has been added to a window.  Have to
        // wait until paintComponent() is first called.
        
        topbar.eraser.addActionListener(new ActionListener() {
    		public void actionPerformed(ActionEvent event) {
    			if (topbar.eraser.isSelected()) {
    				drawing = false;
    				topbar.imageLabel.setIcon(topbar.eraseIcon);
    				topbar.revalidate();
    				topbar.repaint();
    			}
    			else {
    				drawing = true;
    				topbar.imageLabel.setIcon(topbar.drawIcon);
    				topbar.revalidate();
    				topbar.repaint();
    			}
    		}
    	});
        
    	topbar.accessPalette.addActionListener(new ActionListener() {
    		public void actionPerformed(ActionEvent event) {
    			togglePalette();
    		}
    	});
    	
    	topbar.bmp.addActionListener(new ActionListener() {
    		public void actionPerformed(ActionEvent event) {
    			saveBMP();
    		}
    	});
    	
    	bottombar.inputName.addActionListener(new ActionListener() {
    		public void actionPerformed(ActionEvent event) {
    			connect();
    		}
    	});
    	
    	bottombar.inputIP.addActionListener(new ActionListener() {
    		public void actionPerformed(ActionEvent event) {
    			connect();
    		}
    	});
    	
    	bottombar.inputPort.addActionListener(new ActionListener() {
    		public void actionPerformed(ActionEvent event) {
    			connect();
    		}
    	});
    	
    	bottombar.board1.addActionListener(new ActionListener() {
    		public void actionPerformed(ActionEvent event) {
    			whiteboard = "1";
    			bottombar.boardMenu.setText(bottombar.board1.getText());
    		}
    	});
    	
    	bottombar.board2.addActionListener(new ActionListener() {
    		public void actionPerformed(ActionEvent event) {
    			whiteboard = "2";
    			bottombar.boardMenu.setText(bottombar.board2.getText());
    		}
    	});
    	
    	bottombar.connect.addActionListener(new ActionListener() {
    		public void actionPerformed(ActionEvent event) {
    			connect();
    		}
    	});
    	
    	bottombar.joinBoard.addActionListener(new ActionListener() {
    		public void actionPerformed(ActionEvent event) {
    			joinBoard();
    		}
    	});
    	
    	model = new WhiteboardModel(this);
    }
    

    public void connect() {
    	if (bottombar.inputIP.getText().length() > 0) {
    		ip = bottombar.inputIP.getText();
		}
    	if (bottombar.inputPort.getText().length() > 0) {
    		port = Integer.parseInt(bottombar.inputPort.getText());	
		}
		try {
			if (!connectedToServer){
				model.connectToServer(ip, port);
				connectedToServer = true;
			}
			bottombar.removeAll();
			bottombar.add(bottombar.name);
			bottombar.add(bottombar.inputName);
			bottombar.add(bottombar.boardMenuBar);
			bottombar.add(bottombar.joinBoard);
			bottombar.revalidate();
			bottombar.repaint();
		}
		catch(Exception e) {
			System.out.println("Could not connect to Server. Invalid port or IP address");
		}

    }
    
    public void joinBoard() {
    	if (bottombar.inputName.getText().length() > 0) {
			username = bottombar.inputName.getText();	
		}
    	model.connectToWhiteBoard(whiteboard, username);
    	bottombar.boardMenu.setText("Board " + whiteboard);
    	bottombar.remove(bottombar.name);
    	bottombar.remove(bottombar.inputName);
    	bottombar.revalidate();
    	bottombar.repaint();
    }
    
    public void saveBMP() {
    	BufferedImage bi = (BufferedImage) drawingBuffer;
    	try {
			ImageIO.write(bi, "BMP", new File("c:\\CanvasImage.BMP"));
			System.out.println("saved");
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("not saved");
		}
    }
    
    public void togglePalette() {
    	
    	if (topbar.accessPalette.isSelected()) {
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
        fillWithWhite();
    }
    
    /*
     * Make the drawing buffer entirely white.
     */
    public void fillWithWhite() {
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
    private void drawLocalLineSegment(int x1, int y1, int x2, int y2, Color color) {        
        model.drawLineOnServer(x1, y1, x2, y2, topbar.strokeSlider.getValue(), color.getRed(), color.getGreen(), color.getBlue());
    }
    
    public void drawLineOnGUI(String strx1, String stry1, String strx2, String stry2, String strwidth, String strr, String strg, String strb, String user) {
        usersbar.updateUserColor(user, Integer.parseInt(strr), Integer.parseInt(strg), Integer.parseInt(strb));
    	Graphics2D g = (Graphics2D) drawingBuffer.getGraphics();
        
        
        g.setColor(new Color(Integer.parseInt(strr), Integer.parseInt(strg), Integer.parseInt(strb)));
        g.setStroke(new BasicStroke(Integer.parseInt(strwidth)));
        g.drawLine(Integer.parseInt(strx1), Integer.parseInt(stry1), Integer.parseInt(strx2), Integer.parseInt(stry2));
        
        this.repaint();
    }
    
    /**
     * populate usersbar with the list of currently connected users
     * @param users users currently connected to this client's whiteboard
     */
    public void loadGuiUsers(List<String> users) {
    	usersbar.loadUsersBar(users);
    }
    
    /**
     * update usersbar with a newly connected user
     * @param user user connected
     */
    public void addNewUser(String user) {
    	usersbar.addNewUser(user, false); //
    }
    
    /**
     * update usersbar with information that previously connected user is now disconnected 
     * @param user user disconnected
     */
    public void removeUser(String user) {
    	usersbar.removeUser(user);
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
            if (connectedToServer) {
            	if (drawing){
            		drawLocalLineSegment(lastX, lastY, x, y, palette.getColor());
                }
                else{
                	drawLocalLineSegment(lastX, lastY, x, y, Color.WHITE);
                }
                lastX = x;
                lastY = y;
            }
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
                JFrame window = new JFrame("Whiteboard");
                window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                window.setLayout(new BorderLayout());

                TopButtonBar topbar = new TopButtonBar();
                BottomButtonBar bottombar = new BottomButtonBar();
                UsersBar usersbar = new UsersBar(new ArrayList<String>());
                

                final WhiteboardGUI canvas = new WhiteboardGUI(topbar, bottombar, usersbar, 900, 600); //18.217.1.147
                
                window.add(canvas, BorderLayout.CENTER);
                window.add(topbar, BorderLayout.NORTH);
                window.add(bottombar, BorderLayout.SOUTH);
                window.add(usersbar, BorderLayout.EAST);
                window.setResizable(false);
                window.pack();
                window.addWindowListener(new WindowAdapter() {
                	public void windowClosing(WindowEvent e) {
                		canvas.model.disconnectFromServer();
                	}
                });
                window.setVisible(true);
            }
        });
    }
    
    
}
