package chess;

/**
 * An implementation of a tuple containing a piece and a position.
 * This is primarily used by {@link Game#byColor} and similar methods.
 * 
 * @author  Sindre Stephansen
 * @see		Game
 * @see		Game#byColor
 * @see		Game#byType
 * @see		Game#byTypeAndColor
 */
public class PiecePos {
	public final Piece piece;
	public final Pos pos;
	
	/**
	 * Class constructor.
	 * 
	 * @param piece	The piece to put in the tuple.
	 * @param pos	The position to put in the tuple.
	 */
	public PiecePos(Piece piece, Pos pos) {
		this.piece = piece;
		this.pos = pos;
	}
}