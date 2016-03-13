package net.steepout.plugin.jd;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.border.EmptyBorder;
import net.miginfocom.swing.MigLayout;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;
import javax.swing.SpringLayout;
import javax.swing.JLabel;
import javax.swing.JTextField;
import java.awt.Color;

public class LoginDialog extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private JTextField textField;
	private JTextField textField_1;
	private JTextField textField_2;
	private JPasswordField textField_3;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			LoginDialog dialog = new LoginDialog();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public LoginDialog() {
		setResizable(false);
		setBounds(100, 100, 295, 326);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		
		JLabel lblServerAddress = new JLabel("Server Address");
		lblServerAddress.setBounds(15, 15, 102, 15);
		contentPanel.add(lblServerAddress);
		
		textField = new JTextField();
		textField.setBounds(15, 34, 161, 21);
		textField.setText("0.0.0.0");
		contentPanel.add(textField);
		textField.setColumns(10);
		
		JLabel lblPort = new JLabel("Port");
		lblPort.setBounds(15, 61, 24, 15);
		contentPanel.add(lblPort);
		
		textField_1 = new JTextField();
		textField_1.setBounds(15, 82, 161, 21);
		textField_1.setText("7046");
		textField_1.setColumns(10);
		contentPanel.add(textField_1);
		
		textField_2 = new JTextField();
		textField_2.setBounds(15, 133, 161, 21);
		textField_2.setColumns(10);
		contentPanel.add(textField_2);
		
		JLabel lblUsername = new JLabel("Username");
		lblUsername.setBounds(15, 109, 102, 15);
		contentPanel.add(lblUsername);
		
		JLabel lblPassword = new JLabel("Password");
		lblPassword.setBounds(15, 160, 84, 15);
		contentPanel.add(lblPassword);
		
		textField_3 = new JPasswordField();
		textField_3.setBounds(15, 181, 161, 21);
		textField_3.setColumns(10);
		contentPanel.add(textField_3);
		
		JLabel adsn = new JLabel("* Cannot be null");
		adsn.setBounds(182, 37, 96, 15);
		adsn.setForeground(Color.RED);
		adsn.setVisible(false);
		contentPanel.add(adsn);
		
		JLabel pwdn = new JLabel("* Cannot be null");
		pwdn.setBounds(182, 184, 96, 15);
		pwdn.setForeground(Color.RED);
		pwdn.setVisible(false);
		contentPanel.add(pwdn);
		
		JLabel usern = new JLabel("* Cannot be null");
		usern.setForeground(Color.RED);
		usern.setBounds(182, 136, 96, 15);
		usern.setVisible(false);
		contentPanel.add(usern);
		
		JLabel portn = new JLabel("* Cannot be null");
		portn.setForeground(Color.RED);
		portn.setBounds(182, 85, 96, 15);
		portn.setVisible(false);
		contentPanel.add(portn);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
	}
}
