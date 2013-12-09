package client;

import java.awt.Color;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;

public class UsersBar extends JPanel{
	
	public UsersBar(List<String> users) {
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.setBackground(new Color(220,220,220));
        this.setBorder(BorderFactory.createRaisedBevelBorder());
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
	
//	public void updateUsersBar(List<String> users) {
//		this.removeAll();
//		String[] usersArray = users.toArray(new String[users.size()]);
//		JList<String> usersList = new JList<String>(usersArray);
//		this.add(usersList);
//		this.revalidate();
//		this.repaint();
//	}
}
