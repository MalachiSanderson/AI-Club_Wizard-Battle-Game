package arena.core;

import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

final class Tile extends StackPane
{
	private Entity entity = null;
	
	private ImageView backgroundImageView;
	private ImageView foregroundImageView;
	private ColorAdjust colorAdjust;
	private ImageDatabase imageDatabase;
	
	public Tile(ImageDatabase imageDatabase, Entity entity, int tileSize)
	{
		this.imageDatabase = imageDatabase;
		setMinSize(tileSize, tileSize);
		setMaxSize(tileSize, tileSize);
		setPrefSize(tileSize, tileSize);
		initializeImageViews(tileSize);
		setEntity(entity);
		getChildren().addAll(backgroundImageView, foregroundImageView);
	}
	
	private final void initializeImageViews(int tileSize)
	{
		backgroundImageView = new ImageView();
		backgroundImageView.setFitWidth(tileSize);
		backgroundImageView.setFitHeight(tileSize);
		
		foregroundImageView = new ImageView();
		foregroundImageView.setFitWidth(tileSize);
		foregroundImageView.setFitHeight(tileSize);
		colorAdjust = new ColorAdjust();
		foregroundImageView.setEffect(colorAdjust);
	}
	
	final void update()
	{
		backgroundImageView.setImage(imageDatabase.floor);
		colorAdjust.setHue(0);
		foregroundImageView.setScaleX(1);
		
		if(entity == null)
		{
			foregroundImageView.setImage(null);
		}
		else
		{
			if(entity instanceof Player)
			{
				foregroundImageView.setImage(imageDatabase.player);
				
				Player player = (Player) entity; 
				double hue = -1+2*(((player.getColor().getHue() + 180) % 360)/360);
				colorAdjust.setHue(hue);
				
				foregroundImageView.setScaleX(player.getXScaleMultiplier());
			}
			else if(entity instanceof Wall)
			{
				foregroundImageView.setImage(imageDatabase.wall);
			}
			else if(entity instanceof Storm)
			{
				foregroundImageView.setImage(imageDatabase.storm);
			}
			else if(entity instanceof Projectile)
			{
				foregroundImageView.setImage(imageDatabase.projectile);
				
				Projectile projectile = (Projectile) entity; 
				double hue = -1+2*(((projectile.getColor().getHue() + 180) % 360)/360);
				colorAdjust.setHue(hue);
			}
			else if(entity instanceof Mine)
			{
				foregroundImageView.setImage(imageDatabase.mine);
			}
			else if(entity instanceof HealthPack)
			{
				foregroundImageView.setImage(imageDatabase.healthPack);
			}
		}
	}

	final Entity getEntity()
	{
		return entity;
	}

	final void setEntity(Entity entity)
	{
		this.entity = entity;
		update();
	}
}