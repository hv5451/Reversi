import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;


public class Reversi {
	
	static boolean clientConnected = false;
	
	Reversi(int serverPort) {
		try {
			Model model = new Model(1);
			Registry reg = LocateRegistry.createRegistry(serverPort);
			System.out.println("Connecting..");
			reg.rebind("ReversiPlayer1", model);
			connect(reg, model);
			System.out.println("Connected!");
		} catch (AccessException e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
	
	private void connect(Registry reg, Model model) {
		try {
			model.setRemoteObject((RemoteInterface) reg.lookup("ReversiPlayer2"));
		} catch (AccessException e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (NotBoundException e) {
			System.out.println("Reconnecting in 3 secs...");
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			connect(reg, model);
		}		
	}

	Reversi(String serverAddress, int serverPort) {
		try {
			Model model = new Model(2);
			Registry reg = LocateRegistry.getRegistry(serverAddress, serverPort);
			reg.rebind("ReversiPlayer2", model);
			clientConnected = true;
			System.out.println("Connecting..");
			model.setRemoteObject((RemoteInterface) reg.lookup("ReversiPlayer1"));
			System.out.println("Connected!");
		} catch (AccessException e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (NotBoundException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		if(args.length == 1) {
			// Start a server
			System.out.println("Server - Red - X");
			new Reversi(Integer.parseInt(args[0]));
		} else if(args.length == 2) {
			// Start a client
			System.out.println("Client - Blue - O");
			new Reversi(args[0], Integer.parseInt(args[1]));
		} else {
			System.out.println("Invalid Format! Please run as follows:");
			System.out.println("For Server:");
			System.out.println("java Reversi serverPort");
			System.out.println("For Client:");
			System.out.println("java Reversi serverAddress serverPort");
		}
	}
}
