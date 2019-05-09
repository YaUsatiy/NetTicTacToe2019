package client;

import com.company.NetTicTacToe;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;


/**
 * Обеспечивает работу программы в режиме клиента
 */
public class Client {
    private BufferedReader in;
    public static String textToClient="";
    private PrintWriter out;
    private Socket socket;
    String name;

    /**
     * Запрашивает у пользователя ник и организовывает обмен сообщениями с
     * сервером
     */
    public Client() {
        //Scanner scan = new Scanner(NetTicTacToe.GameFrame.textToClient);
        //Scanner scan = new Scanner(System.in);
        Resender resend = null;

        final int Port = 8085;

        try {
            // Подключаемся в серверу и получаем потоки(in и out) для передачи сообщений

            if (!NetTicTacToe.chatServ){ //client
                socket = new Socket(InetAddress.getByName(NetTicTacToe.ipField.getText()), Port);
            } else {
                socket = new Socket("127.0.0.1", Port);
            }
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            if (NetTicTacToe.chatServ){
                name = "Server";
            }
            else {
                name = "Client";
            }
            out.println(name);

            // Запускаем вывод всех входящих сообщений в консоль
            resend = new Resender();
            resend.start();

            String str;
            while (true){
                synchronized (Client.textToClient){
                if (!Client.textToClient.equals("")) {
                    str = Client.textToClient;
                    Client.textToClient = "";
                    out.println(str);
                    NetTicTacToe.GameFrame.textArea.repaint();
                }
                }
            }
            
            //resend.setStop();
        } catch (Exception e) {
            if (resend != null)
              resend.setStop();
            e.printStackTrace();
        } finally {
            close();
        }
    }
//    public static void main (String[] args){
//        new Client();
//    }
    /**
     * Закрывает входной и выходной потоки и сокет
     */
    private void close() {
        try {
            in.close();
            out.close();
            socket.close();
        } catch (Exception e) {
            System.err.println("Потоки не были закрыты!");
        }
    }

    /**
     * Класс в отдельной нити пересылает все сообщения от сервера в textarea.
     * Работает пока не будет вызван метод setStop().
     */
    private class Resender extends Thread {

        private boolean stoped;

        /**
         * Прекращает пересылку сообщений
         */
        public void setStop() {
            stoped = true;
        }

        /**
         * Считывает все сообщения от сервера и печатает их в textarea.
         * Останавливается вызовом метода setStop()
         *
         * @see java.lang.Thread#run()
         */
        @Override
        public void run() {
            try {
                while (!stoped) {
                    String str = in.readLine();
                    System.out.println(str);
                    NetTicTacToe.GameFrame.textArea.append(str + "\n");
                    NetTicTacToe.GameFrame.textArea.repaint();
                }
            } catch (IOException e) {
//                System.out.println("Ошибка при получении сообщения.");
//                JOptionPane.showMessageDialog(null, "Вы проиграли!","Очень жаль!", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

}