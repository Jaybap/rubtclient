

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Vector;



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
	private RUBTClient 					client;
	private boolean 					stillRunning;
        private boolean[] completed;
	

	public DownloadManager(RUBTClient r,TrackerGetr t)
	{
		client = r;
		torrent = r.getTorrentInfo();
		tracker = t;
		peerList = t.getPeerList();
		stillRunning = true;
	}

	/** METHODS */
	
	
	/* +++++++++++++++++++++++++++++++ GET METHODS +++++++++++++++++++++++++++++++++++ */	
	
	ArrayList<Peer> getPeerList()
	{
		return peerList;
	}
	
}
