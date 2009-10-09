package ca.wlu.gisql.gui.view;

import javax.swing.JPopupMenu;

import ca.wlu.gisql.Membership;
import ca.wlu.gisql.graph.Gene;
import ca.wlu.gisql.interactome.CachedInteractome;
import ca.wlu.gisql.util.ShowableStringBuilder;

public class GeneTable extends AbstractTable {

	public GeneTable(CachedInteractome interactome) {
		super(interactome, new Class[] { String.class, Double.class },
				new String[] { "Identifiers", "Membership" });
	}

	@Override
	protected JPopupMenu createMenu(int row) {
		return new GeneInfo(interactome.getGenes().get(row));
	}

	public int getRowCount() {
		return interactome.getGenes().size();
	}

	public Object getValueAt(int rowIndex, int colIndex) {
		Gene gene = interactome.getGenes().get(rowIndex);

		if (gene == null) {
			return null;
		}

		switch (colIndex) {
		case 0:
			return ShowableStringBuilder.toString(gene, Membership
					.collectAll(interactome));
		case 1:
			return gene.getMembership(interactome);
		default:
			return null;
		}
	}
}
