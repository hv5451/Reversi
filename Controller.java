/**
 * Controller.java
 * 
 * Version: 1.3
 * 
 * Revisions:
 * 		Initial version
 */
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;

/**
 * controller for the game of reversi
 * 
 * @author Hitesh Vyas
 */
class Controller implements ActionListener {
	
	Model r;
	
	/**
	 * constructor that initializes r
	 * 
	 * @param r reversi object
	 */
	public Controller(Model r) {
		this.r = r;
	}
	
	/**
	 * method that is called whenever a button is clicked
	 * 
	 * @param e event which is generated when the button is called
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		if(r.thisPlayerCanPlay) {
			Model.MyButton b = (Model.MyButton) e.getSource();
			
			// check if the move is valid
			if (b.isValidMove(r.getWhoseTurn())) {
				r.thisPlayerCanPlay = false;
	
				// change the current button
				b.setText(r.getWhoseTurn());
				b.setBackground(r.getPlayersColor());
				b.setEnabled(false);
	
				// reset the labels
				r.countAndSet();
				
				// change the current player
				r.nextTurn();
				
				// increment the number of buttons opened
				r.buttonsOpened++;
				
				// check if all buttons are opened
				if(r.buttonsOpened == r.MAX_BUTTONS) {
					r.showWinnersDialog();
				}

				try {
					r.remoteObject.updateButton(b.id);
				} catch (RemoteException e1) {
					e1.printStackTrace();
				}
			}
		}
	}
}