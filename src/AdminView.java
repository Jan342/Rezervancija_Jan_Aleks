import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class AdminView {
    private JButton uporabnikiButton;
    private JButton rezervacijeButton;
    private JPanel panel1;
    private JFrame frame;

    Connection con;
    PreparedStatement pst;


    public AdminView() {
        frame = new JFrame("AdminView");
        frame.setContentPane(panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setSize(600, 500);
        frame.setVisible(true);

        connect();
        uporabnikiButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                new AdminPanel1();
            }
        });
        rezervacijeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                new AdminPanel2();
            }
        });
    }
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
}
