package arena.core;

final class Mine extends Entity
{
	Mine(Map map, int x, int y)
	{
		super(map, x, y);
	}
	
	@Override
	protected void onCollided(Player player)
	{
		// Destroy mine when colliding with player
		destroy();
	}
	
	@Override
	protected void onCollided(Projectile projectile)
	{
		// Destroy mine when colliding with projectile
		destroy();
		projectile.destroy();
	}
	
	@Override
	void onCollided(Storm storm)
	{
		destroy();
	}
	
	@Override
	void onCollided(Wall wall)
	{
		destroy();
	}
}