package client;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
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
 * on it freehand, with the mouse
 */ 

/* Rep invariant:
 *  - model != null
 *  - usernameTakenImageLoc.equals("./././images/UsernameTakenImage.bmp")
 *  - welcomeImageLoc.equals("./././images/WelcomeImage.bmp")
 *  - connectedToServerImageLoc.equals("./././images/ConnectedToServerImage.bmp")
 */

/*
 * TESTING:
 * 
 * 	GUI was tested with manual tests. As an overview, each listener/method was tested individually by connecting a client to a server
 * 	and observing that every possible event triggered the expected visual result under differing circumstances. Tests
 * 	were also done for pre-connection actions.  This strategy is documented in much more detail below:
 * 
 * 	Pre-Connection:
 * 		> tested running WhiteboardGUI without a server running, and ensuring that no matter what inputs are put into the 
 * 		  IP and Port fields, the board stays on the same screen and does not connect (tested explicitly with print lines)
 * 		> loadWelcomeImage()
 * 			-ensured the 'Welcome' image is displayed upon opening the GUI
 * 		> before connecting, tested the case when a client still tries to draw to the board and ensured that no error was
 * 		  thrown and no lines were in fact drawn locally
 * 	Post-Connection:
 * 		> eraser listener
 * 			- in the case where we were previously drawing, the toggle button is now toggled on, the eraser icon 
 * 			  replaces the pencil icon, and dragging on the screen now erases (draws white lines)
 * 			- in the case where we were previously erasing, the toggled button is toggled off, the pencil icon replaces 
 * 			  the eraser icon, and dragging on the screen now draws according to the specified color
 * 		> choose color listener
 * 			- in the case where the "Choose Color" button is previously toggled off, it is now toggled on and the color palette appears
 * 			- in the case where the "Choose Color" button is previously toggled on, it is now toggled off and the color palette disappears
 * 			- note that we also tested drawing, erasing, etc. *while* the button is toggled both on and off and ensured that it
 * 			  does not affect the ability to interact with the baord, as is desired
 * 		> save image listener:
 * 			- checked that when "Save Image" button is pressed, an image corresponding to what is currently seen on the canvas
 * 			  is saved locally to the user's computer under a file named "CanvasImage.BMP"
 * 			- checked that pressing this multiple times will simply replace the old image and not cause error
 * 		> input name listener:
 * 			- checked that when someone presses the 'Enter' key while in the username box, that the person joins the board currently selected
 * 			  and that same name is displayed in the users list
 * 				- as subsets of this test, we also tested for cases when usernames were already taken, which prompts a corresponding 
 * 				  display and does not allow the user to join the whiteboard session
 * 		> input IP listener:
 * 			- check that when someone presses the 'Enter' key  while in the IP Address input box, that the person attempts to join the server
 * 			  corresponding to this IP address at the port number currently stated in the port box
 * 			- note that different scenarios (correct/incorrect IPs) are tested in our end-to-end manual tests
 * 		> input port listener:
 * 			- check that when someone presses the 'Enter' key while in the Port input box, that the person attempts to join the server
 * 			  corresponding to the input box's port number on the server currently stated in the IP box
 * 			- note that different scenarios (correct/incorrect ports) are tested in our end-to-end manual tests
 * 		> board # listener:
 * 			- check that board menu displays the message "Board #", where # corresponds to the board # button selected from the
 * 			  drop-down menu
 * 		> connect listener:
 * 			- checked that when someone clicks "Connect", that the person attempts to join the server designated by the provided
 * 			  IP address and port number
 * 			- note that different scenarios (correct/incorrect IP-port combinations) are tested in our end-to-end manual tests
 * 		> join board listener:
 * 			- same as 'input name listener' tests, except instead used for the event that a person pressed the "Join Board" button instead
 * 			  of pressing the 'Enter' key in the input name box
 * 			- see 'switching of board' tests below to see the tests of join board for the cases when the user is already logged in
 * 		> loadConnectedToServerImage()
 * 			- ensured the 'Connected to Server' image is displayed when a client succesfully connects to a server
 * 		> loadUsernameTakenImage()
 * 			- ensured the 'Username Taken' image is displayed when a client attempts to join a board using a username
 * 			  that is currently being used by another client
 * 		> mouseDragged event:
 * 			- visually observed that when a mouse dragged across the canvas, if already connected to a server, a line was drawn
 * 			  on the course of the drag, of the specified width (corresponding to the slider), of the specified color
 * 			- same test, but for the case when erase is toggled on (observed that line was white)
 * 		> before joining a specific whiteboard, tested the case when a user tried to draw to the board, and observed that
 * 		  no error was thrown and no lines were in fact sent to the server or drawn locally
 * 		> tested switching of whiteboards:
 * 			- removed username of client from list of users on previous whiteboard's GUI
 * 			- added username of client to list of users on new whiteboard's GUI
 * 			- changed canvas to display the correct new whiteboard
 * 				- this was itself tested for cases when the whiteboard being switched to was both (a) populated with an image,
 * 				  (b) not populated with an image, and (c) currently being drawn on, and the GUI was up-to-date in all cases
 * 			- also tested case when attempting to switch to same whiteboard and observed that no changes were made and no errors were thrown
 * 			
 * 
 */

/*------------------------------------------------------Thread-safety Argument------------------------------------------------------------
 * There are two types of changes that this GUI can undergo:
 *  o Changes to the canvas which the user draws on 
 *  o Changes to the BottomButtonBar at the bottom of the window, and loading of info message BMPs to canvas
 *  
 * These two changes never overlap in time, as changes to BottomButtonBar and loading of BMPs occur before the client connects to a Whiteboard, and thus before
 * any changes to the canvas can be made. Thus it is viable to argue the thread safety of these two GUI changes independently.
 * 
 * Any time a client draws to the canvas, a "line ... " message is passed to the server, then back to all clients connected to that client's
 * chosen whiteboard. Thus no drawing takes place on this canvas before the a message from the server is received. The thread-safety of 
 * WhiteboardServer and Whiteboard ensures that messages sent from multiple clients are handled in a thread-safe manner server-side, and 
 * thus that lines drawn on this GUI are drawn in a consistent manner across all clients' whiteboards (due to the thread-safe 'filter' of 
 * our server-side classes).
 * 
 * Changes to BottomButtonBar occur in steps, with changes to the client's level of connectivity to the server (Not connected to the server; 
 * connected; connected to a whiteboard). Each time, a change in the GUI only occurs once a message has been received from the server. This
 * step-wise nature of the changes and independence from canvas changes outlined above results in thread-safety of this part of the GUI.
 * 
 * For an example of canvas thread-safety, if a user just connects to a whiteboard, it is sent that whiteboard's history. As the history can take 
 * time to load completely, it can be illustrative to look at behaviour during this time. History is sent line-by-line, and locks on history while 
 * this is taking place. Now any calls to add a new line message (say, when the client draws on their still-loading canvas), also acquire a lock on 
 * the history list (see Whiteboard#addLine), so that this message is only sent back to server and drawn on their canvas once the history has finished 
 * being sent. This results in the line the client drew being drawn over the loaded whiteboard canvas. This is exactly the behaviour we desired. 
 * 
 * -- mention writing while loading canvas goes to front, as ping to server
 * 
 */
public class WhiteboardGUI extends JPanel implements WhiteboardFrontEnd {
	
    // image where the user's drawing is stored
    private Image drawingBuffer;
    
    private boolean drawing = true; //used to track if user is currently in draw or erase mode
    
    private JColorChooser palette;
    
	private String username = "[guest]"; //default username to "guest"
    private String whiteboard = "1"; //default to whiteboard 1
    private String ip = "localhost"; //default IP address to be localhost
    private int port = 4444; //default Port # is 4444
    
    private boolean connectedToServer = false; //initially not connected to server
    
    private TopButtonBar topbar;
    private BottomButtonBar bottombar;
    private UsersBar usersbar;
    
    private WhiteboardModel model;
    
    private String usernameTakenImageLoc = "./././images/UsernameTakenImage.bmp";
    private String welcomeImageLoc = "./././images/WelcomeImage.bmp";
    private String connectedToServerImageLoc = "./././images/ConnectedToServerImage.bmp";
    
    private boolean usernameAccepted = false; // for use in calls to WhiteboardModel#connectToWhiteboard from joinBoard(). True if username has
    										  // already been accepted by the server as unique; false otherwise. 
   
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
    			try {
					joinBoard();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
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
    	
    	bottombar.board3.addActionListener(new ActionListener() {
    		public void actionPerformed(ActionEvent event) {
    			whiteboard = "3";
    			bottombar.boardMenu.setText(bottombar.board3.getText());
    		}
    	});
    	
    	bottombar.board4.addActionListener(new ActionListener() {
    		public void actionPerformed(ActionEvent event) {
    			whiteboard = "4";
    			bottombar.boardMenu.setText(bottombar.board4.getText());
    		}
    	});
    	
    	bottombar.board5.addActionListener(new ActionListener() {
    		public void actionPerformed(ActionEvent event) {
    			whiteboard = "5";
    			bottombar.boardMenu.setText(bottombar.board5.getText());
    		}
    	});
    	
    	bottombar.connect.addActionListener(new ActionListener() {
    		public void actionPerformed(ActionEvent event) {
    			connect();
    		}
    	});
    	
    	bottombar.joinBoard.addActionListener(new ActionListener() {
    		public void actionPerformed(ActionEvent event) {
    			try {
					joinBoard();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    		}
    	});
    	loadWelcomeImage();
    	model = new WhiteboardModel(this);
    }
    
    private boolean isConnectedToServer() {
    	return connectedToServer;
    }

    private void connect() {
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
        	loadConnectedToServerImage();
		}
		catch(Exception e) {
			System.out.println("Could not connect to Server. Invalid port or IP address");
		}

    }

    private void loadImage(String imageLoc) {
        try {
            drawingBuffer = ImageIO.read(new File(imageLoc));
            this.repaint();
    } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Failed to load image @ " + imageLoc + " Please ensure that all files were downloaded correctly");
    }
    }
    
    public void loadWelcomeImage() {
    	loadImage(welcomeImageLoc);
    }
    
    public void loadConnectedToServerImage() {
    	loadImage(connectedToServerImageLoc);
    }
    
    public void loadUsernameTakenImage() {
    	loadImage(usernameTakenImageLoc);
    }
    
    private void joinBoard() throws IOException {
    	fillWithWhite();
    	if (bottombar.inputName.getText().length() > 0) {
			username = bottombar.inputName.getText();	
		}
    	if (model.connectToWhiteBoard(whiteboard, username, usernameAccepted)) {
        	usernameAccepted = true;
    		bottombar.boardMenu.setText("Board " + whiteboard);
        	bottombar.remove(bottombar.name);
        	bottombar.remove(bottombar.inputName);
        	bottombar.revalidate();
        	bottombar.repaint();
    	};
    }
    
    private void saveBMP() {
    	BufferedImage bi = (BufferedImage) drawingBuffer;
    	try {
			ImageIO.write(bi, "BMP", new File("c:\\CanvasImage.BMP"));
			System.out.println("saved");
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("not saved");
		}
    }
    
    private void togglePalette() {
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
    private void fillWithWhite() {
        Graphics2D g = (Graphics2D) drawingBuffer.getGraphics();

        g.setColor(Color.WHITE);
        g.fillRect(0,  0,  getWidth(), getHeight());
        
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
    	usersbar.addNewUser(user, false); 
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
    /**
     * Ensure that our representation invariant is maintained
     */
    public void checkRep() {
    	assert(model != null);
    	assert(usernameTakenImageLoc.equals("./././images/UsernameTakenImage.bmp"));
    	assert(welcomeImageLoc.equals("./././images/WelcomeImage.bmp"));
    	assert(connectedToServerImageLoc.equals("./././images/ConnectedToServerImage.bmp"));
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
                
                final WhiteboardGUI canvas = new WhiteboardGUI(topbar, bottombar, usersbar, 900, 600);
                
                window.add(canvas, BorderLayout.CENTER);
                window.add(topbar, BorderLayout.NORTH);
                window.add(bottombar, BorderLayout.SOUTH);
                window.add(usersbar, BorderLayout.EAST);
                window.setResizable(false);
                window.pack();
                window.addWindowListener(new WindowAdapter() {
                	public void windowClosing(WindowEvent e) {
                		if (canvas.isConnectedToServer()) {
                			canvas.model.disconnectFromServer();
                		}
                	}
                });
                window.setVisible(true);
            }
        });
    }
    
    
}
