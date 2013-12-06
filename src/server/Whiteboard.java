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
 * be updated to reflect the changes.
 */

public class Whiteboard extends JPanel{
	
	private Image drawingBuffer; //
	private List<Client> clients;
	private List<String> history;
	private File bmp = new File("./././savedImages/CanvasImage.BMP");
	
	/**
     * Make a whiteboard.
     */
    public Whiteboard() {
    	this.setPreferredSize(new Dimension(800, 600));
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
    }
    
    private List<Client> getClients() {
    	synchronized (clients) {
    		return this.clients;
    	}
    }
    
    public void addClient(Client client) {
    	synchronized (clients) {
    		clients.add(client);
    		this.loadWhiteboard(client);
    		String message = this.usersMessage();
    		this.sendMessageToAll(message);
    	}
    }
    
    
    public void removeClient(Client client) {
    	synchronized (clients) {
    		clients.remove(client);
    		String message = this.usersMessage();
    		this.sendMessageToAll(message);
    	}
    }
    
    private String usersMessage() {
    	StringBuilder message = new StringBuilder();
		message.append("users");
		for (Client c : this.clients) {
			message.append(" " + c.getUsername());
		}
		return message.toString();
    }

    private void sendMessageToAll(String message) {
    	synchronized (clients) {
    		for (Client client : this.clients) {
        		client.sendMessage(message);
        	}
    	}
    }
    
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
    
    private void checkHistory() {
    	if (history.size() > 50) {
    		this.resetHistory();
    	}
    }
    
    private void addLine(String message) {
    	synchronized (history) {
    		history.add(message);
    		
    		String[] keys = message.split(" ");
    		int x1 = Integer.getInteger(keys[1]);
    		int y1 = Integer.getInteger(keys[2]);
    		int x2 = Integer.getInteger(keys[3]);
    		int y2 = Integer.getInteger(keys[4]);
    		int width = Integer.getInteger(keys[5]); 
    		int r = Integer.getInteger(keys[6]);
    		int g = Integer.getInteger(keys[7]);
    		int b = Integer.getInteger(keys[8]);
    		this.drawLine(x1, y1, x2, y2, width, r, g, b);
    		
    		this.checkHistory();
    	}
    	this.sendMessageToAll(message);
    }
    
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
    
    private void sendHistory(Client client) {
    	synchronized (history) {
    		for (String message : history) {
    			client.sendMessage(message);
    		}
    	}
    }
    
    private void loadWhiteboard(Client client) {
    	this.sendBMP(client);
    	this.sendHistory(client);
    }
}
