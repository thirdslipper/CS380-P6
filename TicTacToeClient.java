import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class TicTacToeClient {

	public static void main(String[] args) throws UnknownHostException {
		try (Socket socket = new Socket("codebank.xyz", 38006)){

			ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
			ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
			
//			ConnectMessage connect = new ConnectMessage(getName());
			oos.writeObject(new ConnectMessage("Colin"));
			oos.writeObject(new CommandMessage(CommandMessage.Command.NEW_GAME));
			
			Thread sl = new Thread(new serverListen(socket, ois));
			sl.start();
			
/*			BoardMessage board = (BoardMessage) ois.readObject();
			displayBoard(board);*/
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void play(ObjectInputStream ois, ObjectOutputStream oos){
		BoardMessage board = null;
		int turns = 0;
		while (true){//change to win condition
		}
	}
	
	public static void displayBoard(BoardMessage boardMsg){
		byte[][] board = boardMsg.getBoard();
		
		for (int i = 0; i < board.length; ++i){
			for (int j = 0; j < board.length; ++j){
				switch (board[i][j]){
					case 0:
						System.out.print(" E ");
						break;
					case 1:
						System.out.print(" O ");
						break;
					case 2:
						System.out.print(" X ");
						break;
					default:
						System.out.println(" N ");
				}
				if (j < board.length-1){
					System.out.print("|");
				}
			}
			System.out.println();
		}
	}
	
	public static String getName(){
		String name;
		Scanner kb = new Scanner(System.in);
		System.out.println("Enter your username: ");
		name = kb.nextLine();
		kb.close();
		return name;
	}
	
	private static class serverListen implements Runnable {
		Socket socket;
		ObjectInputStream ois;
/*		ErrorMessage errorObj;
		BoardMessage boardObj;*/
		
		public serverListen(Socket socket, ObjectInputStream ois){
			socket = this.socket;
			ois = this.ois;
		}
		
		public void run() {
			try {
				while (true){
					Message temp = (Message) ois.readObject();		//error message/board msg
					if (temp instanceof BoardMessage || temp instanceof ErrorMessage){	
						System.out.println("test");
					}
				}
			} catch (IOException | ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
	}
}
