/**
 * View.java
 * 
 * Version: 1.6
 * 
 * Revisions:
 * 		Initial version
 */
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.Serializable;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * View class for the game of reversi. Handles all the UI related part
 * 
 * @author Hitesh Vyas
 */
public class View implements Serializable {

	Model r;
	JFrame frame;
	
	
	/**
	 * constructor that initializes r
	 * 
	 * @param r reversi object
	 */
	View(Model r) {
		this.r = r;
	}
	
	/**
	 * method that initializes the board and starts the game
	 */
	public void initBoard() {

		// create a frame
		if(r.whichPlayer == 1) {
			frame = new JFrame("Red");
		} else {
			frame = new JFrame("Blue");
		}
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});

		// set size of the frame
		JButton dummyButton = new JButton("O");
		Dimension size = dummyButton.getPreferredSize();
		size.setSize(9.5 * size.getWidth(), 10.5 * size.getHeight());
		frame.setPreferredSize(size);

		// get the content pane of the frame
		Container container = frame.getContentPane();
		
		// JPanel to display the buttons
		JPanel pane = new JPanel();
		pane.setLayout(new GridLayout(8, 8));
		
		// create the buttons
		for (int i = 0; i < r.MAX_BUTTONS; i++) {

			r.buttons[i] = r.new MyButton(i);

			r.buttons[i].addActionListener(new Controller(r));

			pane.add(r.buttons[i]);
		}

		// JPanel to display the bottom pane 
		JPanel bottomPane = new JPanel();
		
		// JPanel to display the current player
		r.player = new JLabel("Red plays       ");
		bottomPane.add(r.player, BorderLayout.WEST);
		
		// count of red buttons
		r.xLabel = new JLabel("Red: 2");
		bottomPane.add(r.xLabel, BorderLayout.CENTER);

		// count of blue buttons
		r.oLabel = new JLabel("Blue: 2");
		bottomPane.add(r.oLabel, BorderLayout.EAST);

		// add the grid of buttons and status label to the container
		container.add(pane, BorderLayout.NORTH);
		container.add(bottomPane, BorderLayout.SOUTH);
		
		// display the frame
		frame.pack();
		frame.setVisible(true);
	}

	/**
	 * display the final status dialog
	 */
	public void showFinalDialog() {
		
		// create a frame
		JFrame dialog = new JFrame("Blue");
		dialog.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		
		// set the size
		dialog.setPreferredSize(new Dimension(200, 100));
		
		// add a label to display the winner
		dialog.add(new JLabel(r.getWinnersLabel()), BorderLayout.CENTER);
		
		// display the frame
		dialog.pack();
		dialog.setVisible(true);
	}
}
