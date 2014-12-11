package Client;
import java.io.*;

public class ClientThread extends Thread {
    private Client c;
    private DataInputStream input;

    public ClientThread(Client c, DataInputStream input) {
        this.c = c;
        this.input = input;
        this.start();
    }

    @Override
    public void run() {
        try {
            while (true) {
                String mess = input.readUTF();

                if (mess.equals("CLIentSTT111")) {  // update status
                    String stt = input.readUTF();
                    c.allUsers.append(stt);
                } else if (mess.equals("clientEXIT123")) {  // update the notification center
                    String stt = input.readUTF();
                    c.allUsers.append(stt);
                } else if (mess.equals("fileTranFeRfromserver")) {  // send message from clients or server
                    c.save();
                } else {
                    c.doc.insertString(c.doc.getLength(), mess, null);
                }
            }
        } catch (Exception ex) {}
    }
}
