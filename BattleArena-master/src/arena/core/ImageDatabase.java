package arena.core;

import javafx.scene.image.Image;

final class ImageDatabase
{
	// Game-related images
	public final Image floor = new Image("/res/Floor.jpg");
	public final Image wall = new Image("/res/Wall.png");
	public final Image healthPack = new Image("/res/HealthPack.png");
	public final Image mine = new Image("/res/Mine.png");
	public final Image player = new Image("/res/Player.png");
	public final Image projectile = new Image("/res/Projectile.png");
	public final Image storm = new Image("/res/Storm.png");
	public final Image heartFilled = new Image("res/Heart_Fill.png");
	public final Image heartEmpty = new Image("res/Heart_Empty.png");
	
	// UI-related images
	public final Image play = new Image("/res/Play.png");
	public final Image pause = new Image("/res/Pause.png");
	public final Image replay = new Image("/res/Replay.png");
}