package server.tools;

import org.kohsuke.args4j.Option;

public class ComLineValues {
	@Option(
			required = false,
			name = "-n",
			aliases = {"--serverid"},
			usage = "Server name"
			)
	private String serverid="s";
	@Option(
			required = false,
			name = "-l",
			aliases = {"--serversConf"},
			usage = "Server config path"
			)
	private String serversConf= "C:\\serverLists2.txt";

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
<<<<<<< HEAD
	private int serversPort=4441;
=======
	private int serversPort;
>>>>>>> 1fda8c464f619bf4e55479fa196a32b9e809799c

	@Option(
			required = false,
			name = "-c",
			aliases = {"--coordinationPort"},
			usage = "Server coordinationPort"
			)
	private int coordinationPort;

	@Option(
			required = false,
			name = "-k",
			aliases = {"--keyFilepath"},
			usage = "Server keyFilepath"
			)
<<<<<<< HEAD
	private String keyFilepath="\\DSk.jks";
=======
	private String keyFilepath="DSk.jks";
>>>>>>> 1fda8c464f619bf4e55479fa196a32b9e809799c

	@Option(
			required = false,
			name = "-t",
			aliases = {"--trustFilepath"},
			usage = "Server trustFilepath"
			)
	private String trustFilepath= "DSt.jks";
	
<<<<<<< HEAD
	@Option(
			required = false,
			name = "-ssl",
			aliases = {"--ssl"},
			usage = "Server ssl model"
			)
	private String ssl= "all";
	
=======
>>>>>>> 1fda8c464f619bf4e55479fa196a32b9e809799c
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
	
	public String getKeyFilepath() {
		return this.keyFilepath;
	}
	public String getTrustFilepath() {
		return this.trustFilepath;
	}
<<<<<<< HEAD
	public String getSsl() {
		return this.ssl;
	}
=======
>>>>>>> 1fda8c464f619bf4e55479fa196a32b9e809799c

}

