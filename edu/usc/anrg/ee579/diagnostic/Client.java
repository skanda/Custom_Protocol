package edu.usc.anrg.ee579.diagnostic;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;


public class Client{

	public static void main(String [] args){

		System.out.println("Enter the number to select the type of message");
		System.out.println("1 => HELLO Message");
		System.out.println("2 => LIST Message");
		System.out.println("3 => END Message");

		/*Input arguments to the client*/
		String serverName = args[0];
		int port = Integer.parseInt(args[1]);
		int typeOfMessage = Integer.parseInt(args[2]);
		
		Map<Integer, String> userval = new TreeMap<Integer, String>();

		/*Message related variables*/
		int api_type = 12;
		int msg_length;
		int total_length;
		String message;
		String send_message;
		String tempvalue;

		try{
			System.out.println("Connecting to server "+ " on port " + port);
			Socket client = new Socket(serverName, port);

			OutputStream outToServer = client.getOutputStream();
			DataOutputStream out = new DataOutputStream(outToServer);
			InputStream inFromServer = client.getInputStream();;
			DataInputStream in = new DataInputStream(inFromServer);

			switch (typeOfMessage){

			case 1: /*Sends HELLO message to the server*/
				try{
					message = "HELLO";
					msg_length = message.length();
					total_length = api_type + msg_length;

					send_message = api_type+","+total_length+","+msg_length+","+message;
					System.out.println("Client sent : "+ send_message);
					out.writeUTF(send_message);
					System.out.println("Server sent : " + in.readUTF());

				}
				catch(IOException e){
					e.printStackTrace();
				}
				break;

			case 2:	/*Sends LIST message to the server, after receiving list of files, a file is chosen and requested to the server through GET message*/
				try{
					message = "LIST";
					int count =1;
					msg_length = message.length();
					total_length = api_type + msg_length;

					send_message = api_type+","+total_length+","+msg_length+","+message;
					System.out.println("Client sent : "+ send_message);
					out.writeUTF(send_message);
					
					tempvalue = in.readUTF().toString();
					String[]  values = tempvalue.split(",");

					if(values[0].equalsIgnoreCase("LIST")){
						System.out.println("\nServer sent : ");
						for(int i=1;i<values.length - 1;i=i+2){
							userval.put(count,values[i]);
							System.out.println(count + "." + values[i+1]);
							count++;
						}
					}
					else
						System.out.println("Not the right reply!");


					client.close();
				
					message = "GET";
					msg_length = message.length();
					total_length = api_type + msg_length;
					int i=0;
					
					/*A new connection is created here only for GET as connection is closed by client & server after server replies to the LIST message */
					Socket clientNew = new Socket(serverName, port);

					OutputStream outToServerNew = clientNew.getOutputStream();
					DataOutputStream outNew =
							new DataOutputStream(outToServerNew);

					Scanner reader = new Scanner(System.in);
					System.out.println("Enter the no. to choose the file");
					i = reader.nextInt();

					send_message = api_type+","+total_length+","+msg_length+","+message+","+userval.get(i).replaceAll("\\s+","");
					System.out.println("Client Sent : "+ send_message);
					outNew.writeUTF(send_message);
					
					inFromServer = clientNew.getInputStream();
					in = new DataInputStream(inFromServer);
					tempvalue = in.readUTF().toString();
					values = tempvalue.split(",");
					System.out.println(values[3]);
					reader.close();
					clientNew.close();
				}
				catch(IOException e){
					e.printStackTrace();
				}
				break;
			
			case 3:	/*END message is sent to terminate the connection*/
				try{
					message = "END";
					msg_length = message.length()+1;
					total_length = api_type + msg_length;
					send_message = api_type+","+total_length+","+msg_length+","+message;
					System.out.println("Client sent : "+ send_message);
					out.writeUTF(send_message);
					System.out.println("Server sent : " + in.readUTF());
				}

				catch(IOException e){
					e.printStackTrace();
				}
				break;

			default: /*Default option to handle invalid case*/
				try{
					message = "A";
					msg_length = message.length();
					total_length = api_type + msg_length;
					send_message = api_type+","+total_length+","+msg_length+","+message;
					System.out.println("Client sent : "+ send_message);
					out.writeUTF(send_message);
					System.out.println("Server sent : " + in.readUTF());
				}
				catch(IOException e){
					e.printStackTrace();
				}
				break;
			}
			client.close();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}

	}
}
