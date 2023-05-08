import net.proteanit.sql.DbUtils;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class PogledPage {
    private JTable table1;
    private JButton deleteButton;
    private JPanel panel1;
    private JButton backButton;
    private JButton refreshButton;

    private JFrame frame;
    private Connection con;
    private PreparedStatement pst;
    private int userId;

    public PogledPage(int userId) {
        this.userId = userId;
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                new IzbiraPage(userId);
                frame.dispose();
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                int selectedid = (int) table1.getModel().getValueAt(table1.getSelectedRow(), 0);
                try {
                    PreparedStatement pst = con.prepareStatement("SELECT delete_rezervacija_id(?)");
                    pst.setInt(1, selectedid);
                    ResultSet rs = pst.executeQuery();
                    table1.setModel(DbUtils.resultSetToTableModel(rs));
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                table_load();
            }
        });
        frame = new JFrame("PogledPage");
        frame.setContentPane(panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setSize(600, 500);
        frame.setVisible(true);

        connect();
        table_load();
    }

    public void connect() {
        try {
            Class.forName("org.postgresql.Driver");
            con = DriverManager.getConnection("jdbc:postgresql://ep-lingering-grass-902680.eu-central-1.aws.neon.tech:5432/neondb",
                    "alekshj2004", "ZMmQk5fyAKx4");
            System.out.println("works");
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    void table_load() {
        try {
            pst = con.prepareStatement("SELECT id, id_uporabniki, datum_rezervaije, ura_rezervacije, mesto_rezervacije, sport_rezervacije, igrisca_rezervacije FROM rezervacija WHERE id_uporabniki = ?");
            pst.setInt(1, userId);
            ResultSet rs = pst.executeQuery();
            table1.setModel(DbUtils.resultSetToTableModel(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}