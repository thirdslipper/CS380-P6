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
			/*			oos.writeObject(new ConnectMessage("Colin"));
			oos.writeObject(new CommandMessage(CommandMessage.Command.NEW_GAME));*/

			/*			Thread sl = new Thread(new serverListen(socket));//, ois));
			sl.start();*/

			/*			Object board = ois.readObject();
			if (board instanceof BoardMessage){
				displayBoard ((BoardMessage) board);
			}*/

			/*			BoardMessage board = (BoardMessage) ois.readObject();
			displayBoard(board);
			 */
			play(ois, oos);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void play(ObjectInputStream ois, ObjectOutputStream oos){
		Message message = null;
		BoardMessage casted = null;
		boolean gameOver = false;
		String move = "";

		try {
			oos.writeObject(new ConnectMessage("Colin"));
			oos.writeObject(new CommandMessage(CommandMessage.Command.NEW_GAME));
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		while (!gameOver){//change to win condition

			try { 
				message = (Message) ois.readObject(); 
			} 
			catch (ClassNotFoundException | IOException e1) {
				e1.printStackTrace();
			}
			if (message instanceof BoardMessage){
				System.out.println("board");
				casted = (BoardMessage) message;
				System.out.println(casted.getStatus());
				displayBoard(casted);
			}
			else{
				System.out.println("thinking");
				System.out.println(((ErrorMessage) message).toString());
				gameOver = true;
			}
			if (!casted.getStatus().toString().equals("PLAYER1_VICTORY") && !casted.getStatus().toString().equals("PLAYER2_VICTORY")
					&& !casted.getStatus().toString().equals("STALEMATE")){
				move = playerTurn();
				try {
					oos.writeObject(new MoveMessage((byte) Character.getNumericValue(move.charAt(0)), (byte) Character.getNumericValue(move.charAt(1))));
					/*				message = ois.readObject();
					if (message instanceof BoardMessage){
						displayBoard((BoardMessage) message);
					}*/
				} catch (IOException e) {
					System.out.println("thinking2");
					e.printStackTrace();
				}
			}
		}
	}
	//error check later
	public static String playerTurn(){
		StringBuilder sb = new StringBuilder();
		Scanner kb = new Scanner(System.in);
		String move = "";
		System.out.println("Enter your next move, (row: 0-2, col: 0-2)"
				+ "\nex: \"0,0\" for top left, \"2,2\" for bottom right");
		move = kb.nextLine();
		
		sb.append(move).deleteCharAt(sb.indexOf(",")).trimToSize();
		System.out.println("marking: " + sb.toString());
		return sb.toString();
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
		Socket socket = null;
		ObjectInputStream ois = null;
		BoardMessage temp = null;

		public serverListen(Socket socket, ObjectInputStream ois){
			this.socket = socket;
			//this.ois = ois;
		}
		public void run() {
			try {
				ois.readObject();

				/*				if (ois.readObject() instanceof BoardMessage){
					System.out.println("test");
				}*/
				/*				temp = (BoardMessage) ois.readObject();
				if (temp instanceof BoardMessage){
					System.out.println("test");
				}*/
			} catch (IOException | ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
	}
}
