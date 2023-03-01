package arena.core;

import javafx.scene.paint.Color;

final class Projectile extends Entity
{
	private final int speedX;
	private final int speedY;
	private final Player player;
	private final Color color;
	
	Projectile(Map map, int x, int y, int speedX, int speedY, Player player)
	{
		super(map, x, y);
		this.player = player;
		this.speedX = speedX;
		this.speedY = speedY;
		color = player.getColor();
	}
	
	boolean isOwner(Player player)
	{
		return this.player.equals(player);
	}
	
	@Override
	protected void onUpdate()
	{
		int oldX = getX();
		int oldY = getY();
		int newX = oldX + speedX;
		int newY = oldY + speedY;
		
		Entity destinationEntity = map.getEntity(newX, newY);
		
		setX(getX() + speedX);
		setY(getY() + speedY);
		
		// Didnt move -> destroy
		if(oldX == getX() && oldY == getY())
		{
			destroy();
		}
		// Moved -> check for collisions in between
		else
		{
			if(destinationEntity != null)
				onCollidedGeneric(destinationEntity);
		}
	}
	
	final Color getColor()
	{
		return color;
	}

	@Override
	protected void onCollided(HealthPack healthPack)
	{
		player.setHealth(player.getHealth() + 1);
		destroy();
		healthPack.destroy();
	}
	
	@Override
	protected void onCollided(Mine mine)
	{
		destroy();
		mine.destroy();
	}
	
	@Override
	protected void onCollided(Projectile projectile)
	{
		destroy();
		projectile.destroy();
	}
	
	@Override
	protected void onCollided(Player player)
	{
		// Ignore collision with owner player
		if(player == this.player)
			return;
		
		destroy();
	}
	
	@Override
	protected void onCollided(Wall wall)
	{
		destroy();
	}
	
	@Override
	void onCollided(Storm storm)
	{
		destroy();
	}

	int getSpeedX()
	{
		return speedX;
	}

	int getSpeedY()
	{
		return speedY;
	}
}