/**
 * 
 */
package org.owasp.webscarab.ui.forms;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentAdapter;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.HyperlinkEvent.EventType;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.Position;
import javax.swing.text.StyleConstants;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLEditorKit;

import org.springframework.binding.form.FormModel;
import org.springframework.binding.value.ValueModel;
import org.springframework.richclient.form.AbstractForm;

/**
 * @author rdawes
 * 
 */
public class HtmlForm extends AbstractForm {

	private static String FORM_ID = "htmlForm";

	private ContentListener listener;

	private ValueModel vm;

	private JEditorPane editorPane;

	private boolean upToDate = false;
	
	public HtmlForm(FormModel model, String propertyName) {
		super(model, FORM_ID);
		vm = model.getValueModel(propertyName);
		listener = new ContentListener();
		vm.addValueChangeListener(listener);
	}

	@Override
	protected JComponent createFormControl() {
		editorPane = new NoNetEditorPane();
		editorPane.setEditable(false);
		editorPane.setEditorKit(new MyHTMLEditorKit());
		editorPane.addHyperlinkListener(new LinkToolTipListener());
		JScrollPane scrollPane = getComponentFactory().createScrollPane(editorPane);
		// we use a component listener to delay rendering the HTML until
		// someone is actually interested in it i.e. the component is shown
		// we have to add it to the scrollPane, since the editorPane does not receive
		// the necessary events for some reason.
		scrollPane.addComponentListener(listener);
		return scrollPane;
	}

	private void updateEditorPane() {
		editorPane.setContentType("text/html");
        editorPane.setDocument(JEditorPane.createEditorKitForContentType("text/html").createDefaultDocument());
        editorPane.putClientProperty("IgnoreCharsetDirective", Boolean.TRUE);
        editorPane.getDocument().putProperty("IgnoreCharsetDirective", Boolean.TRUE);
        editorPane.setText(contentString());
        upToDate = true;
	}
	
	private String contentString() {
		byte[] content = (byte[]) vm.getValue();
		if (content == null)
			return null;
		return new String(content);
	}

	private class ContentListener extends ComponentAdapter implements PropertyChangeListener {

		public void propertyChange(PropertyChangeEvent evt) {
			upToDate = false;
			if (editorPane != null && editorPane.isShowing())
				updateEditorPane();
		}

		public void componentShown(ComponentEvent e) {
			if (!upToDate)
				updateEditorPane();
		}

	}

	private static class MyHTMLEditorKit extends HTMLEditorKit {

		private static final long serialVersionUID = 4540043911244942634L;

		private static final ViewFactory defaultFactory = new MyHTMLFactory();

		public ViewFactory getViewFactory() {
			return defaultFactory;
		}

		private static class MyHTMLFactory extends HTMLEditorKit.HTMLFactory {
			public View create(Element elem) {
				Object o = elem.getAttributes().getAttribute(
						StyleConstants.NameAttribute);
				if (o instanceof HTML.Tag) {
					HTML.Tag kind = (HTML.Tag) o;
					if (kind == HTML.Tag.FRAME || kind == HTML.Tag.FRAMESET
							|| kind == HTML.Tag.OBJECT
							|| kind == HTML.Tag.APPLET) {
						return new NoView(elem);
					}
				}
				return super.create(elem);
			}
		}

		private static class NoView extends View {
			public NoView(Element elem) {
				super(elem);
				setSize(0.0f, 0.0f);
			}

			public int viewToModel(float fx, float fy, Shape a,
					Position.Bias[] bias) {
				return 0;
			}

			public Shape modelToView(int pos, Shape a, Position.Bias b)
					throws BadLocationException {
				return new Rectangle(0, 0);
			}

			public float getPreferredSpan(int axis) {
				return 0.0f;
			}

			public void paint(Graphics g, Shape allocation) {
			}
		}

	}

	private class NoNetEditorPane extends JEditorPane {

		private static final long serialVersionUID = 3954553074162913078L;

		protected InputStream getStream(URL page) throws IOException {
			throw new IOException("We do not support network traffic");
		}
	}

	private class LinkToolTipListener implements HyperlinkListener {
		public LinkToolTipListener() {
		}

		public void hyperlinkUpdate(HyperlinkEvent he) {
			EventType type = he.getEventType();
			if (type == EventType.ENTERED) {
				JEditorPane jep = (JEditorPane) he.getSource();
				URL url = he.getURL();
				if (url != null) {
					jep.setToolTipText(url.toString());
				} else {
					jep.setToolTipText(he.getDescription());
				}
			} else if (type == EventType.EXITED) {
				JEditorPane jep = (JEditorPane) he.getSource();
				jep.setToolTipText("");
			}
		}
	}

}