package dao;

import org.apache.struts.upload.FormFile;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class T004Dao {

    private Connection getConnection() throws Exception {
        // TODO: Implement your database connection logic
        // Example:
        // Class.forName("your.database.driver");
        // return DriverManager.getConnection("jdbc:url", "username", "password");
        return null;
    }

    public boolean checkCustomerExists(String customerId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = getConnection();
            String sql = "SELECT COUNT(*) FROM MSTCUSTOMER WHERE CUSTOMER_ID = ? AND DELETE_YMD IS NULL";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, customerId);
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Close resources
            try { if (rs != null) rs.close(); } catch (Exception e) {}
            try { if (pstmt != null) pstmt.close(); } catch (Exception e) {}
            try { if (conn != null) conn.close(); } catch (Exception e) {}
        }
        
        return false;
    }

    public boolean importData(FormFile uploadFile) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        BufferedReader reader = null;
        
        try {
            conn = getConnection();
            conn.setAutoCommit(false);
            
            String sql = "INSERT INTO MSTCUSTOMER (CUSTOMER_ID, CUSTOMER_NAME, SEX, BIRTHDAY, EMAIL, INSERT_YMD) VALUES (?, ?, ?, ?, ?, SYSDATE)";
            pstmt = conn.prepareStatement(sql);
            
            reader = new BufferedReader(new InputStreamReader(uploadFile.getInputStream()));
            String line;
            int lineNumber = 0;
            
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                
                if (line.trim().isEmpty()) {
                    continue;
                }
                
                String[] values = line.split(",");
                if (values.length < 5) {
                    continue;
                }
                
                String customerId = values[0].trim();
                String customerName = values[1].trim();
                String sex = values[2].trim();
                String birthday = values[3].trim();
                String email = values[4].trim();
                
                pstmt.setString(1, customerId);
                pstmt.setString(2, customerName);
                pstmt.setString(3, sex);
                pstmt.setString(4, birthday);
                pstmt.setString(5, email);
                pstmt.addBatch();
                
                // Execute batch every 100 records
                if (lineNumber % 100 == 0) {
                    pstmt.executeBatch();
                }
            }
            
            // Execute remaining batch
            pstmt.executeBatch();
            conn.commit();
            
            return true;
        } catch (Exception e) {
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            // Close resources
            try { if (reader != null) reader.close(); } catch (Exception e) {}
            try { if (pstmt != null) pstmt.close(); } catch (Exception e) {}
            try { if (conn != null) conn.close(); } catch (Exception e) {}
        }
        
        return false;
    }
}