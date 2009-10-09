package ca.wlu.gisql.gui.view;

import javax.swing.JPopupMenu;

import ca.wlu.gisql.Membership;
import ca.wlu.gisql.graph.Interaction;
import ca.wlu.gisql.interactome.CachedInteractome;
import ca.wlu.gisql.util.ShowableStringBuilder;

public class InteractionTable extends AbstractTable {

	public InteractionTable(CachedInteractome interactome) {
		super(interactome, new Class[] { String.class, String.class,
				Double.class, String.class }, new String[] { "Gene1", "Gene2",
				"Membership", "Description" });
	}

	@Override
	protected JPopupMenu createMenu(int row) {
		Interaction interaction = interactome.getInteractions().get(row);
		return new GeneInfo(interaction.getGene1()).append(interaction
				.getGene2());
	}

	public int getRowCount() {
		return interactome.getInteractions().size();
	}

	public final Object getValueAt(int rowIndex, int colIndex) {
		Interaction interaction = interactome.getInteractions().get(rowIndex);

		if (interaction == null) {
			return null;
		}

		switch (colIndex) {
		case 0:
			return ShowableStringBuilder.toString(interaction.getGene1(),
					Membership.collectAll(interactome));
		case 1:
			return ShowableStringBuilder.toString(interaction.getGene2(),
					Membership.collectAll(interactome));
		case 2:
			return interaction.getMembership(interactome);
		case 3:
			return ShowableStringBuilder.toString(interaction, Membership
					.collectAll(interactome));
		default:
			return null;
		}
	}
}
