

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Vector;
import java.io.IOException;



/** 
 * STEPS: 
 * 1) Perform handshake 
 * 2) Send interested message 
 * 3) Wait for unchoke 
 * 4) Unchoke, start sequentially requesting blocks of data
 * 5) When a piece is complete, verify it sends a "have message" and writes to "destination" File
 * 
 */


public class DownloadManager {

	private TrackerGetr tracker;
	private TorrentInfo torrent;
	private ArrayList<Peer> peerList;
	private RUBTClient client;
	private boolean stillRunning;
	

	public DownloadManager(RUBTClient r,TrackerGetr t)
	{
		client = r;
		torrent = r.getTorrentInfo();
		tracker = t;
		peerList = t.getPeerList();
		stillRunning = true;
	}

	/** METHOD: Running the program */
	public void run() throws IOException {
		/* Extracts peer needed */
		Peer peer = peerList.get(0);
		
		/* Set up connection with Peer */
		peer.setPeerConnection();
		
		/* Establish handshake */
		peer.sendHandshake(client.getPeerId(), torrent.info_hash);
		
		/* Receive handshake */
		if(!peer.verifyHandshake(torrent.info_hash)){
			System.err.println("ERROR: Unable to verify handshake. ");
		}
		else{
                       
			peer.client2peer.write(peer.interested);
                       
                        client.am_interested=true;
                  
                        byte[] response = peer.getPeerResponse(5);
                        
                        if (response.equals(peer.unchoke)){
                            System.out.println("Unchoke received. Sending unchoke response now..");
                        }
                        else {
                            System.out.println("Unchoke not received");
                        }
                        peer.client2peer.write(peer.unchoke);
                       
		}
	}

	
	/* +++++++++++++++++++++++++++++++ GET METHODS +++++++++++++++++++++++++++++++++++ */	
	
	ArrayList<Peer> getPeerList()
	{
		return peerList;
	}
	
}
