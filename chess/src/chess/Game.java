package chess;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Stack;

import chess.Piece.Color;
import chess.Piece.Type;

/**
 * This class handles the underlying logic of a chess game.
 * 
 * @author Sindre Stephansen
 * @see Pos
 * @see Piece
 * @see PiecePos
 * @see Move
 */
public class Game implements Cloneable {
	/*
	 * Inner classes
	 */
	
	private class ValidMoves {
		ArrayList<Move> moves;
		
		ValidMoves() {
			moves = new ArrayList<Move>();
		}
		
		void add(Move move) {
			moves.add(move);
		}
		
		void add(int[][][] p) {
			try {
				Pos[][] tmp = new Pos[p.length][p[0].length];
				Piece piece = null;
				for (int i = 0; i < p.length; i++) {
					for (int j = 0; j < p[i].length; j++) {
						Pos pos = new Pos(p[i][j][0], p[i][j][1]);
						Piece other = getPiece(pos);
						
						if (i == 0 && j == 0) {
							if (other == null) {
								// There must be a piece at the starting position
								return;
							} else {
								piece = other;
							}
						}
						
						if (j == 1 && other != null && piece.color == other.color) {
							// A piece cannot stand on another piece of the same color
							return;
						}
						
						tmp[i][j] = pos;
					}
				}
				
				Move move = new Move(tmp, turn);
				Game tmpgame = (Game) Game.this.clone();
				tmpgame.move(move);
				if (!tmpgame.isInCheck(piece.color)) {
					add(move);
				}
			} catch (IllegalArgumentException e) {
			} catch (CloneNotSupportedException e) {}
		}
		
		Move[] toArray() {
			return moves.toArray(new Move[moves.size()]);
		}
	}
	
	/*
	 * Class attributes
	 */
	
	static final Piece[] WHITE = {
		new Piece(Color.WHITE, Type.PAWN),
		new Piece(Color.WHITE, Type.ROOK),
		new Piece(Color.WHITE, Type.KNIGHT),
		new Piece(Color.WHITE, Type.BISHOP),
		new Piece(Color.WHITE, Type.QUEEN),
		new Piece(Color.WHITE, Type.KING)
	};

	static final Piece[] BLACK = {
		new Piece(Color.BLACK, Type.PAWN),
		new Piece(Color.BLACK, Type.ROOK),
		new Piece(Color.BLACK, Type.KNIGHT),
		new Piece(Color.BLACK, Type.BISHOP),
		new Piece(Color.BLACK, Type.QUEEN),
		new Piece(Color.BLACK, Type.KING)
	};

	/*
	 * Attributes
	 */

	// A representation of the chess board. It's going left to right,
	// bottom to top, so board[0][0] is the bottom left corner of the board,
	// and board[7][7] is the top right corner.
	Piece[][] board;
	
	int turn = 0;
	
	Stack<PiecePos> captureStack = new Stack<PiecePos>();
	Stack<Move> undoStack = new Stack<Move>();
	Stack<Move> redoStack = new Stack<Move>();
	
	/*
	 * Constructors
	 */
	
	/**
	 * Class constructor. Puts all pieces on their starting positions,
	 * so the game is ready to play straight away.
	 */
	public Game() {
		// Set up the board
		board = new Piece[8][8];
		
		// Set the empty positions to null
		for (int x = 0; x < 8; x++) {
			for (int y = 2; y < 6; y++) {
				board[x][y] = null;
			}
		}
		
		// Place the pawns
		for (int x = 0; x < 8; x++) {
			board[x][1] = WHITE[0];
			board[x][6] = BLACK[0];
		}
		
		// Rooks
		board[0][0] = WHITE[1];
		board[7][0] = WHITE[1];
		board[0][7] = BLACK[1];
		board[7][7] = BLACK[1];
		
		// Knights
		board[1][0] = WHITE[2];
		board[6][0] = WHITE[2];
		board[1][7] = BLACK[2];
		board[6][7] = BLACK[2];
		
		// Bishops
		board[2][0] = WHITE[3];
		board[5][0] = WHITE[3];
		board[2][7] = BLACK[3];
		board[5][7] = BLACK[3];
		
		// Queens
		board[3][0] = WHITE[4];
		board[3][7] = BLACK[4];
		
		// Kings
		board[4][0] = WHITE[5];
		board[4][7] = BLACK[5];
	}
	
	public static Game load(String filename) throws IOException {
		ChessStorageInterface handler = new ChessStorageBinary();
		return handler.load(filename);
	}
	
	public void save(String filename) throws IOException {
		ChessStorageInterface handler = new ChessStorageBinary();
		handler.save(this, filename);
	}
	
	/*
	 * Inherited methods
	 */
	
	protected Object clone() throws CloneNotSupportedException {
		Game clone = new Game();
		
		clone.turn = turn;
		for (int x = 0; x < 8; x++) {
			for (int y = 0; y < 8; y++) {
				clone.board[x][y] = board[x][y];
			}
		}
		
		return clone;
	}
	
	/**
	 * Returns a text representation of the chess board.
	 * The string begins with the position in the top left corner
	 * and continues left to right, top to bottom, with spaces for
	 * empty positions and newline between rows. See {@link Piece#toString()}
	 * for information about the characters used to represent the pieces.
	 * 
	 * @return  A string representation of the chess board.
	 * @see		Piece
	 * @see		Piece#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		
		for (int y = 7; y >= 0; y--) {
			for (int x = 0; x < 8; x++) {
				Piece piece = board[x][y];
				if (piece == null) {
					builder.append(' ');
				} else {
					if (piece.color == Color.WHITE) {
						builder.append(board[x][y].toString());
					} else {
						builder.append(board[x][y].toString().toLowerCase());
					}
				}
			}

			if (y != 0) {
				builder.append('\n');
			}
		}
		
		return builder.toString();
	}
	
	/*
	 * Utility methods
	 */
	
	private void setPiece(Pos pos, Piece piece) {
		board[pos.x][pos.y] = piece;
	}
	
	private void setPiece(PiecePos piecePos) {
		if (piecePos != null) {
			setPiece(piecePos.pos, piecePos.piece);
		}
	}
	
	/*
	 * Public methods
	 */
	
	/**
	 * Returns the piece at the given coordinates.
	 * 
	 * @param pos	The coordinate of the piece
	 * @return  	The piece at the given coordinates
	 * @see 		Piece
	 * @see			Pos
	 * @see			#getPiece(int, int)
	 */
	public Piece getPiece(Pos pos) {
		return board[pos.x][pos.y];
	}
	
	/**
	 * Returns the piece at the given coordinates. Both coordinates
	 * must between 0 and 7, if not an IllegalArgumentException is thrown.
	 * 
	 * @param x The x-coordinate of the piece
	 * @param y The y-coordinate of the piece
	 * @return	The piece at the given coordinates
	 * @throws	IllegalArgumentException	If the coordinates are out of bounds.
	 * @see 	Piece
	 * @see 	#getPiece(Pos)
	 */
	public Piece getPiece(int x, int y) {
		return getPiece(new Pos(x, y));
	}
	
	/**
	 * Returns the current turn. The current turn is
	 * the same as the number of moves that have been
	 * made up to this point in the game.
	 * 
	 * @return	The current turn
	 */
	public int getTurn() {
		return turn;
	}
	
	/**
	 * Returns an array of all the pieces of a specific color.
	 * The search results are given as PiecePos elements, which
	 * gives both the piece and its position.
	 * 
	 * @param color	The color to search for
	 * @return		An array of PiecePos elements matching the given color
	 * @see			Piece
	 * @see			PiecePos
	 * @see			#byType
	 * @see			#byTypeAndColor
	 */
	public PiecePos[] byColor(Color color) {
		ArrayList<PiecePos> list = new ArrayList<PiecePos>();
		for (int y = 0; y < 8; y++) {
			for (int x = 0; x < 8; x++) {
				Piece piece = board[x][y];
				if (piece != null && piece.color == color) {
					list.add(new PiecePos(piece, new Pos(x, y)));
				}
			}
		}
		
		return list.toArray(new PiecePos[list.size()]);
	}
	
	/**
	 * Returns an array of all the pieces of a specific type.
	 * The search results are given as PiecePos elements, which
	 * gives both the piece and its position.
	 * 
	 * @param type	The type of piece to search for
	 * @return		An array of PiecePos elements matching the given type
	 * @see			Piece
	 * @see			PiecePos
	 * @see			#byColor
	 * @see			#byTypeAndColor
	 */
	public PiecePos[] byType(Type type) {
		ArrayList<PiecePos> list = new ArrayList<PiecePos>();
		for (int y = 0; y < 8; y++) {
			for (int x = 0; x < 8; x++) {
				Piece piece = board[x][y];
				if (piece != null && piece.type == type) {
					list.add(new PiecePos(piece, new Pos(x, y)));
				}
			}
		}
		
		return list.toArray(new PiecePos[list.size()]);
	}

	/**
	 * Returns an array of all the pieces of a specific type and color.
	 * The search results are given as PiecePos elements, which
	 * gives both the piece and its position.
	 * 
	 * @param type	The type of piece to search for
	 * @param color	The color to search for
	 * @return		An array of PiecePos elements matching the given type and color
	 * @see			Piece
	 * @see			PiecePos
	 * @see			#byColor
	 * @see			#byType
	 */
	public PiecePos[] byTypeAndColor(Type type, Color color) {
		ArrayList<PiecePos> list = new ArrayList<PiecePos>();
		for (int y = 0; y < 8; y++) {
			for (int x = 0; x < 8; x++) {
				Piece piece = getPiece(x, y);
				if (piece != null && piece.type == type && piece.color == color) {
					list.add(new PiecePos(piece, new Pos(x, y)));
				}
			}
		}
		
		return list.toArray(new PiecePos[list.size()]);
	}
	
	/**
	 * Moves a piece to a new position, possibly capturing enemy
	 * pieces in the process. The argument will be provided by
	 * the validMoves method.
	 * 
	 * An IllegalArgumentException is thrown if the move isn't valid for
	 * this turn, for example if you pass the same move multiple times.
	 * 
	 * @param move	The move to execute, provided by {@link #validMoves}
	 * @throws		IllegalArgumentException	If the move is invalid. See {@link Move} for details.
	 * @see			#validMoves
	 * @see			Move
	 * @see			Piece
	 * @see			Pos
	 */
	public void move(Move move) {
		if (move.turn != turn) {
			throw new IllegalArgumentException("The move isn't valid this turn");
		}
		
		for (Pos[] tmp : move) {
			Piece piece = getPiece(tmp[0]);
			Piece other = getPiece(tmp[1]);

			if (other != null) {
				captureStack.push(new PiecePos(other, tmp[1]));
			} else {
				captureStack.push(null);
			}

			setPiece(tmp[1], piece);
			setPiece(tmp[0], null);
		}
		
		// Pawn promotion
		Pos target = move.target();
		Piece piece = getPiece(target);
		if (piece != null && piece.type == Type.PAWN) {
			if (piece.color == Color.WHITE && target.y == 7) {
				setPiece(target, WHITE[4]);
			} else if (piece.color == Color.BLACK && target.y == 0) {
				setPiece(target, BLACK[4]);
			}
		}
		
		this.turn++;
		this.undoStack.push(move);
	}
	
	public void undo() {
		if (undoStack.size() > 0) {
			Move m = undoStack.pop();
			Stack<Pos[]> moveStack = new Stack<Pos[]>();

			for (Pos[] tmp : m) {
				moveStack.push(tmp);
			}
			
			for (int i = 0; i < moveStack.size(); i++) {
				Pos[] tmp = moveStack.pop();
				Piece piece = getPiece(tmp[1]);
				setPiece(tmp[0], piece);
				setPiece(tmp[1], null);
			}
			
			setPiece(captureStack.pop());
			redoStack.push(m);
			this.turn--;
		}
	}
	
	public void redo() {
		if (redoStack.size() > 0) {
			move(redoStack.pop());
		}
	}
	
	/**
	 * Calculates all possible legal moves for the piece at the given
	 * position. The resulting elements can be passed to {@link #move}
	 * to execute the move.
	 * 
	 * Throws an IllegalArgumentException if there was no piece at the given position,
	 * or if it's a white piece on the given position when it's blacks turn to move,
	 * or vice versa.
	 * 
	 * @param origin	The position of the piece to calculate moves for
	 * @throws			IllegalArgumentException	If there was no piece at the given position,
	 * 												or if it was the other players turn.
	 * @return			All legal moves for the piece at origin
	 * @see				#move
	 * @see				Move
	 * @see				Pos
	 */
	public Move[] validMoves(Pos origin) {
		Piece piece = getPiece(origin);
		if (piece == null) {
			throw new IllegalArgumentException("There is no piece at that position");
		} else if (piece.color == Color.WHITE && turn % 2 != 0) {
			throw new IllegalArgumentException("It's blacks turn to move");
		} else if (piece.color == Color.BLACK && turn % 2 == 0) {
			throw new IllegalArgumentException("It's whites turn to move");
		}
		
		ValidMoves moves = new ValidMoves();
		
		switch (piece.type){
		case PAWN:
			int mod = 1;
			if (piece.color == Color.BLACK) {
				mod = -1;
			}
			if (origin.y == 1 || origin.y == 6) {
				try {
					if (getPiece(origin.x, origin.y + 2*mod) == null) {
						moves.add(new int[][][]{
							{
								{origin.x, origin.y},
								{origin.x, origin.y + 2*mod}
							}
						});
					}
				} catch (IllegalArgumentException e) {}
			}

			try {
				if (getPiece(origin.x, origin.y + mod) == null) {
					moves.add(new int[][][]{
						{
							{origin.x, origin.y},
							{origin.x, origin.y + mod}
						}
					});
				}
			} catch (IllegalArgumentException e) {}
			
			for (int modx : new int[]{1, -1}) {
				try {
					if (getPiece(origin.x + modx, origin.y + mod) != null) {
						moves.add(new int[][][]{
							{
								{origin.x, origin.y},
								{origin.x + modx, origin.y + mod}
							}
						});
					}
				} catch (IllegalArgumentException e) {}
			}
			break;

		case ROOK:
			for (int x = origin.x + 1; x < 8; x++) {
				moves.add(new int[][][]{
					{
						{origin.x, origin.y},
						{x, origin.y}
					}
				});
				
				if (getPiece(x, origin.y) != null) {
					break;
				}
			}
			for (int x = origin.x - 1; x >= 0; x--) {
				moves.add(new int[][][]{
					{
						{origin.x, origin.y},
						{x, origin.y}
					}
				});
				
				if (getPiece(x, origin.y) != null) {
					break;
				}
			}
			for (int y = origin.y + 1; y < 8; y++) {
				moves.add(new int[][][]{
					{
						{origin.x, origin.y},
						{origin.x, y}
					}
				});
				
				if (getPiece(origin.x, y) != null) {
					break;
				}
			}
			for (int y = origin.y - 1; y >= 0; y--) {
				moves.add(new int[][][]{
					{
						{origin.x, origin.y},
						{origin.x, y}
					}
				});
				
				if (getPiece(origin.x, y) != null) {
					break;
				}
			}
			break;

		case KNIGHT:
			for (int x : new int[]{origin.x - 2, origin.x + 2}) {
				for (int y : new int[]{origin.y - 1, origin.y + 1}) {
					moves.add(new int[][][]{
						{
							{origin.x, origin.y},
							{x, y}
						}
					});
				}
			}
			for (int x : new int[]{origin.x - 1, origin.x + 1}) {
				for (int y : new int[]{origin.y - 2, origin.y + 2}) {
					moves.add(new int[][][]{
						{
							{origin.x, origin.y},
							{x, y}
						}
					});
				}
			}
			break;

		case BISHOP:
			for (int i = 1; origin.x + i < 8 && origin.y + i < 8; i++) {
				moves.add(new int[][][]{
					{
						{origin.x, origin.y},
						{origin.x + i, origin.y + i}
					}
				});
				
				if (getPiece(origin.x + i, origin.y + i) != null) {
					break;
				}
			}
			for (int i = 1; origin.x + i < 8 && origin.y - i >= 0; i++) {
				moves.add(new int[][][]{
					{
						{origin.x, origin.y},
						{origin.x + i, origin.y - i}
					}
				});
				
				if (getPiece(origin.x + i, origin.y - i) != null) {
					break;
				}
			}
			for (int i = 1; origin.x - i >= 0 && origin.y + i < 8; i++) {
				moves.add(new int[][][]{
					{
						{origin.x, origin.y},
						{origin.x - i, origin.y + i}
					}
				});
				
				if (getPiece(origin.x - i, origin.y + i) != null) {
					break;
				}
			}
			for (int i = 1; origin.x - i >= 0 && origin.y - i >= 0; i++) {
				moves.add(new int[][][]{
					{
						{origin.x, origin.y},
						{origin.x - i, origin.y - i}
					}
				});
				
				if (getPiece(origin.x - i, origin.y - i) != null) {
					break;
				}
			}
			break;

		case QUEEN:
			for (int i = 1; origin.x + i < 8 && origin.y + i < 8; i++) {
				moves.add(new int[][][]{
					{
						{origin.x, origin.y},
						{origin.x + i, origin.y + i}
					}
				});
				
				if (getPiece(origin.x + i, origin.y + i) != null) {
					break;
				}
			}
			for (int i = 1; origin.x + i < 8 && origin.y - i >= 0; i++) {
				moves.add(new int[][][]{
					{
						{origin.x, origin.y},
						{origin.x + i, origin.y - i}
					}
				});
				
				if (getPiece(origin.x + i, origin.y - i) != null) {
					break;
				}
			}
			for (int i = 1; origin.x - i >= 0 && origin.y + i < 8; i++) {
				moves.add(new int[][][]{
					{
						{origin.x, origin.y},
						{origin.x - i, origin.y + i}
					}
				});
				
				if (getPiece(origin.x - i, origin.y + i) != null) {
					break;
				}
			}
			for (int i = 1; origin.x - i >= 0 && origin.y - i >= 0; i++) {
				moves.add(new int[][][]{
					{
						{origin.x, origin.y},
						{origin.x - i, origin.y - i}
					}
				});
				
				if (getPiece(origin.x - i, origin.y - i) != null) {
					break;
				}
			}

			for (int x = origin.x + 1; x < 8; x++) {
				moves.add(new int[][][]{
					{
						{origin.x, origin.y},
						{x, origin.y}
					}
				});
				
				if (getPiece(x, origin.y) != null) {
					break;
				}
			}
			for (int x = origin.x - 1; x >= 0; x--) {
				moves.add(new int[][][]{
					{
						{origin.x, origin.y},
						{x, origin.y}
					}
				});
				
				if (getPiece(x, origin.y) != null) {
					break;
				}
			}
			for (int y = origin.y + 1; y < 8; y++) {
				moves.add(new int[][][]{
					{
						{origin.x, origin.y},
						{origin.x, y}
					}
				});
				
				if (getPiece(origin.x, y) != null) {
					break;
				}
			}
			for (int y = origin.y - 1; y >= 0; y--) {
				moves.add(new int[][][]{
					{
						{origin.x, origin.y},
						{origin.x, y}
					}
				});
				
				if (getPiece(origin.x, y) != null) {
					break;
				}
			}
			break;

		case KING:
			for (int i : new int[]{-1, 0, 1}) {
				for (int j : new int[]{-1, 0, 1}) {
					if (i == 0 && j == 0) {
						continue;
					}
					moves.add(new int[][][]{
						{
							{origin.x, origin.y},
							{origin.x + i, origin.y + j}
						}
					});
				}
			}
			break;
		}

		return moves.toArray();
	}
	
	/**
	 * Determines whether the king of the given color is in check.
	 * 
	 * @param color	The color of the king to check for
	 * @return		Whether the king of the given color is in check
	 */
	public boolean isInCheck(Color color) {
		PiecePos[] kings = byTypeAndColor(Type.KING, color);
		if (kings.length == 0) {
			throw new IllegalStateException("There are no kings!");
		}
		Pos pos = kings[0].pos;
		int mod = 1;
		if (color == Color.BLACK) {
			mod = -1;
		}
		
		// Pawn
		for (int modx : new int[]{-1, 1}) {
			try {
				Piece piece = getPiece(pos.x + modx, pos.y + mod);
				if (piece != null && piece.color != color && piece.type == Type.PAWN) {
					return true;
				}
			} catch (IllegalArgumentException e) {}
		}
		
		// Knight
		for (int x : new int[]{pos.x - 2, pos.x + 2}) {
			for (int y : new int[]{pos.y - 1, pos.y + 1}) {
				try {
					Piece piece = getPiece(x, y);
					if (piece != null && piece.color != color && piece.type == Type.KNIGHT) {
						return true;
					}
				} catch (IllegalArgumentException e) {}
			}
		}
		for (int x : new int[]{pos.x - 1, pos.x + 1}) {
			for (int y : new int[]{pos.y - 2, pos.y + 2}) {
				try {
					Piece piece = getPiece(x, y);
					if (piece != null && piece.color != color && piece.type == Type.KNIGHT) {
						return true;
					}
				} catch (IllegalArgumentException e) {}
			}
		}
		
		// Rook and queen
		for (int x = pos.x + 1; x < 8; x++) {
			Piece piece = getPiece(x, pos.y);
			if (piece != null) {
				if (piece.color != color &&
						(piece.type == Type.ROOK || piece.type == Type.QUEEN)) {
					return true;
				} else {
					break;
				}
			}
		}
		for (int x = pos.x - 1; x >= 0; x--) {
			Piece piece = getPiece(x, pos.y);
			if (piece != null) {
				if (piece.color != color &&
						(piece.type == Type.ROOK || piece.type == Type.QUEEN)) {
					return true;
				} else {
					break;
				}
			}
		}
		for (int y = pos.y + 1; y < 8; y++) {
			Piece piece = getPiece(pos.x, y);
			if (piece != null) {
				if (piece.color != color &&
						(piece.type == Type.ROOK || piece.type == Type.QUEEN)) {
					return true;
				} else {
					break;
				}
			}
		}
		for (int y = pos.y - 1; y >= 0; y--) {
			Piece piece = getPiece(pos.x, y);
			if (piece != null) {
				if (piece.color != color &&
						(piece.type == Type.ROOK || piece.type == Type.QUEEN)) {
					return true;
				} else {
					break;
				}
			}
		}
		
		// Bishop and queen
		for (int i = 1; pos.x + i < 8 && pos.y + i < 8; i++) {
			Piece piece = getPiece(pos.x + i, pos.y + i);
			if (piece != null) {
				if (piece.color != color &&
						(piece.type == Type.BISHOP || piece.type == Type.QUEEN)) {
					return true;
				} else {
					break;
				}
			}
		}
		for (int i = 1; pos.x + i < 8 && pos.y - i >= 0; i++) {
			Piece piece = getPiece(pos.x + i, pos.y - i);
			if (piece != null) {
				if (piece.color != color &&
						(piece.type == Type.BISHOP || piece.type == Type.QUEEN)) {
					return true;
				} else {
					break;
				}
			}
		}
		for (int i = 1; pos.x - i >= 0 && pos.y + i < 8; i++) {
			Piece piece = getPiece(pos.x - i, pos.y + i);
			if (piece != null) {
				if (piece.color != color &&
						(piece.type == Type.BISHOP || piece.type == Type.QUEEN)) {
					return true;
				} else {
					break;
				}
			}
		}
		for (int i = 1; pos.x - i >= 0 && pos.y - i >= 0; i++) {
			Piece piece = getPiece(pos.x - i, pos.y - i);
			if (piece != null) {
				if (piece.color != color &&
						(piece.type == Type.BISHOP || piece.type == Type.QUEEN)) {
					return true;
				} else {
					break;
				}
			}
		}
		
		// King
		for (int i : new int[]{-1, 0, 1}) {
			for (int j : new int[]{-1, 0, 1}) {
				if (i == 0 && j == 0) {
					continue;
				}
				try {
					Piece piece = getPiece(pos.x + i, pos.y + j);
					if (piece != null && piece.color != color && piece.type == Type.KING) {
						return true;
					}
				} catch (IllegalArgumentException e) {}
			}
		}
		
		return false;
	}
}