package arena.core;

import javafx.animation.Animation.Status;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import arena.core.GameState.EntityType;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.control.Button;
import javafx.scene.control.Separator;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

final class ArenaGUI extends BorderPane
{
	
	private Button btnPlayNPause;
	private Button btnGenerate;
	private ImageView imageViewPlayNPause;
	private ImageView imageViewGenerate;
	
	private GridPane tilesGridPane;
	
	private final ArenaInfoPanel arenaInfoPanel;
	
	private VBox top;
	
	private Tile[][] tiles;
	private Timeline gameLoopTimeline;
	private ImageDatabase imageDatabase;
	private Game game;
	public static final double SECS_PER_TICK = 0.25;
	
	//**************************GAGE FILE SAVE STUFF*********************
	private static FileWriter fileWriter;
	private static BufferedWriter bufferedWriter;
	
 	public ArenaGUI(Stage stage, int mapWidth, int mapHeight, int pixelSize, Class<? extends PlayerAI> p1Class, Class<? extends PlayerAI> p2Class)
	{
		super();
		
		imageDatabase = new ImageDatabase();
		game = new Game(mapWidth, mapHeight, p1Class, p2Class);
		
		arenaInfoPanel = new ArenaInfoPanel(stage, game, imageDatabase);
		
		top = new VBox(10);
		setTop(top);
		
		initializeTiles(pixelSize);
		initializeControlButtons();
		
		top.getChildren().add(arenaInfoPanel);
		
		generate();
	}
 	
	private void redisplay()
	{
		for(int y = 0; y < game.getMap().getHeight(); y++)
		{
			for(int x = 0; x < game.getMap().getWidth(); x++)
			{
				Entity entity = game.getMap().getEntity(x, y);
				tiles[x][y].setEntity(entity);
			}
		}
		
		arenaInfoPanel.onRedisplay();
	}
	
	private void initializeControlButtons()
	{
		// Generate
		btnGenerate = new Button();
		btnGenerate.setTooltip(new Tooltip("Generates a new map."));
		imageViewGenerate = new ImageView(imageDatabase.replay);
		imageViewGenerate.setFitWidth(32);
		imageViewGenerate.setFitHeight(32);
		btnGenerate.setGraphic(imageViewGenerate);
		btnGenerate.relocate(50, 0);
		btnGenerate.setOnAction(e -> onGenerateClicked());
		
		// Play & Pause
		btnPlayNPause = new Button();
		btnPlayNPause.setTooltip(new Tooltip("Plays or pauses the simulation."));
		imageViewPlayNPause = new ImageView(imageDatabase.play);
		imageViewPlayNPause.setFitWidth(32);
		imageViewPlayNPause.setFitHeight(32);
		btnPlayNPause.setGraphic(imageViewPlayNPause);
		btnPlayNPause.relocate(150, 0);
		btnPlayNPause.setOnAction(e -> onPlayNPauseClicked());
		
		HBox hBox = new HBox(createSeparator(), btnGenerate, createSeparator(), btnPlayNPause, createSeparator());
		hBox.setPadding(new Insets(10, 0, 10, 0));
		top.getChildren().add(hBox);
	}
	
	private Separator createSeparator()
	{
		Separator separator = new Separator(Orientation.HORIZONTAL);
		separator.setVisible(false);
		HBox.setHgrow(separator, Priority.ALWAYS);
		return separator;
	}
	
	private void initializeTiles(int pixelSize)
	{
		tilesGridPane = new GridPane();
		Map map = game.getMap();
		
		tiles = new Tile[map.getWidth()][map.getHeight()];
		for(int y = 0; y < map.getHeight(); y++)
		{
			for(int x = 0; x < map.getWidth(); x++)
			{
				Entity entity = map.getEntity(x, y);
				Tile tile = new Tile(imageDatabase, entity, pixelSize);
				tiles[x][y] = tile;
				tilesGridPane.add(tile, x, y);
			}
		}
		
		setBottom(tilesGridPane);
		
		//[TODO]!!!!!!!!!!!!!!!!!!
		try
		{
			//fileWriter = new FileWriter("ArenaRoundSave.txt",true);
			//bufferedWriter = new BufferedWriter(fileWriter);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
	}
	
	private void onGenerateClicked()
	{
		generate();
	}
	
	private void onPlayNPauseClicked()
	{
		if(isRunning()) // Running, so pause
		{
			stopGameLoop();
			//[TODO]!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
			try {
				saveRoundInCSV();
				bufferedWriter.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else // Not running, so start
		{
			startGameLoop();
		}
	}
	
	private void stopGameLoop()
	{
		if(gameLoopTimeline != null)
		{
			gameLoopTimeline.stop();
			gameLoopTimeline = null;
		}
		
		imageViewPlayNPause.setImage(imageDatabase.play);
	}
	
	private void startGameLoop()
	{
		gameLoopTimeline = new Timeline();
		
		gameLoopTimeline.getKeyFrames().add(new KeyFrame(Duration.seconds(SECS_PER_TICK), e ->
		{
			game.tick();
			redisplay();
			//*************************
			
			saveRoundInCSV();
			
			//****************
			if(game.isGameOver())
			{
				stopGameLoop();
			}
		}));
		
		gameLoopTimeline.setCycleCount(Timeline.INDEFINITE);
		gameLoopTimeline.play();
		
		imageViewPlayNPause.setImage(imageDatabase.pause);
	}
	
	private void generate()
	{
		stopGameLoop();
		game.generateMap();
		game.setRound(0);
		redisplay();
	}
	
	private boolean isRunning()
	{
		if(gameLoopTimeline == null)
			return false;
		
		return gameLoopTimeline.getStatus() == Status.RUNNING;
	}
	
	
	//*******************************GAGE'S ATTEMPTED SAVING ROUNDS IN TEXT FILE******************************************
	//[TODO]
	private void saveRoundInCSV()
	{
		try
		{
			fileWriter = new FileWriter("ArenaRoundSave.txt",true);
			bufferedWriter = new BufferedWriter(fileWriter);
			//bufferedWriter = new BufferedWriter(fileWriter);
			for(int i = 0; i < tiles.length; i++)
			{
				String row = "";
				//System.out.println();
				for(int j = 0; j< tiles[i].length; j++)
				{
					//System.out.print(returnSymbolBasedOnTileEntity(tiles[j][i]));
					row +=returnSymbolBasedOnTileEntity(tiles[j][i]);
				}
				bufferedWriter.write(row);
				bufferedWriter.write("\n");
			}
			//bufferedWriter.write("------------------------------------------------------------");
			//bufferedWriter.write("\n");
			bufferedWriter.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	
	//[TODO] MAKE THIS WORK AND IMPLEMENT TO CLEAR FILE EVERY TIME A NEW MATCH IS STARTED!
	private void clearRoundFile()
	{
		String filename = "ArenaRoundSave.txt";
		BufferedReader br = null;
		try
		{
			FileReader fr = new FileReader(filename);
			br = new BufferedReader(fr);
			fileWriter = new FileWriter(filename,true);
			bufferedWriter = new BufferedWriter(fileWriter);
			String line; //used to get each individual line of the file.
			
			// while line is equal to the next line of the buffered reader is not equal to null
			// this means read the next line in the file until there are not any more line to read
			while (  (line = br.readLine()) != null ) 
			{
				bufferedWriter.write("");
				bufferedWriter.write("\n");
			}	
		}
		catch (Exception e) 
		{

			e.printStackTrace();
		}
		finally
		{
			try 
			{
				br.close();
			}
			catch(Exception e)
			{
				System.out.println("[ERROR NO BUFFERED READER TO CLOSE]");
			}
			
		}
	}

	//[TODO]
	private char returnSymbolBasedOnTileEntity(Tile tile)
	{
		Entity tileEntity = tile.getEntity();
		switch(Entity.getEntityType(tileEntity))
		{
		case Wall:
			return '#';
			
		case Player:
			return '@';
			
		case Storm:
			return 'X';
			
		case HealthPack:
			return '+';
			
		case Empty:
			return '.';
			
		case Mine:
			return '%';
			
		case Projectile:
			return '*';
			
		default:
			throw new IllegalStateException("Unknown entry in tile: (" + tile.getLayoutX() + ", "+ tile.getLayoutY()+")");
		
		}
	}
	
	
	
	
}