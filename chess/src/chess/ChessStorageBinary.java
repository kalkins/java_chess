package chess;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import chess.Piece.Color;

public class ChessStorageBinary implements ChessStorageInterface {
	public static int version = 2;

	public void save(Game game, String filename) throws IOException {
		FileOutputStream out = new FileOutputStream(filename);
		
		out.write(version);
		out.write(game.turn);
		
		// Write out the type and position of all pieces, starting with white
		for (PiecePos piecePos : game.byColor(Color.WHITE)) {
			for (int i = 0; i < 6; i++) {
				if (piecePos.piece == Game.WHITE[i]) {
					out.write(i);
					out.write(piecePos.pos.x);
					out.write(piecePos.pos.y);
					break;
				}
			}
		}
		
		// Marks the transition from white to black pieces
		out.write(255);
		
		for (PiecePos piecePos : game.byColor(Color.BLACK)) {
			for (int i = 0; i < 6; i++) {
				if (piecePos.piece == Game.BLACK[i]) {
					out.write(i);
					out.write(piecePos.pos.x);
					out.write(piecePos.pos.y);
					break;
				}
			}
		}
		
		out.close();
	}
	
	public Game load(String filename) throws IOException {
		FileInputStream in = new FileInputStream(filename);
		Piece[][] board = new Piece[8][8];
		Game game = new Game();
		
		if (in.read() != version) {
			in.close();
			throw new IllegalArgumentException("Incompatible file version.");
		}
		
		game.turn = in.read();
		
		Piece[] pieces = Game.WHITE;
		while (true) {
			int i = in.read();
			
			if (i == -1) {
				break;
			} else if (i == 255) {
				pieces = Game.BLACK;
				continue;
			}

			int x = in.read();
			int y = in.read();
			
			board[x][y] = pieces[i];
		}
		
		in.close();

		game.board = board;
		return game;
	}
}