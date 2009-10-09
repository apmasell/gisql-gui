package ca.wlu.gisql.gui.view;

import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;

import ca.wlu.gisql.gui.FilterBox;
import ca.wlu.gisql.interactome.CachedInteractome;

import com.sun.forums.filteringtable.FilteringTableModel;

public class InteractomeResultView extends JTabbedPane {

	private static final long serialVersionUID = -5429564862842971330L;

	private final JTable genes = new JTable();

	private final JScrollPane genesspane = new JScrollPane(genes);

	private final JTable interactions = new JTable();

	private final JScrollPane interactionspane = new JScrollPane(interactions);

	public InteractomeResultView(CachedInteractome interactome, FilterBox filter) {
		super();
		InteractionTable interactiontable = new InteractionTable(interactome);
		GeneTable genetable = new GeneTable(interactome);

		FilteringTableModel interactionModel = new FilteringTableModel(
				interactiontable);
		interactionModel.addFilter(filter);
		interactions.setModel(interactionModel);

		FilteringTableModel geneModel = new FilteringTableModel(genetable);
		geneModel.addFilter(filter);
		genes.setModel(geneModel);

		genes.addMouseListener(genetable);
		genes.getSelectionModel().addListSelectionListener(genetable);
		interactions.addMouseListener(interactiontable);
		interactions.getSelectionModel().addListSelectionListener(
				interactiontable);

		interactions.setAutoCreateRowSorter(true);
		genes.setAutoCreateRowSorter(true);

		addTab("Interactions", interactionspane);
		addTab("Genes", genesspane);
	}

}
