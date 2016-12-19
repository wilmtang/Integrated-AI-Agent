import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

public class GamePlayer {
	static State result = new State(1);
	static int depthOrigin = 0;
	public static void main (String[] args) throws IOException {
		/*
		 * read input file. create initial state. Find method and depth.
		 */
		
		
		Scanner myscanner = new Scanner(new File("input.txt"));
		int size = myscanner.nextInt();
		String method = myscanner.next();
		char player = myscanner.next().charAt(0);
		depthOrigin = myscanner.nextInt();
		
		State init = new State(size);
		//update value
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				init.setValue(i, j, myscanner.nextInt());
			}
		}
		//update board
		for (int i = 0; i < size; i++) {
			String l = myscanner.next();
			for (int j = 0; j < size; j++) {
				init.setBoard(i, j, l.charAt(j));
			}
		}
		myscanner.close();
		
		/*
		 * call method based on AB or MINIMAX
		 */
		if (method.equals("MINIMAX")) {
			minimax (init, depthOrigin, player, true);
		}
		
		if (method.equals("ALPHABETA")) {
			alphabeta(init, depthOrigin, Integer.MIN_VALUE, Integer.MAX_VALUE, player, true);
		}
		/*
		 * write it to output
		 */
		File output = new File("output.txt");
		output.createNewFile();
		PrintWriter writer = new PrintWriter(output);
		writer.println(result.move);
		for (char[] arr : result.board) {
			writer.println(new String(arr));
		}
		writer.close();
		
	}
	
	class Wrapper_StateScore {
		State result;
		int score;
		Wrapper_StateScore (State s, char player) {
			result = s;
			score = s.score(player);
		}
	}
	
	 /*
	 * Minimax 
	 */
	static int minimax (State init, int depth, char player, boolean isMax) {
		if (depth == 0 || init.isTerminal()) {
			return init.score(player);
		}
		
		char counter = player == 'O' ? 'X' : 'O';
		
		
		//as player, we are maximizing our score. next step is generate player moves. Use score (player)
		if (isMax) {
			init.generateStake(player);
			init.generateRaid(player);
			int bestValue = Integer.MIN_VALUE;
			for (State s : init.subStates) {
				/*
				 * s is one substate. if score if updated, s is the result.
				 */
				int r = minimax (s, depth - 1, player, false);
				if (r > bestValue) {
					if (depth == depthOrigin) result = s;
					bestValue = r;
				}
			}
			return bestValue;
		}
		
		// else
		//as counter, we want to minimize player's score. First generate children as counter.
		init.generateStake(counter);
		init.generateRaid(counter);
		int bestValue = Integer.MAX_VALUE;
		for (State s : init.subStates) {
			int r = minimax (s, depth -1, player, true);
			if (r < bestValue) {
				bestValue = r;
			}
		}
		return bestValue;
		
	}
	
	static int alphabeta (State init, int depth, int alpha, int beta, char player, boolean isMax) {
		if (depth == 0 || init.isTerminal()) {
			return init.score(player);
		}
		
		char counter = player == 'O' ? 'X' : 'O';
		
		
		//as player, we are maximizing our score. next step is generate player moves. Use score (player)
		if (isMax) {
			init.generateStake(player);
			init.generateRaid(player);
			int V = Integer.MIN_VALUE;
			for (State s : init.subStates) {
				/*
				 * s is one substate. if score if updated, s is the result.
				 */
				int r = alphabeta (s, depth - 1, alpha, beta, player, false);
				if (r > V) {
					if (depth == depthOrigin) result = s;
					V = r;
				}
				alpha = Math.max(alpha, V);
				if (beta <= alpha) {
					break;
				}
			}
			return V;
		}
		
		// else
		//as counter, we want to minimize player's score. First generate children as counter.
		init.generateStake(counter);
		init.generateRaid(counter);
		int V = Integer.MAX_VALUE;
		for (State s : init.subStates) {
			int r = alphabeta (s, depth -1, alpha, beta, player, true);
			if (r < V) {
				V = r;
			}
			beta = Math.max(beta, V);
			if (beta <= alpha) {
				break;
			}
		}
		return V;
		
	}
	
	
}


