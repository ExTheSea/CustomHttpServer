package com.server.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;

import com.sun.net.httpserver.Headers;
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
	public String root = "D:/Neuer Ordner";
	
	public class MyHandler implements com.sun.net.httpserver.HttpHandler{

		@Override
		public void handle(com.sun.net.httpserver.HttpExchange exchange)
				throws IOException {
			String response;
			String responsetop;
			String responsebot;
		
			
            OutputStream os = exchange.getResponseBody();
            
			
			if(!exchange.getRequestURI().getPath().equals("/")){
//				byte[] barr = null;
//				Path path;
//					barr = Files.readAllBytes(path = Paths.get(root + exchange.getRequestURI()));
//					os.write(barr);
				
				File file = new File(root + exchange.getRequestURI());
				exchange.sendResponseHeaders(200, file.length());
				Headers h = exchange.getResponseHeaders();
				h.set("Accept-Ranges", "bytes");
				
			    FileInputStream fs = new FileInputStream(file);

			    final byte[] buffer = new byte[1024];
			    int count = 0;
			    while ((count = fs.read(buffer)) >= 0) {
			        os.write(buffer, 0, count);
			    }
			    os.flush();
			    fs.close();
			    os.close();
				
//				
//				PrintWriter out = new PrintWriter(os, true); 
//				File file = new File(root + exchange.getRequestURI());
//				if( !file.exists()){
//				  out.write("HTTP 404"); // the file does not exists  
//				}
//				FileReader fr = new FileReader(file);
//				BufferedReader bfr = new BufferedReader(fr);
//				String line;
//				while((line = bfr.readLine()) != null){
//					System.out.println(line);
//				  out.write(line);
//				}
//
//				bfr.close();
//				out.close();  
			}else{
			responsetop = "<html>" +
					"<head>" +
					"</head>" +
					"<body>";
						
					
			responsebot ="</body>" +
					"</html>";
			folderstr ="";
			filepatharr = new ArrayList<String>();
			listFilesForFolder(new File(root));

			StringBuilder build = new StringBuilder();
			for(Iterator<String> i = filepatharr.iterator(); i.hasNext();){
				String filestr = i.next();
				build.append("<a href='"+filestr.replace(root, "")+"'> "+filestr.replace(root, "")+"</a><br>");
			}
			
			response = responsetop+build.toString()+responsebot;
			exchange.sendResponseHeaders(200, response.length());
			os.write(response.getBytes());
			os.close();
			}

		}
	}
	
	
	public void listFilesForFolder(final File folder) {
		
	    for (final File fileEntry : folder.listFiles()) {
	        if (fileEntry.isDirectory()) {
	        	folderstr = fileEntry.getName();
	            listFilesForFolder(fileEntry);
	        } else {
	            filepatharr.add(fileEntry.getAbsolutePath().replace("\\", "/"));
	        }
	    }
	}
}
