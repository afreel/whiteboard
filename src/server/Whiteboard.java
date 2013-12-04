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
import java.io.File;
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

//Whiteboard represents a whiteboard on the server
public class Whiteboard extends JPanel{
	
	private Image drawingBuffer;
	private List<Client> clients;
	private List<String> history;
	private File bmp = new File("c:\\CanvasImage.BMP");
	
	/**
     * Make a whiteboard.
     * @param width width in pixels
     * @param height height in pixels
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
    	return this.clients;
    }
    
    private void addClient(Client client) {
    	clients.append(client);
    }
    
    private void removeClient(Client client) {
    	clients.remove(client);
    }

    private void sendMessageToAll(String message) {
    	for (Client client : this.clients) {
    		client.sendMessage(message);
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
    	synchronized (history) {
    		BufferedImage bi = (BufferedImage) drawingBuffer;
        	try {
    			ImageIO.write(bi, "BMP", bmp);
    			history.clear();
    		} catch (IOException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
    	}
    }
    
    private void checkHistory() {
    	synchronized (history) {
    		if (history.size() > 50) {
    			this.resetHistory();
    		}
    	}
    }
    
    private void addLine(String message) {
    	[int x1, int y1, int x2, int y2, int width, int r, int g, int b] 
    	String[] keys = message.split(" ");
    	int x1 = (int) keys[0];
    }
}
