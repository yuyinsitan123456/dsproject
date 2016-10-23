package server;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;

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
//		String serversAddress="http://localhost";
		int serversPort = 4443;
		int coordinationPort = 3333;
		String keyFilepath = null;
//		String trustFilepath = null;

		String serversConf = null;
		ComLineValues comLineValues = new ComLineValues();
		CmdLineParser parser = new CmdLineParser(comLineValues);

		try {
			parser.parseArgument(args);
			serverid = comLineValues.getServerid();
			serversConf = comLineValues.getServersConf();
			keyFilepath = comLineValues.getKeyFilepath();
//			trustFilepath = comLineValues.getTrustFilepath();
		} catch (CmdLineException ce) {
			ce.printStackTrace();
		}

		// Read configuration in the config file
		Config config = ConfigLoader.loadConfig(serverid,serversConf);
		
//		Config config = ConfigLoader.loadConfig(serverid,serversAddress,serversPort,coordinationPort);
		SSLServerSocket listeningServerSocket = null;
		SSLServerSocket listeningClientSocket =null;
		//Specify the keystore details (this can be specified as VM arguments as well)
		//the keystore file contains an application's own certificate and private key
		//keytool -genkey -keystore <keystorename> -keyalg RSA
		System.setProperty("javax.net.ssl.keyStore",keyFilepath);
		System.setProperty("javax.net.ssl.trustStore",keyFilepath);
		//Password to access the private key from the keystore file
		System.setProperty("javax.net.ssl.keyStorePassword","666666");
		System.setProperty("javax.net.ssl.trustStorePassword", "666666");

		// Enable debugging to view the handshake and communication which happens between the SSLClient and the SSLServer
		System.setProperty("javax.net.debug","all");

		try {
			
			SSLSocketFactory sslsocketfactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
			SSLSocket sslsocket = (SSLSocket) sslsocketfactory.createSocket(ServerState.getInstance().getCentraladdress(), ServerState.getInstance().getCentralport());
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(sslsocket.getOutputStream(), "UTF-8"));
			@SuppressWarnings("static-access")
			JSONObject mas=new Message().requireServerlist(config.getServerid(),config.getServerAddress(),String.valueOf(config.getClientsPort()),String.valueOf(config.getCoordinationPort()));
			System.out.println("send to centralserver:"+mas);
			writer.write(mas + "\n");
			writer.flush();
			writer.close();
			sslsocket.close();
			
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

			while (true) {

				//Accept an incoming client connection request
				//Accept client connection
				SSLSocket clientSocket = (SSLSocket) listeningClientSocket.accept();

				MessageSendThread messageSendThread = new MessageSendThread(clientSocket);
				messageSendThread.start();
				ManagingThread managingThread = new ManagingThread(clientSocket,messageSendThread);
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
