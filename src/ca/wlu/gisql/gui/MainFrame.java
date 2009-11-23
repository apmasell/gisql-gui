package ca.wlu.gisql.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.GroupLayout;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
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
import javax.swing.GroupLayout.ParallelGroup;
import javax.swing.GroupLayout.SequentialGroup;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
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
import ca.wlu.gisql.runner.AstContext;
import ca.wlu.gisql.runner.ExpressionContext;
import ca.wlu.gisql.runner.ExpressionError;
import ca.wlu.gisql.runner.ExpressionRunListener;
import ca.wlu.gisql.runner.FileContext;
import ca.wlu.gisql.runner.FileLineContext;
import ca.wlu.gisql.runner.PositionContext;
import ca.wlu.gisql.runner.SingleLineContext;
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

	private JComponent makeComponentWrapper(Object value) {
		if (value instanceof List<?>) {
			JPanel panel = new JPanel();
			GroupLayout layout = new GroupLayout(panel);
			panel.setLayout(layout);
			layout.setAutoCreateGaps(true);
			layout.setAutoCreateContainerGaps(true);

			ParallelGroup horizontalgroup = layout.createParallelGroup();
			SequentialGroup verticalgroup = layout.createSequentialGroup();

			for (Object object : (List<?>) value) {
				JComponent component = makeComponentWrapper(object);
				horizontalgroup.addComponent(component);
				verticalgroup.addComponent(component);
			}
			layout.setHorizontalGroup(horizontalgroup);
			layout.setVerticalGroup(verticalgroup);
			return new JScrollPane(panel);
		} else {
			JTextArea text = new JTextArea(value.toString());
			text.setEditable(false);
			text.setLineWrap(true);
			text.setWrapStyleWord(true);
			return new JScrollPane(text);
		}
	}

	public void processInteractome(Interactome value) {
		CachedInteractome interactome = (CachedInteractome) value;
		results.addTab(value.toString(), new InteractomeResultView(interactome,
				filter));
		results.setSelectedIndex(results.getTabCount() - 1);
	}

	public void processOther(Type type, Object value) {
		if (type == Type.UnitType) {
			return;
		} else {
			results.addTab(type.toString(), makeComponentWrapper(value));
			results.setSelectedIndex(results.getTabCount() - 1);
		}
	}

	private DefaultMutableTreeNode reportErrorNode(DefaultMutableTreeNode root,
			Map<ExpressionContext, DefaultMutableTreeNode> map,
			ExpressionContext context) {
		if (map.containsKey(context)) {
			return map.get(context);
		}

		DefaultMutableTreeNode result;

		if (context instanceof PositionContext) {
			PositionContext positioncontext = (PositionContext) context;

			result = new DefaultMutableTreeNode("Position "
					+ positioncontext.getPosition());
			reportErrorNode(root, map, positioncontext.getParent()).add(result);
		} else if (context instanceof SingleLineContext) {
			SingleLineContext singlelinecontext = (SingleLineContext) context;

			result = new DefaultMutableTreeNode("Command: "
					+ singlelinecontext.getLine());
			root.add(result);
		} else if (context instanceof FileLineContext) {
			FileLineContext filelinecontext = (FileLineContext) context;

			result = new DefaultMutableTreeNode("Line"
					+ filelinecontext.getLineNumber());
			reportErrorNode(root, map, filelinecontext.getParent()).add(result);
		} else if (context instanceof FileContext) {
			FileContext filecontext = (FileContext) context;
			result = new DefaultMutableTreeNode(filecontext.getFile().getName());
			root.add(result);
		} else if (context instanceof AstContext) {
			AstContext astcontext = (AstContext) context;

			result = new DefaultMutableTreeNode("Expression ("
					+ astcontext.getNode() + ")");

			reportErrorNode(root, map, astcontext.getParent()).add(result);
		} else {
			result = new DefaultMutableTreeNode("<unknown>");
		}
		map.put(context, result);
		return result;
	}

	public void reportErrors(Collection<ExpressionError> errors) {
		DefaultMutableTreeNode root = new DefaultMutableTreeNode("Errors Found");
		java.util.Map<ExpressionContext, DefaultMutableTreeNode> map = new HashMap<ExpressionContext, DefaultMutableTreeNode>();
		for (ExpressionError error : errors) {
			DefaultMutableTreeNode node = reportErrorNode(root, map, error
					.getContext());
			node.add(new DefaultMutableTreeNode(error.getMessage()));
		}
		JTree errortree = new JTree(root);
		results.addTab("Errors", errortree);
		results.setSelectedIndex(results.getTabCount() - 1);
		for (int index = 0; index < errortree.getRowCount(); index++) {
			errortree.expandRow(index);
		}

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
