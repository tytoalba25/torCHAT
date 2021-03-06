import java.net.InetAddress;

import java.util.concurrent.Future;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Peer {
	// Peer info
	private int ID;
	private InetAddress addr;
	private int port;
	private String channelName;
	
	private PeerGroup group;
	
	// Timer stuff
	private final int PING_INTERVAL = 5;
	private ScheduledThreadPoolExecutor pool;
	private Future<?> future;
	
	private Boolean verbose = false;
	
	public Peer() {
		
	}
	
	public Peer(int id, InetAddress a, int p, PeerGroup g, ScheduledThreadPoolExecutor stpe, String cn) {
		
		ID = id;
		addr = a;
		port = p;
		channelName = cn;
		
		group = g;
		pool = stpe;
	}
	
	public void verbose () {
		verbose = true;
	}
	
	public void restartTimer() {
		stopTimer();
		startTimer();	
	}
	
	// TimeoutTimer will contact the addr and ID given once it expires. It will then contact the tracker if it does not receive a response in time.
	public void startTimer() {
		TimeoutTimer time = new TimeoutTimer(
				group.getTrackIP(), group.getTrackPort(), 
				addr.toString().substring(1).split(":")[0], port, 
				ID, channelName);
		if(verbose)
			time.verbose();
		future = pool.schedule(
				time, 
				PING_INTERVAL, 
				TimeUnit.SECONDS
		);		
	}
	
	public void stopTimer() {
		future.cancel(true);
	}
	
	public int getID() {
		return ID;
	}
	
	public InetAddress getAddr() {
		return addr;
	}
	
	public int getPort() {
		return port;
	}
}
