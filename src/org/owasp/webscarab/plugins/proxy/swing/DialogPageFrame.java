/**
 * 
 */
package org.owasp.webscarab.plugins.proxy.swing;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import org.springframework.richclient.command.ActionCommand;
import org.springframework.richclient.command.ActionCommandInterceptor;
import org.springframework.richclient.dialog.DialogPage;
import org.springframework.richclient.dialog.support.DialogPageUtils;
import org.springframework.richclient.util.GuiStandardUtils;

/**
 * @author rdawes
 * 
 */
public class DialogPageFrame {

	private Object lock = new Object();

	private JFrame frame;

	private ActionCommand cancelCommand;

	private boolean actionPerformed = false;

	public DialogPageFrame(DialogPage page, ActionCommand okCommand,
			ActionCommand cancelCommand) {
		this.cancelCommand = cancelCommand;
		frame = new JFrame();
		Container pane = frame.getContentPane();

		DialogPageUtils.adaptPageCompletetoGuarded(page, okCommand);
		JPanel titlePaneContainer = new JPanel(new BorderLayout());
		titlePaneContainer.add(DialogPageUtils.createTitlePane(page)
				.getControl());
		titlePaneContainer.add(new JSeparator(), BorderLayout.SOUTH);
		pane.add(titlePaneContainer, BorderLayout.NORTH);
		pane.add(DialogPageUtils.createButtonBar(new ActionCommand[] {
				okCommand, cancelCommand }), BorderLayout.SOUTH);
		JComponent pageControl = page.getControl();
		GuiStandardUtils.attachDialogBorder(pageControl);
		pane.add(pageControl);

		ActionCommandInterceptor interceptor = new ActionCommandInterceptor() {
			public void postExecution(ActionCommand command) {
				actionPerformed = true;
				synchronized (lock) {
					lock.notify();
				}
			}

			public boolean preExecution(ActionCommand command) {
				return true;
			}
		};
		okCommand.addCommandInterceptor(interceptor);
		cancelCommand.addCommandInterceptor(interceptor);

		frame.getRootPane().setDefaultButton(
				(JButton) okCommand.getButtonIn(pane));
		JLayeredPane layeredPane = frame.getLayeredPane();
		layeredPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
				KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "close-it");
		layeredPane.getActionMap().put("close-it",
				cancelCommand.getActionAdapter());
		frame.setTitle(page.getTitle());
		frame.setIconImage(page.getImage());
		frame.setSize(800, 600); // FIXME - we need some way of tracking the preferred size/location
		frame.addWindowListener(new CloseListener());
	}

	public void showAsDialog() {
		if (SwingUtilities.isEventDispatchThread())
			throw new IllegalStateException(
					"Cannot show as a dialog on the EDT!");
		frame.setVisible(true);
		frame.toFront();
		try {
			synchronized (lock) {
				lock.wait();
			}
		} catch (InterruptedException ie) {
		}
		frame.setVisible(false);
		frame.dispose();
	}

	private class CloseListener extends WindowAdapter {
		@Override
		public void windowClosed(WindowEvent e) {
			if (!actionPerformed)
				cancelCommand.execute();
		}
	}
}