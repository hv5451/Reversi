import java.rmi.Remote;
import java.rmi.RemoteException;


public interface RemoteInterface extends Remote {
	public void updateButton(int buttonId) throws RemoteException;
}
