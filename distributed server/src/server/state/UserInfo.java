package server.state;

<<<<<<< HEAD
import java.net.Socket;
=======
import javax.net.ssl.SSLSocket;
>>>>>>> 1fda8c464f619bf4e55479fa196a32b9e809799c

public class UserInfo {
	private String identity;
	private String currentChatroom;
<<<<<<< HEAD
	private Socket socket ;
	private ManagingThread managingThread;

	public UserInfo(String currentChatroom, Socket socket, ManagingThread managingThread) {
=======
	private SSLSocket socket ;
	private ManagingThread managingThread;

	public UserInfo(String currentChatroom, SSLSocket socket, ManagingThread managingThread) {
>>>>>>> 1fda8c464f619bf4e55479fa196a32b9e809799c
		super();
		this.currentChatroom = currentChatroom;
		this.socket = socket;
		this.managingThread = managingThread;
	}
	
<<<<<<< HEAD
	public UserInfo(String identity,String currentChatroom, Socket socket, ManagingThread managingThread) {
=======
	public UserInfo(String identity,String currentChatroom, SSLSocket socket, ManagingThread managingThread) {
>>>>>>> 1fda8c464f619bf4e55479fa196a32b9e809799c
		super();
		this.identity = identity;
		this.currentChatroom = currentChatroom;
		this.socket = socket;
		this.managingThread = managingThread;
	}

	public String getIdentity() {
		return identity;
	}

	public void setIdentity(String identity) {
		this.identity=identity;
	}

	public String getCurrentChatroom() {
		return currentChatroom;
	}
	
	public UserInfo setCurrentChatroom(String currentChatroom) {
		this.currentChatroom=currentChatroom;
		return this;
	}

<<<<<<< HEAD
	public Socket getSocket() {
=======
	public SSLSocket getSocket() {
>>>>>>> 1fda8c464f619bf4e55479fa196a32b9e809799c
		return socket;
	}

	public ManagingThread getManagingThread() {
		return managingThread;
	}
}
