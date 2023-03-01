package arena.core;

import arena.core.GameState.EntityType;

abstract class Entity
{
	private int x;
	private int y;
	private boolean destroyed;
	final Map map;
	
	Entity(Map map, int x, int y)
	{
		this.map = map;
		this.x = x;
		this.y = y;
	}
	
	final void destroy()
	{
		map.destroy(this);
		destroyed = true;
	}
	
	void onUpdate() {}
	void onCollided(Wall wall) {}
	void onCollided(Player player) {}
	void onCollided(Projectile projectile) {}
	void onCollided(HealthPack healthPack) {}
	void onCollided(Mine mine) {}
	void onCollided(Storm storm) {}
	
	final void onCollidedGeneric(Entity entity)
	{
		if(entity instanceof Wall)
			onCollided((Wall)entity);
		else if(entity instanceof Player)
			onCollided((Player)entity);
		else if(entity instanceof Projectile)
			onCollided((Projectile)entity);
		else if(entity instanceof HealthPack)
			onCollided((HealthPack)entity);
		else if(entity instanceof Mine)
			onCollided((Mine)entity);
		else if(entity instanceof Storm)
			onCollided((Storm)entity);
		else
			throw new IllegalStateException("Unknown entry in collision matrix.");
	}
	
	
	//*******************GAGE ADDED FOR FILE I/O STUFF***************************
	//[TODO]
	static final EntityType getEntityType(Entity entity)
	{
		if(entity instanceof Wall)
			return EntityType.Wall;
		else if(entity instanceof Player)
			return EntityType.Player;
		else if(entity instanceof Projectile)
			return EntityType.Projectile;
		else if(entity instanceof HealthPack)
			return EntityType.HealthPack;
		else if(entity instanceof Mine)
			return EntityType.Mine;
		else if(entity instanceof Storm)
			return EntityType.Storm;
		else
			return EntityType.Empty;
			//throw new IllegalStateException("Unknown entry in collision matrix.");
	}
	
	//*************************************************************************
	
	

	final int getX()
	{
		return x;
	}
	
	final int getY()
	{
		return y;
	}
	
	final boolean isDestroyed()
	{
		return destroyed;
	}

	final void setX(int x)
	{
		this.x = Math.max(0, Math.min(map.getWidth() - 1, x));
	}

	final void setY(int y)
	{
		this.y = Math.max(0, Math.min(map.getHeight() - 1, y));
	}
}