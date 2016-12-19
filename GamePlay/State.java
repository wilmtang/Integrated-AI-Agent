import java.util.ArrayList;
import java.util.List;

public class State {
	int N;
	int[][] values;
	char[][] board;
	//score can be calculated instantly
	/*
	 * move string to be written.
	 */
	String move;
	String generteMove (int x, int y, String type) {
		StringBuilder sb = new StringBuilder();
		sb.append((char) (y + 'A'));
		x++;
		sb.append(x);
		sb.append(' ');
		sb.append(type);
		return sb.toString();
	}
	
	
	
	List<State> subStates;
	
	/**
	 * used for initializing a new state from file. After this function, call setValue() and setBoard() to update data.
	 * @param size
	 */
	State (int size) {
		this.N  = size;
		values = new int[size][size];
		board = new char[size][size];
		
		subStates = new ArrayList<State>();
	}
	/**
	 * used for generate sub states from self.
	 * @param self
	 * @param x
	 * @param y
	 * @param player
	 */
	State (State self, int x, int y, char player, String type) {
		this.N = self.N;
		this.values = self.values;
		this.board = new char[this.N][this.N];
		subStates = new ArrayList<State>();
		
		for (int i = 0; i < this.N; i++) {
			for (int j = 0; j < this.N; j++) {
				this.board[i][j] = self.board[i][j];
			}
		}
		
		setBoard(x, y, player);
		this.move = this.generteMove(x, y, type);
		
	}
	
	
	/*
	 * set board char, O or X
	 */
	void setBoard(int x, int y, char player) {
		board[x][y] = player;
	}
	/*
	 * set value, integer
	 */
	void setValue(int x, int y, int value) {
		values[x][y] = value;
	}
	/*
	 * generate and store all stake sub states
	 */
	void generateStake(char player) {
		for (int i = 0; i < this.N; i++) {
			for (int j = 0; j < this.N; j++) {
				if (board[i][j] == '.') {
					this.subStates.add(new State (this, i, j, player, "Stake"));
				}
			}
		}
	}
	/*
	 * generate and store all raid sub states
	 */
	void generateRaid(char player) {
		
		boolean[][] visited = new boolean[this.N][this.N];
		int[][] directions = {
				{-1, 0}, {1, 0}, {0, -1}, {0, 1}
		};
		
		for (int i = 0; i < this.N; i++) {
			for (int j = 0; j < this.N; j++) {
				if (this.board[i][j] == player) {
					for (int[] direction : directions) {
						int nX = i + direction[0];
						int nY = j + direction[1];
						/*
						 * if new position is valid on the board
						 */
						if (nX >= 0 && nX < this.N && nY >= 0 && nY < this.N) {
							/*
							 * if new position is unoccupied and unvisited
							 */
							if (!visited[nX][nY] && this.board[nX][nY] == '.') {
								
								/*
								 * create a raid state without conquering. Mark as visited
								 */
								State raid1 = new State (this, nX, nY, player, "Raid");
								visited[nX][nY] = true;
								
								/*
								 * update this raid1 with conquering
								 */
								for (int[] d2 : directions) {
									int nnX = nX + d2[0];
									int nnY = nY + d2[1];
									/*
									 * check the new raid position's neighbors, conquer if applicable.
									 */
									if (nnX >= 0 && nnX < this.N && nnY >= 0 && nnY < this.N) {
										char counter = player == 'X' ? 'O' : 'X';
										if (raid1.board[nnX][nnY] == counter) {
												raid1.setBoard(nnX, nnY, player);
										}
									}
								}
								
								/*
								 * now add it to the list.
								 */
								this.subStates.add(raid1);
							}
						}
					}
				}
			}
		}
	}
	/**
	 * Find out if this is a terminal state.
	 * @return
	 */
	boolean isTerminal() {
		for (char[] l : this.board) {
			for (char ch : l) {
				if (ch == '.') {
					return false;
				}
			}
		}
		
		return true;
	}
	/**
	 * return the heuristic value of this node
	 * @return
	 */
	int score(char player) {
		int Oscore = 0;
		int Xscore = 0;
		
		for (int i = 0; i < this.N; i++) {
			for (int j = 0; j < this.N; j++) {
				if (board[i][j] == 'X') {
					Xscore += values[i][j];
				} else if (board[i][j] == 'O') {
					Oscore += values[i][j];
				}
			}
		}
		
		if (player == 'O') {
			return Oscore - Xscore;
		}
		
		return Xscore - Oscore;
	}
}
