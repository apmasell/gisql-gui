package ca.wlu.gisql;

import java.util.Properties;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

import ca.wlu.gisql.db.DatabaseEnvironment;
import ca.wlu.gisql.db.DatabaseManager;
import ca.wlu.gisql.environment.UserEnvironment;
import ca.wlu.gisql.gui.MainFrame;
import ca.wlu.gisql.gui.login.LoginDialog;

public class GisQLGui {
	protected static final Logger log = Logger.getLogger(GisQLGui.class);

	public static void main(String[] args) {
		ConsoleAppender appender = new ConsoleAppender(new PatternLayout());
		Logger.getRootLogger().addAppender(appender);

		Properties properties = null;
		try {
			properties = DatabaseManager.getPropertiesFromFile();
		} catch (Exception e) {
			log.warn("Could not access config file.", e);
		}
		if (properties == null) {
			properties = new Properties();
		}

		DatabaseManager dm = LoginDialog.connect(null, properties);
		if (dm == null) {
			return;
		}
		UserEnvironment environment = new UserEnvironment(
				new DatabaseEnvironment(dm));
		MainFrame frame = new MainFrame(environment);
		frame.setVisible(true);
	}

}
