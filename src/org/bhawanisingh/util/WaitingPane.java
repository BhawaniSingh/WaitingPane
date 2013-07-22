package org.bhawanisingh.util;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import javax.swing.JComponent;
import javax.swing.JLabel;

/**
 * @author bhawani
 * 
 *         Inbuilt Threading no need to define extra thread in your app
 * 
 *         extends Jcomponent so that it can be added to any Swing Container
 * 
 *         implements Runnable so that it must run in a separate thread
 * 
 *         implements MouseListener, KeyListener in order to block all events
 * 
 *         implements FocusListener so that this pane remains in focus
 */
public class WaitingPane extends JComponent implements Runnable, MouseListener, KeyListener, FocusListener {
	private static final long serialVersionUID = -3781071463506069338L;

	// Gradient for the tickers
	private static Paint gradient = new GradientPaint(10f, 0f, Color.DARK_GRAY, 21f, 0f, Color.BLACK);

	// Outer Tickers
	private Area[] tickerOuter = null;

	// Inner Tickers
	private Area[] tickerInner = null;

	// Thread should stop to continue running
	private boolean cont;

	// Thread to paint tickers.
	private Thread waitThread;

	// This will block all the the events when in focus. FocusListener is implemented so that this
	// label remain in focus
	private JLabel focus;

	// Only COnstructor of this class.
	public WaitingPane() {
		focus = new JLabel();
		focus.setOpaque(false);
		this.add(focus);
		addListeners();
	}

	private void addListeners() {
		// Added ComponentListener in order to fix resizing bug
		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				tickerOuter = buildTicker(true);
				tickerInner = buildTicker(false);
			}

		});
	}

	// This will stop the thread once and for all
	public void stop() {

		// Set running state of the thread to false.
		cont = false;
		// Remove KeyListener so no more blocking of of the KeyEvents
		removeKeyListener(this);
		// Remove MouseListener so no more blocking of of the MouseEvents
		removeMouseListener(this);
		// Remove FocusListener so other elements get focus
		removeFocusListener(this);
		// Visibility toggled to false so the WaitingPane is no more visible
		setVisible(false);
	}

	public void start() {

		// Set running state of the tread to true
		cont = true;
		// KeyListener added to block KeyEvents
		addKeyListener(this);
		// MouseListener added to block MouseEvents
		addMouseListener(this);
		// FocusListener added so that other element can't get focus
		addFocusListener(this);
		// Always get focus
		focus.requestFocusInWindow();
		tickerOuter = buildTicker(true);
		tickerInner = buildTicker(false);
		setVisible(true);
		// Start the thread and now the WaitingPane will be visible on the top of the application
		waitThread = new Thread(this);
		waitThread.start();
	}

	@Override
	public void run() {
		while (cont) {
			try {
				Thread.sleep(100);
				rotateTicker();
				// Invoked Repaint so to paint next location of the tickers
				this.repaint();
			} catch (InterruptedException e) {
			}
			// Synchronized in order to avoid errors in the program
			synchronized (this) {
				if (!isShowing()) {
					cont = false;
				}
			}
		}
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		paintComponents(g);
		g.dispose();
	}

	// Everything in this method is self-explainable. If not drop a note i will explain
	@Override
	public void paintComponents(Graphics g) {
		Graphics2D graphics = (Graphics2D) g;
		graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		graphics.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
		graphics.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
		graphics.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
		graphics.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
		graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		graphics.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
		graphics.setFont(new Font(getFont().getName(), Font.ITALIC, 50));
		graphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.1f));
		graphics.setPaint(WaitingPane.gradient);

		for (int i = 0; i < 11; i++) {
			graphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, i * 0.1f));
			graphics.fill(tickerOuter[i]);
			graphics.fill(tickerInner[i]);

		}
		// This method ensures that the display is up-to-date. It is useful for animation.
		Toolkit.getDefaultToolkit().sync();
		// Releases system resources and disposes graphics context
		g.dispose();
	}

	// This method will rotate the ticker
	private void rotateTicker() {
		int barsCount = 11;
		Area tempOuter = tickerOuter[0];
		Area tempInner = tickerInner[barsCount - 1];
		for (int i = 0; i < barsCount - 1; ++i) {
			tickerOuter[i] = tickerOuter[i + 1];
			tickerInner[barsCount - (i + 1)] = tickerInner[barsCount - (i + 2)];
		}
		tickerOuter[barsCount - 1] = tempOuter;
		tickerInner[0] = tempInner;

	}

	// Build complete set of tickers and modify them so that they form a circle
	private Area[] buildTicker(boolean outer) {
		// Number of tickers that will be displayed
		int barsCount = 11;
		AffineTransform toCenter;
		AffineTransform toBorder;
		AffineTransform toCircle;
		Area[] ticker = new Area[barsCount];
		// Locate the center of the app window/WaitingPane
		Point2D.Double center = new Point2D.Double((double) getWidth() / 2, (double) getHeight() / 2);
		// Calculate the angle between the tickers
		double fixedAngle = 2.0 * Math.PI / barsCount;
		// Align tickers in circle.
		for (int i = 0; i < barsCount; i++) {
			Area primitive = buildPrimitive(outer);
			toCenter = AffineTransform.getTranslateInstance(center.getX(), center.getY());
			if (outer) {
				toBorder = AffineTransform.getTranslateInstance(60, 20.0);
			} else {
				toBorder = AffineTransform.getTranslateInstance(20, -10.0);
			}

			toCircle = AffineTransform.getRotateInstance(-i * fixedAngle, center.getX(), center.getY());

			AffineTransform toWheel = new AffineTransform();
			toWheel.concatenate(toCenter);
			toWheel.concatenate(toBorder);

			primitive.transform(toWheel);
			primitive.transform(toCircle);

			ticker[i] = primitive;
		}

		return ticker;
	}

	// Build single ticker
	private Area buildPrimitive(boolean outer) {
		Rectangle2D.Double body;
		Ellipse2D.Double head;
		Ellipse2D.Double tail;
		if (outer) {
			// Center of the outer tickers
			body = new Rectangle2D.Double(3, 0, 30, 6);
			// Center of the outer tickers
			head = new Ellipse2D.Double(0, 0, 6, 6);
			// Center of the outer tickers
			tail = new Ellipse2D.Double(30, 0, 6, 6);
		} else {
			// Center of the inner tickers
			body = new Rectangle2D.Double(3, 0, 15, 6);
			// Head of the inner tickers
			head = new Ellipse2D.Double(0, 0, 6, 6);
			// Tail of the inner tickers
			tail = new Ellipse2D.Double(15, 0, 6, 6);
		}

		Area tick = new Area(body);
		tick.add(new Area(head));
		tick.add(new Area(tail));
		return tick;
	}

	public Thread getWaitThread() {
		return waitThread;
	}

	/*
	 * Methods below are used for blocking various events during the lifecycle of the WaitingPane
	 * Thread
	 */
	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void keyPressed(KeyEvent e) {
	}

	@Override
	public void keyReleased(KeyEvent e) {
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void focusGained(FocusEvent e) {
	}

	@Override
	public void focusLost(FocusEvent e) {
		if (isVisible()) {
			focus.requestFocusInWindow();
		}

	}

}