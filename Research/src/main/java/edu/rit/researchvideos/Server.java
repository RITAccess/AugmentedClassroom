
package edu.rit.researchvideos;
import java.awt.AWTException;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import java.net.UnknownHostException;

public class Server {
	
	int desiredPort = 5718;
	ServerSocket serverSocket;
	
	DataInputStream socketReader;
	boolean isRunning = true;
    private PlayerPanel[] playerPanels;
	
	public Server(PlayerPanel[] playerPanels) throws IOException {
        this.playerPanels = playerPanels;
		boolean socketFound = false;
		
		while(!socketFound){
			try {
				serverSocket = new ServerSocket(desiredPort);
				socketFound = true;
			} catch (BindException e){
				desiredPort++;
			}
		}
		
		System.out.println("IPAddress: " + getIp());
		System.out.println("Port: " + desiredPort);
	}
	
	public void start() throws IOException, AWTException {
		Socket activeSocket;
		while (isRunning) {
			activeSocket = serverSocket.accept();
			new Thread(new Session(activeSocket,playerPanels)).start();
		}
	}


	public static String getIp(){
//	    String ipAddress = null;
//	    Enumeration<NetworkInterface> net = null;
//	    try {
//	        net = NetworkInterface.getNetworkInterfaces();
//	    } catch (SocketException e) {
//	        throw new RuntimeException(e);
//	    }
//
//	    while(net.hasMoreElements()){
//	        NetworkInterface element = net.nextElement();
//	        Enumeration<InetAddress> addresses = element.getInetAddresses();
//	        while (addresses.hasMoreElements()){
//	            InetAddress ip = addresses.nextElement();
//	            if (ip instanceof Inet4Address){
//	                if (ip.isSiteLocalAddress()){
//	                    ipAddress = ip.getHostAddress();
//	                }
//
//	            }
//
//	        }
//	    }
//	    return ipAddress;
		try{
		InetAddress IP= InetAddress.getLocalHost();
		return IP.getHostAddress();}catch(Exception e){
			return null;
		}
	} 

}