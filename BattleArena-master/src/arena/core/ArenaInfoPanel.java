package arena.core;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

final class ArenaInfoPanel extends AnchorPane
{
	private final Game game;
	private final ImageDatabase imageDatabase;
	private final Stage stage;
	
	private final Label p1Name;
	private final HBox p1SpriteContainer;
	private final ImageView p1Sprite;
	private final ColorAdjust p1ColorAdjust;
	private final HealthPanel p1HealthPanel;
	
	private final Label p2Name;
	private final HBox p2SpriteContainer;
	private final ImageView p2Sprite;
	private final ColorAdjust p2ColorAdjust;
	private final HealthPanel p2HealthPanel;
	
	private final Label roundLabel;
	private final Label stormRoundLabel;
	private final Label stormSizeLabel;
	
	public ArenaInfoPanel(Stage stage, Game game, ImageDatabase imageDatabase)
	{
		super();
		
		setPadding(new Insets(0, 0, 60, 0));
		
		this.stage = stage;
		this.game = game;
		this.imageDatabase = imageDatabase;
		
		p1Name = new Label();
		p1Sprite = new ImageView();
		p1SpriteContainer = new HBox(p1Sprite);
		p1HealthPanel = new HealthPanel(imageDatabase, 24);
		p1ColorAdjust = new ColorAdjust();
		
		p2Name = new Label();
		p2Sprite = new ImageView();
		p2SpriteContainer = new HBox(p2Sprite);
		p2HealthPanel = new HealthPanel(imageDatabase, 24);
		p2ColorAdjust = new ColorAdjust();
		
		roundLabel = new Label();
		stormRoundLabel = new Label();
		stormSizeLabel = new Label();
		
		setup();
	}
	
	private void setup()
	{
		// Add all
		getChildren().addAll(p1Name, p1SpriteContainer, p1HealthPanel, p2Name, p2SpriteContainer, p2HealthPanel, roundLabel, stormRoundLabel, stormSizeLabel);
		
		// Setup p1
		LayoutUtil.fixateX(stage, p1Name, 0.08, 0, 0);
		LayoutUtil.fixateY(stage, p1Name, 0.04, 1, 0);
		LayoutUtil.fixateX(stage, p1SpriteContainer, 0.08, 0, 0);
		LayoutUtil.fixateY(stage, p1SpriteContainer, 0.165, 1, 0);
		LayoutUtil.fixateX(stage, p1HealthPanel, 0.18, 0.5, 0);
		LayoutUtil.fixateY(stage, p1HealthPanel, 0.205, 1, 0);
		p1Sprite.setImage(imageDatabase.player);
		p1Sprite.setScaleX(1);
		p1Sprite.setEffect(p1ColorAdjust);
		p1Sprite.setFitWidth(64);
		p1Sprite.setFitHeight(64);
		p1SpriteContainer.setId("player-info-sprite");
		p1SpriteContainer.setPadding(new Insets(10));
		
		// Setup p2
		LayoutUtil.fixateX(stage, p2Name, 1-0.09, 1, 0);
		LayoutUtil.fixateY(stage, p2Name, 0.04, 1, 0);
		LayoutUtil.fixateX(stage, p2SpriteContainer, 1-0.09, 1, 0);
		LayoutUtil.fixateY(stage, p2SpriteContainer, 0.165, 1, 0);
		LayoutUtil.fixateX(stage, p2HealthPanel, 1-0.19, 0.5, 0);
		LayoutUtil.fixateY(stage, p2HealthPanel, 0.205, 1, 0);
		p2Sprite.setImage(imageDatabase.player);
		p2Sprite.setScaleX(-1);
		p2Sprite.setEffect(p2ColorAdjust);
		p2Sprite.setFitWidth(64);
		p2Sprite.setFitHeight(64);
		p2SpriteContainer.setId("player-info-sprite");
		p2SpriteContainer.setPadding(new Insets(10));
		
		// Setup rounds
		stormRoundLabel.setId("light-label");
		stormSizeLabel.setId("light-label");
		LayoutUtil.fixateX(stage, roundLabel, 0.5, 0.5, 0);
		LayoutUtil.fixateY(stage, roundLabel, 0.07, 0.5, 0);
		LayoutUtil.fixateX(stage, stormRoundLabel, 0.5, 0.5, 0);
		LayoutUtil.fixateY(stage, stormRoundLabel, 0.07, 0.5, 30);
		LayoutUtil.fixateX(stage, stormSizeLabel, 0.5, 0.5, 0);
		LayoutUtil.fixateY(stage, stormSizeLabel, 0.07, 0.5, 50);
	}

	public void onRedisplay()
	{
		// Update p1
		p1Name.setText(game.getPlayer1().getName() + " - " + game.getPlayer1().getClassName());
		p1HealthPanel.update(game.getPlayer1().getHealth());
		double p1Hue = -1+2*(((game.getPlayer1().getColor().getHue() + 180) % 360)/360);
		p1ColorAdjust.setHue(p1Hue);
		
		// Update p2
		p2Name.setText(game.getPlayer2().getClassName() + " - " + game.getPlayer2().getName());
		p2HealthPanel.update(game.getPlayer2().getHealth());
		double p2Hue = -1+2*(((game.getPlayer2().getColor().getHue() + 180) % 360)/360);
		p2ColorAdjust.setHue(p2Hue);
		
		// Update rounds
		roundLabel.setText("Round " + String.valueOf(game.getRound()) + " / " + String.valueOf(Game.MAX_ROUNDS));
		stormRoundLabel.setText("Storm advances in " + String.valueOf(game.getRoundsTillNextStormAdvance()) + " rounds");
		stormSizeLabel.setText("Storm Size is " + String.valueOf(game.getMap().getStormSize()) + "/" + String.valueOf(game.getMap().getStormMaxSize()));
		
		// Show p1 death
		if(game.getPlayer1().isDead())
		{
			p1SpriteContainer.setStyle("-fx-background-color: #FF0000;");
		}
		else
		{
			p1SpriteContainer.setStyle("-fx-background-color: #FFFFFF;");
		}
		
		// Show p2 death
		if(game.getPlayer2().isDead())
		{
			p2SpriteContainer.setStyle("-fx-background-color: #FF0000;");
		}
		else
		{
			p2SpriteContainer.setStyle("-fx-background-color: #FFFFFF;");
		}
	}
}