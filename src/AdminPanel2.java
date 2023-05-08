import net.proteanit.sql.DbUtils;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;

public class AdminPanel2 {

    private JPanel panel1;
    private JTable table2;
    private JButton deleteButton;
    private JButton refreshButton;
    private JButton backButton;
    private JTextField textName;
    private JButton searchButton;

    private JFrame frame;

    Connection con;
    PreparedStatement pst;


    public AdminPanel2() {
        frame = new JFrame("Rezervacija");
        frame.setContentPane(panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setSize(600, 500);
        frame.setVisible(true);

        connect();
        table_load();
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                try {
                    PreparedStatement pst = con.prepareStatement("SELECT * FROM select_rezervacija_user(?)");
                    pst.setString(1, textName.getText());
                    ResultSet rs = pst.executeQuery();
                    table2.setModel(DbUtils.resultSetToTableModel(rs));

                } catch (SQLException e) {
                    e.printStackTrace();
                }
                System.out.println("lmao");
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                int selectedid = (int) table2.getModel().getValueAt(table2.getSelectedRow(), 0) ;
                try {

                    PreparedStatement pst = con.prepareStatement("SELECT delete_rezervacija_id(?)");
                    pst.setInt(1, selectedid);
                    ResultSet rs = pst.executeQuery();
                    table2.setModel(DbUtils.resultSetToTableModel(rs));
                }catch (SQLException e) {
                    e.printStackTrace();
                }
                table_load();
            }
        });
        table2.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                updateCellValue();
                textName.setText("");
            }
        });

        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                table_load();
            }
        });
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                new AdminView();
            }
        });
    }
    private void updateCellValue() {
        int row = table2.getSelectedRow();
        int col = table2.getSelectedColumn();
        Object oldValue = table2.getValueAt(row, col);

        String newValue = JOptionPane.showInputDialog("Enter new value:", oldValue);
        if (newValue != null && !newValue.equals(oldValue)) {

            table2.setValueAt(newValue, row, col);


            updateDatabase(row, col, newValue);
        }
    }
    private void updateDatabase(int row, int col, String newValue) {
        String columnName = table2.getColumnName(col);
        int id = (int) table2.getValueAt(row, 0);
        connect();
        try {
            String sql = "UPDATE rezervacija SET " + columnName + " = ? WHERE id = ?";
            pst = con.prepareStatement(sql);
            pst.setString(1, newValue);
            pst.setInt(2, id);

            pst.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error updating database: " + e.getMessage());
        }
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
    void table_load(){
        try {
            pst = con.prepareStatement("SELECT id,id_uporabniki,datum_rezervaije,ura_rezervacije,mesto_rezervacije,sport_rezervacije,igrisca_rezervacije FROM rezervacija" );
            ResultSet rs = pst.executeQuery();
            table2.setModel(DbUtils.resultSetToTableModel(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
