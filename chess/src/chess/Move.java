package chess;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * A representation of a move a piece can make. This class can only
 * be instantiated by other classes in this package, which ensures
 * that a Move object always is valid. The object will also be locked
 * to the turn it was created, so it can't be saved for later.
 * 
 * @author	Sindre Stephansen
 * @see		Game#validMoves
 * @see		Game#move
 */
public final class Move implements Iterable<Pos[]> {
	public final int turn; 
	private final ArrayList<Pos[]> moves;
	
	/*
	 * Constructors
	 */
	
	// This is only to be used internally.
	protected Move(Pos[][] moves, int turn) {
		this.turn = turn;
		this.moves = new ArrayList<Pos[]>();
		
		for (Pos[] tmp : moves) {
			if (tmp.length != 2) {
				throw new IllegalArgumentException("Each subarray must contain exactly two elements");
			} else if (tmp[0] == null || tmp[1] == null) {
				throw new IllegalArgumentException("The positions can't be null");
			}

			this.moves.add(tmp);
		}
	}
	
	/*
	 * Inherited methods
	 */
	
	public Iterator<Pos[]> iterator() {
		return moves.iterator();
	}
	
	/*
	 * Methods
	 */
	
	/**
	 * Returns the position the piece is being moved from.
	 * 
	 * @return	The origin position of the piece to be moved
	 * @see		Pos
	 * @see		#target
	 */
	public Pos origin() {
		return moves.get(0)[0];
	}
	
	/**
	 * Returns the position the piece is being moved to.
	 * 
	 * @return	The position the piece is being moved to
	 * @see		Pos
	 * @see		#origin
	 */
	public Pos target() {
		return moves.get(moves.size() - 1)[1];
	}
}