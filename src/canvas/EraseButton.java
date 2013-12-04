package canvas;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JToggleButton;

public class  EraseButton extends JToggleButton {
	
	public EraseButton() {
		this.setText("Erase");
		this.addMouseListener(new MouseListener() {
			public void mouseClicked(MouseEvent e) {
				if (isSelected()) {setText("Draw");}
				else {setText("Erase");}
			}
			public void mousePressed(MouseEvent e) {}
			public void mouseReleased(MouseEvent e) {}
			public void mouseEntered(MouseEvent e) {}
			public void mouseExited(MouseEvent e) {}
		});
	}
}
