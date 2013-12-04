package canvas;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;

public class WhiteboardModel {
	
	private Socket socket;
	
	public void drawLine(String line) {
		try{
			socket = new Socket("localhost", 4444);
			
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
	        
	        try {
	            out.write(line);
	        } finally {
	            out.close();
	            in.close();
	        }
		}
		catch(Exception e){
			e.printStackTrace();
		}
			
	}	
	
}
