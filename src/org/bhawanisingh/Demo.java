package org.bhawanisingh;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.WindowConstants;

import org.bhawanisingh.util.WaitingPane;

/**
 * Class to demonstrate the functionality of WaitingPane
 */
public class Demo extends JFrame {

	private JButton startButton;
	private WaitingPane waitingPane;

	public Demo() {
		super("Demo for Waiting Pane");
		setSize(300, 300);
		waitingPane = new WaitingPane();
		startButton = new JButton("Start");
		setGlassPane(waitingPane);

		startButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(Demo.this, "To exit close the window\n or \n wait for 10 seconds", "How to stop", JOptionPane.INFORMATION_MESSAGE);
				waitingPane.start();
				Thread thread = new Thread(new Runnable() {
					@Override
					public void run() {
						for (int i = 0; i < 10; ++i) {
							try {
								Thread.sleep(1000);
							} catch (InterruptedException e1) {
							}
						}
						waitingPane.stop();
					}
				});
				thread.start();
			}
		});

		add(startButton);

		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);

		setVisible(true);
	}

	public static void main(String[] args) {
		new Demo();

	}

}
