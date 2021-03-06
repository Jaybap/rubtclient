
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
 * STEPS: 1) Perform handshake 2) Send interested message 3) Wait for unchoke 4)
 * Unchoke, start sequentially requesting blocks of data 5) When a piece is
 * complete, verify it sends a "have message" and writes to "destination" File
 *
 */
public class DownloadManager extends Thread {

    private TrackerGetr tracker;
    private TorrentInfo torrent;
    private ArrayList<Peer> peerList;
    private RUBTClient client;
    private boolean stillRunning;
    private boolean completed;
    private boolean bitset;

    public DownloadManager(RUBTClient r, TrackerGetr t) {
        client = r;
        torrent = r.getTorrentInfo();
        tracker = t;
        peerList = t.getPeerList();
        stillRunning = true;
        completed=false;
        
    }

    /**
     * METHOD: Running the program
     */
    public void run() {
        /* Extracts peer needed */
        Peer peer = peerList.get(0);

        /* Set up connection with Peer */
        peer.setPeerConnection();

        /* Establish handshake */
        peer.sendHandshake(client.getPeerId(), torrent.info_hash);

        /* Receive and verify handshake */
        if (!peer.verifyHandshake(torrent.info_hash)) {
            System.err.println("ERROR: Unable to verify handshake. ");
        } else {
            
            
            
            /*Steps left 
             * 1. Send interested message and then wait for response
             * 2. After receivng reponse send unchoke message so that we may start
             * requesting files
             * 3. Setup a while loop that stops when boolean completed is true.
             *      The loop contains request messages for pieces and it also sends have 
             *      messages when a piece is completed. Also in the loop is the
             *      output file which we must update everytime we receive a block
             *      When loop is complete send tracker completed event response
             * save output file to disk then close all open connections/streams
             *    create variable bitset completed that keeps track of pieces completed
             * Trying to check if we receive unchoke but i dont think were getting 
             * the response correct
             * 
            */
            int length = peer.unchoke.length;
            byte[] response = peer.getPeerResponse(length);
            System.out.println(response.toString());
            if (response.equals(peer.unchoke)) {
                System.out.println("Unchoke Received");
            } else {
                System.out.println("Unchoke not Received");
            }
        }
    }

    /* +++++++++++++++++++++++++++++++ GET METHODS +++++++++++++++++++++++++++++++++++ */
    ArrayList<Peer> getPeerList() {
        return peerList;
    }
}
