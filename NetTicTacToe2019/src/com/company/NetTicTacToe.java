package com.company;

import client.Client;
import server.Server;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;


public class NetTicTacToe extends JFrame implements Runnable {

    static boolean serverCanGo = false;
    static boolean clientCanGo = true;

    static int serverScore=0;
    static int clientScore=0;

    public static InetAddress ia = null;

    public static boolean chatServ = false;

    boolean modeReady = false;
    public static int[][] board;
    JButton serverButton;
    JButton clientButton;
    JPanel innerPanel;
    JPanel ipPanel;
    public static JTextField ipField;

    JButton ipButton;
    JLabel fromServerLabel;
    JLabel fromClientLabel;


    ServerSocket serverSocket = null;
    Socket clientSocket = null;
    Socket remoteClientSocket = null;
    PrintWriter fromServerToClient = null;
    BufferedReader toServerFromClient = null;
    PrintWriter fromClientToServer = null;
    BufferedReader toClientFromServer = null;

    GameFrame gf = null;

    static final int N = 3;



    public static boolean isServer = false;

    private final static int MY_WIDTH = 400;
    private final static int MY_HEIGHT = 200;
    private final static int MENU_HEIGHT = 20;
    JPanel borderPanel = null;


    static boolean clientSideConnectionOk = false;
    static boolean serverSideConnectionOk = false;


    static int elderFrameXLocation = 0;
    static int elderFrameYLocation = 0;

    static boolean clientDataReady = false;
    static boolean serverDataReady = false;


    static int verStep;
    static int horStep;


    public NetTicTacToe() {


        super("Net Tic Tac Toe 2019");


        this.setSize(new Dimension(MY_WIDTH, MY_HEIGHT));
        this.setResizable(false);


        board = new int[N][N];


        for (int i = 0; i < N; i++) {

            for (int j = 0; j < N; j++) {

                board[i][j] = 0;
            }
        }


        borderPanel = new JPanel();
        borderPanel.setSize(300, 20);
        borderPanel.setMinimumSize(new Dimension(300, 20));

        serverButton = new JButton("I want to be a server!");
        ipButton = new JButton("Connect!");

        //menu
        JMenuBar menuBar = new JMenuBar();
        menuBar.setPreferredSize(new Dimension(100,MENU_HEIGHT));

        JMenu menuFile = new JMenu("File");
        JMenu menuHelp = new JMenu("Help");

        JMenuItem menuItemExit = new JMenuItem("Exit");
        JMenuItem menuItemAbout = new JMenuItem("About us");

        menuFile.add(menuItemExit);
        menuHelp.add(menuItemAbout);

        menuBar.add(menuFile);
        menuBar.add(menuHelp);

        super.setJMenuBar(menuBar);

        menuItemExit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        menuItemAbout.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame frameAbout = new JFrame("About us");
                frameAbout.setSize(400,200);
                JPanel panel = new JPanel();
                panel.setLayout(null);

                JTextArea textArea = new JTextArea();
                textArea.setLineWrap(true);
                textArea.setWrapStyleWord(true);
                textArea.setEditable(false);
                textArea.setBounds(40,10,300,140);
                textArea.append("\tDeveloper: Lukyanchik Ivan\n\n");
                textArea.append("\tGroup: 751004\n\n");
                textArea.append("\tSoftware: Net Tic Tac Toe 2019\n");
                textArea.append("--------------------------------------------------------------------------\n\n");
                textArea.append("               BSUIR, POIT, 2019. All rights reserved. \n");

                panel.add(textArea);

                frameAbout.add(panel);
                frameAbout.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
                frameAbout.setResizable(false);
                frameAbout.setLocationRelativeTo(NetTicTacToe.this);
                frameAbout.setVisible(true);
            }
        });
        //end menu


        ipButton.addActionListener(new ActionListener() { //Connect

            @Override
            public void actionPerformed(ActionEvent e) {
                if (!ipField.getText().equals("Input server's IP here...")){
                try {

                    remoteClientSocket = new Socket(ia = InetAddress.getByName(ipField.getText()), 1234);
                    fromClientToServer = new PrintWriter(remoteClientSocket.getOutputStream(), true);
                    toClientFromServer = new BufferedReader(new InputStreamReader(remoteClientSocket.getInputStream()));

                    clientSideConnectionOk = true;

                    gf = new GameFrame("Game of Client");
                    JOptionPane.showMessageDialog(null, "Вы ходите первым","Начинайте!", JOptionPane.INFORMATION_MESSAGE);

                    NetTicTacToe.this.dispose();
                    NetTicTacToe.this.hide();

                }
                catch (UnknownHostException uhe) {
                    try {
                        System.err.println("Don't know about host: " + ia.getHostAddress());
                        JOptionPane.showMessageDialog(null, "Don't know about host: " + ia.getHostAddress(), "Sorry, fatal error!", 1);
                        System.exit(1);
                    }catch (Exception ex0){
                        JOptionPane.showMessageDialog(null, "Check the entered IP address ", "Sorry, fatal error!", 1);
                    }
                } catch (IOException ioe) {
                    System.err.println("Couldn't get I/O for the connection to: " + ia.getHostAddress());
                    JOptionPane.showMessageDialog(null, "Couldn't get I/O for the connection to: " + ia.getHostAddress(), "Sorry, fatal error!", 1);
                    System.exit(1);
                }
                }
                else{
                    JOptionPane.showMessageDialog(NetTicTacToe.this, "Enter correct IP address in the TextField: ","Be careful!", 1);
                }

            }
        });


        serverButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                clientButton.setEnabled(false);

                InetAddress thisIp = null;

                try {
                    thisIp = InetAddress.getLocalHost();
                } catch (UnknownHostException ex) {
                    JOptionPane.showMessageDialog(null, "Fatal error! Can't get IP!", "Sorry, fatal error!", 1);
                    System.exit(1);
                }


                setTitle("Your IP: " + thisIp.getHostAddress());

                isServer = true;
                modeReady = true;


                try {


                    serverSocket = new ServerSocket(1234);


                } catch (IOException ioe) {

                    JOptionPane.showMessageDialog(null, "Error!");
                    System.exit(1);
                }



                serverSideConnectionOk = true;

                serverButton.setEnabled(false);

                JOptionPane.showMessageDialog(null, "Ожидайте подключения соперника!\nВаш IP: "+thisIp.getHostAddress(),"Ожидайте!", JOptionPane.INFORMATION_MESSAGE);

            }
        });

        clientButton = new JButton("I want to be a client!");
        ipField = new JTextField("Input server's IP here...");
        //ipField = new JTextField("127.0.0.1");
        ipField.setColumns(15);
        ipField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (  ((c < '0') || (c > '9')) && (c != '.') && (c != '\b') ) e.consume();
            }
        });


        ipField.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent me) {

                    // clear the box...

                    ipField.setText("");
            }
        });
        ipField.setEnabled(false);
        ipButton.setEnabled(false);

        clientButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                clientButton.setEnabled(false);

                serverButton.setEnabled(false);
                ipField.setEnabled(true);
                ipButton.setEnabled(true);


                isServer = false;
                modeReady = true;
            }
        });


        innerPanel = new JPanel();
        innerPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 30));
        //innerPanel.setBackground(Color.red);


        ipPanel = new JPanel();
        //ipPanel.setBackground(Color.green);
        ipPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 20));


        this.getContentPane().add(innerPanel);

        innerPanel.add(serverButton);
        innerPanel.add(clientButton);

        ipPanel.add(ipField);
        ipPanel.add(ipButton);

        innerPanel.add(ipPanel);
        innerPanel.add(borderPanel);



        this.setDefaultCloseOperation(EXIT_ON_CLOSE);

        Toolkit t = getToolkit();

        Dimension screenSize = t.getScreenSize();


        elderFrameXLocation = screenSize.width / 2 - MY_WIDTH / 2;
        elderFrameYLocation = (screenSize.height / 2 - MY_HEIGHT / 2) - 265;

        this.setLocation(elderFrameXLocation, elderFrameYLocation);
        //this.setLocationRelativeTo(null);

        this.setVisible(true);
    }

    public static boolean isDraw(){

        System.out.println("Check for draw...");

        for(int i = 0; i < N; i++){

            for(int j = 0; j < N; j++){

                if( board[i][j] == 0 ){

                    return false;
                }
            }
        }

        System.out.println("All cells are filled!");

        if( isKrestikWin() ){

            System.out.println("Krestik win, not draw!");
            return false;
        }

        if( isNolikWin() ){

            System.out.println("Nolik win, not draw!");
            return false;
        }


        return true;
    }

    public static boolean isKrestikWin() {

        System.out.println("Is krestiki win?");



        for (int i = 0; i < N; i++) {

            boolean horWin = true;

            for (int j = 0; j < N; j++) {

                if (board[i][j] != 1) {

                    horWin = false;
                    break;
                }
            }

            if (horWin) {

                return true;
            }
        }


        for (int i = 0; i < N; i++) {


            boolean verWin = true;

            for (int j = 0; j < N; j++) {

                if (board[j][i] != 1) {

                    verWin = false;
                    break;
                }
            }

            if (verWin) {

                return true;
            }
        }


        boolean diagWin = true;

        for (int i = 0; i < N; i++) {

            int j = i;

            if (board[i][j] != 1) {

                diagWin = false;
                break;
            }
        }

        if (diagWin) {


            return true;
        }

        diagWin = true;



        for (int i = 0; i < N; i++) {

            int j = N - 1 - i;

            if (board[i][j] != 1) {

                diagWin = false;
                break;
            }
        }

        if (diagWin) {


            return true;
        }



        return false;
    }

    // то же самое для ноликов
    public static boolean isNolikWin() {


        for (int i = 0; i < N; i++) {

            boolean horWin = true;

            for (int j = 0; j < N; j++) {

                if (board[i][j] != 2) {

                    horWin = false;
                    break;
                }
            }

            if (horWin) {

                return true;
            }
        }

        for (int i = 0; i < N; i++) {


            boolean verWin = true;

            for (int j = 0; j < N; j++) {

                if (board[j][i] != 2) {

                    verWin = false;
                    break;
                }
            }

            if (verWin) {

                return true;
            }
        }



        boolean diagWin = true;

        for (int i = 0; i < N; i++) {

            int j = i;

            if (board[i][j] != 2) {

                diagWin = false;
                break;
            }
        }

        if (diagWin) {

            return true;
        }

        diagWin = true;

        for (int i = 0; i < N; i++) {

            int j = N - 1 - i;

            if (board[i][j] != 2) {

                diagWin = false;
                break;
            }
        }

        if (diagWin) {

            return true;
        }

        return false;
    }

    @Override
    public void run() {

        while (!modeReady) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
                System.out.println("Problem with thread sleeping!");
            }
        }


        if (!isServer) { //client

            while (!clientSideConnectionOk) {
                try {
                    Thread.sleep(50);
                } catch (InterruptedException ex) {
                }
            }

            while (true) {
                try {
                    while (true) {

                        if (clientDataReady) {


                            System.out.println("Client: ready to send data!");

                            String s = "";

                            s = s.concat(String.valueOf(verStep));
                            s = s.concat(" ");
                            s = s.concat(String.valueOf(horStep));

                            fromClientToServer.println(s);

                            clientDataReady = false;

                            System.out.println("Client: data transported!");
                            break;

                        } else {
                            // иначе - ждем, потом - все снова
                            try {
                                Thread.sleep(5);
                            } catch (InterruptedException ex) {
                            }
                        }
                    }

                    String str;

                    try {
                        while ((str = toClientFromServer.readLine()) != null) {


                            System.out.println("Client called from server...");


                            String[] words = str.split(" ");


                            int verStep = Integer.parseInt(words[0]);
                            int horStep = Integer.parseInt(words[1]);

                            // ставим в полученные координаты символ нолика
                            board[verStep][horStep] = 2;

                            // перерисовываем доску
                            gf.repaint();

                            // теперь мы ( клиент ) можем ходить
                            clientCanGo = true;

                            break;
                        }
                    }catch (SocketException sex){
                        JOptionPane.showMessageDialog(null, "Server disconnected...","Очень жаль!", JOptionPane.INFORMATION_MESSAGE);
                        System.exit(1);
                    }
                }
                catch (Exception ex) {
                    Logger.getLogger(NetTicTacToe.class.getName()).log(Level.SEVERE, null, ex);
                    JOptionPane.showMessageDialog(null, "Server disconnected...","Очень жаль!", JOptionPane.INFORMATION_MESSAGE);
                    System.exit(1);
                }

            }
        } else { //server
            try {

                clientSocket = serverSocket.accept();

                NetTicTacToe.this.dispose();

                fromServerToClient = new PrintWriter(clientSocket.getOutputStream(), true);
                toServerFromClient = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                java.awt.Toolkit.getDefaultToolkit().beep();
                Thread.sleep(700);
                java.awt.Toolkit.getDefaultToolkit().beep();

                String inputLine;
                String outputLine;


                gf = new GameFrame("Game of Server");
                JOptionPane.showMessageDialog(null, "Вы ходите вторым","Ожидайте!", JOptionPane.INFORMATION_MESSAGE);


                while (true) {


                    while ((inputLine = toServerFromClient.readLine()) != null) {


                        System.out.println("Client say: " + inputLine);

                        String[] coords = inputLine.split(" ");

                        int vStep = Integer.parseInt(coords[0]);
                        int hStep = Integer.parseInt(coords[1]);

                        if (board[vStep][hStep] != 0) {
                            java.awt.Toolkit.getDefaultToolkit().beep();
                        } else {
                            board[vStep][hStep] = 1;
                        }


                        gf.repaint();


                        serverCanGo = true;

                        break;
                    }


                    while( true ){
                        if( serverDataReady ){

                            System.out.println("Server: starting transmission...");
                            outputLine = String.valueOf(verStep);

                            outputLine = outputLine.concat(" ");

                            outputLine = outputLine.concat(String.valueOf(horStep));

                            serverDataReady = false;

                            fromServerToClient.println(outputLine);

                            break;
                        }
                        else{

                            Thread.sleep(5);
                        }

                    }
                }

            } catch (InterruptedException ex) {
                Logger.getLogger(NetTicTacToe.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ioe) {
                JOptionPane.showMessageDialog(null, "Client disconnected...","Очень жаль!", JOptionPane.INFORMATION_MESSAGE);
                System.exit(1);
            }

        }

    }


    public static class GameFrame extends JFrame {

        public static JTextArea textArea;
        public static JTextArea scoreArea;
        public static JButton sendBtn;
        public static JButton ExitBtn;
        public static JTextField textMessage;
        public static GamePanel gamePanel;


        final static int width = 400;
        final static int height = 400;
        GameFrame.GamePanel gp;
        private static BufferedReader in;
        private static BufferedWriter out;
        private static ServerSocket serverSocket;
        private static Socket clientSocket;
        private static BufferedReader reader;


        public GameFrame(String n) throws IOException {

            super(n);

            //menu
            JMenuBar menuBar = new JMenuBar();

            JMenu menuFile = new JMenu("File");
            JMenu menuHelp = new JMenu("Help");

            JMenuItem menuItemExit = new JMenuItem("Exit");
            JMenuItem menuItemAbout = new JMenuItem("About us");

            menuFile.add(menuItemExit);
            menuHelp.add(menuItemAbout);

            menuBar.add(menuFile);
            menuBar.add(menuHelp);

            //GameFrame.this.setJMenuBar(menuBar);

            menuItemExit.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    System.exit(0);
                }
            });
            //end menu

            sendBtn = new JButton("SEND");
            sendBtn.setBounds(400, 400, 200, 100);
            GameFrame.this.add(sendBtn);

            ExitBtn = new JButton("EXIT");
            ExitBtn.setBounds(400, 500, 200, 25);
            GameFrame.this.add(ExitBtn);

            ExitBtn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    System.exit(0);
                }
            });

            textMessage = new JTextField("Enter your message for chatting here:");
            textMessage.setBounds(0, 400, 400, 100);
            textMessage.setToolTipText("Enter your message for chatting here:");

            textMessage.addMouseListener(new MouseAdapter() {

                @Override
                public void mouseClicked(MouseEvent me) {
                        // clear the box...

                        textMessage.setText("");
                }
            });
            GameFrame.this.add(textMessage);


            sendBtn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (!textMessage.getText().equals("Enter your message for chatting here:")) {
                        Client.textToClient = textMessage.getText();
                        System.out.println(Client.textToClient);
                        textMessage.setText("Enter your message for chatting here:");
                    }
                    else{
                        JOptionPane.showMessageDialog(GameFrame.this, "Enter your message in the TextField for chatting ","Be careful!", 1);
                    }
                }
            });

            textArea = new JTextArea(16,15);
            textArea.setLineWrap(true);
            textArea.setWrapStyleWord(true);
            textArea.setToolTipText("Here you can see your chat with rival");
            textArea.setEditable(false);
            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setBounds(400, 0, 185, 400);
            scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
            textArea.append("               Your chat:\n");
            GameFrame.this.add(scrollPane);

            scoreArea = new JTextArea(16,15);
            scoreArea.setLineWrap(true);
            scoreArea.setWrapStyleWord(true);
            scoreArea.setForeground(Color.BLUE);
            scoreArea.setText("Your score: \t Server " + serverScore + " - " + clientScore + " Client ");
            scoreArea.setEditable(false);
            scoreArea.setBounds(0,500,400,25);
            GameFrame.this.add(scoreArea);

            gp = new GameFrame.GamePanel();


            gp.setPreferredSize(new Dimension(400+200, 400+163));//400+138


            getContentPane().add(gp);

            setSize(new Dimension(400+200, 400+163));

            setResizable(false);


            setLocation(elderFrameXLocation, elderFrameYLocation + MY_HEIGHT);
            //setLocationRelativeTo(null);

            this.setDefaultCloseOperation(EXIT_ON_CLOSE);

            setVisible(true);
        }


        public static class GamePanel extends JPanel {

           public boolean gameOverHappened = false;
           public boolean krestikLastClick;

            public GamePanel() {
                GameFrame.gamePanel = this;////////////////////////потом удалить
                this.addMouseListener(new MouseAdapter() {

                    @Override
                    public void mouseClicked(MouseEvent me) {


                        if( (isServer && !serverCanGo) || (!isServer && !clientCanGo) ){

                            return;
                        }


                        int type;


                        if (!isServer) {

                            type = 1;//клиент
                            krestikLastClick = true;
                        } else {

                            type = 2;//сервер
                            krestikLastClick = false;
                        }


                        int x = me.getX();
                        int y = me.getY();

                        int xIndex = 0;
                        int yIndex = 0;


                        if ( (x >= width) || (y >= height) ) return;

                        for (int i = 0; i < N; i++) {

                            if (x < (i + 1) * width / N) {

                                xIndex = i;
                                break;
                            }
                        }
                        for (int i = 0; i < N; i++) {

                            if (y < (i + 1) * height / N) {

                                yIndex = i;
                                break;
                            }
                        }


                        if (board[yIndex][xIndex] == 0) {


                            board[yIndex][xIndex] = type;


                            verStep = yIndex;
                            horStep = xIndex;


                            if( !isServer ){


                                clientDataReady = true;


                                clientCanGo = false;
                            }

                            else{

                                serverDataReady = true;
                                serverCanGo = false;
                            }
                            System.out.println("Client: new click processed!");
                        } else {

                            java.awt.Toolkit.getDefaultToolkit().beep();
                        }


                        repaint();
                    }
                });
            }

            @Override
            public void paintComponent(Graphics g) {

                Graphics2D g2d = (Graphics2D) g;


                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2d.setColor(Color.white);
                g2d.fillRect(0, 0, width, height);


                g2d.setColor(Color.BLACK);

                //рисуем разметку
                g2d.setStroke(new BasicStroke(5.0f));//толщина линий
                for (int i = 0; i < N - 1; i++) {

                    g2d.drawLine((i + 1) * width / N, 0, (i + 1) * width / N, height-2);
                }
                for (int i = 0; i < N - 1; i++) {

                    g2d.drawLine(0, (i + 1) * height / N, width, (i + 1) * height / N);
                }


                for (int i = 0; i < N; i++) {

                    for (int j = 0; j < N; j++) {

                        if (board[i][j] == 1) {

                            drawKrestik(g2d, j, i, width, height, N);
                        } else {

                            if (board[i][j] == 2) {

                                drawNolik(g2d, j, i, width, height, N);
                            }
                        }
                    }
                }


                if (isKrestikWin() && !gameOverHappened ) {

                    clientScore++;
                    scoreArea.setText("Your score: \t Server " + serverScore + " - " + clientScore + " Client ");

                    System.out.println("Krestik win!!!");

                    gameOverHappened = true;

                    SwingUtilities.invokeLater(new Runnable(){

                        @Override
                        public void run() {


                            if( isServer ){

                                JOptionPane.showMessageDialog(null, "Вы проиграли!","Очень жаль!", JOptionPane.INFORMATION_MESSAGE);
                            }
                            else{

                                JOptionPane.showMessageDialog(null, "Вы победили!","Поздравляю!", JOptionPane.INFORMATION_MESSAGE);
                            }

                        }
                    });
                    //повтор игры, если пользователь захотел
                    JFrame repeatFrame = new JFrame("Would you like to repeat game?");
                    repeatFrame.setSize(400,200);
                    JPanel panel = new JPanel();
                    panel.setLayout(null);

                    JButton yesBtn = new JButton("Of course I want!!!");
                    yesBtn.setBounds(50,30,180,100);
                    JButton noBtn = new JButton("NO");
                    noBtn.setBounds(250,30,100,100);
                    panel.add(yesBtn);
                    panel.add(noBtn);

                    noBtn.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            System.exit(0);
                        }
                    });

                    yesBtn.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {

                            gameOverHappened = false;
                            for (int i = 0; i < N; i++) {

                                for (int j = 0; j < N; j++) {

                                    NetTicTacToe.board[i][j] = 0;
                                }
                            }
                            repaint();
                            repeatFrame.hide();

                            if (isServer) {JOptionPane.showMessageDialog(null, "Вы ходите первым","Начинайте!", JOptionPane.INFORMATION_MESSAGE);}
                            else {JOptionPane.showMessageDialog(null, "Вы ходите вторым","Ожидайте!", JOptionPane.INFORMATION_MESSAGE);}

                        }
                    });

                    repeatFrame.add(panel);
                    repeatFrame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
                    repeatFrame.setResizable(false);
                    repeatFrame.setLocationRelativeTo(GameFrame.GamePanel.this);
                    repeatFrame.setVisible(true);


                    return;
                }
                if (isNolikWin() && !gameOverHappened ) {

                    serverScore++;
                    scoreArea.setText("Your score: \t Server " + serverScore + " - " + clientScore + " Client ");

                    gameOverHappened = true;

                    SwingUtilities.invokeLater(new Runnable() {

                        @Override
                        public void run() {



                            if( isServer ){

                                JOptionPane.showMessageDialog(null, "Вы победили!","Поздравляю!", JOptionPane.INFORMATION_MESSAGE);
                            }
                            else{

                                JOptionPane.showMessageDialog(null, "Вы проиграли!","Очень жаль!", JOptionPane.INFORMATION_MESSAGE);
                            }
                        }
                    });

                    //повтор игры, если пользователь захотел
                    JFrame repeatFrame = new JFrame("Would you like to repeat game?");
                    repeatFrame.setSize(400,200);
                    JPanel panel = new JPanel();
                    panel.setLayout(null);

                    JButton yesBtn = new JButton("Of course I want!!!");
                    yesBtn.setBounds(50,30,180,100);
                    JButton noBtn = new JButton("NO");
                    noBtn.setBounds(250,30,100,100);
                    panel.add(yesBtn);
                    panel.add(noBtn);

                    noBtn.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            System.exit(0);
                        }
                    });

                    yesBtn.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {

                            gameOverHappened = false;
                            for (int i = 0; i < N; i++) {

                                for (int j = 0; j < N; j++) {

                                    NetTicTacToe.board[i][j] = 0;
                                }
                            }
                            repaint();
                            repeatFrame.hide();

                            if (!isServer) {JOptionPane.showMessageDialog(null, "Вы ходите первым","Начинайте!", JOptionPane.INFORMATION_MESSAGE);}
                            else {JOptionPane.showMessageDialog(null, "Вы ходите вторым","Ожидайте!", JOptionPane.INFORMATION_MESSAGE);}
                        }
                    });

                    repeatFrame.add(panel);
                    repeatFrame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
                    repeatFrame.setResizable(false);
                    repeatFrame.setLocationRelativeTo(GameFrame.GamePanel.this);
                    repeatFrame.setVisible(true);

                    return;
                }


                if( isDraw() && !gameOverHappened ){

                    serverScore++;
                    clientScore++;
                    scoreArea.setText("Your score: \t Server " + serverScore + " - " + clientScore + " Client ");

                    gameOverHappened = true;

                    SwingUtilities.invokeLater(new Runnable() {

                        @Override
                        public void run() {

                            JOptionPane.showMessageDialog(null, "Ничья!!!","Бывает и такое!", JOptionPane.INFORMATION_MESSAGE);
                        }
                    });

                    //повтор игры, если пользователь захотел
                    JFrame repeatFrame = new JFrame("Would you like to repeat game?");
                    repeatFrame.setSize(400,200);
                    JPanel panel = new JPanel();
                    panel.setLayout(null);

                    JButton yesBtn = new JButton("Of course I want!!!");
                    yesBtn.setBounds(50,30,180,100);
                    JButton noBtn = new JButton("NO");
                    noBtn.setBounds(250,30,100,100);
                    panel.add(yesBtn);
                    panel.add(noBtn);

                    noBtn.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            System.exit(0);
                        }
                    });

                    yesBtn.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            gameOverHappened = false;
                            for (int i = 0; i < N; i++) {

                                for (int j = 0; j < N; j++) {

                                    NetTicTacToe.board[i][j] = 0;
                                }
                            }
                            repaint();
                            repeatFrame.hide();

                            if ( ( (isServer/*нолик*/) && (krestikLastClick) ) || ( (!isServer/*крестик*/) && (!krestikLastClick) ) ) {JOptionPane.showMessageDialog(null, "Вы ходите первым","Начинайте!", JOptionPane.INFORMATION_MESSAGE);}
                            else {JOptionPane.showMessageDialog(null, "Вы ходите вторым","Ожидайте!", JOptionPane.INFORMATION_MESSAGE);}

                        }
                    });

                    repeatFrame.add(panel);
                    repeatFrame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
                    repeatFrame.setResizable(false);
                    repeatFrame.setLocationRelativeTo(GameFrame.GamePanel.this);
                    repeatFrame.setVisible(true);

                    return;
                }
            }


            public void drawKrestik(Graphics2D g2d, int xStep, int yStep, int width, int height, int N) {

                g2d.setColor(Color.red);

                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2d.setStroke(new BasicStroke(8.0f));

                int xLeft = xStep * width / N + 25;
                int xRight = (xStep + 1) * width / N - 25;

                int yUp = yStep * height / N + 20;
                int yLow = (yStep + 1) * height / N - 20;

                g2d.drawLine(xLeft, yUp, xRight, yLow);
                g2d.drawLine(xLeft, yLow, xRight, yUp);
            }

            public void drawNolik(Graphics2D g2d, int xStep, int yStep, int width, int height, int N) {

                g2d.setColor(Color.blue);

                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2d.setStroke(new BasicStroke(8.0f));

                int xLeft = xStep * width / N + 25;
                int yUp = yStep * height / N + 25;

                int diameter = width / N - 50;

                g2d.drawOval(xLeft, yUp, diameter, diameter);
            }
        }
    }

    static class MyThread implements Runnable {

        boolean flag = true;


        @Override
        public void run(){
            while (true){
            if ( (isServer) && (serverSideConnectionOk) &&(flag) ){
                NetTicTacToe.chatServ = true;
                Thread myThread2 = new Thread(new MyThread2());
                myThread2.start();
                new Client();
                flag = false;
            }
            else if ( (!isServer) && (clientSideConnectionOk) &&(flag) ){
                NetTicTacToe.chatServ = false;
                new Client();
                flag = false;
            }
            else {
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            }
        }
    }
    static class MyThread2 implements Runnable{
        @Override
        public void run() {
            new Server();
        }
    }


    public static void main(String[] args) {

        Thread thread = new Thread(new NetTicTacToe());
        thread.start();

        Thread myThread = new Thread(new MyThread());
        myThread.start();
    }
}

