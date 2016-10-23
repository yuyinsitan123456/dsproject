package server;

public class CurrentServerInfo {
	private String serverid;
	private String serverAddress;
	private int clientsPort = 4444;
	private int coordinationPort = 5555;
	private boolean work=true;
	private int number = 0;

	public CurrentServerInfo(String serverid, String serverAddress, int clientsPort, int coordinationPort ,int number) {
		super();
		this.serverid = serverid;
		this.serverAddress = serverAddress;
		this.clientsPort = clientsPort;
		this.coordinationPort = coordinationPort;
		this.work=true;
		this.number=number;
	}

	public String getServerid() {
		return serverid;
	}

	public String getServerAddress() {
		return serverAddress;
	}

	public int getClientsPort() {
		return clientsPort;
	}

	public int getNumber() {
		return number;
	}
	
	public boolean getWork() {
		return work;
	}
	public int getCoordinationPort() {
		return coordinationPort;
	}

	public void setWork(boolean work) {
		this.work=work;
	}
	public void addNumber() {
		this.number=this.number+1;
	}
	public void deleteNumber() {
		this.number=this.number-1;
	}
}