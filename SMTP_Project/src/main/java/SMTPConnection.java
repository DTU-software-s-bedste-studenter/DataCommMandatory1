import java.io.*;
import java.net.Socket;

/**
 * Open an SMTP connection to a mailserver and send one mail.
 *
 */
public class SMTPConnection {
    /* The socket to the server */
    private Socket connection;

    /* Streams for reading and writing the socket */
    private BufferedReader fromServer;
    private DataOutputStream toServer;

    private static final int SMTP_PORT = 25;
    private static final String CRLF = "\r\n";

    /* Are we connected? Used in close() to determine what to do. */
    private boolean isConnected = false;

    /* Create an SMTPConnection object. Create the socket and the 
       associated streams. Initialize SMTP connection. */
    public SMTPConnection(Envelope envelope) throws IOException {
        connection = new Socket(envelope.DestHost, SMTP_PORT);
        fromServer = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        toServer = new DataOutputStream(connection.getOutputStream());

        /* Fill in */
	/* Read a line from server and check that the reply code is 220.
	   If not, throw an IOException. */
        /* Fill in */
        String reply = fromServer.readLine();
        if(parseReply(reply)!=220){
            close();
            throw new IOException();
        }


	/* SMTP handshake. We need the name of the local machine.
	   Send the appropriate SMTP handshake command. */
        String localhost = "localhost";
        sendCommand("EHLO " + localhost + CRLF, 250);
        sendCommand("AUTH LOGIN", );
        isConnected = true;
    }

    /* Send the message. Write the correct SMTP-commands in the
       correct order. No checking for errors, just throw them to the
       caller. */
    public void send(Envelope envelope) throws IOException {
        sendCommand("mail from: <"+ envelope.Message.getFrom() +">" + CRLF, 250);
        sendCommand("rcpt to: <"+ envelope.Message.getTo() +">" + CRLF, 250);
        sendCommand("data" + CRLF,354);
        toServer.writeBytes(envelope.Message.toString() + CRLF);
        sendCommand("." + CRLF, 250);
        /* Fill in */
	/* Send all the necessary commands to send a message. Call
	   exception thrown from sendCommand(). */
        /* Fill in */
    }

    /* Close the connection. First, terminate on SMTP level, then
       close the socket. */

    public void close() {
        isConnected = false;
        try {
            sendCommand("QUIT" + CRLF, 221);
            connection.close();
        } catch (IOException e) {
            System.out.println("Unable to close connection: " + e);
            isConnected = true;
        }
    }


    /* Send an SMTP command to the server. Check that the reply code is
       what is supposed to be according to RFC 821. */
    private void sendCommand(String command, int rc) throws IOException {
        toServer.writeBytes(command);
        String replyCode = fromServer.readLine();
        if (rc != parseReply(replyCode)) {
            throw new IOException();
        }
    }

    private int parseReply(String reply) {
        return Integer.parseInt(reply.substring(0, 3));
    }

    /* Destructor. Closes the connection if something bad happens. */
    protected void finalize() throws Throwable {
        if(isConnected) {
            close();
        }
        super.finalize();
    }
}