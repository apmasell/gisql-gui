package ca.wlu.gisql.gui.login;

import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;
import java.util.Map.Entry;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

import org.apache.log4j.Logger;

import ca.wlu.gisql.db.DatabaseManager;

public class LoginDialog extends JDialog implements ActionListener {

	private class ConnectionParameters extends JPanel {

		private String description = null;

		private final JTextField driver = new JTextField();

		private String id = "";

		private final JPasswordField password = new JPasswordField();

		private final JTextField url = new JTextField();

		private final JTextField username = new JTextField();

		public ConnectionParameters() {
			url.setText(defaulturl);
			driver.setText("org.postgresql.Driver");

			setLayout(new GridBagLayout());
			add(new JLabel("User:"), new GridBagConstraints(0, 0, 1, 1, 1, 1,
					GridBagConstraints.CENTER, GridBagConstraints.NONE,
					new Insets(0, 0, 0, 0), 0, 0));
			add(username, new GridBagConstraints(1, 0, 2, 1, 1, 1,
					GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
					new Insets(0, 0, 0, 0), 0, 0));
			add(new JLabel("Password:"), new GridBagConstraints(0, 1, 1, 1, 1,
					1, GridBagConstraints.CENTER, GridBagConstraints.NONE,
					new Insets(0, 0, 0, 0), 0, 0));
			add(password, new GridBagConstraints(1, 1, 2, 1, 1, 1,
					GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
					new Insets(0, 0, 0, 0), 0, 0));
			add(new JLabel("JDBC URL:"), new GridBagConstraints(0, 2, 1, 1, 1,
					1, GridBagConstraints.CENTER, GridBagConstraints.NONE,
					new Insets(0, 0, 0, 0), 0, 0));
			add(url, new GridBagConstraints(1, 2, 2, 1, 1, 1,
					GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
					new Insets(0, 0, 0, 0), 0, 0));
			add(new JLabel("JDBC Driver:"), new GridBagConstraints(0, 3, 1, 1,
					1, 1, GridBagConstraints.CENTER, GridBagConstraints.NONE,
					new Insets(0, 0, 0, 0), 0, 0));
			add(driver, new GridBagConstraints(1, 3, 2, 1, 1, 1,
					GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
					new Insets(0, 0, 0, 0), 0, 0));
		}

		public DatabaseManager connect() throws SQLException,
				ClassNotFoundException {
			return new DatabaseManager(driver.getText(), url.getText(),
					username.getText(), new String(password.getPassword()));

		}

		@Override
		public String toString() {
			return description == null ? id : description;
		}
	}

	private final static String defaulturl = "jdbc:postgresql://localhost/interactome";

	private static final Logger log = Logger.getLogger(LoginDialog.class);

	private static final long serialVersionUID = -3790429193777222647L;

	public static DatabaseManager connect(Frame parent, Properties properties) {
		LoginDialog dialog = new LoginDialog(parent, properties);
		dialog.setVisible(true);
		return dialog.databasemanager;
	}

	private final JButton cancel = new JButton("Cancel");

	private final JButton connect = new JButton("Connect");

	private final JComboBox connections = new JComboBox();

	private DatabaseManager databasemanager = null;

	private final JPanel panel = new JPanel();

	private final Frame parent;

	private LoginDialog(Frame parent, Properties properties) {
		super(parent, "Log in", true);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

		this.parent = parent;
		setResizable(false);
		setSize(500, 200);
		getContentPane().setLayout(new GridBagLayout());

		setModal(true);
		connect.addActionListener(this);
		cancel.addActionListener(this);
		connections.addActionListener(this);

		Map<String, ConnectionParameters> parameters = new TreeMap<String, ConnectionParameters>();
		parameters.put("", new ConnectionParameters());
		for (Entry<Object, Object> entry : properties.entrySet()) {
			if (entry.getKey() instanceof String) {
				String key = (String) entry.getKey();
				int index = key.indexOf('.');
				ConnectionParameters currentparameter;
				if (index > 0) {
					String id = key.substring(0, index);
					currentparameter = parameters.get(id);
					if (currentparameter == null) {
						currentparameter = new ConnectionParameters();
						currentparameter.id = id;
						parameters.put(id, currentparameter);
					}
					key = key.substring(index + 1);
				} else {
					currentparameter = parameters.get("");
				}

				if (key.equals("driver")) {
					currentparameter.driver
							.setText(entry.getValue().toString());
				} else if (key.equals("url")) {
					currentparameter.url.setText(entry.getValue().toString());
				} else if (key.equals("user")) {
					currentparameter.username.setText(entry.getValue()
							.toString());
				} else if (key.equals("password")) {
					currentparameter.password.setText(entry.getValue()
							.toString());
				} else if (key.equals("description")) {
					currentparameter.description = entry.getValue().toString();
				}
			}
		}

		int index = 0;
		for (Entry<String, ConnectionParameters> entry : parameters.entrySet()) {
			connections.insertItemAt(entry.getValue(), index++);
		}
		ConnectionParameters custom = new ConnectionParameters();
		custom.description = "Custom";
		connections.insertItemAt(custom, index);
		connections.setSelectedIndex(0);

		getContentPane().add(
				connections,
				new GridBagConstraints(0, 0, 2, 1, 1, 1,
						GridBagConstraints.CENTER, GridBagConstraints.NONE,
						new Insets(0, 0, 0, 0), 0, 0));

		getContentPane().add(
				panel,
				new GridBagConstraints(0, 1, 2, 1, 1, 1,
						GridBagConstraints.CENTER, GridBagConstraints.NONE,
						new Insets(0, 0, 0, 0), 0, 0));
		getContentPane().add(
				connect,
				new GridBagConstraints(0, 2, 1, 1, 1, 1,
						GridBagConstraints.CENTER, GridBagConstraints.NONE,
						new Insets(0, 0, 0, 0), 0, 0));
		getContentPane().add(
				cancel,
				new GridBagConstraints(1, 2, 1, 1, 1, 1,
						GridBagConstraints.CENTER, GridBagConstraints.NONE,
						new Insets(0, 0, 0, 0), 0, 0));

	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == connect) {
			try {
				databasemanager = ((ConnectionParameters) connections
						.getSelectedItem()).connect();
			} catch (Exception ex) {
				log.error("Failed to connect to database.", ex);
				JOptionPane.showMessageDialog(parent,
						"Failed to connect to database. " + ex.getMessage(),
						"gisQL", JOptionPane.ERROR_MESSAGE);
				databasemanager = null;
				return;
			}
			dispose();

		} else if (e.getSource() == connections) {
			panel.removeAll();
			panel.add((ConnectionParameters) connections.getSelectedItem());
			panel.validate();
			this.repaint();
		} else if (e.getSource() == cancel) {
			dispose();
		}
	}
}
