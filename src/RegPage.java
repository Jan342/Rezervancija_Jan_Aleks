import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
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

    Connection con;
    PreparedStatement pst;

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

    public RegPage(){

        frame = new JFrame("empRegistration");
        frame.setContentPane(panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setSize(600,500);
        frame.setVisible(true);

        connect();
        regButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {

                String name,email,pass,confirmPass;

                    email = emailText.getText();
                    name = userText.getText();
                    pass = passText.getText();
                    confirmPass = confpassText.getText();

                if (!pass.equals(confirmPass)) {
                    JOptionPane.showMessageDialog(null, "Passwords do not match!");
                    return;
                }
                if(name.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Did not insert username");
                    return;
                }

                try{
                    pst = con.prepareStatement("insert into uporabniki (username, password, email) values(?,?,?)");
                    pst.setString(1,name);
                    pst.setString(2, pass);
                    pst.setString(3,email);
                    pst.executeUpdate();
                    JOptionPane.showMessageDialog(null,"Registered!");

                    emailText.setText("");
                    userText.setText("");
                    passText.setText("");
                    emailText.requestFocus();


                }catch (SQLException e1){
                    e1.printStackTrace();

                }

                new LoginPage();
                frame.dispose();


            }
        });
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                new LoginPage();
                frame.dispose();
            }
        });
    }
}
