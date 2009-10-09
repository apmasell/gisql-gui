package ca.wlu.gisql.gui;

import java.util.Collection;

import javax.swing.SwingUtilities;

import ca.wlu.gisql.ast.type.Type;
import ca.wlu.gisql.interactome.CachedInteractome;
import ca.wlu.gisql.interactome.Interactome;
import ca.wlu.gisql.runner.ExpressionError;
import ca.wlu.gisql.runner.ExpressionRunListener;

public final class SwingThreadBouncer implements ExpressionRunListener {

	private final ExpressionRunListener parent;

	public SwingThreadBouncer(ExpressionRunListener parent) {
		this.parent = parent;
	}

	public void processInteractome(final Interactome value) {
		final CachedInteractome interactome = CachedInteractome.wrap(value,
				null);
		interactome.process();
		if (SwingUtilities.isEventDispatchThread()) {
			parent.processInteractome(interactome);
		} else {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					parent.processInteractome(interactome);
				}
			});
		}
	}

	public void processOther(final Type type, final Object value) {
		if (SwingUtilities.isEventDispatchThread()) {
			parent.processOther(type, value);
		} else {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					parent.processOther(type, value);
				}
			});
		}
	}

	public void reportErrors(final Collection<ExpressionError> errors) {
		if (SwingUtilities.isEventDispatchThread()) {
			parent.reportErrors(errors);
		} else {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					parent.reportErrors(errors);
				}
			});
		}
	}

}
