package chess;
import java.io.File;
import java.io.IOException;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class Chess extends Application {
	GridPane root = new GridPane();
	Game game = new Game();
	Pos selected = null;
	Move[] moves = null;
    final int size = 8;
    BorderWidths borderw = new BorderWidths(20.0);
    
    Color white = Color.WHITE;
    Color black = Color.BLACK;
    Color red = Color.DARKRED;
    Color blue = Color.DODGERBLUE;
    Color green = Color.FORESTGREEN;
    
    BackgroundFill whiteFill = new BackgroundFill(white, null, null);
    BackgroundFill blackFill = new BackgroundFill(black, null, null);
    BackgroundFill redFill = new BackgroundFill(red, null, null);
    BackgroundFill blueFill = new BackgroundFill(blue, null, null);
    BackgroundFill greenFill = new BackgroundFill(green, null, null);

    public void start(Stage primaryStage) {
    	root.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
    	
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                Label label = new Label("p");
                StackPane pane = new StackPane(label);
                root.add(pane, col, row);
                pane.setMaxSize(80, 80);
                pane.setMinSize(80, 80);
            }
        }
        
        Label saveLabel = new Label("Save");
        Label loadLabel = new Label("Load");
        Label undoLabel = new Label("Undo");
        Label redoLabel = new Label("Redo");
        
        StackPane savePane = new StackPane(saveLabel);
        StackPane loadPane = new StackPane(loadLabel);
        StackPane undoPane = new StackPane(undoLabel);
        StackPane redoPane = new StackPane(redoLabel);

        root.add(savePane, size, 0, 1, 2);
        root.add(loadPane, size, 2, 1, 2);
        root.add(undoPane, size, 4, 1, 2);
        root.add(redoPane, size, 6, 1, 2);

        root.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent e) {
                for(Node node : root.getChildren()) {
					if(node.getBoundsInParent().contains(e.getSceneX(),  e.getSceneY())) {
						int col = GridPane.getColumnIndex(node);
						int row = GridPane.getRowIndex(node);
						
						if (col == size) {
							FileChooser chooser = new FileChooser();
							Stage window = new Stage();
							if (row < 2) {
								File file = chooser.showSaveDialog(window);
								try {
									game.save(file.getPath());
								} catch (IOException e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								}
							} else if (row == 2 || row == 3) {
								File file = chooser.showOpenDialog(window);
								try {
									game = Game.load(file.getPath());
								} catch (IOException e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								}
							} else if (row == 4 || row == 5) {
								game.undo();
							} else if (row == 6 || row == 7) {
								game.redo();
							}
						} else {
							Pos pos = new Pos(col, 7-row);
							Piece piece = game.getPiece(pos);

							if (selected != null && selected.equals(pos)) {
								selected = null;
								moves = null;
								break;
							}
							
							if (selected == null || moves == null || moves.length == 0) {
								if (piece == null) {
									selected = null;
									moves = null;
								} else {
									selected = pos;
									try {
										moves = game.validMoves(pos);
									} catch (IllegalArgumentException exception) {
										moves = null;
									}
								}
							} else {
								try {
									boolean moved = false;
									for (Move move : moves) {
										if (move.target().equals(pos)) {
											game.move(move);
											selected = null;
											moves = null;
											moved = true;
											break;
										}
									}
									
									if (!moved) {
										if (piece == null) {
											selected = null;
											moves = null;
										} else {
											selected = pos;
											moves = game.validMoves(pos);
										}
									}
								} catch (IllegalArgumentException exception) {
									moves = null;
								}
							}
						}
						break;
					}
                }
                
				update();
            }
        });
        
        update();
        
        primaryStage.setScene(new Scene(root, 400, 400));
        primaryStage.show();
    }
    
    public void update() {
    	for (Node node : root.getChildren()) {
    		int col = GridPane.getColumnIndex(node);
    		int row = GridPane.getRowIndex(node);
    		if (col >= size) {
    			continue;
    		}

    		StackPane pane = (StackPane) node;
    		Label label = (Label) pane.getChildren().get(0);
    		Pos pos = new Pos(col, 7-row);
    		String text;
    		BackgroundFill fill = null;

    		if ((col + 7*row) % 2 == 0) {
    			fill = whiteFill;
    		} else {
    			fill = blackFill;
    		}
    		
    		if (moves != null) {
    			for (Move move : moves) {
    				if (move.target().equals(pos)) {
    					fill = blueFill;
    				}
    			}
    		}
    		
    		if (pos.equals(selected)) {
    			if (moves == null || moves.length == 0) {
    				fill = redFill;
    			} else {
    				fill = greenFill;
    			}
    		}
    		
    		try {
    			text = game.getPiece(pos).toString();
    		} catch (NullPointerException e) {
    			text = " ";
    		}
    		
			pane.setBackground(new Background(fill));
			pane.setBorder(new Border(new BorderStroke(black, null, null, borderw)));
    		label.setText(text);
    	}
    }

    public static void main(String[] args) {
        launch(args);
    }
}