package ca.wlu.gisql.gui.util;

import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Map.Entry;

import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import ca.wlu.gisql.ast.type.Type;
import ca.wlu.gisql.environment.Environment;
import ca.wlu.gisql.environment.EnvironmentListener;

public class EnvironmentTreeView extends DefaultTreeModel implements
		EnvironmentListener {

	public static class AstNodeTreeNode extends DefaultMutableTreeNode {

		private static final long serialVersionUID = 6806723183738094759L;
		private final Object node;

		private AstNodeTreeNode(String name, Object node) {
			super(name);
			this.node = node;
		}

		public Object getNode() {
			return node;
		}
	}

	private static final long serialVersionUID = -4745797101983139778L;

	private final Environment environment;
	private final DefaultMutableTreeNode root;

	public EnvironmentTreeView(Environment environment) {
		super(new DefaultMutableTreeNode("Environment"));
		root = (DefaultMutableTreeNode) getRoot();
		this.environment = environment;
		environment.addListener(this);
		prepareTree();
	}

	public void addedEnvironmentVariable(String name, Object value, Type type) {
		prepareTree();
	}

	private void appendFromMap(String name, SortedMap<String, Object> sortedMap) {
		if (sortedMap.size() == 0) {
			return;
		}
		DefaultMutableTreeNode tree = new DefaultMutableTreeNode(name);
		for (Entry<String, Object> entry : sortedMap.entrySet()) {
			DefaultMutableTreeNode child = new AstNodeTreeNode(entry.getKey(),
					entry.getValue());
			tree.add(child);
		}
		root.add(tree);
	}

	public void droppedEnvironmentVariable(String name, Object value, Type type) {
		prepareTree();
	}

	public void lastChanged() {
		prepareTree();
	}

	private void prepareTree() {
		if (SwingUtilities.isEventDispatchThread()) {
			root.removeAllChildren();

			SortedMap<String, SortedMap<String, Object>> typemap = new TreeMap<String, SortedMap<String, Object>>();
			for (Entry<String, Object> entry : environment) {
				String type = environment.getTypeOf(entry.getKey()).toString();

				SortedMap<String, Object> valuemap = typemap.get(type);
				if (valuemap == null) {
					valuemap = new TreeMap<String, Object>();
					typemap.put(type, valuemap);
				}
				valuemap.put(entry.getKey(), entry.getValue());

			}

			for (Entry<String, SortedMap<String, Object>> entry : typemap
					.entrySet()) {
				appendFromMap(entry.getKey(), entry.getValue());
			}

			nodeStructureChanged(root);
		} else {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					prepareTree();
				}
			});

		}
	}
}
