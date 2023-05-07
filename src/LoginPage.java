import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class LoginPage extends JFrame implements ActionListener {
    private JLabel loginLabel, usernameLabel, passwordLabel, messageLabel;
    private JTextField usernameText, passwordText;
    private JButton submitButton;

    public LoginPage() {
        loginLabel = new JLabel("Login");
        usernameLabel = new JLabel("Username:");
        passwordLabel = new JLabel("Password:");
        messageLabel = new JLabel("");
        usernameText = new JTextField(20);
        passwordText = new JPasswordField(20);
        submitButton = new JButton("Submit");
        submitButton.addActionListener(this);


        JPanel panel = new JPanel(new GridLayout(4, 2));
        panel.add(loginLabel);
        panel.add(new JLabel(""));
        panel.add(usernameLabel);
        panel.add(usernameText);
        panel.add(passwordLabel);
        panel.add(passwordText);
        panel.add(submitButton);

        panel.add(messageLabel);

        add(panel, BorderLayout.CENTER);
        setTitle("Login Page");
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == submitButton) {
            String username = usernameText.getText();
            String password = passwordText.getText();
            if (validateLogin(username, password)) {
                new IzbiraPage();
                dispose();
            } else {
                messageLabel.setText("Invalid username or password");
            }
        } else {
            usernameText.setText("");
            passwordText.setText("");
        }
    }
    public boolean validateLogin(String username, String password) {
        Connection conn = null;
        CallableStatement stmt = null;
        ResultSet rs = null;
        boolean isValid = false;

        try {
            // Register JDBC driver
            Class.forName("org.postgresql.Driver");

            // Open a connection
            String dbUrl = "jdbc:postgres://alekshj2004:ZMmQk5fyAKx4@ep-lingering-grass-902680.eu-central-1.aws.neon.tech/neondb";
            String dbUser = "alekshj2004";
            String dbPassword = "ZMmQk5fyAKx4";
            conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);

            // Prepare a call to the validate_login function
            String sql = "{ ? = call validate_login(?, ?) }";
            stmt = conn.prepareCall(sql);
            stmt.registerOutParameter(1, Types.BOOLEAN);
            stmt.setString(2, username);
            stmt.setString(3, password);

            // Execute the call and get the result
            stmt.execute();
            isValid = stmt.getBoolean(1);
        } catch (SQLException se) {
            // Handle errors for JDBC
            se.printStackTrace();
        } catch (Exception ex) {
            // Handle errors for Class.forName
            ex.printStackTrace();
        } finally {
            // Close resources
            try {
                if (rs != null) rs.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
            try {
                if (stmt != null) stmt.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
            try {
                if (conn != null) conn.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }

        return isValid;
    }
    public static void main(String[] args) {
        new LoginPage();
    }
}