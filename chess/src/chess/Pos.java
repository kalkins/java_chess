package chess;

/**
 * A representation of a position on a chess board. This guarantees that
 * the position is valid (and throws an exception if it isn't) and should
 * therefore be used for all passing of coordinates to the game.
 * 
 * The parameters x and y are public to make them easier to access,
 * but they can't be changed. A Pos object is immutable.
 * 
 * @author Sindre Stephansen
 * @see Game
 */
public class Pos {
	public final int x;
	public final int y;
	
	/**
	 * Class constructor. Creates a representation of the given coordinates.
	 * Both coordinates must be between 0 and 7, if they are not an
	 * IllegalArgumentException is thrown.
	 * 
	 * @param x	The x-coordinate
	 * @param y The y-coordinate
	 * @throws	IllegalArgumentException	If x or y is out of bounds.
	 */
	public Pos(int x, int y) {
		if (x < 0 || x > 7 || y < 0 || y > 7) {
			throw new IllegalArgumentException("x and y must be between 0 and 7");
		}

		this.x = x;
		this.y = y;
	}
	
	/**
	 * Returns a string representation of the position. The string is
	 * on the form '(x, y)'.
	 * 
	 * @return	A string representation of the position.
	 */
	@Override
	public String toString() {
		return "("+x+", "+y+")";
	}
	
	/**
	 * Checks whether this object represents the same position as
	 * the given object. Returns false if the objects are incomparable.
	 * 
	 * @param o	The object to compare to
	 * @return	Whether the object represents the same position as this object
	 */
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Pos)) {
			return false;
		}
		
		Pos other = (Pos) o;
		return other.x == x && other.y == y;
	}
	
	/**
	 * Checks whether this object matches the given coordinates. If the
	 * given coordinates are invalid no exception is thrown, but they
	 * can't match this object, so false is returned.
	 * 
	 * @param x The x-coordinate to compare to
	 * @param y The y-coordinate to compare to
	 * @return	true if the coordinates match, false if not.
	 */
	public boolean equals(int x, int y) {
		return x == this.x && y == this.y;
	}
}
	