package arena.core;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import arena.agents.RandomAI;

/**
 * The <code>BattleArena</code> class contains the JavaFX application and all necessary dependencies.
 * <p>
 * To start a match with SimpleAI vs RandomAI, simply call:
 * </p>
 * <pre>
 * BattleArena.startArena(SimpleAI.class, RandomAI.class);
 * </pre>
 *  
 * @author ERAU AI Club
 */
public class BattleArena extends Application
{
	// The number of columns in the map
	private static int mapWidth;
	
	// The number of rows in the map
	private static int mapHeight;
	
	// The size, in pixels, of each cell
	private static int pixelSize;
	
	// The class of the 1st AI agent
	private static Class<? extends PlayerAI> p1Class;
	
	// The class of the 2nd AI agent
	private static Class<? extends PlayerAI> p2Class;
	
	/**
	 * Starts the arena application with the default sizes (20x20 map size) and 24 pixel size.
	 * The parameters are the classes of the players, in the format of: <code>YourClassName.class</code>.
	 * 
	 * <p>
	 * For example, the following code will start an arena game with RandomAI versus CowardAI.
	 * </p>
	 * <pre>
	 * BattleArena.startArena(RandomAI.class, CowardAI.class);
	 * </pre>
	 * 
	 * @param p1Class - the 1st player's class
	 * @param p2Class - the 2nd player's class 
	 */
	public static void startArena(Class<? extends PlayerAI> p1Class, Class<? extends PlayerAI> p2Class)
	{
		startArena(20, 20, 24, p1Class, p2Class);
	}
	
	/**
	 * Starts the arena application with the specified map size and pixel size.
	 * The parameters are the classes of the players, in the format of: <code>YourClassName.class</code>.
	 * 
	 * <p>
	 * For example, the following code will start an arena game with RandomAI versus CowardAI at a map of size 20x20 and 24 pixels for each cell.
	 * </p>
	 * <pre>
	 * BattleArena.startArena(20, 20, 24, RandomAI.class, CowardAI.class);
	 * </pre>
	 * 
	 * @param mapWidth - the width of the map (8 is minimum)
	 * @param mapHeight - the height of the map (8 is minimum)
	 * @param pixelSize - the pixel size of each cell (8 is minimum)
	 * @param p1Class - the 1st player's class
	 * @param p2Class - the 2nd player's class 
	 */
	public static void startArena(int mapWidth, int mapHeight, int pixelSize, Class<? extends PlayerAI> p1Class, Class<? extends PlayerAI> p2Class)
	{
		if(mapWidth < 8)
		{
			System.err.println("Error in startArena(): The map width must be 8 or greater, was given " + mapWidth + ". Will use 8 instead.");
			mapWidth = 8;
		}
		
		if(mapHeight < 8)
		{
			System.err.println("Error in startArena(): The map height must be 8 or greater, was given " + mapHeight + ". Will use 8 instead.");
			mapHeight = 8;
		}
		
		if(pixelSize < 8)
		{
			System.err.println("Error in startArena(): The pixel size must be 8 or greater, was given " + pixelSize + ". Will use 8 instead.");
			pixelSize = 8;
		}
		
		if(p1Class == null)
		{
			System.err.println("Error in startArena(): The class for the 1st player cannot be null. Will use RandomAI class instead.");
			p1Class = RandomAI.class;
		}
		
		if(p2Class == null)
		{
			System.err.println("Error in startArena(): The class for the 2nd player cannot be null. Will use RandomAI class instead.");
			p2Class = RandomAI.class;
		}
		
		BattleArena.mapWidth = mapWidth;
		BattleArena.mapHeight = mapHeight;
		BattleArena.pixelSize = pixelSize;
		BattleArena.p1Class = p1Class;
		BattleArena.p2Class = p2Class;
		
		Application.launch(BattleArena.class);
	}
	
	@Override
	public void start(Stage stage) throws Exception
	{
		ArenaGUI pane = new ArenaGUI(stage, mapWidth, mapHeight, pixelSize, p1Class, p2Class);
		
		Scene scene = new Scene(pane);
		scene.getStylesheets().add("/res/style.css");
		
		stage.setScene(scene);
		stage.setTitle("Battle Arena");
		stage.setMinWidth(492);
		stage.setWidth(492);
		stage.setMaxWidth(492);
		stage.setMinHeight(760);
		stage.setHeight(760);
		stage.setMaxHeight(760);
		stage.getIcons().add(new Image("res/icon.png"));
		
		stage.show();
	}
}