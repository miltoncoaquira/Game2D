package main.game;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class LeaderboardDB {

    private static final String DB_PATH = getDbPath();
    private static final String DB_URL = "jdbc:sqlite:" + DB_PATH;
    private static boolean dbAvailable = true;
    private static final List<ScoreEntry> fallback = new ArrayList<>();

    private static String getDbPath() {
        try {
            String projectPath = System.getProperty("user.dir");
            java.io.File gameDataDir = new java.io.File(projectPath, "game_data");
            if (!gameDataDir.exists()) {
                gameDataDir.mkdirs();
            }
            return new java.io.File(gameDataDir, "leaderboard.db").getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
            return "leaderboard.db";
        }
    }

    public static void init() throws SQLException {
        try (Connection connection = DriverManager.getConnection(DB_URL);
             Statement statement = connection.createStatement()) {
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS leaderboard ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "player_name TEXT NOT NULL, "
                    + "map_name TEXT NOT NULL, "
                    + "score INTEGER NOT NULL, "
                    + "created_at DATETIME DEFAULT CURRENT_TIMESTAMP)");
            dbAvailable = true;
        } catch (SQLException ex) {
            dbAvailable = false;
        }
    }

    public static void saveScore(String playerName, String mapName, int score) throws SQLException {
        if (!dbAvailable) {
            fallback.add(new ScoreEntry(playerName, mapName, score, java.time.Instant.now().toString()));
            return;
        }

        String sql = "INSERT INTO leaderboard(player_name, map_name, score) VALUES(?,?,?)";
        try (Connection connection = DriverManager.getConnection(DB_URL);
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, playerName);
            statement.setString(2, mapName);
            statement.setInt(3, score);
            statement.executeUpdate();
        } catch (SQLException ex) {
            dbAvailable = false;
            fallback.add(new ScoreEntry(playerName, mapName, score, java.time.Instant.now().toString()));
        }
    }

    public static List<ScoreEntry> getTopScores(String mapName, int limit) throws SQLException {
        if (!dbAvailable) {
            return fallback.stream()
                    .filter(s -> s.mapName().equals(mapName))
                    .sorted((a, b) -> Integer.compare(b.score(), a.score()))
                    .limit(limit)
                    .toList();
        }
        
        String sql = "SELECT player_name, map_name, score, created_at "
                + "FROM leaderboard WHERE map_name = ? "
                + "ORDER BY score DESC, created_at ASC LIMIT ?";
        List<ScoreEntry> scores = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(DB_URL);
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, mapName);
            statement.setInt(2, limit);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    scores.add(new ScoreEntry(
                            resultSet.getString("player_name"),
                            resultSet.getString("map_name"),
                            resultSet.getInt("score"),
                            resultSet.getString("created_at")
                    ));
                }
            }
        } catch (SQLException ex) {
            dbAvailable = false;
            return fallback.stream()
                    .filter(s -> s.mapName().equals(mapName))
                    .sorted((a, b) -> Integer.compare(b.score(), a.score()))
                    .limit(limit)
                    .toList();
        }

        return scores;
    }

    // El record ahora solo tiene las variables estrictamente necesarias
    public record ScoreEntry(String playerName, String mapName, int score, String createdAt) {
    }
}
