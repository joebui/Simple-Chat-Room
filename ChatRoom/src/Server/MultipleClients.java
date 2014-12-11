package Server;
import javax.swing.*;
import javax.swing.text.*;
import java.awt.event.*;
import java.net.*; import java.io.*;
import java.util.*;


public class MultipleClients extends Thread {
    private String selectedUser;
    private JFileChooser fileChooser = new JFileChooser();
    private Socket s;
    protected DataInputStream inputLoggingFromClient;
    protected DataOutputStream outputLoggingToClient;
    private Server server;
    private Map<String, String> users;
    private StyledDocument doc;

    public MultipleClients(Socket s, final Server server, Map<String, String> map) {
        this.s = s;
        this.server = server;
        users = map;
        doc = server.getAllConversation();
        selectedUser = "All users";
        try {
            inputLoggingFromClient = new DataInputStream(s.getInputStream());
            outputLoggingToClient = new DataOutputStream(s.getOutputStream());
        } catch (Exception ex) {}

        server.getChat().addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {  // send message when users press ENTER
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ENTER:
                        if (!server.getChat().getText().equals("")) {
                            StyledDocument doc = server.getAllConversation();
                            // send message of the server to all connected clients
                            try {
                                if (selectedUser.equals("All users")) {  // send to all clients
                                    doc.insertString(doc.getLength(), " Server: " + server.getChat().getText() + "\n", null);
                                    Iterator entry = server.clients.entrySet().iterator();
                                    while (entry.hasNext()) {
                                        Map.Entry ele = (Map.Entry) entry.next();
                                        String name = (String) ele.getKey();
                                        server.clients.get(name).outputLoggingToClient.writeUTF(
                                                " Server: " + server.getChat().getText() + "\n");
                                    }
                                } else {  // send to a specific client
                                    doc.insertString(doc.getLength(), " Server: " + server.getChat().getText() + "\n", null);
                                    server.clients.get(selectedUser).outputLoggingToClient.writeUTF(
                                            " Server: " + server.getChat().getText() + "\n");
                                }
                            } catch (Exception ex) {
                            }
                            server.getChat().setText("");
                        }
                        break;
                    default:
                }
            }
        });

        server.getSending().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {  // send message when users click "Send"
                if (!server.getChat().getText().equals("")) {
                    try {
                        if (selectedUser.equals("All users")) {  // send to all clients
                            doc.insertString(doc.getLength(), " Server: " + server.getChat().getText() + "\n", null);
                            Iterator entry = server.clients.entrySet().iterator();
                            while (entry.hasNext()) {
                                Map.Entry ele = (Map.Entry) entry.next();
                                String name = (String) ele.getKey();
                                server.clients.get(name).outputLoggingToClient.writeUTF(
                                        " Server: " + server.getChat().getText() + "\n");
                            }
                        } else {  // send to a specific client
                            doc.insertString(doc.getLength(), " Server: " + server.getChat().getText() + "\n", null);
                            server.clients.get(selectedUser).outputLoggingToClient.writeUTF(
                                    " Server: " + server.getChat().getText() + "\n");
                        }
                    } catch (Exception ex) {
                    }
                    server.getChat().setText("");
                }
            }
        });

        server.getCurrentUsers().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {  // set the name of client to which server wishes to send
                selectedUser = (String) server.getCurrentUsers().getSelectedItem();
            }
        });
    }

    @Override
    public void run() {  // run thread
        try {
            inputLoggingFromClient = new DataInputStream(s.getInputStream());  // get input from client
            outputLoggingToClient = new DataOutputStream(s.getOutputStream());  // output to server

            while (true) {
                int check = inputLoggingFromClient.readInt();

                if (check == 0) {  /* handle inputs and outputs of logging when clients click "Log in" */
                    handleLogin(inputLoggingFromClient, outputLoggingToClient);
                } else if (check == 1) {  /* handle inputs and outputs of signing up when clients click "Sign Up" */
                    handleSignUp(inputLoggingFromClient, outputLoggingToClient);
                } else if (check == 2) {  /* handle inputs and outputs of chatting when clients finish logging in */
                    handleChatting(inputLoggingFromClient);
                }
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void handleLogin(DataInputStream inputLoggingFromClient, DataOutputStream outputLoggingToClient) {
        try {
            while (true) {
                int checkLogin = 0, checkCurrent = 0;
                String userName = inputLoggingFromClient.readUTF();  // get username from client
                String pass = inputLoggingFromClient.readUTF();  // get password from client

                Iterator entry = users.entrySet().iterator();  // check to the database if they both exist
                while (entry.hasNext()) {
                    Map.Entry entries = (Map.Entry) entry.next();
                    String id = (String) entries.getKey();
                    String password = (String) entries.getValue();
                    if (userName.equals(id) && pass.equals(password)) {
                        checkLogin = 1;
                        break;  // exit searching if username, password are found
                    }
                }

                Iterator currentEntry = server.clients.entrySet().iterator();  // check if the entered username is
                while (currentEntry.hasNext()) {                               // already online
                    Map.Entry entries = (Map.Entry) currentEntry.next();
                    String id = (String) entries.getKey();
                    if (userName.equals(id)) {
                        checkCurrent = 1;
                        break;
                    }
                }
                outputLoggingToClient.writeInt(checkLogin);  // send output to the client to check
                outputLoggingToClient.writeInt(checkCurrent);
                if (checkLogin == 1 && checkCurrent == 0) {  // exit this method if users log in successfully
                    server.clients.put(userName, this);  // add this client to the Map of current users
                    server.getCurrentUsers().addItem(userName);  // add new name to the current user list
                    break;
                }
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void handleSignUp(DataInputStream inputSignUpFromClient, DataOutputStream outputSignUpToClient) {
        try {
            while (true) {
                int checkSignUp = 1;
                String id = inputSignUpFromClient.readUTF();
                String password = inputSignUpFromClient.readUTF();

                Iterator entry = users.entrySet().iterator();
                while (entry.hasNext()) {
                    Map.Entry entries = (Map.Entry) entry.next();
                    String name = (String) entries.getKey();
                    if (id.equals(name)) {
                        checkSignUp = 0;
                        break;  // exit searching if the entered name is match with one in the system
                    } else {
                        checkSignUp = 1;
                    }
                }

                outputSignUpToClient.writeInt(checkSignUp);

                if (checkSignUp == 1) {
                    PrintWriter writer = new PrintWriter(new FileWriter("users.txt", true));
                    writer.println(id + " : " + password);  // write the new username and password to text file
                    writer.close();
                    server.getAllUsers().append(" A Client has signed up with ID: " + id + "\n");
                    users.put(id, password);  // add new username and password to the Map
                    break;  // exit the method after finishing signing up
                }
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void handleChatting(DataInputStream inputSignUpFromClient) {
        try {
            while (true) {
                String signal = inputSignUpFromClient.readUTF();  // get signal (key word) from clients

                if (signal.equals("clientEXIT123")) {  /* client exits */
                    String name = inputSignUpFromClient.readUTF();
                    server.clients.remove(name);  // remove the client from the Map
                    String message = " Client \"" + name + "\" has EXISTED the chat room\n";
                    server.getAllUsers().append(message);  // set status
                    server.getCurrentUsers().removeItem(name);  // remove the client's name from the ComboBox

                    Iterator entry = server.clients.entrySet().iterator();  // send the status to all clients
                    while (entry.hasNext()) {
                        Map.Entry ele = (Map.Entry) entry.next();
                        String n = (String) ele.getKey();
                        if (n.equals(name)) {
                            continue;
                        } else {
                            server.clients.get(n).outputLoggingToClient.writeUTF("clientEXIT123");
                            server.clients.get(n).outputLoggingToClient.writeUTF(message);
                        }
                    }
                } else if (signal.equals("clientSTATUS123")) {  /* update status */
                    String userName = inputSignUpFromClient.readUTF();
                    String stt = inputSignUpFromClient.readUTF();
                    server.getAllUsers().append(stt);  // update client's status to server

                    Iterator entry = server.clients.entrySet().iterator();  // send the status to all clients
                    while (entry.hasNext()) {
                        Map.Entry ele = (Map.Entry) entry.next();
                        String name = (String) ele.getKey();
                        if (name.equals(userName)) {
                            continue;
                        } else {
                            server.clients.get(name).outputLoggingToClient.writeUTF("CLIentSTT111");
                            server.clients.get(name).outputLoggingToClient.writeUTF(stt);
                        }
                    }
                } else if (signal.equals("1248CLientTOSEnd")) {  /* send message to server and all clients */
                    try {
                        String user = inputSignUpFromClient.readUTF();
                        String message = inputSignUpFromClient.readUTF();

                        server.getAllConversation().insertString(server.getAllConversation().getLength(),
                                message, null);
                        Iterator entry = server.clients.entrySet().iterator();  // send the message to all clients
                        while (entry.hasNext()) {
                            Map.Entry ele = (Map.Entry) entry.next();
                            String name = (String) ele.getKey();
                            if (name.equals(user))
                                continue;
                            else
                                server.clients.get(name).outputLoggingToClient.writeUTF(message);
                        }
                    } catch (Exception ex) {}
                }
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
