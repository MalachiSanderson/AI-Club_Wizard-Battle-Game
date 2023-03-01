package arena.core;

final class Wall extends Entity
{
	Wall(Map map, int x, int y)
	{
		super(map, x, y);
	}
	
	@Override
	final void onCollided(HealthPack healthPack)
	{
		healthPack.destroy();
	}
	
	@Override
	final void onCollided(Mine mine)
	{
		mine.destroy();
	}
	
	@Override
	final void onCollided(Player player)
	{
		player.destroy();
	}
	
	@Override
	final void onCollided(Projectile projectile)
	{
		projectile.destroy();
	}
	
	@Override
	void onCollided(Storm storm)
	{
		destroy();
	}
}