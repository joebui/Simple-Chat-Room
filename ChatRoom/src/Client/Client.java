package Client;
import java.awt.*; import javax.swing.*;
import java.awt.event.*;
import javax.swing.filechooser.*;
import javax.swing.text.*;
import java.io.*; import java.net.*; import javax.swing.event.*;

public class Client extends JFrame {
    private Socket s, socket;
    private String userName;
    // main menu panel
    private JLabel llb = new JLabel("Java students' chat room");
    private JButton loggin = new JButton("Log in");
    private JButton signUp = new JButton("Sign Up");
    private JButton exitMenu = new JButton("Exit");
    // logging panel
    private JButton login = new JButton("Login");
    private JButton exitLogin = new JButton("Exit");
    private JTextField userLogin = new JTextField(10);
    private JPasswordField passLogin = new JPasswordField(10);
    private JLabel text1Login = new JLabel("Username :");
    private JLabel text2Login = new JLabel("Password :");
    // sign up panel
    private JButton submit = new JButton("Register");
    private JButton exit = new JButton("Exit");
    private JTextField user = new JTextField(10);
    private JPasswordField pass = new JPasswordField(10);
    private JPasswordField repass = new JPasswordField(10);
    private JLabel text1 = new JLabel("Name:");
    private JLabel text2 = new JLabel("Password:");
    private JLabel text3 = new JLabel("Retype Password :");
    // chatting panel
    private String[] status = {"Available", "Busy", "Do not Disturb", "Invisible"};  // array of status
    private JComboBox sttList = new JComboBox();
    private JButton exitButton = new JButton("Exit");
    private JTextPane allConversation = new JTextPane();
    protected StyledDocument doc = allConversation.getStyledDocument();
    protected JTextArea allUsers = new JTextArea(30, 10);
    private Client.Chat chat = new Client.Chat();
    private JButton sending = new JButton("Send");
    private JFileChooser choser = new JFileChooser();

    private ImageIcon happy_img = new ImageIcon("images/happy.png");
    private ImageIcon teeth_img = new ImageIcon("images/teeth.png");
    private ImageIcon sad_img = new ImageIcon("images/sad.png");
    private ImageIcon cry_img = new ImageIcon("images/cry.png");
    private ImageIcon tongue_img = new ImageIcon("images/tongue.png");
    private Image logo = new ImageIcon("images/chattingLogo.jpg").getImage();
    private JPanel menuPanel = new JPanel();
    private JPanel loginPanel = new JPanel();
    private JPanel signupPanel = new JPanel();
    private JPanel chatPanel = new JPanel();

    private ClientThread clientThread;
    private DataOutputStream outputToServer;  // send output to server
    private DataInputStream inputFromServer;  // get input from server

    public Client() {
        Font font = new Font("Arial", Font.BOLD, 16);
        llb.setFont(new Font("Arial", Font.BOLD, 20));
        login.setBackground(Color.white);
        exitLogin.setBackground(Color.white);
        submit.setBackground(Color.white);
        exit.setBackground(Color.white);
        loggin.setBackground(Color.white);
        loggin.setFont(font);
        signUp.setBackground(Color.white);
        signUp.setFont(font);
        exitMenu.setBackground(Color.white);
        exitMenu.setFont(font);
        allConversation.setFont(new Font("Arial", Font.PLAIN, 14));
        allConversation.setBackground(Color.gray);  allConversation.setForeground(Color.white);
        allUsers.setBackground(Color.gray);  allUsers.setForeground(Color.white);
        sttList.setBackground(Color.white);
        exitButton.setBackground(Color.white);
        exitButton.setFont(font);
        sending.setBackground(Color.white);

        menuPanel.setLayout(new GridLayout(2,0,0,10));
        menuPanel.setBackground(new Color(0, 204, 204));
        JPanel label = new JPanel();
        label.setLayout(new FlowLayout(FlowLayout.CENTER,0,20));
        label.setBackground(new Color(0, 204, 204));
        label.add(llb);
        menuPanel.add(label);
        JPanel buttons = new JPanel();
        buttons.setLayout(new FlowLayout(FlowLayout.CENTER,20,0));
        buttons.setBackground(new Color(0, 204, 204));
        buttons.add(loggin);  buttons.add(signUp);
        buttons.add(exitMenu);
        menuPanel.add(buttons);

        /* LOGIN */

        loginPanel.setLayout(new GridLayout(3,0));
        loginPanel.setBackground(new Color(0, 204, 204));
        JPanel idPanel1 = new JPanel();
        idPanel1.setLayout(new FlowLayout(FlowLayout.CENTER,20,20));
        idPanel1.setBackground(new Color(0, 204, 204));
        idPanel1.add(text1Login); idPanel1.add(userLogin);
        loginPanel.add(idPanel1);
        JPanel passPanel1 = new JPanel();
        passPanel1.setLayout(new FlowLayout(FlowLayout.CENTER,20,20));
        passPanel1.setBackground(new Color(0, 204, 204));
        passPanel1.add(text2Login); passPanel1.add(passLogin);
        loginPanel.add(passPanel1);
        JPanel buttonPanel1 = new JPanel();
        buttonPanel1.setLayout(new FlowLayout(FlowLayout.CENTER,20,10));
        buttonPanel1.setBackground(new Color(0, 204, 204));
        buttonPanel1.add(login); buttonPanel1.add(exit);
        loginPanel.add(buttonPanel1);

        /* SIGN UP */

        signupPanel.setLayout(new GridLayout(4,0));
        signupPanel.setBackground(new Color(0, 204, 204));
        JPanel idPanel2 = new JPanel();
        idPanel2.setLayout(new FlowLayout(FlowLayout.CENTER,20,20));
        idPanel2.setBackground(new Color(0, 204, 204));
        idPanel2.add(text1); idPanel2.add(user);
        signupPanel.add(idPanel2);
        JPanel passPanel2 = new JPanel();
        passPanel2.setLayout(new FlowLayout(FlowLayout.CENTER,20,20));
        passPanel2.setBackground(new Color(0, 204, 204));
        passPanel2.add(text2); passPanel2.add(pass);
        signupPanel.add(passPanel2);
        JPanel rePassPanel2 = new JPanel();
        rePassPanel2.setLayout(new FlowLayout(FlowLayout.CENTER,20,20));
        rePassPanel2.setBackground(new Color(0, 204, 204));
        rePassPanel2.add(text3); rePassPanel2.add(repass);
        signupPanel.add(rePassPanel2);
        JPanel buttonPanel2 = new JPanel();
        buttonPanel2.setLayout(new FlowLayout(FlowLayout.CENTER,20,10));
        buttonPanel2.setBackground(new Color(0, 204, 204));
        buttonPanel2.add(submit); buttonPanel2.add(exitLogin);
        signupPanel.add(buttonPanel2);

        /* CHAT AREA */

        chatPanel.setLayout(new GridLayout(0,2,10,0));
        chatPanel.setBackground(new Color(0, 204, 204));

        JPanel leftP = new JPanel();
        leftP.setLayout(new BorderLayout(0,10));
        leftP.setBackground(new Color(0, 204, 204));
        allConversation.setEditable(false);
        leftP.add(new JScrollPane(allConversation), BorderLayout.CENTER);
        leftP.add(chat, BorderLayout.SOUTH);
        for (byte i = 0; i < status.length; i++) {
            sttList.addItem(status[i]);
        }
        sttList.addActionListener(new SttListAction());
        leftP.add(sttList, BorderLayout.NORTH);
        chatPanel.add(leftP);

        JPanel rightP = new JPanel();
        rightP.setLayout(new BorderLayout(0,10));
        rightP.setBackground(new Color(0, 204, 204));
        allUsers.append("\t- - - - Notification Center - - - -\n\n");
        allUsers.setEditable(false);
        rightP.add(allUsers, BorderLayout.NORTH);
        JPanel b1 = new JPanel();
        b1.setBackground(new Color(0, 204, 204));
        b1.add(sending);
        rightP.add(b1, BorderLayout.CENTER);
        JPanel b2 = new JPanel();
        b2.setBackground(new Color(0, 204, 204));
        b2.add(exitButton);
        rightP.add(b2, BorderLayout.SOUTH);
        chatPanel.add(rightP);

        loggin.addActionListener(new MenuActionListener());
        signUp.addActionListener(new MenuActionListener());
        exitMenu.addActionListener(new MenuActionListener());
        login.addActionListener(new LoggingMenuAction());
        exitLogin.addActionListener(new LoggingMenuAction());
        submit.addActionListener(new SignUpMenuAction());
        exit.addActionListener(new SignUpMenuAction());
        sending.addActionListener(new ChatAreaAction());
        exitButton.addActionListener(new ChatAreaAction());
        this.addWindowListener(new CloseWindowsAction());
        this.editText();

        add(menuPanel);
        setTitle("Main Menu");
        setVisible(true);
        setIconImage(logo);
        setResizable(false);
        setSize(350,200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        try {
            s = new Socket("localhost", 7500);  // establish connection to server
            outputToServer = new DataOutputStream(s.getOutputStream());
            inputFromServer = new DataInputStream(s.getInputStream());
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void activateThread() {  // create a thread to get notifications and messages from server
        clientThread = new ClientThread(this, this.inputFromServer);
    }

    public void save() {  // save files uploaded by the server
        System.out.println("aaaaaaaaaaaaaaaaaaaaaaaaaa");
        try {
            socket = new Socket("localhost",8000);
            FileNameExtensionFilter filter = new FileNameExtensionFilter("pdf,java,txt,zip","pdf","java", "txt", "zip");
            choser.setFileFilter(filter);
            int filesize = 1022386;
            int bytesRead;
            int currentTot = 0;
            byte[] bytearray = new byte[filesize];
            if (choser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                String direction = choser.getSelectedFile().getAbsolutePath();
                InputStream is = socket.getInputStream();
                FileOutputStream fos = new FileOutputStream(direction);
                BufferedOutputStream bos = new BufferedOutputStream(fos);
                bytesRead = is.read(bytearray, 0, bytearray.length);
                currentTot = bytesRead;
                do {
                    bytesRead = is.read(bytearray, currentTot, (bytearray.length - currentTot));
                    if (bytesRead >= 0) {
                        currentTot += bytesRead;
                    }
                } while (bytesRead > -1);
                bos.write(bytearray, 0, currentTot);
                bos.flush();
                bos.close();
                socket.close();
            }
        } catch (Exception ex) {
            System.out.println(ex);
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

    /*********************/

    class CloseWindowsAction implements WindowListener {  // disconnect the client from server when window is closed
        public void windowOpened(WindowEvent e) {}
        @Override
        public void windowClosing(WindowEvent e) {
            try {  // disconnect the client from server
                outputToServer.writeUTF("clientEXIT123");
                outputToServer.writeUTF(userName);
            } catch (Exception ex) {}
        }
        public void windowClosed(WindowEvent e) {}
        public void windowIconified(WindowEvent e) {}
        public void windowDeiconified(WindowEvent e) {}
        public void windowActivated(WindowEvent e) {}
        public void windowDeactivated(WindowEvent e) {}
    }

    class Chat extends JTextField {  // send what users type to the server when pressing "Enter"
        public Chat() {
            setColumns(40);
            addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ENTER:
                        if (!chat.getText().equals("")) {
                            try {
                                doc.insertString(doc.getLength(), " Me: " + chat.getText() + "\n", null);
                                outputToServer.writeUTF("1248CLientTOSEnd");
                                outputToServer.writeUTF(userName);
                                outputToServer.writeUTF(" " + userName + ": " + chat.getText() + "\n");
                                chat.setText("");
                            } catch (Exception ex) {}
                        }
                        break;
                    default:
                }
                }
            });
        }
    }

    class MenuActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent ae) {
            if (ae.getSource() == loggin) {
                try {
                    outputToServer.writeInt(0);  /* send 0 to indicate that users pressed Log In button */
                    outputToServer.flush();
                } catch (Exception ex) {}
                menuPanel.setVisible(false);
                add(loginPanel);
                loginPanel.setVisible(true);
                setSize(250,190);
                setTitle("Logging");
                setLocationRelativeTo(null);
            } else if (ae.getSource() == signUp) {  /* send 1 to indicate that users pressed Sign Up button */
                try {
                    outputToServer.writeInt(1);
                    outputToServer.flush();
                } catch (Exception ex) {}
                menuPanel.setVisible(false);
                add(signupPanel);
                signupPanel.setVisible(true);
                setSize(300, 250);
                setTitle("Sign in");
                setLocationRelativeTo(null);
            } else if (ae.getSource() == exitMenu) {  /* exit */
                System.exit(0);
            }
        }
    }

    class LoggingMenuAction implements ActionListener {  // handle logging action
        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == login) {
                try {                    
                    if (!userLogin.getText().equals("") && !passLogin.getText().equals("")) {
                        outputToServer.writeUTF(userLogin.getText().trim());  // send user name and password to server
                        outputToServer.writeUTF(passLogin.getText());
                        outputToServer.flush();
                        System.out.println(userLogin.getText().trim() + " " + passLogin.getText());

                        int checkLogging = inputFromServer.readInt();
                        int checkCurrent = inputFromServer.readInt();

                        System.out.println(checkLogging + " " + checkCurrent);
                        if (checkLogging == 1 && checkCurrent == 0) {  // open chat panel if users log in successfully
                            try {
                                outputToServer.writeInt(2);
                                outputToServer.flush();
                            } catch (Exception ex) {}
                            userName = userLogin.getText();
                            userLogin.setText("");  passLogin.setText("");
                            loginPanel.setVisible(false);
                            /* open chatPanel */
                            add(chatPanel);
                            chatPanel.setVisible(true);
                            sttList.setSelectedIndex(0);
                            setSize(700, 620);
                            setTitle("Student chat room - " + userName);
                            setLocationRelativeTo(null);
                            activateThread();  // activate the thread
                        } else if (checkLogging != 1) {
                            JOptionPane.showMessageDialog(null, "User name or password is not available in the system.",
                                    "Message", JOptionPane.ERROR_MESSAGE);
                        } else if (checkLogging != 0) {
                            JOptionPane.showMessageDialog(null, "This username has already joined the chat room",
                                    "Message", JOptionPane.ERROR_MESSAGE);
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "Enter user name and password to continue", "Message", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    System.out.println(ex);
                }
            } else if (e.getSource() == exitLogin) {
                System.exit(0);
            }
        }
    }

    class SignUpMenuAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == submit) {
                try {                                
                    if (!user.getText().equals("") && !pass.getText().equals("")) {
                        if (pass.getText().equals(repass.getText())) {
                            System.out.println(user.getText() + " " + pass.getText());
                            outputToServer.writeUTF(user.getText());  // send user name
                            outputToServer.writeUTF(pass.getText());  // and password to server
                            outputToServer.flush();

                            int checkSignUp = inputFromServer.readInt();

                            if (checkSignUp == 1) {
                                try {
                                    outputToServer.writeInt(0);
                                    outputToServer.flush();
                                } catch (Exception ex) {}
                                signupPanel.setVisible(false);
                                /* open loginPanel */
                                add(loginPanel);
                                loginPanel.setVisible(true);
                                setSize(350,200);
                                setTitle("Log in to chat room");
                                setLocationRelativeTo(null);
                            } else {
                                JOptionPane.showMessageDialog(null, user.getText() + " already exists. Try another id", "Message", JOptionPane.ERROR_MESSAGE);
                                user.setText(""); pass.setText(""); repass.setText("");
                            }
                        } else {
                            JOptionPane.showMessageDialog(null, "Passwords do not match. Try again", "Message", JOptionPane.ERROR_MESSAGE);
                            user.setText(""); pass.setText(""); repass.setText("");
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "Enter user name and password to continue", "Message", JOptionPane.ERROR_MESSAGE);
                        user.setText(""); pass.setText(""); repass.setText("");
                    }
                } catch (Exception ex) {
                    System.out.println(ex);
                }
            } else if (e.getSource() == exit) {
                System.exit(0);
            }
        }
    }

    class ChatAreaAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == sending) {  // send what users type to the server when clicking Send
                if (!chat.getText().equals("")) {  // output the message to server
                    try {
                        doc.insertString(doc.getLength(), " Me: " + chat.getText() + "\n", null);
                        outputToServer.writeUTF("1248CLientTOSEnd");
                        outputToServer.writeUTF(userName);
                        outputToServer.writeUTF(" " + userName + ": " + chat.getText() + "\n");
                        chat.setText("");
                    } catch (Exception ex) {}
                    chat.setText("");
                }
            } else if (e.getSource() == exitButton) {
                try {  // disconnect the client from server
                    outputToServer.writeUTF("clientEXIT123");
                    outputToServer.writeUTF(userName);
                } catch (Exception ex) {}
                System.exit(0);
            }
        }
    }

    class SttListAction implements ActionListener {  // update client's status
        @Override
        public void actionPerformed(ActionEvent a) {
            try {
                outputToServer.writeUTF("clientSTATUS123");
                outputToServer.writeUTF(userName);
                outputToServer.writeUTF(" STATUS of client \"" + userName + "\" : " + sttList.getSelectedItem() + "\n");
            } catch (Exception e) {}
        }
    }
}