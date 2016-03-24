package ch.heigvd.res.caesar.client;

import ch.heigvd.res.caesar.protocol.Protocol;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Logger;

/**
 *
 * @author Olivier Liechti (olivier.liechti@heig-vd.ch)
 */
public class CaesarClient extends Protocol {

    private static final Logger LOG = Logger.getLogger(CaesarClient.class.getName());

    private int port;
    private String host;

    private int key = 6;

    PrintWriter out;
    BufferedReader in;

    Socket clientSocket;

    public CaesarClient(int port, String host) {
        this.port = port;
        this.host = host;
        try{
            clientSocket = new Socket(host,port);
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(
                    new InputStreamReader(clientSocket.getInputStream()));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void read() {
        String inputLine, decryptedLine;

        try {
            inputLine = in.readLine();
            decryptedLine = decrypt(inputLine, key);
            System.out.println("Server: " + decryptedLine);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void write(String msg) {
        String encryptedMsg = encrypt(msg, key);
        out.println(encryptedMsg);
    }

    public void sendKey() {
        out.println(6);
    }

    public boolean readAck() {
        try {
            String inputLine = in.readLine();
            return inputLine.equals("OK");
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
        return false;
    }


    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        System.setProperty("java.util.logging.SimpleFormatter.format", "[%1$tH:%1$tM:%1$tS::%1$tL] Client > %5$s%n");
        LOG.info("Caesar client starting...");
        LOG.info("Protocol constant: " + Protocol.A_CONSTANT_SHARED_BY_CLIENT_AND_SERVER);

        Scanner scanner = new Scanner(System.in);

        CaesarClient cClient = new CaesarClient(4444, "localhost");

        cClient.sendKey();
        if(cClient.readAck())
        {
            while(true) {
                String msg;
                msg = scanner.nextLine();
                cClient.write(msg);
                cClient.read();
            }
        }
    }

}
