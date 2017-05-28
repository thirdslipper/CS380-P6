/**
 * Author: Colin Koo
 * Professor: Nima Davarpanah
 * Program: This program emulates playing tic-tac-toe with a server.
 */
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class TicTacToeClient {
	static Scanner kb = new Scanner(System.in);
	/**
	 * Connects to the server and starts the main loop to play the game.
	 * @param args
	 * @throws UnknownHostException
	 */
	public static void main(String[] args) throws UnknownHostException {
		try (Socket socket = new Socket("codebank.xyz", 38006)){
			play(new ObjectInputStream(socket.getInputStream()), new ObjectOutputStream(socket.getOutputStream()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * This method is the main method to control the flow of the game, ending the game on certain conditions
	 * and allowing the user to provide input.
	 * @param ois
	 * @param oos
	 */
	public static void play(ObjectInputStream ois, ObjectOutputStream oos){
		Message message = null;
		BoardMessage casted = null;
		boolean gameOver = false;
		String move = "";
		
		try {	//New game
			oos.writeObject(new ConnectMessage(getName()));
			oos.writeObject(new CommandMessage(CommandMessage.Command.NEW_GAME));
			System.out.println("You are O, Enemy is X, Blank spaces are E");
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		while (!gameOver){
			try { 
				message = (Message) ois.readObject();	//Only should receive BoardMessage or ErrorMessage
			} 
			catch (ClassNotFoundException | IOException e1) {
				e1.printStackTrace();
			}
			if (message instanceof BoardMessage){	// Should print board and status of the game.
				casted = (BoardMessage) message;
				System.out.println("Game Status: " + casted.getStatus());
				displayBoard(casted);
			}
			else{	
				System.out.println(((ErrorMessage)message).toString());
				gameOver = true;
			}
				//Establish game ending conditions
			if (!casted.getStatus().toString().equals("PLAYER1_VICTORY") && !casted.getStatus().toString().equals("PLAYER2_VICTORY")
					&& !casted.getStatus().toString().equals("STALEMATE")){
				gameOver = false;
			}
			else{
				gameOver = true;
			}
			if (!gameOver){
				move = playerTurn();
				try {
					oos.writeObject(new MoveMessage((byte) Character.getNumericValue(move.charAt(0)), (byte) Character.getNumericValue(move.charAt(1))));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		kb.close();
		System.out.println("Good Game.");
	}
	/**
	 * This method takes a valid player input and passes it within the play method to allow
	 * the player to select a spot to mark.
	 * @return
	 */
	public static String playerTurn(){
		StringBuilder sb = new StringBuilder();
		String move = "";
		boolean fail = false;
		
		while (!(move.length() == 3 && move.contains(",") && (Character.isDigit(move.charAt(0))) && Character.isDigit(move.charAt(2)))){
			System.out.println("Enter your next move, (row: 0-2, col: 0-2)"
					+ "\nex: \"0,0\" for top left, \"2,2\" for bottom right");
			move = kb.nextLine();
			if (!(move.length() == 3 && move.contains(",") && (Character.isDigit(move.charAt(0))) && Character.isDigit(move.charAt(2)))){
				System.out.println("Re-enter move");
			}
		}
		sb.append(move).deleteCharAt(sb.indexOf(",")).trimToSize();
		return sb.toString();
	}
	/**
	 * Displays the BoardMessage byte[][] into a 3x3 board representation including symbols representing 
	 * the player(O), player2(X), and empty spaces(E).
	 * @param boardMsg
	 */
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
			System.out.println("");
		}
		System.out.println("");
	}
	/**
	 * Gets a name from the user.
	 * @return
	 */
	public static String getName(){
		String name;
		System.out.println("Enter your username: ");
		name = kb.nextLine();
		return name;
	}
}
