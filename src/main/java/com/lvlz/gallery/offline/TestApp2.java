package com.lvlz.gallery.offline;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.io.File;

public class TestApp2 {

  public static class MainFrame extends JFrame {

    public MainFrame() {

      setTitle("Lovelyz Classifier");
      setSize(300, 300);
      setLocationRelativeTo(null);
      final JButton button = new JButton("Open image");
      button.addActionListener(new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent event) {
          
          button.setText("Loading image...");
          button.setEnabled(false);

          JFileChooser fileChooser = new JFileChooser();
          fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
          int result = fileChooser.showOpenDialog(MainFrame.this);

          if (result == JFileChooser.APPROVE_OPTION) {

            TestApp.testImage(fileChooser.getSelectedFile().getAbsolutePath());

          }

          button.setText("Open image");
          button.setEnabled(true);

        }

      });
      add(button);
      setDefaultCloseOperation(EXIT_ON_CLOSE);

    }

  }

  public TestApp2(String[] args) {

    SwingUtilities.invokeLater(new Runnable() {

      @Override
      public void run() {

       MainFrame ex = new MainFrame();
       ex.setVisible(true);

      }

    });

  }

}

