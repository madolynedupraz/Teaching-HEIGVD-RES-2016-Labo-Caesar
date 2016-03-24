package ch.heigvd.res.caesar.server;

import ch.heigvd.res.caesar.client.*;
import ch.heigvd.res.caesar.protocol.Protocol;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Scanner;
import java.util.logging.Logger;

/**
 *
 * @author Olivier Liechti (olivier.liechti@heig-vd.ch)
 */
public class CaesarServer extends Protocol {

    private static final Logger LOG = Logger.getLogger(CaesarServer.class.getName());

    private int port;
    private int key;
    private static int cmp = 1;

    ServerSocket serverSocket;

    CaesarServer(int port) {
        this.port = port;
        try{
            serverSocket = new ServerSocket(port);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        while(true) {
            try {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client " + cmp + " connected");
                new Thread(new CaesarServerThread(clientSocket)).start();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    class CaesarServerThread implements Runnable {
        PrintWriter out;
        BufferedReader in;
        private int num;
        Socket clientSocket;

        public CaesarServerThread(Socket clientSocket) {
            this.clientSocket = clientSocket;
            num = cmp++;
        }

        @Override
        public void run() {
            try{
                out = new PrintWriter(clientSocket.getOutputStream(), true);
                in = new BufferedReader(
                        new InputStreamReader(clientSocket.getInputStream()));
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }

            readKey();
            ack();

            while(true) {

                String msg = read();
                if (msg == null)
                {
                    try {
                        out.close();
                        in.close();
                        clientSocket.close();
                    } catch (IOException e){e.printStackTrace();}

                    System.out.println("Client n°" + num + " disconnected");
                    break;
                }
                System.out.println("Client n°" + num + ": " + msg);
                write(msg);
            }
        }

        public String read() {
            String decryptedLine = null;

            try {
                String inputLine = in.readLine();
                if (inputLine == null)
                    return null;
                decryptedLine = decrypt(inputLine, key);
            }
            catch(IOException e)
            {
                return null;
            }


            return decryptedLine;
        }

        public void write(String msg) {
            String encryptedMsg = encrypt(msg, key);
            out.println(encryptedMsg);
        }

        public void readKey() {
            try{
                key = Integer.parseInt(in.readLine());
            }
            catch(IOException e)
            {
                e.printStackTrace();
            }
        }

        public void ack() {
            out.println("OK");
        }
    }


    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        System.setProperty("java.util.logging.SimpleFormatter.format", "[%1$tH:%1$tM:%1$tS::%1$tL] Server > %5$s%n");
        LOG.info("Caesar server starting...");
        LOG.info("Protocol constant: " + Protocol.A_CONSTANT_SHARED_BY_CLIENT_AND_SERVER);

        CaesarServer cServer = new CaesarServer(4444);
    }

}
