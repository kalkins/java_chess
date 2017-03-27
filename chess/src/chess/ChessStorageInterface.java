package chess;

import java.io.IOException;

public interface ChessStorageInterface {
	public void save(Game game, String filename) throws IOException;
	
	public Game load(String filename) throws IOException;
}