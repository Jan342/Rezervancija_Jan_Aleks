import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.sql.Timestamp;


public class IzbiraPage extends JFrame {
    private JButton vnesiButton;
    private JComboBox sportBox;
    private JComboBox mestoBox;
    private JPanel vnesibtn;
    private JComboBox UraBox;
    private JComboBox datumBox;
    private JLabel Igrišča;
    private JComboBox igriscaBox;
    private JButton pregledRezervacijButton;
    public static int userId;


    private String dbUrl = "jdbc:postgresql://ep-lingering-grass-902680.eu-central-1.aws.neon.tech/neondb";
    private String dbUser = "alekshj2004";
    private String dbPassword = "ZMmQk5fyAKx4";

    public IzbiraPage(int userId) {
        JFrame frame = new JFrame("ComboBox Demo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new FlowLayout());
        this.userId = userId;



        mestoBox = new JComboBox<>();
        sportBox = new JComboBox<>();
        datumBox = new JComboBox<>();
        UraBox = new JComboBox<>();
        igriscaBox = new JComboBox<>();
        vnesiButton = new JButton("Vnesi");
        frame.add(vnesiButton);
        pregledRezervacijButton = new JButton("Pregled Rezervacij");
        frame.add(pregledRezervacijButton);


        frame.add(new JLabel("Mesta:"));
        frame.add(mestoBox);
        frame.add(new JLabel("Sport:"));
        frame.add(sportBox);
        frame.add(new JLabel("Datum:"));
        frame.add(datumBox);
        frame.add(new JLabel("Ure:"));
        frame.add(UraBox);
        frame.add(new JLabel("Igrisce:"));
        frame.add(igriscaBox);
        frame.pack();
        frame.setSize(600,500);
        frame.setVisible(true);

        loadMesta();
        loadSport();
        loadDatumi();
        loadUre();
        loadIgrisca();
        vnesiButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                insertReservation();
            }
        });
        pregledRezervacijButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new PogledPage(userId);
                dispose();
            }
        });
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
    private void loadIgrisca() {
        try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
             CallableStatement cstmt = conn.prepareCall("{call get_igrisca()}")) {

            ResultSet rs = cstmt.executeQuery();

            while (rs.next()) {
                String name = rs.getString("naslov");
                igriscaBox.addItem(name);
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
    private void loadDatumi() {
        try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
             CallableStatement cstmt = conn.prepareCall("{call get_datumi()}")) {

            ResultSet rs = cstmt.executeQuery();

            while (rs.next()) {
                Timestamp timestamp = rs.getTimestamp("datum");
                datumBox.addItem(timestamp);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadUre() {
        try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
             CallableStatement cstmt = conn.prepareCall("{call get_ure()}")) {

            ResultSet rs = cstmt.executeQuery();

            while (rs.next()) {
                int hour = rs.getInt("ure");
                UraBox.addItem(String.valueOf(hour));
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

        SwingUtilities.invokeLater(() -> new LoginPage());    }
    private void insertReservation() {
        String mesto = (String) mestoBox.getSelectedItem();
        String sport = (String) sportBox.getSelectedItem();
        Timestamp datum = (Timestamp) datumBox.getSelectedItem();
        String ura = (String) UraBox.getSelectedItem();
        String igrisca = (String) igriscaBox.getSelectedItem();
        try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
             CallableStatement cstmt = conn.prepareCall("{call insert_rezervacija(?,?, ?, ?, ?,?)}")) {
            cstmt.setInt(1, userId);
            cstmt.setString(2, mesto);


            cstmt.setString(3, sport);
            cstmt.setString(4, datum.toString()); // Set as string
            cstmt.setString(5, ura);
            cstmt.setString(6, igrisca);
            cstmt.executeUpdate();

            JOptionPane.showMessageDialog(null, "Rezervacija uspešno vnesena!");

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Napaka pri vnosu rezervacije!", "Napaka", JOptionPane.ERROR_MESSAGE);
        }

    }
}
