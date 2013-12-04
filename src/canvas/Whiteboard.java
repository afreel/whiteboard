package canvas;

import java.awt.Image;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

public class Whiteboard extends JPanel{
	private Image bmp;
	private List<String> eventsSinceLastBMP;
	private int maxEventSize = 100;
	private int eventCount = 0;
	private Canvas canvas;
	
	public Whiteboard(Canvas canvas) {
		this.eventsSinceLastBMP = new ArrayList<String>();
		this.canvas = canvas;
	}
	
	public synchronized void addEvent(String event) {
		if (eventCount == maxEventSize) {
			//TODO: Generate new BMP
		}
		else {
			eventsSinceLastBMP.add(event);
			eventCount += 1;
		}
	}
	
	public List<String> getEventsSinceLastBMP() {
		return this.eventsSinceLastBMP;
	}
	
	public Image getBMP() {
		return this.bmp;
	}
}
