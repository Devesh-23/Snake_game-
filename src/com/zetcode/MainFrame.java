package com.zetcode;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class MainFrame extends JFrame {

    private JTextField playerNameField;
    private JTextField highScoreField;

    private final String DB_URL = "jdbc:mysql://localhost:3306/Snake_game";
    private final String DB_USER = "user_name";
    private final String DB_PASSWORD = "password";

    public MainFrame() {
        setTitle("Main Frame");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(300, 300);
        setLayout(new FlowLayout());

        JLabel playerNameLabel = new JLabel("Player Name:");
        playerNameField = new JTextField(20);

        JLabel highScoreLabel = new JLabel("High Score:");
        highScoreField = new JTextField(20);

        JButton startButton = new JButton("Start");
        startButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openNewFrames();
            }
        });

        JButton submitButton = new JButton("Submit");
        submitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String playerName = playerNameField.getText();
                int highScore = Integer.parseInt(highScoreField.getText());
                submitPlayerNameAndHighScore(playerName, highScore);
                retrieveRecentHighScore();
            }
        });

        add(playerNameLabel);
        add(playerNameField);
        add(highScoreLabel);
        add(highScoreField);
        add(startButton);
        add(submitButton);
    }

    private void openNewFrames() {
        JFrame boardFrame = new JFrame("Board.java");
        boardFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        boardFrame.setSize(300, 300);

        Board board = new Board();
        boardFrame.add(board);

        boardFrame.setVisible(true);

        JFrame snakeFrame = new JFrame("Snake.java");
        snakeFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        snakeFrame.setSize(300, 300);

        Snake snake = new Snake();
        snakeFrame.add(snake);

        snakeFrame.setVisible(true);
    }

    private void submitPlayerNameAndHighScore(String playerName, int highScore) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "INSERT INTO highscore (name, score) VALUES (?, ?)";
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1, playerName);
            statement.setInt(2, highScore);
            statement.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void retrieveRecentHighScore() {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "SELECT score FROM highscore ORDER BY score DESC LIMIT 1";
            Statement statement = conn.createStatement();
            ResultSet result = statement.executeQuery(query);
            if (result.next()) {
                int highScore = result.getInt("score");
                highScoreField.setText(String.valueOf(highScore));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                MainFrame frame = new MainFrame();
                frame.setVisible(true);
            }
        });
    }
}
