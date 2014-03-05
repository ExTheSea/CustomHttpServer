package com.server.main;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Iterator;

import com.sun.net.httpserver.HttpServer;

public class InitServer {

	public static void main(String[] args) throws IOException {
		new InitServer();
	}
	
	public InitServer() throws IOException {
		HttpServer server = HttpServer.create(new InetSocketAddress(8080),0);	
		server.createContext("/", new MyHandler());
		server.setExecutor(null);
		server.start();
	}

	
	public String folderstr ="";
	public ArrayList<String> filepatharr;
	
	public class MyHandler implements com.sun.net.httpserver.HttpHandler{

		@Override
		public void handle(com.sun.net.httpserver.HttpExchange exchange)
				throws IOException {
			String response;
			String responsetop;
			String responsebot;
		
			if(!exchange.getRequestURI().getPath().equals("/")){
				response="File";
			}else{
			responsetop = "<html>" +
					"<head>" +
					"</head>" +
					"<body>";
						
					
			responsebot ="</body>" +
					"</html>";
			folderstr ="";
			filepatharr = new ArrayList<String>();
			listFilesForFolder(new File("D:/Neuer Ordner"));

			StringBuilder build = new StringBuilder();
			for(Iterator<String> i = filepatharr.iterator(); i.hasNext();){
				String filestr = i.next();
				build.append("<a href='"+filestr+"'> "+filestr+"</a><br>");
			}
			response = responsetop+build.toString()+responsebot;
			}
			System.out.println(response);
			exchange.sendResponseHeaders(200, response.length());
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
		}
	}
	
	
	public void listFilesForFolder(final File folder) {
		
	    for (final File fileEntry : folder.listFiles()) {
	        if (fileEntry.isDirectory()) {
	        	folderstr = fileEntry.getName();
	            listFilesForFolder(fileEntry);
	        } else {
//	            System.out.println(folderstr + "\\" + fileEntry.getName());
	            filepatharr.add(folderstr + "\\" + fileEntry.getName());
	        }
	    }
	}
}
