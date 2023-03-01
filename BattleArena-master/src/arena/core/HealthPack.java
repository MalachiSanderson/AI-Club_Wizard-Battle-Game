package arena.core;

final class HealthPack extends Entity
{
	HealthPack(Map map, int x, int y)
	{
		super(map, x, y);
	}
	
	@Override
	protected void onCollided(Player player)
	{
		// Destroy health pack when colliding with player
		destroy();
	}
	
	@Override
	protected void onCollided(Projectile projectile)
	{
		// Destroy health pack when colliding with projectile
		destroy();
		projectile.destroy();
	}
	
	@Override
	void onCollided(Wall wall)
	{
		destroy();
	}
	
	@Override
	void onCollided(Storm storm)
	{
		destroy();
	}
	
	@Override
	void onCollided(Mine mine)
	{
		destroy();
		mine.destroy();
	}
}