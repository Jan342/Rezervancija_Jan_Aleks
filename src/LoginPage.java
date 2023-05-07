import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class LoginPage extends JFrame implements ActionListener {
    private JLabel Password, LoginPage, usernameLabel, messageLabel;
    private JTextField usernameText, passwordText;
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
       /* LoginPage = new JLabel("Login");
        usernameLabel = new JLabel("Username:");
        Password = new JLabel("Password:");
        messageLabel = new JLabel("");
        usernameText = new JTextField(20);
        passwordText = new JPasswordField(20);
        submitButton = new JButton("Submit");
        ChangePassword = new JButton("Change Password");
        Login = new JButton("Login");*/
        submitButton.addActionListener(this);
        ChangePassword.addActionListener(this);
        frame = new JFrame("empRegistration");
        frame.setContentPane(panel2);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setSize(600,500);
        frame.setVisible(true);


  /*      JPanel panel = new JPanel(new GridLayout(4, 2));
        panel.add(LoginPage);
        panel.add(new JLabel(""));
        panel.add(usernameLabel);
        panel.add(usernameText);
        panel.add(Password);
        panel.add(passwordText);
        panel.add(submitButton);
        panel.add(ChangePassword);


        panel.add(messageLabel);

        add(panel, BorderLayout.CENTER);
        setTitle("Login Page");
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
*/
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
        private JLabel currentPasswordLabel;
        private JLabel newPasswordLabel;
        private JTextField usernameText;
        private JPasswordField currentPasswordText;
        private JPasswordField newPasswordText;
        private JButton SubmitButton;
        private JLabel messageLabel;

        public ChangePasswordPanel() {
            usernameLabel = new JLabel("username");
            currentPasswordLabel = new JLabel("Current Password:");
            newPasswordLabel = new JLabel("New Password:");
            usernameText = new JTextField(20);
            currentPasswordText = new JPasswordField(20);
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
            add(currentPasswordLabel);
            add(currentPasswordText);
            add(newPasswordLabel);
            add(newPasswordText);
            add(SubmitButton);
            add(messageLabel);

        }

        private void changePassword() {
            String username = usernameText.getText();
            String currentPassword = new String(currentPasswordText.getPassword());
            String newPassword = new String(newPasswordText.getPassword());

            if (ValidateLogin(username, currentPassword)) {
                if (updatePasswordInDatabase(username, newPassword)) {
                    messageLabel.setText("Password changed successfully!");
                } else {
                    messageLabel.setText("Error changing password.");
                }
            } else {
                messageLabel.setText("Invalid username or current password.");
            }
        }

        private boolean ValidateLogin(String username, String password) {

            ResultSet rs = null;
            boolean isValid = false;

            try {
                connect();
                // Prepare a call to the validate_login function
                String sql = "{ ? = call validate_login(?, ?) }";
                stmt = con.prepareCall(sql);
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
            // ... (existing login logic)
        } else if (e.getSource() == ChangePassword) {
            showChangePasswordDialog();
        } else {
            usernameText.setText("");
            passwordText.setText("");
        }
        if (e.getSource() == submitButton) {
            String username = usernameText.getText();
            String password = passwordText.getText();
            if (validateLogin(username, password)) {
                // Login successful, open the izbiraPage form
                new IzbiraPage();
                dispose(); // close the current login page form
            } else if (username.equals("admin") && password.equals("123")) {
                messageLabel.setText("Logged in as admin!");
            } else {
                messageLabel.setText("Invalid username or password");
            }
        } else {
            usernameText.setText("");
            passwordText.setText("");
        }
    }

  public boolean validateLogin(String username, String password) {

        ResultSet rs = null;
        boolean isValid = false;

        try {
          connect();

            // Prepare a call to the validate_login function
            String sql = "{ ? = call validate_login(?, ?) }";
            stmt = con.prepareCall(sql);
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
                if (con != null) con.close();
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
