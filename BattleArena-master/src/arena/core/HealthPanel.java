package arena.core;

import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

final class HealthPanel extends HBox
{
	private final ImageDatabase imageDatabase;
	private final ImageView[] healthImageViews;
	
	public HealthPanel(ImageDatabase imageDatabase, int pixelSize)
	{
		super();
		
		this.imageDatabase = imageDatabase;
		healthImageViews = new ImageView[Player.HEALTH_MAX];
		
		for(int i = 0; i < healthImageViews.length; i++)
		{
			healthImageViews[i] = new ImageView(imageDatabase.heartEmpty);
			healthImageViews[i].setFitWidth(pixelSize);
			healthImageViews[i].setFitHeight(pixelSize);
		}
		
		setSpacing(4);
		getChildren().addAll(healthImageViews);
	}
	
	public void update(int health)
	{
		for(int i = 0; i < healthImageViews.length; i++)
		{
			if(i+1 <= health)
				healthImageViews[i].setImage(imageDatabase.heartFilled);
			else
				healthImageViews[i].setImage(imageDatabase.heartEmpty);
		}
	}
}