package server.tools;

import org.kohsuke.args4j.Option;

public class ComLineValues {
	@Option(
			required = true,
			name = "-n",
			aliases = {"--serverid"},
			usage = "Server name"
			)
	private String serverid;
	@Option(
			required = false,
			name = "-l",
			aliases = {"--serversConf"},
			usage = "Server config path"
			)
	private String serversConf;

	@Option(
			required = false,
			name = "-a",
			aliases = {"--serversAddress"},
			usage = "Server Address"
			)
	private String serversAddress;
	
	@Option(
			required = false,
			name = "-p",
			aliases = {"--serversPort"},
			usage = "Server Port"
			)
	private int serversPort;
	
	@Option(
			required = false,
			name = "-c",
			aliases = {"--coordinationPort"},
			usage = "Server coordinationPort"
			)
	private int coordinationPort;

	public ComLineValues() {
	}

	public String getServerid() {
		return this.serverid;
	}
	
	public int getServersPort() {
		return this.serversPort;
	}
	
	public String getServersAddress() {
		return this.serversAddress;
	}

	public int getCoordinationPort() {
		return this.coordinationPort;
	}
	public String getServersConf() {
		return this.serversConf;
	}

}

