package main.game;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JComboBox;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.BorderFactory;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class StartMenu extends JFrame {

    private JList<String> mapList;
    private JComboBox<Integer> characterCombo;

    public StartMenu() {
        setTitle("Game2D - Menu Principal");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 100, 15, 100));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        //Seleccion de Mapa
        gbc.gridx = 0; gbc.gridy = 0;
        mainPanel.add(new JLabel("Mapa:"), gbc);

        gbc.gridx = 1; gbc.gridy = 0;
        gbc.gridwidth = 2; gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0; gbc.weighty = 1.0;

        List<String> maps = loadAvailableMaps();
        mapList = new JList<>(maps.toArray(new String[0]));
        mapList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        mapList.setSelectedIndex(0);
        mapList.setVisibleRowCount(5);
        mainPanel.add(new JScrollPane(mapList), gbc);

        //Seleccion de Personaje
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.gridwidth = 1; gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0; gbc.weighty = 0;
        mainPanel.add(new JLabel("Personaje:"), gbc);

        gbc.gridx = 1; gbc.gridy = 1;
        characterCombo = new JComboBox<>(new Integer[]{1, 2});
        characterCombo.setSelectedIndex(0);
        mainPanel.add(characterCombo, gbc);

        //Boton Jugar
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 3; gbc.fill = GridBagConstraints.HORIZONTAL;
        JButton playButton = new JButton("Jugar");
        playButton.addActionListener(e -> startGame());
        mainPanel.add(playButton, gbc);

        add(mainPanel);
        pack();
    }

    //Lee el archivo de mapas disponibles
    private List<String> loadAvailableMaps() {
        List<String> maps = new ArrayList<>();
        try {
            InputStream is = getClass().getResourceAsStream("/availableMaps.txt");
            if (is != null) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                String line;
                while ((line = reader.readLine()) != null) {
                    line = line.trim();
                    if (!line.isEmpty() && !line.startsWith("#")) {
                        maps.add(line);
                    }
                }
                reader.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return maps;
    }

    private void startGame() {
        String selectedMap = mapList.getSelectedValue();
        int character = (Integer) characterCombo.getSelectedItem();

        GamePanel gamePanel = new GamePanel(character, selectedMap);

        JFrame gameFrame = new JFrame();
        gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gameFrame.setResizable(true);
        gameFrame.setTitle("2D Game - " + selectedMap);
        gameFrame.add(gamePanel);
        gameFrame.pack();
        gameFrame.setLocationRelativeTo(null);
        gameFrame.setVisible(true);

        gamePanel.requestFocusInWindow();
        gamePanel.startGameThread();

        this.dispose();
    }
}