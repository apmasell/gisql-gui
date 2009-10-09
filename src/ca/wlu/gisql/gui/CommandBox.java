package ca.wlu.gisql.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JToolBar;

public class CommandBox extends JToolBar implements ActionListener, KeyListener {

	private static final long serialVersionUID = 1202534133559622272L;

	private final JTextField command = new JTextField();

	private ActionListener listener = null;

	private final JButton run = new JButton("Run");

	private final Separator seperator = new Separator();

	public CommandBox() {
		setFloatable(false);
		setRollover(true);

		this.add(new JLabel("Query: "));
		command.addKeyListener(this);
		this.add(command);
		this.add(seperator);

		run.setFocusable(false);
		run.addActionListener(this);
		this.add(run);

	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == run) {
			prepareCommand();
		}
	}

	public void appendText(String text) {
		String existing = command.getText();
		if (existing.length() > 0
				&& !Character.isWhitespace(existing
						.charAt(existing.length() - 1))) {
			existing += " ";
		}
		existing += text;
		command.setText(existing);

	}

	public void clearCommand() {
		command.setText("");
	}

	public String getText() {
		return command.getText();
	}

	public void keyPressed(KeyEvent evt) {
		if (evt.getSource() == command && evt.getKeyCode() == KeyEvent.VK_ENTER) {
			prepareCommand();
		}
	}

	public void keyReleased(KeyEvent evt) {
	}

	public void keyTyped(KeyEvent evt) {
	}

	private void prepareCommand() {
		if (command.getText().trim().length() == 0) {
			return;
		}

		if (listener != null) {
			listener
					.actionPerformed(new ActionEvent(this, 0, command.getText()));
		}
	}

	public void setActionListener(ActionListener listener) {
		this.listener = listener;
	}
}
