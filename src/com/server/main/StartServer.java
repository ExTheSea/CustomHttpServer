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
		if(filelocation.isEmpty()){ // Wenn noch nicht gesetzt wird der Pfad zum "files" Ordner der dort liegen sollte, wo das Programm ausgeführt wird
			File f = new File( RequestThread.class.getProtectionDomain().getCodeSource().getLocation().getPath());
			f = f.getParentFile();
			f = new File(f.getAbsolutePath()+"/files");
			filelocation = f.getAbsolutePath();
		}
		new StartServer(); //Erstellen einer Instanz dieser Klasse um nicht-statische Variablen/Methoden zu nutzen
	}
	
	public StartServer() {

		final JFrame frame = new JFrame("Server"); //Erstellen eines simplen Fensters (mit Java swing) mit einem Button um den Server zu schließen
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
 
        JButton btn = new JButton("Close Server");
        frame.getContentPane().add(btn);
        btn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					servsocket.close(); //Schließen des sockets
					frame.dispose(); //Schließen des Fensters
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
 
        frame.setSize(200, 200); //Fenster (Frame) anzeigen
        frame.pack();
        frame.setVisible(true);
        
		try {
			servsocket = new ServerSocket(8080); //Initieren des ServerSockets auf Port 8080
			checkrequest(); 
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * While-loop, der bei jeder rein kommenden Request einen RequestThread startet um diesen zu bearbeiten
	 * @throws IOException
	 */
	public void checkrequest() throws IOException{
		while (!servsocket.isClosed()) { //Loop stoppen when ServerSocket geschlossen
			try{
			Socket clientsocket = servsocket.accept();
			new RequestThread(clientsocket);
			}catch(SocketException e){}; //Exception wird geworfen, wenn socket während socket.accept() geschlossen wird
		}
	}
	
}
