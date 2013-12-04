package canvas;

import java.awt.Dimension;

import javax.swing.GroupLayout;
import javax.swing.JPanel;

public class ButtonBar extends JPanel {
	public ButtonBar(EraseButton eraseButton) {
		setPreferredSize(new Dimension(60,60));
		GroupLayout layout = new GroupLayout(this);
		setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup().addComponent(eraseButton, 60, 60, 60));
		layout.setVerticalGroup(layout.createSequentialGroup().addComponent(eraseButton, 60, 100, 100));
	}
}
