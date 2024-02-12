import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;

public class RegistrationForm extends JDialog {
    private JTextField tfName;
    private JTextField tfEmail;
    private JTextField tfPhone;
    private JTextField tfAddress;
    private JPasswordField pfPassword;
    private JPasswordField pfConfirmPassword;
    private JButton btnRegister;
    private JButton btnCancel;
    private JPanel registerPanel;
    public RegistrationForm(JFrame parent){
        super(parent);
        setTitle("Create a new account");
        setContentPane(registerPanel);
        setMinimumSize(new Dimension(450,474));
        setModal(true);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        btnRegister.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                registeruser();
            }
        });
        btnCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
              dispose();
            }
        });
        setVisible(true);
    }

    private void registeruser() {
        String name = tfName.getText();
        String email = tfEmail.getText();
        String phone = tfPhone.getText();
        String address = tfAddress.getText();
        String password = String.valueOf(pfPassword.getPassword());
        String confirmPassword = String.valueOf(pfConfirmPassword.getPassword());
        // to check for error message
        if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || address.isEmpty() || password.isEmpty()){
            JOptionPane.showMessageDialog(this,
                    "please enter all fields",
                    "try again",
                    JOptionPane.ERROR_MESSAGE);
            return;

        }
        if (!password.equals(confirmPassword)){
            JOptionPane.showMessageDialog(this,
                    "confirm password does not match",
                    "try again",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        // this method allows us to add a new user to the database

       user = addUserToDatabase(name, email, phone, address, password);
        if (user != null){
            dispose();
        }
        else {
            JOptionPane.showMessageDialog(this,
                    "failed to register new user",
                    "try again",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
 public User user;
    private User addUserToDatabase(String name, String email, String phone, String address, String password) {
        User user = null;
        // let's define some variables that allows us to connect to a database
        final String OB_URL = "jdbc:mysql://localhost/MyStore?serverTimeZone=UTC";
        final String USERNAME = "root";
        final String PASSWORD = "";
        try{
           Connection conn = DriverManager.getConnection(OB_URL,USERNAME,PASSWORD);
           // connected to database successful......

            //we need to create a sql statement that allows us to add new user
             Statement stat = conn.createStatement();
            String sql = "INSERT INTO userid (username, email, password)  " +
                    "VALUES (?, ?, ?, ?, ?)";
            PreparedStatement prepareStatement = conn.prepareStatement(sql);
            prepareStatement.setString(1, name);
            prepareStatement.setString(2, email);
            prepareStatement.setString(3, phone);
            prepareStatement.setString(4, address);
            prepareStatement.setString(5, password);
            //Insert row into the table
            int addedRow = prepareStatement.executeUpdate();
            if(addedRow > 0){
                user = new User();
                user.name = name;
                user.email = email;
                user.phone = phone;
                user.address = address;
                user.password = password;
            }
            stat.close();
            conn.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        return user;
    }

    public static void main(String[] args){
        RegistrationForm myForm = new RegistrationForm(null);
        User user = myForm.user;
        if(user!= null){
            System.out.println("Successful registration of: " + user.name);
        }else {
            System.out.println("Registration canceled");
        }
    }
}
