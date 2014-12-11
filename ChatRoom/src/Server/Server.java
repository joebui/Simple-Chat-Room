package Server;
import javax.swing.*; import javax.swing.event.*;
import javax.swing.filechooser.*;
import javax.swing.text.*; import java.awt.*;
import java.awt.event.*; import java.io.*;
import java.net.*; import java.util.*;

public class Server extends JFrame {
    private Map<String, String> users = new LinkedHashMap<String, String>();  // store user names and passwords
    protected Map<String, MultipleClients> clients = new LinkedHashMap<String, MultipleClients>();  // store clients who are
                                                                              // connecting to server.
    private JButton exitButton = new JButton("Exit");
    private JTextPane allConversation = new JTextPane();
    private StyledDocument doc = allConversation.getStyledDocument();
    private JTextArea allUsers = new JTextArea(30, 10);
    private JTextField chat = new JTextField();
    private JButton sending = new JButton("Send");
    private JButton blockUser = new JButton("Block user");
    private JButton file = new JButton("Attach file");
    private JComboBox currentUsers = new JComboBox();
    private JFileChooser fileChooser = new JFileChooser();

    private ImageIcon happy_img = new ImageIcon("images/happy.png");
    private ImageIcon teeth_img = new ImageIcon("images/teeth.png");
    private ImageIcon sad_img = new ImageIcon("images/sad.png");
    private ImageIcon cry_img = new ImageIcon("images/cry.png");
    private ImageIcon tongue_img = new ImageIcon("images/tongue.png");

    public Server() {
        Font font = new Font("Arial", Font.BOLD, 16);
        exitButton.setBackground(Color.white);
        exitButton.setFont(font);
        sending.setBackground(Color.white);
        blockUser.setBackground(Color.white);
        file.setBackground(Color.white);
        currentUsers.setBackground(Color.white);
        allConversation.setFont(new Font("Arial", Font.PLAIN, 14));
        allConversation.setBackground(Color.gray);  allConversation.setForeground(Color.white);
        allUsers.setBackground(Color.gray);  allUsers.setForeground(Color.white);

        JPanel chatPanel = new JPanel();
        chatPanel.setLayout(new GridLayout(0,2,10,0));
        chatPanel.setBackground(new Color(0, 204, 204));

        JPanel leftP = new JPanel();
        leftP.setLayout(new BorderLayout(0,10));
        leftP.setBackground(new Color(0, 204, 204));
        allConversation.setEditable(false);
        leftP.add(new JScrollPane(allConversation), BorderLayout.CENTER);
        leftP.add(chat, BorderLayout.SOUTH);
        chatPanel.add(leftP);

        JPanel rightP = new JPanel();
        rightP.setLayout(new BorderLayout(0,10));
        rightP.setBackground(new Color(0, 204, 204));
        allUsers.append("\t- - - - Notification Center - - - -\n\n");
        allUsers.setEditable(false);
        rightP.add(new JScrollPane(allUsers), BorderLayout.NORTH);
        JPanel b1 = new JPanel();
        b1.setBackground(new Color(0, 204, 204));
        b1.add(sending); b1.add(blockUser);
        b1.add(file);
        file.addActionListener(new FileActionListener());
        blockUser.addActionListener(new BlockingAction());
        currentUsers.addItem("All users");
        b1.add(currentUsers);
        rightP.add(b1, BorderLayout.CENTER);
        JPanel b2 = new JPanel();
        b2.setBackground(new Color(0, 204, 204));
        exitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        b2.add(exitButton);
        rightP.add(b2, BorderLayout.SOUTH);
        chatPanel.add(rightP);

        this.editText();

        chat.setFocusable(true);
        add(chatPanel);
        setSize(720, 620);
        setTitle("Server.Server information");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        setResizable(false);

        print();  // read users' information from text file

        try {
            ServerSocket server = new ServerSocket(7500);  // start server
            System.out.println("Server started on " + new Date());
            System.out.println("-----------------------------");

            while (true) {
                Socket s = server.accept();  // accept connection to server

                InetAddress addr = s.getInetAddress();
                System.out.println("Client with host name: " + addr.getHostName() + " has logged in on " + new Date());
                System.out.println("Client with host address: " + addr.getHostAddress() + " has logged in on " +
                        new Date());
                MultipleClients task = new MultipleClients(s, this, users);  // handle threads for multiple clients
                task.start();
            }
        } catch (Exception e) {
            System.out.println("Error");
        }
    }

    public void print() {  // read data from text file
        try {
            Scanner scanner = new Scanner(new File("users.txt"));
            String line;
            while ((line = scanner.nextLine()) != null) {
                String[] token = line.split(" : ");
                users.put(token[0], token[1]);
            }
            scanner.close();
        } catch (Exception e) {
        }
    }


    public void open() throws IOException {  // send files to clients
        ServerSocket serverSocket = new ServerSocket(8000);
        String direction = "";
        FileNameExtensionFilter filter = new FileNameExtensionFilter("pdf,java,txt,zip", "pdf", "java", "txt", "zip");
        fileChooser.setFileFilter(filter);
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            direction = fileChooser.getSelectedFile().getAbsolutePath();
        }
        int i = 0;
        Iterator entry = clients.entrySet().iterator();
        while (entry.hasNext()) {
            Map.Entry ele = (Map.Entry) entry.next();
            String name = (String) ele.getKey();
            clients.get(name).outputLoggingToClient.writeUTF("fileTranFeRfromserver");
            i++;
        }
        int c = 0 ;
        while (true){
            if (c<i){Socket socket = serverSocket.accept();
                System.out.println("Accepted connection : " + socket);
                File transferFile = new File(direction);
                byte[] bytearray = new byte[(int) transferFile.length()];
                FileInputStream fin = new FileInputStream(transferFile);
                BufferedInputStream bin = new BufferedInputStream(fin);
                bin.read(bytearray, 0, bytearray.length);
                OutputStream os = socket.getOutputStream();
                System.out.println("file transfer");
                os.write(bytearray, 0, bytearray.length);
                os.flush();
                socket.close();
                System.out.println("File transfer complete");
                c++;}
            else{break;}
        }
    }

    public void editText() {  // change emoticon symbols to image icon
        allConversation.getDocument().addDocumentListener(new DocumentListener(){
            public void insertUpdate(DocumentEvent event) {
                final DocumentEvent de = event;
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        if (de.getDocument() instanceof StyledDocument) {
                            try {
                                StyledDocument styledoc = (StyledDocument) de.getDocument();
                                int start = Utilities.getRowStart(allConversation,Math.max(0,de.getOffset()-1));
                                int end = Utilities.getWordStart(allConversation, de.getOffset() + de.getLength());
                                String text = styledoc.getText(start, end-start);

                                int happy = text.indexOf(":)");
                                int teeth = text.indexOf(":D");
                                int sad = text.indexOf(":(");
                                int cry = text.indexOf(":-(");
                                int tongue = text.indexOf(":P");

                                while(happy >= 0) {  // change :) symbol
                                    final SimpleAttributeSet attrs = new SimpleAttributeSet(
                                            styledoc.getCharacterElement(start + happy).getAttributes()
                                    );
                                    if (StyleConstants.getIcon(attrs) == null) {
                                        StyleConstants.setIcon(attrs, happy_img);
                                        styledoc.remove(start + happy, 2);
                                        styledoc.insertString(start + happy,":)", attrs);

                                    }
                                    happy=text.indexOf(":)", happy + 2);
                                }
                                while(teeth >= 0) {  // change :D symbol
                                    final SimpleAttributeSet attrs = new SimpleAttributeSet(
                                            styledoc.getCharacterElement(start + teeth).getAttributes()
                                    );
                                    if (StyleConstants.getIcon(attrs) == null) {
                                        StyleConstants.setIcon(attrs, teeth_img);
                                        styledoc.remove(start + teeth, 2);
                                        styledoc.insertString(start + teeth,":D", attrs);

                                    }
                                    teeth=text.indexOf(":D", teeth + 2);
                                }
                                while(sad >= 0) {  // change :( symbol
                                    final SimpleAttributeSet attrs = new SimpleAttributeSet(
                                            styledoc.getCharacterElement(start + sad).getAttributes()
                                    );
                                    if (StyleConstants.getIcon(attrs) == null) {
                                        StyleConstants.setIcon(attrs, sad_img);
                                        styledoc.remove(start + sad, 2);
                                        styledoc.insertString(start + sad,":(", attrs);

                                    }
                                    sad=text.indexOf(":(", sad + 2);
                                }
                                while(cry >= 0) {  // change :-( symbol
                                    final SimpleAttributeSet attrs = new SimpleAttributeSet(
                                            styledoc.getCharacterElement(start + cry).getAttributes()
                                    );
                                    if (StyleConstants.getIcon(attrs) == null) {
                                        StyleConstants.setIcon(attrs, cry_img);
                                        styledoc.remove(start + cry, 3);
                                        styledoc.insertString(start + cry,":-(", attrs);

                                    }
                                    cry=text.indexOf(":-(", cry + 3);
                                }
                                while(tongue >= 0) {  // change :P symbol
                                    final SimpleAttributeSet attrs = new SimpleAttributeSet(
                                            styledoc.getCharacterElement(start + tongue).getAttributes());
                                    if (StyleConstants.getIcon(attrs) == null) {
                                        StyleConstants.setIcon(attrs, tongue_img);
                                        styledoc.remove(start + tongue, 2);
                                        styledoc.insertString(start + tongue,":P", attrs);

                                    }
                                    tongue=text.indexOf(":P", tongue + 2);
                                }
                            } catch (BadLocationException e1) {
                                e1.printStackTrace();
                            }
                        }
                    }
                });
            }
            public void removeUpdate(DocumentEvent e) {
            }
            public void changedUpdate(DocumentEvent e) {
            }
        });
    }

    class BlockingAction implements ActionListener {  // block a user
        @Override
        public void actionPerformed(ActionEvent e) {
            String name = JOptionPane.showInputDialog(null, "Enter the user name that will be blocked",
                    "Block user", JOptionPane.CANCEL_OPTION);  // ask for name of users that needs blocking

            Iterator entry = clients.entrySet().iterator();
            while (entry.hasNext()) {
                Map.Entry ele = (Map.Entry) entry.next();
                String n = (String) ele.getKey();
                if (!name.equals(null) && name.equals(n)) {
                    try {
                        clients.get(name).outputLoggingToClient.writeUTF(" Server: Sorry, you have been removed from the " +
                                "chat room\n");  // inform that client
                    } catch (Exception ex) {}
                    clients.get(name).stop();  // stop the thread
                    clients.remove(name);  // remove the name from the Map
                    allUsers.append(" Client " + name + " has been removed from the chat room\n");  // update status
                    currentUsers.removeItem(name);  // remove the name from the combo box
                }
            }
        }
    }

    class FileActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent ae) {
            try {
                open();
            } catch (Exception e) {}
        }
    }

    public JTextArea getAllUsers() {
        return allUsers;
    }

    public StyledDocument getAllConversation() {
        return doc;
    }

    public JTextField getChat() { return chat; }

    public JButton getSending() {
        return sending;
    }

    public JComboBox getCurrentUsers() { return  currentUsers; }
}
