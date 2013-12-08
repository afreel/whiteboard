package client;

import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class UsersBar extends JPanel{
	
	public UsersBar(List<String> users) {
		this.updateUsers(users);
	}
	
	public void updateUsers(List<String> users) {
		this.removeAll();
		for (String user : users) {
			this.add(new JLabel(user));
		this.repaint();
		}
	}
}
