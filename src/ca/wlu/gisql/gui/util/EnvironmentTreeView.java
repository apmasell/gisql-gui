package ca.wlu.gisql.gui.util;

import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Map.Entry;

import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import ca.wlu.gisql.ast.AstNode;
import ca.wlu.gisql.environment.Environment;
import ca.wlu.gisql.environment.EnvironmentListener;

public class EnvironmentTreeView extends DefaultTreeModel implements
		EnvironmentListener {

	public static class AstNodeTreeNode extends DefaultMutableTreeNode {

		private static final long serialVersionUID = 6806723183738094759L;
		private final AstNode node;

		private AstNodeTreeNode(String name, AstNode node) {
			super(name);
			this.node = node;
		}

		public AstNode getNode() {
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

	public void addedEnvironmentVariable(String name, AstNode node) {
		prepareTree();
	}

	private void appendFromMap(String name, SortedMap<String, AstNode> map) {
		if (map.size() == 0) {
			return;
		}
		DefaultMutableTreeNode tree = new DefaultMutableTreeNode(name);
		for (Entry<String, AstNode> entry : map.entrySet()) {
			DefaultMutableTreeNode child = new AstNodeTreeNode(entry.getKey(),
					entry.getValue());
			tree.add(child);
		}
		root.add(tree);
	}

	public void droppedEnvironmentVariable(String name, AstNode node) {
		prepareTree();
	}

	public void lastChanged() {
		prepareTree();
	}

	private void prepareTree() {
		if (SwingUtilities.isEventDispatchThread()) {
			root.removeAllChildren();

			SortedMap<String, SortedMap<String, AstNode>> typemap = new TreeMap<String, SortedMap<String, AstNode>>();
			for (Entry<String, AstNode> entry : environment) {
				String type = entry.getValue().getType().toString();

				SortedMap<String, AstNode> valuemap = typemap.get(type);
				if (valuemap == null) {
					valuemap = new TreeMap<String, AstNode>();
					typemap.put(type, valuemap);
				}
				valuemap.put(entry.getKey(), entry.getValue());

			}

			for (Entry<String, SortedMap<String, AstNode>> entry : typemap
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
