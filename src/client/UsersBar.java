package client;

import java.awt.Color;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;

public class UsersBar extends JPanel{
	private final HashMap<String, JLabel> usersLabelMap =  new HashMap<String, JLabel>();
	
	public UsersBar(List<String> users) {
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.setBackground(new Color(220,220,220));
        this.setBorder(BorderFactory.createRaisedBevelBorder());
		this.loadUsersBar(users);
	}

	/**
	 * Populate this with all the users currently connected to this client's whiteboard
	 * @param users names of all users currently connected
	 */
	public void loadUsersBar(List<String> users) {
		this.removeAll();
		if (users.size() > 0) {
			for (String user : users) {
				addNewUser(user, true);
			}
		}
		this.revalidate();
		this.repaint();
	}
	
	/**
	 * Updates this with a newly connected user
	 * @param user name of the newly connected user
	 * @param inloadUsersBarCall true if this was called in loadUsersBar. Stops repeated revalidation and repainting
	 * 							 for each connected user, so that those two processes can instead be performed once all users
	 * 							 are added, at the end of the loadUsersBar call. false if called anywhere else, so updated
	 * 							 usersbar is immediately displayed.
	 */
	public void addNewUser(String user, boolean inloadUsersBarCall) {
		System.out.println("Adding new user " + user);
		JLabel j = new JLabel(user);
		j.setForeground(new Color(150,150,150));
		this.add(j);
		usersLabelMap.put(user, j);
		if(!inloadUsersBarCall) {
			this.revalidate();
			this.repaint();
		}
	}

	/**
	 * Removes a disconnected user from this
	 * @param user name of disconnected user
	 */
	public void removeUser(String user) {
		
		this.remove(usersLabelMap.get(user));
		Iterator<Entry<String, JLabel>> iter = usersLabelMap.entrySet().iterator();
		while (iter.hasNext()) {
			if(iter.next().getKey().equals(user)) {iter.remove();}
		}
		this.revalidate();
		this.repaint();
	}
	
	/**
	 * When a user draws, change the color of their name displayed in this usersbar 
	 * to the color they are coloring in.
	 * @param user the user drawing
	 * @param r red component of the color they are drawing in
	 * @param g green component of the color they are drawing in
	 * @param b blue component of the color they are drawing in
	 */
	public void updateUserColor(String user, int r, int g, int b) {
		System.out.println(usersLabelMap.toString());
		usersLabelMap.get(user).setForeground(new Color(r,g,b));
	}
	
}
