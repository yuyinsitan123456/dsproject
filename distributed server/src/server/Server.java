package server;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
<<<<<<< HEAD
import java.net.ServerSocket;
import java.net.Socket;
=======
>>>>>>> 1fda8c464f619bf4e55479fa196a32b9e809799c

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import org.json.simple.JSONObject;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

import server.config.Config;
import server.config.ConfigLoader;
import server.state.ManagingThread;
import server.state.Message;
import server.state.MessageSendThread;
import server.state.ServerState;
import server.tools.ComLineValues;

public class Server  {

	public static void main(String[] args) throws IOException {

		String serverid = null;
<<<<<<< HEAD
		//		String serversAddress="http://localhost";
		int serversPort = 4443;
		int coordinationPort = 3333;
		String keyFilepath = null;
		String ssl="all";
		//		String trustFilepath = null;
=======
//		String serversAddress="http://localhost";
		int serversPort = 4443;
		int coordinationPort = 3333;
		String keyFilepath = null;
//		String trustFilepath = null;
>>>>>>> 1fda8c464f619bf4e55479fa196a32b9e809799c

		String serversConf = null;
		ComLineValues comLineValues = new ComLineValues();
		CmdLineParser parser = new CmdLineParser(comLineValues);

		try {
			parser.parseArgument(args);
			serverid = comLineValues.getServerid();
			serversConf = comLineValues.getServersConf();
			keyFilepath = comLineValues.getKeyFilepath();
<<<<<<< HEAD
			ssl=comLineValues.getSsl();
			//			trustFilepath = comLineValues.getTrustFilepath();
=======
//			trustFilepath = comLineValues.getTrustFilepath();
>>>>>>> 1fda8c464f619bf4e55479fa196a32b9e809799c
		} catch (CmdLineException ce) {
			ce.printStackTrace();
		}

		// Read configuration in the config file
		Config config = ConfigLoader.loadConfig(serverid,serversConf);
<<<<<<< HEAD

		//		Config config = ConfigLoader.loadConfig(serverid,serversAddress,serversPort,coordinationPort);
		ServerSocket listeningServerSocket = null;
		ServerSocket listeningClientSocket =null;
=======
		
//		Config config = ConfigLoader.loadConfig(serverid,serversAddress,serversPort,coordinationPort);
		SSLServerSocket listeningServerSocket = null;
		SSLServerSocket listeningClientSocket =null;
>>>>>>> 1fda8c464f619bf4e55479fa196a32b9e809799c
		//Specify the keystore details (this can be specified as VM arguments as well)
		//the keystore file contains an application's own certificate and private key
		//keytool -genkey -keystore <keystorename> -keyalg RSA
		System.setProperty("javax.net.ssl.keyStore",keyFilepath);
		System.setProperty("javax.net.ssl.trustStore",keyFilepath);
		//Password to access the private key from the keystore file
<<<<<<< HEAD
		System.setProperty("javax.net.ssl.keyStorePassword","888888");
		System.setProperty("javax.net.ssl.trustStorePassword", "888888");

		// Enable debugging to view the handshake and communication which happens between the SSLClient and the SSLServer
		System.setProperty("javax.net.debug","none");
		SSLSocketFactory sslsocketfactory=null;
		Socket sslsocket =null;
		try {
			serversPort =config.getClientsPort();
			coordinationPort = config.getCoordinationPort();
			// Create a server socket listening on given port
			//Create SSL server socket
			SSLServerSocketFactory sslserversocketfactory = (SSLServerSocketFactory) SSLServerSocketFactory
					.getDefault();

			if("all".equals(ssl)){
				listeningServerSocket = (SSLServerSocket) sslserversocketfactory.createServerSocket(coordinationPort);
				listeningClientSocket = (SSLServerSocket) sslserversocketfactory.createServerSocket(serversPort);
			}else{
				listeningServerSocket =new ServerSocket(coordinationPort);
				listeningClientSocket =new ServerSocket(serversPort);
			}
			ServerListening ServerListening = new ServerListening(listeningServerSocket,ssl);
			ServerListening.start(); 
			
			if("all".equals(ssl)){
				sslsocketfactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
				sslsocket = (SSLSocket) sslsocketfactory.createSocket(ServerState.getInstance().getCentraladdress(), ServerState.getInstance().getCentralport());
			}else{
				sslsocket=new Socket(ServerState.getInstance().getCentraladdress(), ServerState.getInstance().getCentralport());
			}
=======
		System.setProperty("javax.net.ssl.keyStorePassword","666666");
		System.setProperty("javax.net.ssl.trustStorePassword", "666666");

		// Enable debugging to view the handshake and communication which happens between the SSLClient and the SSLServer
		System.setProperty("javax.net.debug","all");

		try {
			
			SSLSocketFactory sslsocketfactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
			SSLSocket sslsocket = (SSLSocket) sslsocketfactory.createSocket(ServerState.getInstance().getCentraladdress(), ServerState.getInstance().getCentralport());
>>>>>>> 1fda8c464f619bf4e55479fa196a32b9e809799c
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(sslsocket.getOutputStream(), "UTF-8"));
			@SuppressWarnings("static-access")
			JSONObject mas=new Message().requireServerlist(config.getServerid(),config.getServerAddress(),String.valueOf(config.getClientsPort()),String.valueOf(config.getCoordinationPort()));
			System.out.println("send to centralserver:"+mas);
			writer.write(mas + "\n");
			writer.flush();
			writer.close();
			sslsocket.close();
<<<<<<< HEAD
=======
			
			serversPort =config.getClientsPort();
			coordinationPort = config.getCoordinationPort();
			// Create a server socket listening on given port
			//Create SSL server socket
			SSLServerSocketFactory sslserversocketfactory = (SSLServerSocketFactory) SSLServerSocketFactory
					.getDefault();
			listeningClientSocket = (SSLServerSocket) sslserversocketfactory.createServerSocket(serversPort);

			SSLServerSocketFactory sslserversocketfactory1 = (SSLServerSocketFactory) SSLServerSocketFactory
					.getDefault();
			
			listeningServerSocket = (SSLServerSocket) sslserversocketfactory1.createServerSocket(coordinationPort);
			ServerListening ServerListening = new ServerListening(listeningServerSocket);
			ServerListening.start(); 
>>>>>>> 1fda8c464f619bf4e55479fa196a32b9e809799c

			while (true) {

				//Accept an incoming client connection request
				//Accept client connection
<<<<<<< HEAD
				Socket clientSocket = listeningClientSocket.accept();

				MessageSendThread messageSendThread = new MessageSendThread(clientSocket);
				messageSendThread.start();
				ManagingThread managingThread = new ManagingThread(clientSocket,messageSendThread,ssl);
=======
				SSLSocket clientSocket = (SSLSocket) listeningClientSocket.accept();

				MessageSendThread messageSendThread = new MessageSendThread(clientSocket);
				messageSendThread.start();
				ManagingThread managingThread = new ManagingThread(clientSocket,messageSendThread);
>>>>>>> 1fda8c464f619bf4e55479fa196a32b9e809799c
				managingThread.start();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(listeningClientSocket != null) {
				try {
					listeningClientSocket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(listeningServerSocket != null) {
				try {
					listeningServerSocket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
