package com.server.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URLConnection;
import java.net.URLDecoder;

public class RequestThread extends Thread {

	
	Socket clientsocket;
	public RequestThread(Socket clientsocket) {
		
		this.clientsocket = clientsocket;
		this.start(); //Starten des Threads um die Request enthalten in der clientsocket Variable zu bearbeiten
	}
	
	@Override
	public void run() {
		try {
			InputStream str = clientsocket.getInputStream(); //Http Request Header und Body aus socket als InputStream
			BufferedReader br = new BufferedReader(new InputStreamReader(str)); //BufferedReader um InputStream zu lesen
			String reqstr = br.readLine(); //Lesen der ersten Zeile, in der der GET pfad liegt, wie z.B. "GET /somefile.txt HTTP/1.1"
			if(reqstr != null){
				String getstr = reqstr.substring(reqstr.indexOf("GET")+3, reqstr.lastIndexOf("HTTP")).trim(); //Heraus lesen des Request-Pfads
				handlereq(getstr, clientsocket.getOutputStream());
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}	
	
	
	/**
	 * Genaue Behandlung des Requests.
	 * @param requeststr String, der auf ein Verzeichnis oder Datei zielt, z.B. "/somefolder/somehtmlfile.html"
	 * @param os OutputStream, in den die Response geschrieben wird
	 * @throws IOException
	 */
	public void handlereq(String requeststr, OutputStream os) throws IOException{
//		System.out.println(URLDecoder.decode(StartServer.filelocation+requeststr,"utf-8"));
		File f = new File(URLDecoder.decode(StartServer.filelocation+requeststr,"utf-8")); //Instanz von File, die auf den tatsächlichen Ordner/Datei auf dem System innerhalb des files Ordner zeigt
		if(f.exists()){
			if(f.isDirectory()){ //Wenn es sich um ein Verzeichnis handelt
				File[] farr = f.listFiles(); //Auflisten aller Dateien und Ordner innerhalb des Verzeichnis
				StringBuilder htmlstringbuilder = new StringBuilder(); //Zum Aufbau des Htmls zur Anzeige der Dateien und Ordner
				htmlstringbuilder.append("<html><body>"); 
				for (int i = 0; i < farr.length; i++) { //Einen Link pro Datei/Ordner zu dem String hinzufügen
					File file = farr[i];
					String str = file.getAbsolutePath().replace(StartServer.filelocation+"\\", "").replace("\\", "/");
					htmlstringbuilder.append("<a href='"+str+"'>"+file.getName()+"</a><br>");
				}
				
				htmlstringbuilder.append("</body></html>");
				
				os.write("HTTP/1.1 200 OK \r\n".getBytes()); //Standard Header für HTml Dateien um Probleme in manchen Browsern zu umgehen
				os.write("Content-Type:text/html \r\n".getBytes());
				os.write("\r\n".getBytes()); // \r\n fügt einen Absatz ein. Nach dem Header muss ein doppelter Absatz eingefügt werden um den Content abzutrennen
				os.write(htmlstringbuilder.toString().getBytes()); //Schreiben des Contents zum Outputstream
				os.flush(); 
				os.close(); //Flush + Close um Response zum Client zu schicken
			}else if(f.isFile()){ //Wenn es sich um eine File handelt
				FileInputStream fs = new FileInputStream(f); 

			    final byte[] buffer = new byte[1024];
			    int count = 0;
			    
			    String conttype = URLConnection.guessContentTypeFromName(f.getName()); //MIME Type der Datei herrausfinden
			    
			    os.write("HTTP/1.1 200 OK \r\n".getBytes()); //Header hinzufügen
				os.write(("Content-Type:"+conttype+" \r\n").getBytes());
				os.write("\r\n".getBytes());
				
			    while ((count = fs.read(buffer)) >= 0) {
			        os.write(buffer, 0, count); //Schreiben der Datei zum Outputstream
			    }
			    os.flush();
			    fs.close();
			    os.close();
			}
		}else{ //Wenn Datei nicht existiert
			os.write("HTTP/1.1 404 Not Found \r\n".getBytes()); //Zurückgeben eines Standard 404 Not Found Headers und eine Html Seite. 
			os.write("Content-Type:text/html \r\n".getBytes());
			os.write("\r\n".getBytes());
			os.write(("<!DOCTYPE html><html lang=en>  <meta charset=utf-8>  <meta name=viewport content='initial-scale=1, minimum-scale=1, width=device-width'>  <title>Error 404 (Not Found)!!1</title>  <style>    *{margin:0;padding:0}html,code{font:15px/22px arial,sans-serif}html{background:#fff;color:#222;padding:15px}body{margin:7% auto 0;max-width:390px;min-height:180px;padding:30px 0 15px}* > body{background:url(/robot.png) 100% 5px no-repeat;padding-right:205px}p{margin:11px 0 22px;overflow:hidden}ins{color:#777;text-decoration:none}a img{border:0}@media screen and (max-width:772px){body{background:none;margin-top:0;max-width:none;padding-right:0}}#logo{background:url(/logo_sm_2.png) no-repeat}@media only screen and (min-resolution:192dpi){#logo{background:url(/logo_sm_2_hr.png) no-repeat 0% 0%/100% 100%;-moz-border-image:url(/logo_sm_2_hr.png) 0}}@media only screen and (-webkit-min-device-pixel-ratio:2){#logo{background:url(/logo_sm_2_hr.png) no-repeat;-webkit-background-size:100% 100%}}#logo{display:inline-block;height:55px;width:150px}  </style>  <a href=/><span id=logo aria-label=Google></span></a>  <p><b>404.</b> <ins>That is an error.</ins>  <p>The requested URL "+requeststr+" was not found on this server.  <ins>That is all we know.</ins>").getBytes());
			os.flush();
			os.close();
			}
	}
}


