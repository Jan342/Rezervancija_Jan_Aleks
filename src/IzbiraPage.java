import javax.swing.*;
import java.awt.*;
import java.sql.*;
public class IzbiraPage {
    private JButton vnesiButton;
    private JComboBox sportBox;
    private JComboBox mestoBox;
    private JPanel vnesibtn;

    private String dbUrl = "jdbc:postgresql://ep-lingering-grass-902680.eu-central-1.aws.neon.tech/neondb";
    private String dbUser = "alekshj2004";
    private String dbPassword = "ZMmQk5fyAKx4";

    public IzbiraPage() {
        JFrame frame = new JFrame("ComboBox Demo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new FlowLayout());

        mestoBox = new JComboBox<>();
        sportBox = new JComboBox<>();

        frame.add(new JLabel("Mesta:"));
        frame.add(mestoBox);
        frame.add(new JLabel("Sport:"));
        frame.add(sportBox);

        frame.pack();
        frame.setSize(600,500);
        frame.setVisible(true);

        loadMesta();
        loadSport();
    }

    private void loadMesta() {
        try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
             CallableStatement cstmt = conn.prepareCall("{call get_kraji_names()}")) {

            ResultSet rs = cstmt.executeQuery();

            while (rs.next()) {
                String name = rs.getString("ime");
                mestoBox.addItem(name);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadSport() {
        try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
             CallableStatement cstmt = conn.prepareCall("{call get_sport_names()}")) {

            ResultSet rs = cstmt.executeQuery();

            while (rs.next()) {
                String name = rs.getString("ime");
                sportBox.addItem(name);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        // Add this line to load the PostgreSQL JDBC driver
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.exit(1);
        }

        SwingUtilities.invokeLater(() -> new IzbiraPage());
    }
}
