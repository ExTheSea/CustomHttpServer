package com.server.main;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.net.InetSocketAddress;

import sun.misc.IOUtils;


import com.sun.net.httpserver.HttpServer;

public class InitServer {

	public static void main(String[] args) throws IOException {
		HttpServer server = HttpServer.create(new InetSocketAddress(8080),0);	
		server.createContext("/", new MyHandler());
		server.setExecutor(null);
		server.start();
	}

	
	static class MyHandler implements com.sun.net.httpserver.HttpHandler{

		@Override
		public void handle(com.sun.net.httpserver.HttpExchange exchange)
				throws IOException {
			String response = "This is the response";
			exchange.sendResponseHeaders(200, response.length());
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
		}
	}
		
}
