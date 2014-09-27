/**
 * Reversi.java
 * 
 * Version: 1.8
 * 
 * Revisions:
 * 		Initial version
 */
import java.awt.Color;
import java.awt.Dimension;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import javax.swing.JButton;
import javax.swing.JLabel;

/**
 * Play a game of Reversi or Othello as player 2
 * 
 * @author Hitesh Vyas
 */
public class Model extends UnicastRemoteObject implements Serializable, RemoteInterface {

	public String toString() {
		return "" + whichPlayer;
	};
	
	// string to indicate who plays next
	String whoseTurn;

	// labels to display status
	JLabel player;
	JLabel xLabel;
	JLabel oLabel;

	// count for winning conditions
	int xCount;
	int oCount;
	int buttonsOpened;

	// Color of the current player
	Color playersColor;

	// view for the UI
	View view;

	// constant for no of buttons
	final int MAX_BUTTONS = 64;

	// 2d-array of all the buttons
	MyButton[] buttons = new MyButton[MAX_BUTTONS];

	int whichPlayer;

	public boolean thisPlayerCanPlay;

	public RemoteInterface remoteObject;
	
	/**
	 * default constructor. Initializes and starts the game.
	 */
	Model(int player) throws RemoteException {
		buttonsOpened = 4;
		xCount = 2;
		oCount = 2;
		whichPlayer = player;
		if(whichPlayer == 1) {
			thisPlayerCanPlay = true;
		} else {
			thisPlayerCanPlay = false;
		}
		whoseTurn = "X";
		playersColor = Color.RED;
		view = new View(this);
		view.initBoard();
	}
	
	/**
	 * @param remoteObject the remoteObject to set
	 */
	public void setRemoteObject(RemoteInterface remoteObject) {
		this.remoteObject = remoteObject;
	}

	public void updateButton(int buttonId) {
		Model.MyButton b = buttons[buttonId];

		// check if the move is valid
		if (b.isValidMove(getWhoseTurn())) {
			// change the current button
			b.setText(getWhoseTurn());
			b.setBackground(getPlayersColor());
			b.setEnabled(false);

			// reset the labels
			countAndSet();
			
			// change the current player
			nextTurn();
			
			// increment the number of buttons opened
			buttonsOpened++;
			
			// check if all buttons are opened
			if(buttonsOpened == MAX_BUTTONS) {
				showWinnersDialog();
			}

			thisPlayerCanPlay = true;
		}
	}
	/**
	 * Inner class for buttons
	 * 
	 * @author Kedarnath Calangutkar
	 * @author Hitesh Vyas
	 */
	class MyButton extends JButton implements Serializable {

		// Id of the button
		int id;

		/**
		 * Constructor which initializes the id
		 * 
		 * @param id
		 */
		MyButton(int id) {
			super();

			// set size of the button
			JButton dummyButton = new JButton("00");
			Dimension size = dummyButton.getPreferredSize();
			size = dummyButton.getPreferredSize();

			this.setPreferredSize(size);

			// open 4 buttons in the center
			if (id == 28 || id == 35) {
				this.setText("O");
				this.setBackground(Color.BLUE);
				this.setEnabled(false);
			} else if (id == 27 || id == 36) {
				this.setText("X");
				this.setBackground(Color.RED);
				this.setEnabled(false);
			}

			this.id = id;
		}

		/**
		 * getter method for the id
		 * 
		 * @return id of the button
		 */
		int getId() {
			return id;
		}

		/**
		 * get the button to the west of the current button
		 * 
		 * @return button to the west
		 */
		MyButton getWest() {
			if (id % 8 != 0) {
				return buttons[id - 1];
			} else {
				return null;
			}
		}

		/**
		 * get the button to the east of the current button
		 * 
		 * @return button to the east
		 */
		MyButton getEast() {
			if (id % 8 != 7) {
				return buttons[id + 1];
			} else {
				return null;
			}
		}

		/**
		 * get the button to the north of the current button
		 * 
		 * @return button to the north
		 */
		MyButton getNorth() {
			if (id > 7) {
				return buttons[id - 8];
			} else {
				return null;
			}
		}

		/**
		 * get the button to the south of the current button
		 * 
		 * @return button to the south
		 */
		MyButton getSouth() {
			if (id < 56) {
				return buttons[id + 8];
			} else {
				return null;
			}
		}

		/**
		 * get the button to the north west of the current button
		 * 
		 * @return button to the north west
		 */
		MyButton getNorthWest() {
			MyButton north = getNorth();
			return north == null ? null : north.getWest();
		}

		/**
		 * get the button to the north east of the current button
		 * 
		 * @return button to the north east
		 */
		MyButton getNorthEast() {
			MyButton north = getNorth();
			return north == null ? null : north.getEast();
		}

		/**
		 * get the button to the south west of the current button
		 * 
		 * @return button to the south west
		 */
		MyButton getSouthWest() {
			MyButton south = getSouth();
			return south == null ? null : south.getWest();
		}

		/**
		 * get the button to the south east of the current button
		 * 
		 * @return button to the south east
		 */
		MyButton getSouthEast() {
			MyButton south = getSouth();
			return south == null ? null : south.getEast();
		}

		/**
		 * checks if the button pressed is vlid for the current player
		 * 
		 * @param player
		 * @return true if the move is valid, false otherwise
		 */
		public boolean isValidMove(String string) {

			// get buttons in all directions
			MyButton west = getWest();
			MyButton east = getEast();
			MyButton north = getNorth();
			MyButton south = getSouth();
			MyButton northWest = getNorthWest();
			MyButton northEast = getNorthEast();
			MyButton southWest = getSouthWest();
			MyButton southEast = getSouthEast();

			// get the opponent
			String opponent = getOpponent();

			boolean valid = false;

			// check for valid move to the west
			if (west != null && west.getText().equals(opponent)) {
				while (west != null) {
					west = west.getWest();
					if (west != null && west.getText().equals(whoseTurn)) {
						west = west.getEast();
						while (west != null && west.getText().equals(opponent)) {
							west.setText(whoseTurn);
							west.setBackground(playersColor);
							west = west.getEast();
						}
						valid = true;
						break;
					} else if (west != null && west.getText().equals(opponent)) {
						continue;
					} else {
						break;
					}
				}
			}

			// check for valid move to the east
			if (east != null && east.getText().equals(opponent)) {
				while (east != null) {
					east = east.getEast();
					if (east != null && east.getText().equals(whoseTurn)) {
						east = east.getWest();
						while (east != null && east.getText().equals(opponent)) {
							east.setText(whoseTurn);
							east.setBackground(playersColor);
							east = east.getWest();
						}
						valid = true;
						break;
					} else if (east != null && east.getText().equals(opponent)) {
						continue;
					} else {
						break;
					}
				}
			}

			// check for valid move to the north
			if (north != null && north.getText().equals(opponent)) {
				while (north != null) {
					north = north.getNorth();
					if (north != null && north.getText().equals(whoseTurn)) {
						north = north.getSouth();
						while (north != null
								&& north.getText().equals(opponent)) {
							north.setText(whoseTurn);
							north.setBackground(playersColor);
							north = north.getSouth();
						}
						valid = true;
						break;
					} else if (north != null
							&& north.getText().equals(opponent)) {
						continue;
					} else {
						break;
					}
				}
			}

			// check for valid move to the north
			if (south != null && south.getText().equals(opponent)) {
				while (south != null) {
					south = south.getSouth();
					if (south != null && south.getText().equals(whoseTurn)) {
						south = south.getNorth();
						while (south != null
								&& south.getText().equals(opponent)) {
							south.setText(whoseTurn);
							south.setBackground(playersColor);
							south = south.getNorth();
						}
						valid = true;
						break;
					} else if (south != null
							&& south.getText().equals(opponent)) {
						continue;
					} else {
						break;
					}
				}
			}

			// check for valid move to the north east
			if (northEast != null && northEast.getText().equals(opponent)) {
				while (northEast != null) {
					northEast = northEast.getNorthEast();
					if (northEast != null
							&& northEast.getText().equals(whoseTurn)) {
						northEast = northEast.getSouthWest();
						while (northEast != null
								&& northEast.getText().equals(opponent)) {
							northEast.setText(whoseTurn);
							northEast.setBackground(playersColor);
							northEast = northEast.getSouthWest();
						}
						valid = true;
						break;
					} else if (northEast != null
							&& northEast.getText().equals(opponent)) {
						continue;
					} else {
						break;
					}
				}
			}

			// check for valid move to the north west
			if (northWest != null && northWest.getText().equals(opponent)) {
				while (northWest != null) {
					northWest = northWest.getNorthWest();
					if (northWest != null
							&& northWest.getText().equals(whoseTurn)) {
						northWest = northWest.getSouthEast();
						while (northWest != null
								&& northWest.getText().equals(opponent)) {
							northWest.setText(whoseTurn);
							northWest.setBackground(playersColor);
							northWest = northWest.getSouthEast();
						}
						valid = true;
						break;
					} else if (northWest != null
							&& northWest.getText().equals(opponent)) {
						continue;
					} else {
						break;
					}
				}
			}

			// check for valid move to the south east
			if (southEast != null && southEast.getText().equals(opponent)) {
				while (southEast != null) {
					southEast = southEast.getSouthEast();
					if (southEast != null
							&& southEast.getText().equals(whoseTurn)) {
						southEast = southEast.getNorthWest();
						while (southEast != null
								&& southEast.getText().equals(opponent)) {
							southEast.setText(whoseTurn);
							southEast.setBackground(playersColor);
							southEast = southEast.getNorthWest();
						}
						valid = true;
						break;
					} else if (southEast != null
							&& southEast.getText().equals(opponent)) {
						continue;
					} else {
						break;
					}
				}
			}

			// check for valid move to the south west
			if (southWest != null && southWest.getText().equals(opponent)) {
				while (southWest != null) {
					southWest = southWest.getSouthWest();
					if (southWest != null
							&& southWest.getText().equals(whoseTurn)) {
						southWest = southWest.getNorthEast();
						while (southWest != null
								&& southWest.getText().equals(opponent)) {
							southWest.setText(whoseTurn);
							southWest.setBackground(playersColor);
							southWest = southWest.getNorthEast();
						}
						valid = true;
						break;
					} else if (southWest != null
							&& southWest.getText().equals(opponent)) {
						continue;
					} else {
						break;
					}
				}
			}
			return valid;
		}
	}

	/**
	 * count the number of buttons for a player
	 * 
	 * @param player
	 *            whose number of buttons are to be calculated
	 * @return number of buttons
	 */
	int count(String player) {
		int count = 0;

		for (int i = 0; i < MAX_BUTTONS; i++) {
			if (player.equals(buttons[i].getText())) {
				count++;
			}
		}

		return count;
	}

	/**
	 * next player plays
	 */
	void nextTurn() {
		whoseTurn = whoseTurn.equals("X") ? "O" : "X";
		if (whoseTurn.equals("X")) {
			playersColor = Color.RED;
			player.setText("Red plays       ");
		} else {
			playersColor = Color.BLUE;
			player.setText("Blue plays      ");
		}
	}

	/**
	 * get the opponent
	 * 
	 * @return opponent
	 */
	String getOpponent() {
		return getWhoseTurn().equals("X") ? "O" : "X";
	}

	/**
	 * get the current player
	 * 
	 * @return current player
	 */
	public String getWhoseTurn() {
		return whoseTurn;
	}

	/**
	 * getter for the color of the players
	 * 
	 * @return players color
	 */
	public Color getPlayersColor() {
		return playersColor;
	}

	/**
	 * sets the labels at the bottom
	 */
	public void countAndSet() {
		xCount = count("X");
		xLabel.setText("Red: " + xCount);
		oCount = count("O");
		oLabel.setText("Blue: " + oCount);
	}

	/**
	 * check which player has won
	 * 
	 * @return a string that indicates who won
	 */
	public String getWinnersLabel() {
		String labelString = null;
		if (xCount > oCount) {
			labelString = "You Lose!";
		} else if (xCount < oCount) {
			labelString = "You Win!";
		} else {
			labelString = "Draw!";
		}
		return labelString;
	}

	/**
	 * display the final dialog
	 */
	public void showWinnersDialog() {
		view.showFinalDialog();
	}
}
