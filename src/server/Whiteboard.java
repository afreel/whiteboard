package server;

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
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
 * Whiteboard represents a whiteboard on the server.  We will have an instance of this class for every whiteboard we have on
 * the server so that we may maintain an updated copy of every whiteboard whether there are clients connected to it or not.
 * When clients interact with this whiteboard and change it (connecting/disconnnecting/drawing/erasing), this whiteboard will
 * be updated to reflect the changes and stored in a list of whiteboards on the server side.
 */

public class Whiteboard extends JPanel {
	
	private Image drawingBuffer; //image of our whiteboard
	private List<Client> clients; //list of clients currently using this whiteboard
	private List<String> history; //list of line draw messages sent to this whiteboard since its creation
	private File bmp = new File("./././savedImages/CanvasImage.BMP"); //BMP file storing our image
	
	/**
     * Make a whiteboard.
     */
    public Whiteboard() {
    	this.setPreferredSize(new Dimension(800, 600));
    	clients = new ArrayList<Client>();
    	history = new ArrayList<String>();
    }
    
    /**
     * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
     */
    @Override
    public void paintComponent(Graphics g) {
        // If this is the first time paintComponent() is being called,
        // make our drawing buffer.
        System.out.println("Paint component being called");
    	if (drawingBuffer == null) {
            makeDrawingBuffer();
        }
        
        // Copy the drawing buffer to the screen.
        g.drawImage(drawingBuffer, 0, 0, null);
    }
    
    /**
     * Make the drawing buffer
     */
    private void makeDrawingBuffer() {
        drawingBuffer = createImage(getWidth(), getHeight());
    }
    
    /**
     * 
     * @return clients currently working on this whiteboard
     */
    private List<Client> getClients() {
    	synchronized (clients) {
    		return this.clients;
    	}
    }
    
    /**
     * Adds a client to the whiteboard
     * @param client client to be added to this whiteboard
     */
    public void addClient(Client client) {
    	synchronized (clients) {
    		clients.add(client);
    		System.out.println("--> Sending history to client");
    		this.loadWhiteboard(client);
    		String message = this.usersMessage();
    		System.out.println("--> Sending list of current users to client");
    		this.sendMessageToAll(message);
    	}
    }
    
    /**
     * Remove a client from the whiteboard
     * @param client client to be removed from this whiteboard
     */
    public void removeClient(Client client) {
    	synchronized (clients) {
    		clients.remove(client);
    		String message = this.usersMessage();
    		this.sendMessageToAll(message);
    	}
    }
    
    /**
     * 
     * @return a String of the form "users a b c ... n", where a, b, c, ... , n are the current clients
     * interacting with this whiteboard
     */
    private String usersMessage() {
    	StringBuilder message = new StringBuilder();
		message.append("users");
		for (Client c : this.clients) {
			message.append(" " + c.getUsername());
		}
		return message.toString();
    }
    
    /**
     * Sends a String message to all current clients interacting with this whiteboard
     * @param message message to send
     */
    private void sendMessageToAll(String message) {
    	synchronized (clients) {
    		for (Client client : this.clients) {
        		client.sendMessage(message);
        	}
    	}
    }
    
    /**
     * Draws a line onto our whiteboard image.
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @param width pixel thickness of line
     * @param r red value [0-255]
     * @param g green value [0-255]
     * @param b blue value [0-255]
     */
    private void drawLine(int x1, int y1, int x2, int y2, int width, int r, int g, int b) {
    	Graphics2D graphics = (Graphics2D) drawingBuffer.getGraphics();

		Color color = new Color(r,g,b);
        graphics.setColor(color);
        graphics.setStroke(new BasicStroke(width));
        graphics.drawLine(x1, y1, x2, y2);
        
        this.repaint();
    }
    
    /**
     * reset the whiteboard's history by saving the current Image as a BMP file and clearing the history list
     */
    //NOT USED RIGHT NOW ... WILL USE IF IMPLEMENTING BMP
    private void resetHistory() {
    	BufferedImage bi = (BufferedImage) drawingBuffer;
 
    	try {
    		ImageIO.write(bi, "BMP", bmp);
    		history.clear();
    	} catch (IOException e) {
    		// TODO Auto-generated catch block
    		e.printStackTrace();
    	}
    }
    
    //NOT USED RIGHT NOW ... WILL USE IF IMPLEMENTING BMP
    private void checkHistory() {
    	if (history.size() > 50) {
    		this.resetHistory();
    	}
    }
    
    /**
     * Adds a line to this whiteboard.
     * @param message String message encoding the line being added to this whiteboard
     */
    public void addLine(String message) {
    	synchronized (history) {
    		history.add(message);
    		String[] keys = message.split(" ");
    		System.out.println(keys.toString());
    		int x1 = Integer.parseInt(keys[1]);
    		int y1 = Integer.parseInt(keys[2]);
    		int x2 = Integer.parseInt(keys[3]);
    		int y2 = Integer.parseInt(keys[4]);
    		int width = Integer.parseInt(keys[5]); 
    		int r = Integer.parseInt(keys[6]);
    		int g = Integer.parseInt(keys[7]);
    		int b = Integer.parseInt(keys[8]);
    		//this.drawLine(x1, y1, x2, y2, width, r, g, b);
    		
    		//this.checkHistory();
    	}
    	this.sendMessageToAll(message);
    }
    
    //NOT USED RIGHT NOW ... WILL USE IF IMPLEMENTING BMP
    private void sendBMP(Client client) {
    	byte[] b = new byte[(int) bmp.length()];
    	try{
    		FileInputStream fileInputStream = new FileInputStream(bmp);
        	fileInputStream.read(b);
    	}
    	catch(Exception e) {
    		e.printStackTrace();
    	}
    	
    	//send byte[] b to client
    }
    
    /**
     * Sends the list of lines drawn on this board to a client.
     * Note: this is used to allow a new client connecting to this board to have full access to the board's history.
     * @param client client to send history to
     */
    private void sendHistory(Client client) {
    	synchronized (history) {
    		for (String message : history) {
    			client.sendMessage(message);
    		}
    	}
    }
    
    /**
     * Sends the information of the whiteboard to the client.
     * @param client client to send whiteboard info to
     */
    private void loadWhiteboard(Client client) {
    	//this.sendBMP(client);
    	this.sendHistory(client);
    }
}
