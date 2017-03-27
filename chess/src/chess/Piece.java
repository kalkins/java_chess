package chess;

/**
 * A class representing a chess piece. Instances of this class is immutable,
 * and the attributes are public, to make them easier to access.
 * 
 * @author 	Sindre Stephansen
 * @see		Game
 */
public class Piece {
	/**
	 * The colors of chess pieces.
	 */
	public enum Color {
		WHITE, BLACK
	}
	
	/**
	 * The types of chess pieces.
	 */
	public enum Type {
		PAWN, ROOK, KNIGHT, BISHOP, QUEEN, KING
	}
	
	/*
	 * Attributes
	 */

	/**
	 * The color of the piece
	 */
	public final Color color;

	/**
	 * The type of the piece.
	 */
	public final Type type;
	
	/*
	 * Constructors
	 */

	/**
	 * Class constructor.
	 * 
	 * @param color	The color of the piece to be created.
	 * @param type	The type of the piece to be created.
	 */
	public Piece(Color color, Type type) {
		this.color = color;
		this.type = type;
	}
	
	/*
	 * Inherited methods
	 */
	
	/**
	 * Returns a the character that represents this chess piece
	 * in algebraic notation. White pieces are upper-case while
	 * black pieces are lower-case.
	 * 
	 * @return	The character used to represent the piece in algebraic notation.
	 * @see		<a href="https://en.wikipedia.org/wiki/Algebraic_notation_%28chess%29">Algebraic notation</a>
	 */
	@Override
	public String toString() {
		if (color == Color.WHITE) {
			switch (this.type) {
			case PAWN:
				return "P";
			case ROOK:
				return "R";
			case KNIGHT:
				return "N";
			case BISHOP:
				return "B";
			case QUEEN:
				return "Q";
			case KING:
				return "K";
			default:
				return "";
			}
		} else {
			switch (this.type) {
			case PAWN:
				return "p";
			case ROOK:
				return "r";
			case KNIGHT:
				return "n";
			case BISHOP:
				return "b";
			case QUEEN:
				return "q";
			case KING:
				return "k";
			default:
				return "";
			}
		}
	}
}