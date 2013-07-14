
import java.io.*;
import java.net.*;
import java.util.*;
import java.nio.*;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayDeque;
import java.util.BitSet;
import java.util.Date;
import java.util.Queue;

public class Peer {

    /**
     * It has methods to perform handshake, alive, interested, and other
     * messages.
     */
    public RUBTClient client;
    public byte[] info_hash;
    public String peerID = null;
    public int peerPort = 0;
    public String peerIP=null;
    public Socket peerSocket = null;
    public String hostIP = null;
    public DataOutputStream client2peer = null;
    public DataInputStream peer2client = null;
    public boolean[] booleanBitField = null;
    public boolean peerInterested;
    public boolean peerChoking;
    public boolean Running;
    public boolean connected;
    public Socket peerconnection;
    public DataOutputStream topeer;
    
    
    final static int KEY_CHOKE = 0;
    final static int KEY_UNCHOKE = 1;
    final static int KEY_INTERESTED = 2;
    final static int KEY_UNINTERESTED = 3;
    final static int KEY_HAVE = 4;
    final static int KEY_BITFIELD = 5;
    final static int KEY_REQUEST = 6;
    final static int KEY_PIECE = 7;
    final static int KEY_CANCEL = 8;
    final static int KEY_PORT = 9;
    /**
     * Set the byte arrays for the static peer messages
     */
    final static byte[] interested = {0, 0, 0, 1, 2};
    final static byte[] uninterested = {0, 0, 0, 1, 3};
    final static byte[] choke = {0, 0, 0, 1, 0};
    final static byte[] unchoke = {0, 0, 0, 1, 1};
    final static byte[] empty_bitfield = {0, 0, 0, 2, 5, 0};
    final static byte[] keep_alive = {0, 0, 0, 0};

    public Peer(String peerIdNum, String IpNum, int peerPortNum, ByteBuffer info_hash, RUBTClient client, String peerip) {
        peerID = peerIdNum;
        hostIP = IpNum;
        peerIP= peerip;
        peerPort = peerPortNum;
        booleanBitField = new boolean[RUBTClient.numPieces];
        this.info_hash = info_hash.array();
        this.client = client;
    }

    public static byte[] getInfoHashFromHandshake(byte[] response) {
        byte[] info_hash = new byte[20];
        int index = 28;
        for (int i = 0; i < 20; ++i) {
            info_hash[i] = response[index];
            ++index;
        }
        return info_hash;
    }

    public static int getNextPiece(boolean[] completed) {

        for (int i = 0; i < completed.length; ++i) {
            if (!completed[i]) {
                return i;
            }
        }
        return -1; // no more pieces are needed.
    }

    private ByteBuffer buildHandshake(String localPeerID, ByteBuffer info_hash) {
        byte[] handshake_bytes = new byte[68];
        handshake_bytes[0] = (char) 19;
        try {
            byte[] ptsr = "BitTorrent protocol".getBytes("ASCII");
            System.arraycopy(ptsr, 0, handshake_bytes, 1, ptsr.length);
            Arrays.fill(handshake_bytes, 20, 28, (byte) 0);
            System.arraycopy(info_hash.array(), 0, handshake_bytes, 28, 20);
            System.arraycopy(localPeerID.getBytes("ASCII"), 0, handshake_bytes, 48, 20);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            System.exit(1);
        }
        return ByteBuffer.wrap(handshake_bytes);
    }

    private void sendHandshake(String localPeerID, ByteBuffer info_hash) {
        try {
            ByteBuffer handshake = buildHandshake(localPeerID, info_hash);
            if (!connected) {
                this.connect();
            }
            DataOutputStream serverOut = new DataOutputStream(peerconnection.getOutputStream());
            serverOut.write(handshake.array());
            serverOut.flush();
        } catch (IOException e) {
            System.out.println("Problem sending handshake to " + peerID);
            e.printStackTrace();
            //System.exit(1);
        }
    }

     public void connect() {
        try {
            peerconnection = new Socket(peerIP, peerPort);
            this.topeer = new DataOutputStream(this.peerconnection.getOutputStream());
            this.Running = true;
            connected = true;
        } catch (IOException e) {
            System.out.println(peerID + " could not connect.");
            System.exit(1);
        }
    }
}
