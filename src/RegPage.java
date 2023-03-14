import javax.swing.*;
import java.sql.DriverManager;
import java.sql.SQLException;

public class RegPage {
    private JTextField emailText;
    private JTextField userText;
    private JTextField passText;
    private JTextField confpassText;
    private JButton regButton;
    private JButton loginButton;
    private JPanel panel1;
    private JFrame frame;

    public void connect() {
        try {
            Class.forName("org.postgresql.Driver");
            con = DriverManager.getConnection("jdbc:postgresql://snuffleupagus.db.elephantsql.com:5432/hbfssypp", "hbfssypp", "Oq5dieXemgwvy-Qhf8MB3dFl2K5Q0-CR");
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

    public RegPage(){

        frame = new JFrame("empRegistration");
        frame.setContentPane(panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setSize(600,500);
        frame.setVisible(true);

        connect();
    }
}
