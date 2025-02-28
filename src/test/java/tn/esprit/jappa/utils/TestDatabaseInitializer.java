package tn.esprit.jappa.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.stream.Collectors;

public class TestDatabaseInitializer {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/";
    private static final String USER = "root";
    private static final String PASS = "";
    private static final String TEST_DB = "jawher_db_test";
    
    public static Connection initializeTestDatabase() throws Exception {
        // First connect to MySQL server without database
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             Statement stmt = conn.createStatement()) {
            
            // Read SQL script
            InputStream is = TestDatabaseInitializer.class.getClassLoader()
                .getResourceAsStream("init_test_db.sql");
            if (is == null) {
                throw new RuntimeException("Could not find init_test_db.sql");
            }
            
            String sql = new BufferedReader(new InputStreamReader(is))
                .lines().collect(Collectors.joining("\n"));
                
            // Split and execute statements
            for (String statement : sql.split(";")) {
                if (!statement.trim().isEmpty()) {
                    stmt.execute(statement);
                }
            }
        }
        
        // Return connection to test database
        return DriverManager.getConnection(DB_URL + TEST_DB, USER, PASS);
    }
} 