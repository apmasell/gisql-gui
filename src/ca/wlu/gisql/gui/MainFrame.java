package ca.wlu.gisql.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.Timer;
import javax.swing.ToolTipManager;
import javax.swing.WindowConstants;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreeSelectionModel;

import org.apache.log4j.Logger;

import ca.wlu.gisql.ast.type.Type;
import ca.wlu.gisql.environment.UserEnvironment;
import ca.wlu.gisql.gui.util.EnvironmentTreeView;
import ca.wlu.gisql.gui.util.InteractomeTreeCellRender;
import ca.wlu.gisql.gui.util.EnvironmentTreeView.AstNodeTreeNode;
import ca.wlu.gisql.gui.view.InteractomeResultView;
import ca.wlu.gisql.interactome.CachedInteractome;
import ca.wlu.gisql.interactome.Interactome;
import ca.wlu.gisql.runner.ExpressionError;
import ca.wlu.gisql.runner.ExpressionRunListener;
import ca.wlu.gisql.runner.ThreadedExpressionRunner;

import com.sun.forums.closabletab.ClosableTabbedPaneUI;

public class MainFrame extends JFrame implements ActionListener,
		ExpressionRunListener, TreeSelectionListener {

	private static final Logger log = Logger.getLogger(MainFrame.class);

	private static final long serialVersionUID = -1767901719339978452L;

	private final JProgressBar busy = new JProgressBar();

	private final CommandBox command;

	private final UserEnvironment environment;

	private final EnvironmentTreeView environmentTree;

	private final FilterBox filter = new FilterBox();

	private final JSplitPane innersplitpane = new JSplitPane();

	private final JMenuBar menu = new JMenuBar();

	private final JMenuItem menuClear = new JMenuItem("Clear Variables");

	private final JMenu menuMain = new JMenu("Main");

	private final JMenuItem menuQuit = new JMenuItem("Quit");

	private final JSeparator quitseparator = new JSeparator();

	private final JTabbedPane results = new JTabbedPane();

	private final ThreadedExpressionRunner runner;

	private final Timer timer;

	private final JTree variablelist;

	private final JScrollPane variablelistPane;

	public MainFrame(UserEnvironment environment) {
		super("gisQL");
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		this.environment = environment;

		runner = new ThreadedExpressionRunner(environment,
				new SwingThreadBouncer(this));

		timer = new Timer(100, this);

		command = new CommandBox();
		command.setActionListener(this);
		command.add(filter);
		command.add(busy);

		environmentTree = new EnvironmentTreeView(environment);
		variablelist = new JTree(environmentTree);
		variablelist.addTreeSelectionListener(this);
		ToolTipManager.sharedInstance().registerComponent(variablelist);
		variablelist.setCellRenderer(new InteractomeTreeCellRender());
		variablelist.getSelectionModel().setSelectionMode(
				TreeSelectionModel.SINGLE_TREE_SELECTION);
		variablelistPane = new JScrollPane(variablelist);

		results.setUI(new ClosableTabbedPaneUI());
		results.setPreferredSize(new Dimension(400, 300));

		innersplitpane.setRightComponent(variablelistPane);
		innersplitpane.setLeftComponent(results);

		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(command, BorderLayout.NORTH);
		getContentPane().add(innersplitpane, BorderLayout.CENTER);

		menuClear.addActionListener(this);
		menuMain.add(menuClear);
		menuMain.add(quitseparator);

		menuQuit.setAccelerator(KeyStroke.getKeyStroke(
				java.awt.event.KeyEvent.VK_Q,
				java.awt.event.InputEvent.CTRL_MASK));
		menuQuit.addActionListener(this);
		menuMain.add(menuQuit);

		menu.add(menuMain);

		setJMenuBar(menu);

		pack();
		timer.start();
	}

	public void actionPerformed(ActionEvent evt) {
		if (evt.getSource() == command) {
			runner.run(command.getText(), null);
		} else if (evt.getSource() == timer) {
			busy.setIndeterminate(runner.isBusy());
		} else if (evt.getSource() == menuClear) {
			environment.clear();
		} else if (evt.getSource() == menuQuit) {
			System.exit(0);
		}
	}

	public void processInteractome(Interactome value) {
		CachedInteractome interactome = (CachedInteractome) value;
		results.addTab(value.toString(), new InteractomeResultView(interactome,
				filter));
	}

	public void processOther(Type type, Object value) {
		if (type == Type.UnitType) {
			return;
		} else {
			JTextArea text = new JTextArea(value.toString());
			text.setEditable(false);
			results.addTab(type.toString(), text);
		}
	}

	public void reportErrors(Collection<ExpressionError> errors) {
		// TODO Auto-generated method stub

	}

	public void valueChanged(TreeSelectionEvent evt) {
		try {
			Object selected = variablelist.getLastSelectedPathComponent();
			if (selected instanceof AstNodeTreeNode) {
				command.appendText(selected.toString());
			}
		} catch (Exception e) {
			log.error("Error picking interactome from list.", e);
		}
	}
}
