import net.proteanit.sql.DbUtils;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class AdminPanel1 {
    private JPanel panel1;
    private JTable table1;
    private JTextField textName;
    private JButton updateButton;
    private JButton deleteButton;
    private JButton searchButton;

    private JFrame frame;

    Connection con;
    PreparedStatement pst;


    public AdminPanel1(){
        frame = new JFrame("Rezervacija");
        frame.setContentPane(panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setSize(600,500);
        frame.setVisible(true);

        connect();
        table_load();

        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                try {
                    PreparedStatement pst = con.prepareStatement("SELECT * FROM select_zaposleni_byname(?)");
                    pst.setString(1, textName.getText());
                    ResultSet rs = pst.executeQuery();
                    table1.setModel(DbUtils.resultSetToTableModel(rs));

                } catch (SQLException e) {
                    e.printStackTrace();
                }
                System.out.println("lmao");
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                int selectedid = (int) table1.getModel().getValueAt(table1.getSelectedRow(), 0) ;
                try {

                    PreparedStatement pst = con.prepareStatement("SELECT select_zaposleni_id(?)");
                    pst.setInt(1, selectedid);
                    ResultSet rs = pst.executeQuery();
                    table1.setModel(DbUtils.resultSetToTableModel(rs));
                }catch (SQLException e) {
                    e.printStackTrace();
                }
                table_load();
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
    void table_load(){
        try{

            pst = con.prepareStatement("SELECT * FROM rezervacija");
            ResultSet rs = pst.executeQuery();

            table1.setModel(DbUtils.resultSetToTableModel(rs));
        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }
    }
}
