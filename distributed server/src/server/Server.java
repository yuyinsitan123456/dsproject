package server;

import java.io.IOException;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

import server.config.Config;
import server.config.ConfigLoader;
import server.state.ManagingThread;
import server.state.MessageSendThread;
import server.tools.ComLineValues;

public class Server {

	public static void main(String[] args) throws IOException {

		String serverid = null;
		String serversConf = null;
		ComLineValues comLineValues = new ComLineValues();
		CmdLineParser parser = new CmdLineParser(comLineValues);

		try {
			parser.parseArgument(args);
			serverid = comLineValues.getServerid();
			serversConf = comLineValues.getServersConf();
		} catch (CmdLineException ce) {
			ce.printStackTrace();
		}

		// Read configuration in the config file
		Config config = ConfigLoader.loadConfig(serverid,serversConf);
		int port;
		int coordinationPort;
		SSLServerSocket listeningServerSocket = null;
		SSLServerSocket listeningClientSocket =null;

		//Specify the keystore details (this can be specified as VM arguments as well)
		//the keystore file contains an application's own certificate and private key
		//keytool -genkey -keystore <keystorename> -keyalg RSA
		System.setProperty("javax.net.ssl.keyStore","D:\\DS.jks");
		//Password to access the private key from the keystore file
		System.setProperty("javax.net.ssl.keyStorePassword","888888");

		// Enable debugging to view the handshake and communication which happens between the SSLClient and the SSLServer
		System.setProperty("javax.net.debug","all");

		try {
			port =config.getClientsPort();
			coordinationPort = config.getCoordinationPort();
			// Create a server socket listening on given port
			//Create SSL server socket
			SSLServerSocketFactory sslserversocketfactory = (SSLServerSocketFactory) SSLServerSocketFactory
					.getDefault();
			listeningClientSocket = (SSLServerSocket) sslserversocketfactory.createServerSocket(port);

			listeningServerSocket = (SSLServerSocket) sslserversocketfactory.createServerSocket(coordinationPort);
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
