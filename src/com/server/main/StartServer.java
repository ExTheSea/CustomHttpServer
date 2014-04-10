package com.server.main;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.URLDecoder;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextArea;

public class StartServer {

	static ServerSocket servsocket;
	
	static String filelocation = "";
	static StartServer ser;
	private JTextArea txt_log;
	
	public static void main(String[] args) throws UnsupportedEncodingException {
		if(filelocation.isEmpty()){ // Wenn noch nicht gesetzt wird der Pfad zum "files" Ordner der dort liegen sollte, wo das Programm ausgeführt wird
			File f = new File( RequestThread.class.getProtectionDomain().getCodeSource().getLocation().getPath());
			f = f.getParentFile();
			f = new File(f.getPath()+"/files");
			filelocation = URLDecoder.decode(f.getAbsolutePath(),"utf-8");
		}
		new StartServer(); //Erstellen einer Instanz dieser Klasse um nicht-statische Variablen/Methoden zu nutzen
	}
	
	public StartServer() {

		ser = this;
		
		final JFrame frame = new JFrame("Server"); //Erstellen eines simplen Fensters (mit Java swing) mit einem Button um den Server zu schließen
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
 
        JButton btn = new JButton("Close Server");
        btn.setSize(100, 50);
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
 
       	txt_log = new JTextArea("Logging");
        txt_log.setSize(300, 400);
        txt_log.setAlignmentY(50);
        frame.getContentPane().add(txt_log);
        
        
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
	
	public void logging(String message){
		txt_log.setText(txt_log.getText()+"\n" + message);
	}
	
	static void log(String message){
		ser.logging(message);
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
