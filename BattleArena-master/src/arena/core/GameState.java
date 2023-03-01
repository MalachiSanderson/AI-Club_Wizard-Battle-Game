package arena.core;

import java.util.ArrayList;
import java.util.List;

import arena.core.GameState.EntityType;

/**
 * The <code>GameState</code> class represents a snapshot of the environment at a specific round. It contains crucial information about
 * everything going on in the world at that point, including: position of players, storm size, and access to the entire map for processing.
 * 
 * @author ERAU AI Club
 */
public final class GameState
{
	/**
	 * The <code>EntityType</code> enum represents a set of possible types an entity in the environment may be. Ranging from {@link #Empty} to {@link #Player} to {@link #HealthPack}, and much more.
	 * 
	 * @author ERAU AI Club
	 */
	public enum EntityType
	{
		/** There is no entity. */
		Empty,
		
		/** The entity is a wall, impassable by projectiles, players, and mines. */
		Wall,
		
		/** The entity is a AI-controlled player that interacts with the environment by moving, shooting, and placing mines. */
		Player,
		
		/** The entity is a mine. Explodes and hurt any {@link #Player} that comes in contact with it by 1 health point. Also, explodes when in contact with any {@link #Projectile} or {@link #Storm}, destroying both in the process. */
		Mine,
		
		/** The entity is a projectile. Hurts hostile {@link #Player} by 1 health point. Also, projectiles can be destroyed when in contact with any other {@link #Mine} or {@link #Projectile}. */
		Projectile,
		
		/** The entity is a health pack. Any {@link #Player} that comes in contact with it gains 1 health point. Also, health packs can be destroyed when in contact with any other {@link #Mine} or {@link #Projectile}. */
		HealthPack,
		
		/** The entity is a storm. Instantly kills any {@link #Player} when in contact. Also, destroys everything it touches, including: {@link #Wall}, {@link #Mine}, {@link #Projectile}, and {@link #HealthPack}. */
		Storm
	}
	
	/**
	 * The <code>Direction</code> enum represents a set of 4-way directions.
	 * 
	 * <p>
	 * Possible values include:
	 * </p>
	 * <ul>
	 * <li>{@link #Up}</li>
	 * <li>{@link #Down}</li>
	 * <li>{@link #Left}</li>
	 * <li>{@link #Right}</li>
	 * </ul> 
	 * 
	 * @author ERAU AI Club
	 */
	public enum Direction
	{
		/** The direction is up. That means (x=0, y=-1).*/
		Up,
		
		/** The direction is down. That means (x=0, y=1).*/
		Down,
		
		/** The direction is left. That means (x=-1, y=0).*/
		Left,
		
		/** The direction is right. That means (x=1, y=0).*/
		Right
	}
	
	/**
	 *The <code>ProjectileData</code> represents a snapshot of a projectile's data such as position and movement direction.
	 * 
	 * 
	 * @author ERAU AI Club
	 */
	public class ProjectileData
	{
		private final int x;
		private final int y;
		private final Direction moveDirection;
		
		ProjectileData(Projectile projectile)
		{
			x = projectile.getX();
			y = projectile.getY();
			int dirX = projectile.getSpeedX();
			int dirY = projectile.getSpeedY();
			if(dirX > 0)
				moveDirection = Direction.Right;
			else if(dirX < 0)
				moveDirection = Direction.Left;
			else if(dirY > 0)
				moveDirection = Direction.Down;
			else
				moveDirection = Direction.Up;
		}

		/**
		 * Returns the projectile's x-position. The x-position is a value from 0 (left-most) to <code>{@link #getMapWidth()}-1</code> (right-most).
		 * 
		 * @return the projectile's x-position
		 */
		public int getX()
		{
			return x;
		}

		/**
		 * Returns the projectile's y-position. The y-position is a value from 0 (top-most) to <code>{@link #getMapHeight()}-1</code> (bottom-most).
		 * 
		 * @return the projectile's y-position
		 */
		public int getY()
		{
			return y;
		}

		/**
		 * Returns the projectile's movement direction. See {@link Direction} for all possible values.
		 * 
		 * @return the projectile's movement direction
		 */
		public Direction getMoveDirection()
		{
			return moveDirection;
		}

		@Override
		public String toString()
		{
			return "ProjectileData [x=" + x + ", y=" + y + ", moveDirection=" + moveDirection + "]";
		}
	}
	
	private final Player player;
	private Player otherPlayer;
	private final Map map;
	private final EntityType[][] visualMap;
	private final Game game;
	
	GameState(Game game, Player player, Player otherPlayer)
	{
		this.game = game;
		map = player.map;
		this.player = player;
		this.otherPlayer = otherPlayer;
		
		visualMap = new EntityType[map.getWidth()][map.getHeight()];
		constructVisualMap();
	}
	
	private final void constructVisualMap()
	{
		for(int y = 0; y < map.getHeight(); y++)
		{
			for(int x = 0; x < map.getWidth(); x++)
			{
				visualMap[x][y] = getEntityType(x, y); 
			}
		}
	}
	
	private final EntityType getEntityType(int x, int y)
	{
		Entity entity = map.getEntity(x, y);
		if(entity == null)
			return EntityType.Empty;
		
		if(entity instanceof Wall)
			return EntityType.Wall;
		
		if(entity instanceof Player)
			return EntityType.Player;
		
		if(entity instanceof Mine)
			return EntityType.Mine;
		
		if(entity instanceof HealthPack)
			return EntityType.HealthPack;
		
		if(entity instanceof Projectile)
			return EntityType.Projectile;
		
		if(entity instanceof Storm)
			return EntityType.Storm;
		
		return EntityType.Empty;
	}
	
	/**
	 * Returns an array of all <b>hostile</b> projectiles in the map. See {@link ProjectileData} for the structure of the projectile data.
	 * 
	 * @return an array of all hostile projectiles in the map
	 */
	public final ProjectileData[] getProjectileDatas()
	{
		List<ProjectileData> data = new ArrayList<>();
		
		for(Entity entity : map.getEntities())
		{
			if(entity instanceof Projectile)
			{
				Projectile projectile = (Projectile) entity;
				
				// Ignore friendly projectiles
				if(projectile.isOwner(player)) continue;
				
				data.add(new ProjectileData(projectile));
			}
		}
		
		// Convert to array
		return data.toArray(new ProjectileData[data.size()]);
	}
	
	//---------------------------------------------------------------------------------------------------------------------------------------
	//[TODO]
	/** 
	 * Returns an array of all <b>non-hostile</b> projectiles in the map. See {@link ProjectileData} for the structure of the projectile data.
     * 
     * @return an array of all non-hostile projectiles in the map
     */
    public final ProjectileData[] getSelfProjectileDatas()
    {
        List<ProjectileData> data = new ArrayList<>();
        
        for(Entity entity : map.getEntities())
        {
            if(entity instanceof Projectile)
            {
                Projectile projectile = (Projectile) entity;
                
                // Ignore enemy projectiles
                if(projectile.isOwner(otherPlayer)) continue;
                
                data.add(new ProjectileData(projectile));
            }
        }
        
        // Convert to array
        return data.toArray(new ProjectileData[data.size()]);
    }
    
    
    /**
     * 
     * The position is zero-based, meaning the the minimum coordinate is <code>(0, 0)</code> (top-left) and the maximum coordinate is <code>({@link #getMapWidth()}-1, {@link #getMapHeight()}-1)</code> (bottom-right).
	 *  
	 * <p> <b>Returns null if the coordinate has no projectile.</b> Thus, you should only use this to grab the {@link #ProjectileData } for a KNOWN projectile!</p>
	 * <p> Otherwise, returns the respective projectile's projectile data which contains a projectile's position and move direction data.</p>
	 * 
	 * Example Use:
	 * <pre> 
	 * Vector2 temp = gu.findNearest(x, y, EntityType.Projectile);
	 * 
	 * if(temp != null) //confirming that this projectile does exist.
	 * {
	 *	System.out.println("Projectile found moving: "+gs.getProjectileAtPosition(temp.getX(), temp.getY()).getMoveDirection());
	 * }
	 * </pre>
	 * @param x - a zero-based x-position
	 * @param y - a zero-based y-position
	 * 
	 * @throws OutOfBoundsException when the position is out of bounds
     * @return projectile at the specified location.
     */
    public final ProjectileData getProjectileAtPosition(int x, int y) throws OutOfBoundsException
    {
    	if(visualMap[x][y].equals(EntityType.Projectile))
    	{
    		return new ProjectileData( (Projectile) map.getEntity(x, y));
    	}
    	return null;
    }
    
    
    
    /**
     * 
     * The position is zero-based, meaning the the minimum coordinate is <code>(0, 0)</code> (top-left) and the maximum coordinate is <code>({@link #getMapWidth()}-1, {@link #getMapHeight()}-1)</code> (bottom-right).
	 *  
	 * <p> <b>Returns null if the coordinate has no projectile.</b> Thus, you should only use this to grab the {@link #ProjectileData} for a KNOWN projectile!</p>
	 * <p> Otherwise, returns the respective projectile's projectile data which contains a projectile's position and move direction data.</p>
	 * 
	 * Example Use:
	 * <pre> 
	 * Vector2 temp = gu.findNearest(x, y, EntityType.Projectile);
	 * 
	 * if(temp != null) //confirming that this projectile does exist.
	 * {
	 *	System.out.println("Projectile found moving: "+gs.getProjectileAtPosition(temp).getMoveDirection());
	 * }
	 * </pre>
	 * @param projectilePosition - the zero-based (x,y) vector position.
	 * @throws OutOfBoundsException when the position is out of bounds
     * @return projectile at the specified location.
     */
    public final ProjectileData getProjectileAtPosition(Vector2 projectilePosition) throws OutOfBoundsException
    {
    	if(visualMap[projectilePosition.getX()][projectilePosition.getY()].equals(EntityType.Projectile))
    	{
    		return new ProjectileData( (Projectile) map.getEntity(projectilePosition.getX(), projectilePosition.getY()));
    	}
    	return null;
    }
    
    
    //------------------------------------------------------------------------------------------------------------------------------------------
	
	
	/**
	 * Returns true if the tile at the specified position is out of the map bounds.
	 * 
	 * <p>
	 * The position is zero-based, meaning the the minimum coordinate is <code>(0, 0)</code> (top-left) and the maximum coordinate is <code>({@link #getMapWidth()}-1, {@link #getMapHeight()}-1)</code> (bottom-right).
	 * </p>
	 * 
	 * @param x - a zero-based x-position
	 * @param y - a zero-based y-position
	 * 
	 * @return true if the specified position is out of bounds, false otherwise.
	 */
	public final boolean isOutOfBounds(int x, int y)
	{
		return (x < 0 || y < 0 || x >= getMapWidth() || y >= getMapHeight());
	}
	
	/**
	 * Returns the entity type at the specified position.
	 * The position is zero-based, meaning the the minimum coordinate is <code>(0, 0)</code> (top-left) and the maximum coordinate is <code>({@link #getMapWidth()}-1, {@link #getMapHeight()}-1)</code> (bottom-right).
	 *  
	 * <p>Returns {@link EntityType#Empty} if the coordinate  has no entity at the specified coordinate. Otherwise, returns the respective entity as defined by the set of values in {@link EntityType}.</p>
	 * 
	 * @param x - a zero-based x-position
	 * @param y - a zero-based y-position
	 * 
	 * @throws OutOfBoundsException when the position is out of bounds
	 * 
	 * @return the entity at the specified position
	 */
	public final EntityType getEntityAt(int x, int y) throws OutOfBoundsException
	{
		if(isOutOfBounds(x, y))
			throw new OutOfBoundsException(x, y);
		
		return visualMap[x][y];
	}
	
	/**
	 * Returns the width of the map. This represents the number of columns in the world.
	 * 
	 * @return width of the map
	 */
	public final int getMapWidth()
	{
		return map.getWidth();
	}
	
	/**
	 * Returns the height of the map. This represents the number of rows in the world.
	 * 
	 * @return height of the map
	 */
	public final int getMapHeight()
	{
		return map.getHeight();
	}
	
	/**
	 * Returns a 2-dimensional array of the map populated with {@link EntityType} for each position, where the 2D index is the position.
	 * This contains all entities in the map at this point in time. The size of the array is {@link #getMapWidth()} by {@link #getMapHeight()}.
	 * 
	 * <p>
	 * For example, to get which entity is at coordinate <code>(3, 4)</code>, one would do:
	 * </p>
	 * <pre>
	 * EntityType[][] map = gameState.getMap();
	 * EntityType entity = map[3][4];
	 * </pre>
	 * 
	 * @return a 2D map of the entities
	 */
	public final EntityType[][] getMap()
	{
		return visualMap;
	}
	
	/**
	 * Returns the opponent's health points. The health is a value from 0 (dead) to 5 (maximum).
	 * 
	 * @return the opponent's health
	 */
	public final int getOpponentHealth()
	{
		if(otherPlayer != null)
			return otherPlayer.getHealth();
		return -1;
	}
	
	/**
	 * Returns the opponent's x-position. The x-position is a value from 0 (left-most) to <code>{@link #getMapWidth()}-1</code> (right-most).
	 * 
	 * @return the opponent's x-position
	 */
	public final int getOpponentX()
	{
		if(otherPlayer != null)
			return otherPlayer.getX();
		return -1;
	}
	
	/**
	 * Returns the opponent's y-position. The y-position is a value from 0 (top) to <code>{@link #getMapHeight()}-1</code> (bottom).
	 * 
	 * @return the opponent's y-position
	 */
	public final int getOpponentY()
	{
		if(otherPlayer != null)
			return otherPlayer.getY();
		return -1;
	}
	
	/**
	 * Returns the player's health points. The health is a value from 0 (dead) to 5 (maximum).
	 * 
	 * @return the player's health
	 */
	public final int getPlayerHealth()
	{
		return player.getHealth();
	}
	
	/**
	 * Returns the maximum health possible for a {@link Player} entity as defined by {@link Player#HEALTH_MAX}.
	 * 
	 * @return maximum player health
	 */
	public final int getPlayerMaxHealth()
	{
		return Player.HEALTH_MAX;
	}
	
	/**
	 * Returns the player's x-position. The x-position is a value from 0 (left-most) to <code>{@link #getMapWidth()}-1</code> (right-most).
	 * 
	 * @return the player's x-position
	 */
	public final int getPlayerX()
	{
		return player.getX();
	}
	
	/**
	 * Returns the player's y-position. The y-position is a value from 0 (top) to <code>{@link #getMapHeight()}-1</code> (bottom).
	 * 
	 * @return the player's y-position
	 */
	public final int getPlayerY()
	{
		return player.getY();
	}
	
	/**
	 * Checks if the coordinate specified contains no entities ({@link EntityType#Empty}).
	 * 
	 * <p>
	 * The position is zero-based, meaning the the minimum coordinate is <code>(0, 0)</code> (top-left) and the maximum coordinate is <code>({@link #getMapWidth()}-1, {@link #getMapHeight()}-1)</code> (bottom-right).
	 * </p>
	 * 
	 * @param x - zero-based x-position
	 * @param y - zero-based y-position
	 * 
	 * @throws OutOfBoundsException when the position is out of bounds
	 * 
	 * @return true if empty, false otherwise
	 */
	public final boolean isEmpty(int x, int y) throws OutOfBoundsException
	{
		if(isOutOfBounds(x, y))
			throw new OutOfBoundsException(x, y);
		
		return map.isEmpty(x, y);
	}
	
	/**
	 * Checks if the coordinate specified contains a wall ({@link EntityType#Wall}).
	 * 
	 * <p>
	 * The position is zero-based, meaning the the minimum coordinate is <code>(0, 0)</code> (top-left) and the maximum coordinate is <code>({@link #getMapWidth()}-1, {@link #getMapHeight()}-1)</code> (bottom-right).
	 * </p>
	 * 
	 * @param x - zero-based x-position
	 * @param y - zero-based y-position
	 * 
	 * @throws OutOfBoundsException when the position is out of bounds
	 * 
	 * @return true if contains a wall, false otherwise
	 */
	public final boolean isWall(int x, int y) throws OutOfBoundsException
	{
		if(isOutOfBounds(x, y))
			throw new OutOfBoundsException(x, y);
		
		return map.getEntity(x, y) instanceof Wall;
	}
	
	/**
	 * Checks if the coordinate specified contains the storm ({@link EntityType#Storm}).
	 * 
	 * <p>
	 * The position is zero-based, meaning the the minimum coordinate is <code>(0, 0)</code> (top-left) and the maximum coordinate is <code>({@link #getMapWidth()}-1, {@link #getMapHeight()}-1)</code> (bottom-right).
	 * </p>
	 * 
	 * @param x - zero-based x-position
	 * @param y - zero-based y-position
	 * 
	 * @throws OutOfBoundsException when the position is out of bounds
	 * 
	 * @return true if contains the storm, false otherwise
	 */
	public final boolean isStorm(int x, int y) throws OutOfBoundsException
	{
		if(isOutOfBounds(x, y))
			throw new OutOfBoundsException(x, y);
		
		return map.getEntity(x, y) instanceof Storm;
	}
	
	/**
	 * Checks if the coordinate specified contains any projectile ({@link EntityType#Projectile}).
	 * 
	 * <p>
	 * The position is zero-based, meaning the the minimum coordinate is <code>(0, 0)</code> (top-left) and the maximum coordinate is <code>({@link #getMapWidth()}-1, {@link #getMapHeight()}-1)</code> (bottom-right).
	 * </p>
	 * 
	 * @param x - zero-based x-position
	 * @param y - zero-based y-position
	 * 
	 * @throws OutOfBoundsException when the position is out of bounds
	 * 
	 * @return true if contains any projectile, false otherwise
	 * 
	 * @see #isFriendlyProjectile(int, int)
	 * @see #isHostileProjectile(int, int)
	 */
	public final boolean isProjectile(int x, int y) throws OutOfBoundsException
	{
		if(isOutOfBounds(x, y))
			throw new OutOfBoundsException(x, y);
		
		return map.getEntity(x, y) instanceof Projectile;
	}
	
	/**
	 * Checks if the coordinate specified contains a <b>hostile</b> projectile ({@link EntityType#Projectile}).
	 * 
	 * <p>
	 * The position is zero-based, meaning the the minimum coordinate is <code>(0, 0)</code> (top-left) and the maximum coordinate is <code>({@link #getMapWidth()}-1, {@link #getMapHeight()}-1)</code> (bottom-right).
	 * </p>
	 * 
	 * @param x - zero-based x-position
	 * @param y - zero-based y-position
	 * 
	 * @throws OutOfBoundsException when the position is out of bounds
	 * 
	 * @return true if contains a hostile projectile, false otherwise
	 * 
	 * @see #isFriendlyProjectile(int, int)
	 * @see #isProjectile(int, int)
	 */
	public final boolean isHostileProjectile(int x, int y) throws OutOfBoundsException
	{
		if(isOutOfBounds(x, y))
			throw new OutOfBoundsException(x, y);
		
		if(!isProjectile(x, y))
			return false;
		
		Projectile projectile = (Projectile) map.getEntity(x, y);
		return !projectile.isOwner(player);
	}
	
	/**
	 * Checks if the coordinate specified contains a <b>friendly</b> projectile ({@link EntityType#Projectile}).
	 * 
	 * <p>
	 * The position is zero-based, meaning the the minimum coordinate is <code>(0, 0)</code> (top-left) and the maximum coordinate is <code>({@link #getMapWidth()}-1, {@link #getMapHeight()}-1)</code> (bottom-right).
	 * </p>
	 * 
	 * @param x - zero-based x-position
	 * @param y - zero-based y-position
	 * 
	 * @throws OutOfBoundsException when the position is out of bounds
	 * 
	 * @return true if contains a friendly projectile, false otherwise
	 * 
	 * @see #isHostileProjectile(int, int)
	 * @see #isProjectile(int, int)
	 */
	public final boolean isFriendlyProjectile(int x, int y) throws OutOfBoundsException
	{
		if(isOutOfBounds(x, y))
			throw new OutOfBoundsException(x, y);
		
		if(!isProjectile(x, y))
			return false;
		
		Projectile projectile = (Projectile) map.getEntity(x, y);
		return projectile.isOwner(player);
	}
	
	/**
	 * Checks if the coordinate specified contains a mine ({@link EntityType#Mine}).
	 * 
	 * <p>
	 * The position is zero-based, meaning the the minimum coordinate is <code>(0, 0)</code> (top-left) and the maximum coordinate is <code>({@link #getMapWidth()}-1, {@link #getMapHeight()}-1)</code> (bottom-right).
	 * </p>
	 * 
	 * @param x - zero-based x-position
	 * @param y - zero-based y-position
	 * 
	 * @throws OutOfBoundsException when the position is out of bounds
	 * 
	 * @return true if contains a mine, false otherwise
	 */
	public final boolean isMine(int x, int y) throws OutOfBoundsException
	{
		if(isOutOfBounds(x, y))
			throw new OutOfBoundsException(x, y);
		
		return map.getEntity(x, y) instanceof Mine;
	}
	
	/**
	 * Checks if the coordinate specified contains a health pack ({@link EntityType#HealthPack}).
	 * 
	 * <p>
	 * The position is zero-based, meaning the the minimum coordinate is <code>(0, 0)</code> (top-left) and the maximum coordinate is <code>({@link #getMapWidth()}-1, {@link #getMapHeight()}-1)</code> (bottom-right).
	 * </p>
	 * 
	 * @param x - zero-based x-position
	 * @param y - zero-based y-position
	 * 
	 * @throws OutOfBoundsException when the position is out of bounds
	 * 
	 * @return true if contains a health pack, false otherwise
	 */
	public final boolean isHealthPack(int x, int y) throws OutOfBoundsException
	{
		if(isOutOfBounds(x, y))
			throw new OutOfBoundsException(x, y);
		
		return map.getEntity(x, y) instanceof HealthPack;
	}
	
	/**
	 * Checks if the coordinate specified contains any player ({@link EntityType#Player}).
	 * 
	 * <p>
	 * The position is zero-based, meaning the the minimum coordinate is <code>(0, 0)</code> (top-left) and the maximum coordinate is <code>({@link #getMapWidth()}-1, {@link #getMapHeight()}-1)</code> (bottom-right).
	 * </p>
	 * 
	 * @param x - zero-based x-position
	 * @param y - zero-based y-position
	 * 
	 * @throws OutOfBoundsException when the position is out of bounds
	 * 
	 * @return true if contains any player, false otherwise
	 * 
	 * @see #isFriendlyPlayer(int, int)
	 * @see #isHostilePlayer(int, int)
	 */
	public final boolean isPlayer(int x, int y) throws OutOfBoundsException
	{
		if(isOutOfBounds(x, y))
			throw new OutOfBoundsException(x, y);
		
		return map.getEntity(x, y) instanceof Player;
	}
	
	/**
	 * Checks if the coordinate specified contains a hostile player ({@link EntityType#Player}).
	 * 
	 * <p>
	 * The position is zero-based, meaning the the minimum coordinate is <code>(0, 0)</code> (top-left) and the maximum coordinate is <code>({@link #getMapWidth()}-1, {@link #getMapHeight()}-1)</code> (bottom-right).
	 * </p>
	 * 
	 * @param x - zero-based x-position
	 * @param y - zero-based y-position
	 * 
	 * @throws OutOfBoundsException when the position is out of bounds
	 * 
	 * @return true if contains a hostile player, false otherwise
	 * 
	 * @see #isPlayer(int, int)
	 * @see #isFriendlyPlayer(int, int)
	 */
	public final boolean isHostilePlayer(int x, int y) throws OutOfBoundsException
	{
		if(isOutOfBounds(x, y))
			throw new OutOfBoundsException(x, y);
		
		if(!isPlayer(x, y))
			return false;
		
		Player player = (Player)map.getEntity(x, y);
		return player.equals(this.otherPlayer);
	}
	
	/**
	 * Checks if the coordinate specified contains a friendly player ({@link EntityType#Player}).
	 * 
	 * <p>
	 * The position is zero-based, meaning the the minimum coordinate is <code>(0, 0)</code> (top-left) and the maximum coordinate is <code>({@link #getMapWidth()}-1, {@link #getMapHeight()}-1)</code> (bottom-right).
	 * </p>
	 * 
	 * @param x - zero-based x-position
	 * @param y - zero-based y-position
	 * 
	 * @throws OutOfBoundsException when the position is out of bounds
	 * 
	 * @return true if contains a friendly player, false otherwise
	 * 
	 * @see #isPlayer(int, int)
	 * @see #isHostilePlayer(int, int)
	 */
	public final boolean isFriendlyPlayer(int x, int y) throws OutOfBoundsException
	{
		if(isOutOfBounds(x, y))
			throw new OutOfBoundsException(x, y);
		
		if(!isPlayer(x, y))
			return false;
		
		Player player = (Player)map.getEntity(x, y);
		return player.equals(this.player);
	}
	
	/**
	 * Returns the current storm size. It is the number of diagonal blocks from the map's edge.
	 * 
	 * @return the storm size
	 */
	public final int getStormSize()
	{
		return map.getStormSize();
	}
	
	/**
	 * Returns the maximum size the storm will get to. It is the number of diagonal blocks from the map's edge.
	 * 
	 * @return the maximum storm size
	 */
	public final int getStormMaxSize()
	{
		return map.getStormMaxSize();
	}
	
	/**
	 * Returns the number of rounds before the next storm advancement.
	 * 
	 * @see #getRound()
	 * 
	 * @return number of rounds
	 */
	public final int getRoundsTillNextStormAdvance()
	{
		return game.getRoundsTillNextStormAdvance();
	}
	
	/**
	 * Returns the current round.
	 * 
	 * @return current round
	 */
	public final int getRound()
	{
		return game.getRound();
	}
	
	/**
	 * Returns the maximum round before the game ends as a tie.
	 * 
	 * @return maximum round
	 */
	public final int getMaxRound()
	{
		return Game.MAX_ROUNDS;
	}
	
	/**
	 * Returns true if the player can shoot a projectile, false otherwise.
	 * Note that the player can only shoot 1 projectile per 3 rounds.
	 * 
	 * @return true if can shoot projectile, false otherwise
	 */
	public final boolean canShoot()
	{
		return player.canShoot();
	}
	
	/**
	 * Returns the amount of rounds before the player can shoot a projectile.
	 * Note that the player can only shoot 1 projectile per 3 rounds.
	 * 
	 * @return amount of rounds before can shoot a projectile
	 */
	public final int getCurrentShootCooldown()
	{
		return player.getShootCooldown();
	}
	
	/**
	 * Returns true if the player can place a mine, false otherwise.
	 * Note that the player can only place 1 mine per 10 rounds.
	 * 
	 * @return true if can place mine, false otherwise
	 */
	public final boolean canPlaceMine()
	{
		return player.canPlaceMine();
	}
	
	/**
	 * Returns the amount of rounds before the player can place a mine.
	 * Note that the player can only place 1 mine per 10 rounds.
	 * 
	 * @return amount of rounds before can place a mine
	 */
	public final int getCurrentMineCooldown()
	{
		return player.getPlaceMineCooldown();
	}
}