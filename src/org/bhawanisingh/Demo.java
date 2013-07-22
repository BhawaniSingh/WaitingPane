/**
  *  WaitingPanel
  *      Copyright (C) 2013  Bhawani Singh
  *  
  *      This program is free software: you can redistribute it and/or modify
  *      it under the terms of the GNU General Public License as published by
  *      the Free Software Foundation, either version 3 of the License, or
  *      (at your option) any later version.
  *  
  *      This program is distributed in the hope that it will be useful,
  *      but WITHOUT ANY WARRANTY; without even the implied warranty of
  *      MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  *      GNU General Public License for more details.
  *  
  *      You should have received a copy of the GNU General Public License
  *      along with this program.  If not, see <http://www.gnu.org/licenses/>.
  */
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
