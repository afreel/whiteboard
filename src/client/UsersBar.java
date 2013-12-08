package client;

import java.awt.Color;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class UsersBar extends JPanel{
	
	public UsersBar(List<String> users) {
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.updateUsersBar(users);
	}
	
	public void updateUsersBar(List<String> users) {
		this.removeAll();
		if (users.size() > 0) {
			for (String user : users) {
				JLabel j = new JLabel(user);
				j.setForeground(new Color(165,42,42));
				this.add(j);
			}
		}
		this.revalidate();
		this.repaint();	
	}
}
