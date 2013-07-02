package org.bigloupe.web.console;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.util.Properties;

import javax.swing.*;

import jline.ConsoleReader;
import jline.ConsoleReaderInputStream;
import jline.History;

import org.apache.pig.ExecType;
import org.apache.pig.Main;
import org.apache.pig.impl.PigContext;
import org.apache.pig.impl.util.PropertiesUtil;
import org.apache.pig.impl.util.Utils;
import org.apache.pig.tools.grunt.Grunt;

@SuppressWarnings("serial")
public class GruntConsole extends JPanel {

   public GruntConsole() throws IOException {
      setLayout(new BorderLayout());
      add(new JScrollPane(new GruntConsoleTextArea(), JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, 
            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER));
   }

   private static void createAndShowGui() throws IOException {
      JFrame frame = new JFrame("Apache PIG - Grunt - Grunt");
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      frame.getContentPane().add(new GruntConsole());
      frame.setSize(600, 400);
      frame.setLocationRelativeTo(null);
      frame.setVisible(true);
   }

   public static void main(String[] args) {
      SwingUtilities.invokeLater(new Runnable() {
         public void run() {
            try {
				createAndShowGui();
			} catch (IOException e) {
				e.printStackTrace();
			}
         }
      });
   }
}
