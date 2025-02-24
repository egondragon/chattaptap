package com.example.websocket.client;

import com.example.websocket.myMessage;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import static java.awt.Font.BOLD;

public class ClientGUI extends JFrame implements MessageListener {
    private JPanel connectedUsersPanel, mesgPanel;
    private String sz_username;
    private MyStompClient myStompClient;
    private JScrollPane mesgPanelScrollPane;

    /*
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        MyStompClient myStompClient = new MyStompClient("TapTap");
        myStompClient.sendMessage(new myMessage("TapTap", "Hi World!"));
        myStompClient.disconnectUser("TapTap");
    }
    */
    public ClientGUI(String username_) throws ExecutionException, InterruptedException {
        super("User: " + username_);
        this.sz_username = username_;
        this.myStompClient = new MyStompClient(this, sz_username);

        setSize(1024, 768);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int option = JOptionPane.showConfirmDialog(ClientGUI.this,
                        "Do you really want to leave ?",
                        "Exit",
                        JOptionPane.YES_NO_OPTION
                );
                if (option == JOptionPane.YES_OPTION) {
                    myStompClient.disconnectUser(sz_username);
                    ClientGUI.this.dispose();
                }
            }
        });

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent evt) {
                updateMessageSize();
            }
        });

        getContentPane().setBackground(Utilities.PRIMARY_COLOR);

        addGuiComponents();
    }

    private void addGuiComponents() {
        addConnectedUserComponents();
        addChatComponents();
    }

    private void addChatComponents() {
        JPanel chatPanel = new JPanel();
        chatPanel.setLayout(new BorderLayout());
        chatPanel.setBackground(Utilities.TRANSPARENT_COLOR);

        mesgPanel = new JPanel();
        mesgPanel.setLayout(new BoxLayout(mesgPanel, BoxLayout.Y_AXIS));
        mesgPanel.setBackground(Utilities.TRANSPARENT_COLOR);

        mesgPanelScrollPane = new JScrollPane(mesgPanel);
        mesgPanelScrollPane.setBackground(Utilities.TRANSPARENT_COLOR);
        mesgPanelScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        mesgPanelScrollPane.getVerticalScrollBar().getUnitIncrement(16);
        mesgPanelScrollPane.getViewport().addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                revalidate();
                repaint();
            }
        });

        chatPanel.add(mesgPanelScrollPane, BorderLayout.CENTER);

        //JLabel mesg = new JLabel("Random Message");
        //mesg.setFont(new Font("Inter", Font.BOLD, 18));
        //mesg.setForeground(Utilities.TEXT_COLOR);
        mesgPanel.add(createChatMessageComponent(new myMessage("TapTap", "Hello World!")));

        JPanel inputPanel = new JPanel();
        inputPanel.setBorder(Utilities.addPadding(10, 10, 10, 10));
        inputPanel.setLayout(new BorderLayout());
        inputPanel.setBackground(Utilities.TRANSPARENT_COLOR);

        JTextField inputField = new JTextField();
        inputField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent evt) {
                if (evt.getKeyChar() == KeyEvent.VK_ENTER) {
                    String sz_input = inputField.getText();

                    if (!sz_input.isEmpty()) {
                        inputField.setText("");
                        mesgPanel.add(createChatMessageComponent(new myMessage("TapTap", sz_input)));
                        repaint();
                        revalidate();

                        myStompClient.sendMessage(new myMessage(sz_username, sz_input));
                    }
                }
            }
        });
        inputField.setBackground(Utilities.SECONDARY_COLOR);
        inputField.setBackground(Utilities.TEXT_COLOR);
        inputField.setBorder(Utilities.addPadding(0, 10, 0, 10));
        inputField.setFont(new Font("Inter", Font.PLAIN, 16));
        inputField.setPreferredSize(new Dimension(inputPanel.getWidth(), 50));
        inputPanel.add(inputField, BorderLayout.CENTER);
        chatPanel.add(inputPanel, BorderLayout.SOUTH);

        add(chatPanel, BorderLayout.CENTER);
    }

    private JPanel createChatMessageComponent(myMessage mesg) {
        JPanel chatMesg = new JPanel();
        chatMesg.setBackground(Utilities.TRANSPARENT_COLOR);
        chatMesg.setLayout(new BoxLayout(chatMesg, BoxLayout.Y_AXIS));
        chatMesg.setBorder(Utilities.addPadding(20, 20, 10, 20));

        JLabel usernameLabel = new JLabel(mesg.getUser());
        usernameLabel.setFont(new Font("Inter", Font.BOLD, 18));
        usernameLabel.setForeground(Utilities.TEXT_COLOR);
        chatMesg.add(usernameLabel);

        JLabel mesgLabel = new JLabel();
        mesgLabel.setText("<html>" +
                "<body style='width:" + (0.60 * getWidth()) +"'px>" +
                        mesg.getMesg() +
                "</body>" +
                "</html>"
                );
        mesgLabel.setFont(new Font("Inter", Font.BOLD, 18));
        mesgLabel.setForeground(Utilities.TEXT_COLOR);
        chatMesg.add(mesgLabel);

        return chatMesg;
    }

    private void addConnectedUserComponents() {
        connectedUsersPanel = new JPanel();
        connectedUsersPanel.setBorder(Utilities.addPadding(10, 10, 10, 10));
        connectedUsersPanel.setLayout(new BoxLayout(
                connectedUsersPanel,
                BoxLayout.Y_AXIS
        ));
        connectedUsersPanel.setBackground(Utilities.SECONDARY_COLOR);
        connectedUsersPanel.setPreferredSize(new Dimension(200, getHeight()));

        JLabel connectedUsersLabel = new JLabel("Connected Users");
        connectedUsersLabel.setFont(new Font("Inter", BOLD, 18));
        connectedUsersLabel.setForeground(Utilities.TEXT_COLOR);
        connectedUsersPanel.add(connectedUsersLabel);

        add(connectedUsersPanel, BorderLayout.WEST);
    }

    @Override
    public void onMessageReceive(myMessage mesg) {
        mesgPanel.add(createChatMessageComponent(mesg));
        revalidate();
        repaint();

        mesgPanelScrollPane.getVerticalScrollBar().setValue(Integer.MAX_VALUE);
    }

    @Override
    public void onActiveUsersUpdated(ArrayList<String> users) {
        if (connectedUsersPanel.getComponents().length >= 2) {
            connectedUsersPanel.remove(1);
        }

        JPanel userListPanel = new JPanel();
        userListPanel.setBackground(Utilities.TRANSPARENT_COLOR);
        userListPanel.setLayout(new BoxLayout(userListPanel, BoxLayout.Y_AXIS));

        for (String user: users) {
            JLabel username = new JLabel();
            username.setText(user);
            username.setForeground(Utilities.TEXT_COLOR);
            username.setFont(new Font("Inter", Font.BOLD, 16));
            userListPanel.add(username);
        }

        connectedUsersPanel.add(userListPanel);
        revalidate();
        repaint();
    }

    private void updateMessageSize() {
        for (int i = 0; i < mesgPanel.getComponents().length; i++) {
            Component component = mesgPanel.getComponent(i);
            if (component instanceof JPanel) {
                JPanel chatMessage = (JPanel)component;
                if (chatMessage.getComponent(1) instanceof JLabel) {
                    JLabel mesgLabel = (JLabel)chatMessage.getComponent(1);
                    mesgLabel.setText("<html>"+
                            "<body style='width:" + (0.60 * getWidth()) + "'px>" +
                                mesgLabel.getText() +
                            "</body>" +
                            "</html>");
                }
            }
        }
    }
}
