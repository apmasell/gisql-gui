package ca.wlu.gisql.gui.util;

import java.awt.Component;

import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;

import ca.wlu.gisql.ast.AstNode;
import ca.wlu.gisql.gui.util.EnvironmentTreeView.AstNodeTreeNode;
import ca.wlu.gisql.util.ShowableStringBuilder;

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
				AstNode node = ((AstNodeTreeNode) value).getNode();
				if (node != null) {
					treerenderer.setToolTipText(ShowableStringBuilder.toString(
							node, null));
					return treerenderer;
				}
			}
			treerenderer.setToolTipText(null);
		}
		return treerenderer;
	}

}
