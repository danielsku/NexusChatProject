//package com.nexus.nexuschat.client;
//
//import com.formdev.flatlaf.FlatDarkLaf;
//
//import javax.swing.*;
//import java.util.concurrent.ExecutionException;
//
//public class App {
//    public static void main(String[] args){
//        // UIManager.put("defaultFont", new Font("Arial", Font.PLAIN, 14));
//        FlatDarkLaf.setup();
//
//        SwingUtilities.invokeLater(() -> {
//            ClientGUI frame = null;
//            try {
//                frame = new ClientGUI("AnotherUser");
//            } catch (ExecutionException e) {
//                throw new RuntimeException(e);
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }
//
//            frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
//            frame.setVisible(true);
//        });
//    }
//}
