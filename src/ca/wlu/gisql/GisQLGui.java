package ca.wlu.gisql;

import javax.swing.JOptionPane;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

import ca.wlu.gisql.db.DatabaseEnvironment;
import ca.wlu.gisql.db.DatabaseManager;
import ca.wlu.gisql.environment.UserEnvironment;
import ca.wlu.gisql.gui.MainFrame;

public class GisQLGui {
	public static void main(String[] args) {
		ConsoleAppender appender = new ConsoleAppender(new PatternLayout());
		Logger.getRootLogger().addAppender(appender);

		DatabaseManager dm;
		try {
			dm = new DatabaseManager(DatabaseManager.getPropertiesFromFile());
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null,
					"Failed to connect to database.", "Error - gisQL",
					JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
			return;
		}

		UserEnvironment environment = new UserEnvironment(
				new DatabaseEnvironment(dm));
		MainFrame frame = new MainFrame(environment);
		frame.setVisible(true);
	}

}
