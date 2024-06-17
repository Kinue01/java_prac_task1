package org.example;

import org.example.forms.ChooseForm;
import javax.swing.*;

public class App {
    public static Database db;

    public static void main(String[] args) {
        db = new Database();
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Выбор протокола");
            frame.setContentPane(new ChooseForm().panel1);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.pack();
            frame.setVisible(true);
        });
    }
}
