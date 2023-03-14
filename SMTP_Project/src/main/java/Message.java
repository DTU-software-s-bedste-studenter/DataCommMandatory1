import java.io.*;
import java.nio.file.Files;
import java.util.*;
import java.text.*;

/* $Id: Message.java,v 1.5 1999/07/22 12:10:57 kangasha Exp $ */

/**
 * Mail message.
 *
 * @author Jussi Kangasharju
 */
public class Message {
    /* The headers and the body of the message. */
    public String Headers;
    public String Body;

    /* Sender and recipient. With these, we don't need to extract them
       from the headers. */
    private String From;
    private String To;

    private File image;
    private String base64encoded;

    //File f = new File(System.getProperty("/Users/matt/IdeaProjects/DataCommMandatory1/SMTP_Project/src/resources/amazed-man.jpg"));



    /* To make it look nicer */
    private static final String CRLF = "\r\n";

    /* Create the message object by inserting the required headers from
       RFC 822 (From, To, Date). */
    public Message(String from, String to, String subject, String text) {
        /* Remove whitespace */
        From = from.trim();
        To = to.trim();
        Headers = "From: " + From + CRLF;
        Headers += "To: " + To + CRLF;
        Headers += "Subject: " + subject.trim() + CRLF;
        Headers += "MIME-Version: 1.0" + CRLF;
        Headers += "Content-Type:multipart/mixed;boundary=-x-x-x-x-x-" + CRLF;
        Headers += "Content-Transfer-Encoding: base64" + CRLF;
        String imageHeader = "--X-=-=-=-text boundary" + CRLF + "Content-Type: image/jpeg; name=amazed-man.jpg" + CRLF + "Content-Transfer-Encoding: base64" + CRLF + "Content-Disposition: attachment; filename=amazed-man.jpg";
	/* A close approximation of the required format. Unfortunately
	   only GMT. */
        SimpleDateFormat format =
                new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss 'GMT'");
        String dateString = format.format(new Date());
        /*try {
            //FileInputStream fin = new FileInputStream(f);
            byte imagebytearray[] = new byte[(int) f.length()];
            fin.read(imagebytearray);
            String imagetobase64 = Base64.getEncoder().encodeToString(imagebytearray);
            fin.close();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

         */


        //https://stackoverflow.com/questions/20389255/reading-a-resource-file-from-within-jar
        try (InputStream in = getClass().getResourceAsStream("amazed-man.jpg");
             BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {


        } catch (IOException e){
            e.printStackTrace();
        }

        this.image = new File(System.getProperty("user.dir") + File.separator + "resources" + File.separator + "amazed-man.jpg");

        try {
            this.base64encoded = Base64.getEncoder().encodeToString(Files.readAllBytes(image.toPath()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Headers += "Date: " + dateString + CRLF;
        Body = text + CRLF;
        Body += imageHeader + CRLF;
        Body += base64encoded;
    }

    /* Two functions to access the sender and recipient. */
    public String getFrom() {
        return From;
    }

    public String getTo() {
        return To;
    }

    /* Check whether the message is valid. In other words, check that
       both sender and recipient contain only one @-sign. */
    public boolean isValid() {
        int fromat = From.indexOf('@');
        int toat = To.indexOf('@');

        if(fromat < 1 || (From.length() - fromat) <= 1) {
            System.out.println("Sender address is invalid");
            return false;
        }
        if(toat < 1 || (To.length() - toat) <= 1) {
            System.out.println("Recipient address is invalid");
            return false;
        }
        if(fromat != From.lastIndexOf('@')) {
            System.out.println("Sender address is invalid");
            return false;
        }
        if(toat != To.lastIndexOf('@')) {
            System.out.println("Recipient address is invalid");
            return false;
        }
        return true;
    }

    /* For printing the message. */
    public String toString() {
        String res;

        res = Headers + CRLF;
        res += Body;
        return res;
    }
}
