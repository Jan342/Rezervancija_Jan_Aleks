import org.mindrot.jbcrypt.BCrypt;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class LoginPage extends JFrame implements ActionListener {
    private JLabel Password, LoginPage, usernameLabel, messageLabel;
    private JTextField usernameText;
    private JPasswordField passwordText;
    private JButton submitButton;
    private JButton ChangePassword;
    private JButton Login;
    private JPanel panel2;
    private JFrame frame;


    Connection con;
    PreparedStatement pst;
    CallableStatement stmt = null;

    public void connect() {
        try {
            Class.forName("org.postgresql.Driver");
            con = DriverManager.getConnection("jdbc:postgresql://ep-lingering-grass-902680.eu-central-1.aws.neon.tech:5432/neondb",
                    "alekshj2004", "ZMmQk5fyAKx4");
            System.out.println("works");
        }
        catch (ClassNotFoundException ex)
        {
            ex.printStackTrace();
        }
        catch (SQLException ex)
        {
            ex.printStackTrace();
        }
    }
    public LoginPage() {

        submitButton.addActionListener(this);
        ChangePassword.addActionListener(this);
        frame = new JFrame("empRegistration");
        frame.setContentPane(panel2);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setSize(600,500);
        frame.setVisible(true);


        Login.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                dispose();
                new RegPage();
            }
        });
    }
    private void showChangePasswordDialog() {
        JDialog changePasswordDialog = new JDialog(this, "Change Password", true);
        ChangePasswordPanel changePasswordPanel = new ChangePasswordPanel();
        changePasswordDialog.add(changePasswordPanel);
        changePasswordDialog.pack();
        changePasswordDialog.setLocationRelativeTo(this);
        changePasswordDialog.setVisible(true);
    }



    public class ChangePasswordPanel extends JPanel {
        private JLabel usernameLabel;
        private JLabel currentEmailLabel;
        private JLabel newPasswordLabel;
        private JTextField usernameText;
        private JTextField currentEmailText;
        private JPasswordField newPasswordText;
        private JButton SubmitButton;
        private JLabel messageLabel;

        public ChangePasswordPanel() {
            usernameLabel = new JLabel("username");
            currentEmailLabel = new JLabel("Current Email:");
            newPasswordLabel = new JLabel("New Password:");
            usernameText = new JTextField(20);
            currentEmailText = new JTextField(20);
            newPasswordText = new JPasswordField(20);
            SubmitButton = new JButton("Submit");
            messageLabel = new JLabel("");

            SubmitButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    changePassword();
                }
            });

            setLayout(new GridLayout(5, 2));
            add(usernameLabel);
            add(usernameText);
            add(currentEmailLabel);
            add(currentEmailText);
            add(newPasswordLabel);
            add(newPasswordText);
            add(SubmitButton);
            add(messageLabel);

        }

        private void changePassword() {
            String username = usernameText.getText();
            String currentEmail = currentEmailText.getText();
            String newPassword = new String(newPasswordText.getPassword());

            if (ValidateLogin(username, currentEmail)) {
                if (updatePasswordInDatabase(username, newPassword)) {
                    messageLabel.setText("Password changed successfully!");
                } else {
                    messageLabel.setText("Error changing password.");
                }
            } else {
                messageLabel.setText("Invalid username or current password.");
            }
        }

        private boolean ValidateLogin(String username, String email) {

            ResultSet rs = null;
            boolean isValid = false;

            try {
                connect();
                // Prepare a call to the validate_login function
                String sql = "{ ? = call validate_changePassword(?, ?) }";
                stmt = con.prepareCall(sql);
                stmt.registerOutParameter(1, Types.BOOLEAN);
                stmt.setString(2, username);
                stmt.setString(3, email);

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
                    if (con != null) con.close();
                } catch (SQLException se) {
                    se.printStackTrace();
                }
            }

            return isValid;
        }

        private boolean updatePasswordInDatabase(String username, String newPassword) {

            int rowsAffected = 0;

            try {
                connect();

                // Create an SQL update statement
                String sql = "UPDATE uporabniki SET password = ? WHERE username = ?";
                stmt = con.prepareCall(sql);
                stmt.setString(1, newPassword);
                stmt.setString(2, username);

                // Execute the update statement and get the number of affected rows
                rowsAffected = stmt.executeUpdate();
            } catch (SQLException se) {
                // Handle errors for JDBC
                se.printStackTrace();
            } catch (Exception ex) {
                // Handle errors for Class.forName
                ex.printStackTrace();
            } finally {
                // Close resources
                try {
                    if (stmt != null) stmt.close();
                } catch (SQLException se) {
                    se.printStackTrace();
                }
                try {
                    if (con != null) con.close();
                } catch (SQLException se) {
                    se.printStackTrace();
                }
            }
            return rowsAffected > 0;

        }
    }
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == submitButton) {
            String username = usernameText.getText();
            String password = passwordText.getText();
            int userId = validateLogin(username, password);
            if (userId != 0) {
                messageLabel.setText("Login successful!");
                SwingUtilities.invokeLater(() -> new IzbiraPage(userId));
                this.dispose(); // Close the login window
            }else if(username.equals("admin") && password.equals("1234")){
                new AdminView();
                this.dispose();
            }
            else{
                messageLabel.setText("Invalid username or password");
            }
        } else if (e.getSource() == ChangePassword) {
            showChangePasswordDialog();
        } else {
            usernameText.setText("");
            passwordText.setText("");
        }
    }

    private int validateLogin(String username, String password) {
        int userId = 0;
        String storedHash = "";

        try {
            connect();

            // Get the stored password hash
            String sql = "SELECT id, password FROM uporabniki WHERE username = ?";
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                userId = rs.getInt("id");
                storedHash = rs.getString("password");
            }

            // Close resources
            pstmt.close();
            rs.close();

            // Verify the password
            if (!BCrypt.checkpw(password, storedHash)) {
                userId = 0;
            }
        } catch (SQLException se) {
            // Handle errors for JDBC
            se.printStackTrace();
        } catch (Exception ex) {
            // Handle errors for Class.forName
            ex.printStackTrace();
        } finally {
            // Close resources
            try {
                if (stmt != null) stmt.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
            try {
                if (con != null) con.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }

        return userId;
    }
    public static void main(String[] args) {
        new LoginPage();
    }
}
