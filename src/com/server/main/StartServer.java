package com.server.main;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

import javax.swing.JButton;
import javax.swing.JFrame;

public class StartServer {

	static ServerSocket servsocket;
	
	static String filelocation = "";
	
	public static void main(String[] args) {
		if(filelocation.isEmpty()){
			File f = new File( RequestThread.class.getProtectionDomain().getCodeSource().getLocation().getPath());
			f = f.getParentFile();
			f = new File(f.getAbsolutePath()+"/files");
			filelocation = f.getAbsolutePath();
		}
		new StartServer();
	}
	
	public StartServer() {

		final JFrame frame = new JFrame("Server");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
 
        JButton btn = new JButton("Close Server");
        frame.getContentPane().add(btn);
        btn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					servsocket.close();
					frame.dispose();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
 
        frame.setSize(200, 200);
        frame.pack();
        frame.setVisible(true);
        
		try {
			servsocket = new ServerSocket(8080);
			checkrequest();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public void checkrequest() throws IOException{
		while (!servsocket.isClosed()) {
			try{
			Socket clientsocket = servsocket.accept();
			new RequestThread(clientsocket);
			}catch(SocketException e){};
		}
	}
	
}
