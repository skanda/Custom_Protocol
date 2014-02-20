package edu.usc.anrg.ee579.diagnostic;


import java.io.DataInputStream;
import java.io.DataOutputStream;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.TreeMap;

public class Server {

	public static void main(String[] args) {
		try{

			int port = 9777;
			ServerSocket serverSocket = new ServerSocket(port);
			System.out.println("Server Started and listening to the port 9777");

			String []values;
			String sendmessage = "INVALID";
			String temp="";
			boolean flag = true;

			Map<String, String> userval = new TreeMap<String, String>();

			userval.put("34", "Somename.txt");
			userval.put("67", "Another.dat");


			//Server is running always. This is done using this while(true) loop
			while(flag) {
				//Reading the message from the client

				Socket server = serverSocket.accept();
				DataInputStream in = new DataInputStream(server.getInputStream());

				values = in.readUTF().split(",");

				DataOutputStream out = new DataOutputStream(server.getOutputStream());
				
				/*Classification of messages*/
				if(values[3].equalsIgnoreCase("HELLO"))
					sendmessage = values[3];
				
				/*For LIST message, server sends back the list of files present in its database*/
				else if(values[3].equalsIgnoreCase("LIST")){
					for(Map.Entry<String,String> entry : userval.entrySet()) {
						String key = entry.getKey();
						String value = entry.getValue();
						temp+=","+key+","+value;
					}
					sendmessage = "LIST"+temp+"\n";
				}

				/*GET message fetches the file requested by the client*/
				else if(values[3].equalsIgnoreCase("GET")){
					System.out.println("In GET");
					for(Map.Entry<String,String> entry : userval.entrySet()) {
						if(entry.getKey().equalsIgnoreCase(values[4]))
							sendmessage = values[3]+","+entry.getKey()+","+entry.getValue()+"," +
									"A small piece of text from the file! Have a nice time! :)";
					}
				}

				/*END message terminates connection by setting flag to false and breaking out of while loop*/
				else if(values[3].equalsIgnoreCase("END")){
					sendmessage = "Connection Terminated";
					flag =false;
				}

				else {
					sendmessage = "INVALID";
				}

				out.writeUTF(sendmessage);
				temp = "";
				server.close();

			}
			serverSocket.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
