package com.sun.forums.closabletab;

//From http://forums.sun.com/thread.jspa?threadID=384894
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.plaf.basic.BasicTabbedPaneUI;

import ca.wlu.gisql.gui.MainFrame;

public class ClosableTabbedPaneUI extends BasicTabbedPaneUI {
	class ClosableTabbedPaneMouseHandler extends MouseHandler {
		public ClosableTabbedPaneMouseHandler() {
			super();
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			int x = e.getX();
			int y = e.getY();
			int tabCount = tabPane.getTabCount();
			for (int index = 0; index < tabCount; index++) {
				if (rects[index].contains(x, y)) {
					x = x - rects[index].x + iconXOffset + image.getWidth(null)
							- rects[index].width;
					y = y - rects[index].y + iconYOffset
							+ image.getHeight(null) - rects[index].height;
					if (x >= 0 && x <= image.getWidth(null) && y >= 0
							&& y <= image.getHeight(null)) {
						tabPane.remove(index);
					}
					return;
				}
			}
		}

	}

	private static final int iconXOffset = 5;

	private static final int iconYOffset = 2;

	private final Image image = Toolkit.getDefaultToolkit().getImage(
			MainFrame.class.getResource("images/close.png"));

	public ClosableTabbedPaneUI() {
		super();
	}

	@Override
	protected int calculateTabWidth(int tabPlacement, int tabIndex,
			FontMetrics metrics) {
		return super.calculateTabWidth(tabPlacement, tabIndex, metrics)
				+ image.getWidth(null) + iconXOffset * 2;
	}

	@Override
	protected MouseListener createMouseListener() {
		return new ClosableTabbedPaneMouseHandler();
	}

	@Override
	protected void paintTab(Graphics g, int tabPlacement, Rectangle[] rects,
			int tabIndex, Rectangle iconRect, Rectangle textRect) {

		super.paintTab(g, tabPlacement, rects, tabIndex, iconRect, textRect);

		Rectangle rect = rects[tabIndex];
		g.drawImage(image, rect.x + rect.width - iconXOffset
				- image.getWidth(null), rect.y + rect.height - iconYOffset
				- image.getHeight(null), null);

	}
}
