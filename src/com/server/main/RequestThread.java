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

public class RequestThread extends Thread {

	
	Socket clientsocket;
	public RequestThread(Socket clientsocket) {
		
		this.clientsocket = clientsocket;
		this.start();
	}
	
	@Override
	public void run() {
		try {
			InputStream str = clientsocket.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(str));
			String reqstr = br.readLine();
			String getstr = reqstr.substring(reqstr.indexOf("GET")+3, reqstr.lastIndexOf("HTTP")).trim();

			handlereq(getstr, clientsocket.getOutputStream());

			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}	
	
	public void handlereq(String requeststr, OutputStream os) throws IOException{
		File f = new File(StartServer.filelocation+requeststr);
		if(f.exists()){
			if(f.isDirectory()){
				File[] farr = f.listFiles();
				StringBuilder htmlstringbuilder = new StringBuilder();
				htmlstringbuilder.append("<html><body>");
				
				for (int i = 0; i < farr.length; i++) {
					File file = farr[i];
					htmlstringbuilder.append("<a href="+file.getAbsolutePath().replace(StartServer.filelocation+"\\", "").replace("\\", "/")+">"+file.getName()+"</a><br>");
				}
				
				htmlstringbuilder.append("</body></html>");
				
				os.write("HTTP/1.1 200 OK \r\n".getBytes());
				os.write("Content-Type:text/html \r\n".getBytes());
				os.write("\r\n".getBytes());
				os.write(htmlstringbuilder.toString().getBytes());
				os.flush();
				os.close();
			}else if(f.isFile()){
				FileInputStream fs = new FileInputStream(f);

			    final byte[] buffer = new byte[1024];
			    int count = 0;
			    String conttype = URLConnection.guessContentTypeFromName(f.getName());
			    os.write("HTTP/1.1 200 OK \r\n".getBytes());
				os.write(("Content-Type:"+conttype+" \r\n").getBytes());
				os.write("\r\n".getBytes());
				
			    while ((count = fs.read(buffer)) >= 0) {
			        os.write(buffer, 0, count);
			    }
			    os.flush();
			    fs.close();
			    os.close();
			}
		}else{
			os.write("HTTP/1.1 404 Not Found \r\n".getBytes());
			os.write("Content-Type:text/html \r\n".getBytes());
			os.write("\r\n".getBytes());
			os.write(("<!DOCTYPE html><html lang=en>  <meta charset=utf-8>  <meta name=viewport content='initial-scale=1, minimum-scale=1, width=device-width'>  <title>Error 404 (Not Found)!!1</title>  <style>    *{margin:0;padding:0}html,code{font:15px/22px arial,sans-serif}html{background:#fff;color:#222;padding:15px}body{margin:7% auto 0;max-width:390px;min-height:180px;padding:30px 0 15px}* > body{background:url(/robot.png) 100% 5px no-repeat;padding-right:205px}p{margin:11px 0 22px;overflow:hidden}ins{color:#777;text-decoration:none}a img{border:0}@media screen and (max-width:772px){body{background:none;margin-top:0;max-width:none;padding-right:0}}#logo{background:url(/logo_sm_2.png) no-repeat}@media only screen and (min-resolution:192dpi){#logo{background:url(/logo_sm_2_hr.png) no-repeat 0% 0%/100% 100%;-moz-border-image:url(/logo_sm_2_hr.png) 0}}@media only screen and (-webkit-min-device-pixel-ratio:2){#logo{background:url(/logo_sm_2_hr.png) no-repeat;-webkit-background-size:100% 100%}}#logo{display:inline-block;height:55px;width:150px}  </style>  <a href=/><span id=logo aria-label=Google></span></a>  <p><b>404.</b> <ins>That is an error.</ins>  <p>The requested URL "+requeststr+" was not found on this server.  <ins>That is all we know.</ins>").getBytes());
			os.flush();
			os.close();
			}
	}
}


