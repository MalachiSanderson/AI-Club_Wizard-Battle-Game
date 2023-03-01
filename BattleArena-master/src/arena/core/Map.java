package arena.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javafx.scene.paint.Color;

final class Map
{
	private final int width;
	private final int height;
	private final Set<Entity> entities;
	private final Set<Entity> entitiesToRemove;
	private final List<Player> players;
	private int stormSize;
	private final double stormCoverage = 0.65; // 0.65 is how much the storm can advance (0 none, 1 full map)
	private final int stormMaxSize;
	
	Map(int width, int height)
	{
		if(width <= 0)
			throw new IllegalArgumentException("Width not in range [1..inf]: " + width);
		
		if(height <= 0)
			throw new IllegalArgumentException("Height not in range [1..inf]: " + width);
		
		this.width = width;
		this.height = height;
		entities = new HashSet<>();
		entitiesToRemove = new HashSet<>();
		players = new ArrayList<>();
		stormSize = 0;
		stormMaxSize = (int) Math.max(1, (int)Math.sqrt(width * height / 2) * stormCoverage);
	}
	
	final void clear()
	{
		entitiesToRemove.clear();
		entities.clear();
		players.clear();
		stormSize = 0;
	}
	
	final void generateWalls(double wallDensity) // wallDensity = [0, 1]
	{
		int centerX = width / 2;
		int centerY = height / 2;
		int centerSize = 2;
		
		for(int y = 0; y < height; y++)
		{
			for(int x = 0; x < width / 2; x++)
			{
				// Ignore center
				if(	x >= centerX - centerSize &&
					x <= centerX + centerSize &&
					y >= centerY - centerSize &&
					y <= centerY + centerSize)
					continue;
				
				if(Math.random() < wallDensity)
				{
					addWall(x, y);
					addWall(width - x - 1, y); // vertical symmetry
				}
			}
		}
	}
	
	final void generateHealthPacks(double density) // density = [0, 1]
	{
		for(int y = 0; y < height; y++)
		{
			for(int x = 0; x < width / 2; x++)
			{
				if(Math.random() < density)
				{
					addHealthPack(x, y);
					addHealthPack(width - x - 1, y); // vertical symmetry
				}
			}
		}
	}
	
	final int getWidth()
	{
		return width;
	}

	final int getHeight()
	{
		return height;
	}

	final Wall addWall(int x, int y)
	{
		if(!canAddWall(x, y))
			return null;
		
		Wall wall = new Wall(this, x, y);
		entities.add(wall);
		return wall;
	}
	
	final boolean canAddWall(int x, int y)
	{
		if(!isWithinBounds(x, y))
			return false;
		
		Entity destEntity = getEntity(x, y);
		if(destEntity instanceof Wall || destEntity instanceof Storm)
			return false;
		
		return isEmpty(x, y);
	}
	
	final Player addPlayer(int x, int y, String name, String className, Color color)
	{
		if(!canAddPlayer(x, y))
			return null;
		
		Player player = new Player(this, x, y, name, className, color);
		entities.add(player);
		players.add(player);
		
		return player;
	}
	
	final boolean canAddPlayer(int x, int y)
	{
		if(!isWithinBounds(x, y))
			return false;
		
		Entity destEntity = getEntity(x, y);
		if(destEntity instanceof Wall || destEntity instanceof Storm)
			return false;
		
		return isEmpty(x, y);
	}
	
	final Projectile addProjectile(int x, int y, int speedX, int speedY, Player ownerPlayer)
	{
		if(!canAddProjectile(x, y))
			return null;
		
		Projectile projectile = new Projectile(this, x, y, speedX, speedY, ownerPlayer);
		entities.add(projectile);
		return projectile;
	}
	
	final boolean canAddProjectile(int x, int y)
	{
		if(!isWithinBounds(x, y))
			return false;
		
		Entity destEntity = getEntity(x, y);
		if(destEntity instanceof Wall || destEntity instanceof Storm)
			return false;
		
		return true;
	}
	
	final Mine addMine(int x, int y)
	{
		if(!canAddMine(x, y))
			return null;
		
		Mine mine = new Mine(this, x, y);
		entities.add(mine);
		return mine;
	}
	
	final boolean canAddMine(int x, int y)
	{
		if(!isWithinBounds(x, y))
			return false;
		
		Entity destEntity = getEntity(x, y);
		if(destEntity instanceof Wall || destEntity instanceof Storm)
			return false;
		
		return true;
	}
	
	final HealthPack addHealthPack(int x, int y)
	{
		if(!canAddHealthPack(x, y))
			return null;
		
		HealthPack healthPack = new HealthPack(this, x, y);
		entities.add(healthPack);
		return healthPack;
	}
	
	final boolean canAddHealthPack(int x, int y)
	{
		if(!isWithinBounds(x, y))
			return false;
		
		Entity destEntity = getEntity(x, y);
		if(destEntity instanceof Wall || destEntity instanceof Storm)
			return false;
		
		return isEmpty(x, y);
	}
	
	final Storm addStorm(int x, int y)
	{
		if(!isWithinBounds(x, y))
			return null;
		
		Entity entity = getEntity(x, y);
		
		if(entity instanceof Storm)
			return null;
		
		Storm storm = new Storm(this, x, y);
		entities.add(storm);
		
		if(entity != null)
		{
			entity.onCollidedGeneric(storm);
			storm.onCollidedGeneric(entity);
		}
		
		return storm;
	}
	
	final boolean isWithinBounds(int x, int y)
	{
		return 	x >= 0 &&
				y >= 0 &&
				x < width &&
				y < height;
	}
	
	final boolean isEmpty(int x, int y)
	{
		return getEntity(x, y) == null;
	}
	
	final Entity getEntity(int x, int y)
	{
		if(!isWithinBounds(x, y))
			return null;
		
		for(Player player : players)
		{
			if(player.getX() == x && player.getY() == y)
				return player;
		}
		
		for(Entity entity : entities)
		{
			if(entity.getX() == x && entity.getY() == y)
				return entity;
		}
		
		return null;
	}
	
	final void destroy(Entity entity)
	{
		entitiesToRemove.add(entity);
		
		if(entity instanceof Player)
		{
			Iterator<Player> iterator = players.iterator();
			while(iterator.hasNext())
			{
				Player next = iterator.next();
				if(next.equals(entity))
				{
					iterator.remove();
					break;
				}
			}
		}
	}
	
	private final void checkCollisions()
	{
		Entity[] entities = this.entities.toArray(new Entity[0]);
		for(int i = 0; i < entities.length - 1; i++)
		{
			for(int j = i + 1; j < entities.length; j++)
			{
				Entity entity1 = entities[i];
				Entity entity2 = entities[j];
				if(entity1.getX() == entity2.getX() && entity1.getY() == entity2.getY())
				{
					entity1.onCollidedGeneric(entity2);
					entity2.onCollidedGeneric(entity1);
				}
			}
		}
	}
	
	private final void updateAll(Collection<? extends Entity> entities)
	{
		for(Entity entity : entities)
		{
			entity.onUpdate();
		}
	}
	
	private final void deleteRemovedEntities()
	{
		if(entitiesToRemove.size() > 0)
		{
			entities.removeAll(entitiesToRemove);
			entitiesToRemove.clear();
		}
	}
	
	final void tick(Collection<? extends Entity> entities)
	{
		deleteRemovedEntities();
		updateAll(entities);
		checkCollisions();
		deleteRemovedEntities();
	}
	
	final void advanceStorm()
	{
		stormSize = Math.min(stormSize + 1, stormMaxSize);
		
		for(int y = 0; y < height; y++)
		{
			for(int x = 0; x < width; x++)
			{
				if(isWithinStorm(x, y))
				{
					addStorm(x, y);
				}
			}
		}
		
		deleteRemovedEntities();
	}
	
	final int getStormSize()
	{
		return stormSize;
	}
	
	final int getStormMaxSize()
	{
		return stormMaxSize;
	}
	
	final boolean isWithinStorm(int x, int y)
	{
		double minX = Math.min(x + 1, Math.abs(x - width));
		double minY = Math.min(y + 1, Math.abs(y - height));
		double distance = Math.min(minX, minY);
		return distance <= stormSize;
	}

	final Set<Entity> getEntities()
	{
		return entities;
	}
}