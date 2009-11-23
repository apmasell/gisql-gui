package ca.wlu.gisql.gui.util;

import java.awt.Component;

import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;

import ca.wlu.gisql.ast.AstNative;
import ca.wlu.gisql.ast.util.GenericFunction;
import ca.wlu.gisql.gui.util.EnvironmentTreeView.AstNodeTreeNode;

public class InteractomeTreeCellRender implements TreeCellRenderer {

	private final DefaultTreeCellRenderer treerenderer = new DefaultTreeCellRenderer();

	public InteractomeTreeCellRender() {
		super();
	}

	public Component getTreeCellRendererComponent(JTree tree, Object value,
			boolean selected, boolean expanded, boolean leaf, int row,
			boolean hasFocus) {
		treerenderer.getTreeCellRendererComponent(tree, value, selected,
				expanded, leaf, row, hasFocus);
		if (value != null) {
			if (value instanceof AstNodeTreeNode) {
				Object node = ((AstNodeTreeNode) value).getNode();
				if (node != null) {
					if (node instanceof GenericFunction) {
						treerenderer.setToolTipText(((AstNative) node)
								.getDescription());
					} else {
						treerenderer.setToolTipText(node.toString());
					}
					return treerenderer;
				}
			}
			treerenderer.setToolTipText(null);
		}
		return treerenderer;
	}

}
