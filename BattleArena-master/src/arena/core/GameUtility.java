package arena.core;

import arena.core.GameState.EntityType;

/**
 * The <code>GameUtility</code> class represents a toolkit of utility functions available to the user. Most are common methods to simplify the lives of developers.
 * Contains methods that handle path-finding, line-of-sight checks, and finding nearest or futher of a certain entity, and much more. This is used in conjuction with {@link GameState}.
 * 
 * @author ERAU AI Club
 */
public final class GameUtility
{
	private final GameState gameState;
	private final Node[][] nodes;
	
	GameUtility(GameState gameState)
	{
		this.gameState = gameState;
		nodes = new Node[gameState.getMapWidth()][gameState.getMapHeight()];
		constructPathfindingNodes();
	}
	
	/**
	 * Returns true if the tile at the specified position is walkable by players and projectiles, false otherwise.
	 * 
	 * <p>
	 * The position is zero-based, meaning the the minimum coordinate is <code>(0, 0)</code> (top-left) and the maximum coordinate is <code>({@link GameState#getMapWidth()}-1, {@link GameState#getMapHeight()}-1)</code> (bottom-right).
	 * </p>
	 * 
	 * @param x - a zero-based x-position
	 * @param y - a zero-based y-position
	 * 
	 * @throws OutOfBoundsException when the position is out of bounds
	 * 
	 * @return true if the tile at the specified position is walked, false otherwise.
	 */
	public final boolean isWalkable(int x, int y) throws OutOfBoundsException
	{
		if(gameState.isOutOfBounds(x, y))
			return false;
		
		EntityType entityType = gameState.getEntityAt(x, y);
		if(entityType == EntityType.Empty)
			return true;
		if(entityType == EntityType.Storm || entityType == EntityType.Wall)
			return false;
		return true;
	}
	//-----------------------------------------------------------------------------------------------------------------
	//[TODO]
	/**
	 * Returns true if the tile at the specified position is walkable by players and projectiles, false otherwise.
	 * 
	 * <p>
	 * The position is zero-based, meaning the the minimum coordinate is <code>(0, 0)</code> (top-left) and the maximum coordinate is <code>({@link GameState#getMapWidth()}-1, {@link GameState#getMapHeight()}-1)</code> (bottom-right).
	 * </p>
	 * 
	 * @param pos - a zero-based Vector2 (x,y) 
	 * 
	 * @throws OutOfBoundsException when the position is out of bounds
	 * 
	 * @return true if the tile at the specified position is walked, false otherwise.
	 */
	public final boolean isWalkable(Vector2 pos) throws OutOfBoundsException
	{
		int x = pos.getX();
		int y = pos.getY();
		if(gameState.isOutOfBounds(x, y))
			return false;
		
		EntityType entityType = gameState.getEntityAt(x, y);
		if(entityType == EntityType.Empty)
			return true;
		if(entityType == EntityType.Storm || entityType == EntityType.Wall)
			return false;
		return true;
	}
	
	
	
	
	
	//----------------------------------------------------------------------------------------------------------------
	
	
	
	
	private final void constructPathfindingNodes()
	{
		for(int y = 0; y < gameState.getMapHeight(); y++)
		{
			for(int x = 0; x < gameState.getMapWidth(); x++)
			{
				nodes[x][y] = new Node(x, y, isWalkable(x, y));
			}
		}
	}
	
	/**
	 * Returns the path as an array of tile positions that are taken to reach the destination position. The path contains, in order, the tiles that it takes to reach the destination.
	 * The first element in the array is the first tile to visit and not the start tile.
	 * 
	 * <p>
	 * Calculates the shortest path from <code>(startX, startY)</code> to <code>(destinationX, destinationY)</code> using the A* path-finding algorithm.
	 * If no path is possible, returns an empty array.
	 * </p>
	 * 
	 * <p>
	 * The positions are zero-based, meaning the the minimum coordinate is <code>(0, 0)</code> (top-left) and the maximum coordinate is <code>({@link GameState#getMapWidth()}-1, {@link GameState#getMapHeight()}-1)</code> (bottom-right).
	 * </p>
	 * 
	 * @param startX - the start zero-based x-position
	 * @param startY - the start zero-based y-position
	 * @param destinationX - the destination zero-based x-position
	 * @param destinationY - the destination zero-based y-position
	 * 
	 * @throws OutOfBoundsException when either position is out of bounds
	 * 
	 * @return a series of vectors of the tiles in the path, from start to destination
	 */
	public final Vector2[] calculatePath(int startX, int startY, int destinationX, int destinationY) throws OutOfBoundsException
	{
		if(gameState.isOutOfBounds(startX, startY))
			throw new OutOfBoundsException(startX, startY);
		
		if(gameState.isOutOfBounds(destinationX, destinationY))
			throw new OutOfBoundsException(destinationX, destinationY);
		
		Node startNode = nodes[startX][startY];
		Node destinationNode = nodes[destinationX][destinationY];
		Node[] pathNodes = AStar.calculatePath(nodes, startNode, destinationNode);
		Vector2[] path = new Vector2[pathNodes.length];
		for(int i = 0; i < path.length; i++)
		{
			Node node = pathNodes[i];
			path[i] = new Vector2(node.getX(), node.getY()); 
		}
		
		return path;
	}
	
	
	//--------------------------------------------------------------------------------------
	//[TODO]
	
	/**
	 * Returns the path as an array of tile positions that are taken to reach the destination position. The path contains, in order, the tiles that it takes to reach the destination.
	 * The first element in the array is the first tile to visit and not the start tile.
	 * 
	 * <p>
	 * Calculates the shortest path from <code>(startX, startY)</code> to <code>(destinationX, destinationY)</code> using the A* path-finding algorithm.
	 * If no path is possible, returns an empty array.
	 * </p>
	 * 
	 * <p>
	 * The positions are zero-based, meaning the the minimum coordinate is <code>(0, 0)</code> (top-left) and the maximum coordinate is <code>({@link GameState#getMapWidth()}-1, {@link GameState#getMapHeight()}-1)</code> (bottom-right).
	 * </p>
	 * 
	 * @param start - the start zero-based Vector2 (x,y)
	 * @param destination - the destination zero-based Vector2 (x,y)
	 * 
	 * @throws OutOfBoundsException when either position is out of bounds
	 * 
	 * @return a series of vectors of the tiles in the path, from start to destination
	 */
	public final Vector2[] calculatePath(Vector2 start, Vector2 destination) throws OutOfBoundsException
	{
		int startX = start.getX();
		int startY = start.getY();
		int destinationX = destination.getX();
		int destinationY = destination.getY();
		if(gameState.isOutOfBounds(startX, startY))
			throw new OutOfBoundsException(startX, startY);
		
		if(gameState.isOutOfBounds(destinationX, destinationY))
			throw new OutOfBoundsException(destinationX, destinationY);
		
		Node startNode = nodes[startX][startY];
		Node destinationNode = nodes[destinationX][destinationY];
		Node[] pathNodes = AStar.calculatePath(nodes, startNode, destinationNode);
		Vector2[] path = new Vector2[pathNodes.length];
		for(int i = 0; i < path.length; i++)
		{
			Node node = pathNodes[i];
			path[i] = new Vector2(node.getX(), node.getY()); 
		}
		
		return path;
	}
	
	
	
	//---------------------------------------------------------------------------------------
	
	/**
	 * Returns true if the tile at the specified position is reachable via path-finding, false otherwise.
	 * 
	 * <p>
	 * The positions are zero-based, meaning the the minimum coordinate is <code>(0, 0)</code> (top-left) and the maximum coordinate is <code>({@link GameState#getMapWidth()}-1, {@link GameState#getMapHeight()}-1)</code> (bottom-right).
	 * </p>
	 * 
	 * @param startX - the start zero-based x-position
	 * @param startY - the start zero-based y-position
	 * @param destinationX - the destination zero-based x-position
	 * @param destinationY - the destination zero-based y-position
	 * 
	 * @throws OutOfBoundsException when either position is out of bounds
	 * 
	 * @return true if the tile at the specified position is reachable, false otherwise.
	 */
	public final boolean isReachable(int startX, int startY, int destinationX, int destinationY) throws OutOfBoundsException
	{
		if(gameState.isOutOfBounds(startX, startY))
			throw new OutOfBoundsException(startX, startY);
		
		if(gameState.isOutOfBounds(destinationX, destinationY))
			throw new OutOfBoundsException(destinationX, destinationY);
		
		return calculatePath(startX, startY, destinationX, destinationY).length > 0;
	}
	
	/**
	 * Returns true if the tile at the specified position is reachable via path-finding, false otherwise.
	 * 
	 * <p>
	 * The position is zero-based, meaning the the minimum coordinate is <code>(0, 0)</code> (top-left) and the maximum coordinate is <code>({@link GameState#getMapWidth()}-1, {@link GameState#getMapHeight()}-1)</code> (bottom-right).
	 * </p>
	 * 
	 * @param x - a zero-based x-position
	 * @param y - a zero-based y-position
	 * 
	 * @return true if the tile at the specified position is reachable, false otherwise.
	 */
	
	/**
	 * Returns true if the start tile has a line of sight towards the destination tile.
	 * 
	 * <p><b>
	 * Note: This method only handles tiles that are in the same row or same column. They <u>need</u> to share a common x-position or y-position.
	 * </b></p> 
	 * 
	 * <p>
	 * The positions are zero-based, meaning the the minimum coordinate is <code>(0, 0)</code> (top-left) and the maximum coordinate is <code>({@link GameState#getMapWidth()}-1, {@link GameState#getMapHeight()}-1)</code> (bottom-right).
	 * </p>
	 * 
	 * @param startX - the start zero-based x-position
	 * @param startY - the start zero-based y-position
	 * @param destinationX - the destination zero-based x-position
	 * @param destinationY - the destination zero-based y-position
	 * 
	 * @throws OutOfBoundsException when either position is out of bounds
	 * 
	 * @return true if the start tile can see without obstacles the destination tile, false otherwise
	 */
	public final boolean haveLineOfSight(int startX, int startY, int destinationX, int destinationY) throws OutOfBoundsException
	{
		if(gameState.isOutOfBounds(startX, startY))
			throw new OutOfBoundsException(startX, startY);
		
		if(gameState.isOutOfBounds(destinationX, destinationY))
			throw new OutOfBoundsException(destinationX, destinationY);
		
		// Check same lane
		if(startX != destinationX && startY != destinationY)
			return false;
		
		int dx = destinationX - startX;
		int dy = destinationY - startY;
		
		if(dx != 0) // x changing
		{
			for(int x = startX; x != destinationX; x += Math.signum(dx))
			{
				if(gameState.isWall(x, startY) || gameState.isStorm(x, startY))
					return false;
			}
			
			return true;
		}
		else if(dy != 0) // y changing
		{
			for(int y = startY; y != destinationY; y += Math.signum(dy))
			{
				if(gameState.isWall(startX, y) || gameState.isStorm(startX, y))
					return false;
			}
			
			return true;
		}
		else // on top of each other
		{
			return false;
		}
	}
	
	/**
	 * Returns an action that moves the player to the specified location; if there is no path, it will return {@link Action#NoAction}.
	 * 
	 * <p>
	 * This uses A* pathfinding to calculate the shortest path from {@link #calculatePath(int, int, int, int)}.
	 * </p>
	 * 
	 * <p>
	 * The position is zero-based, meaning the the minimum coordinate is <code>(0, 0)</code> (top-left) and the maximum coordinate is <code>({@link GameState#getMapWidth()}-1, {@link GameState#getMapHeight()}-1)</code> (bottom-right).
	 * </p>
	 * 
	 * @param destinationX - the destination zero-based x-position
	 * @param destinationY - the destination zero-based y-position
	 * 
	 * @throws OutOfBoundsException when the position is out of bounds
	 * 
	 * @return an action to move the player to the destination location.
	 */
	public final Action moveTowards(int destinationX, int destinationY) throws OutOfBoundsException
	{
		if(gameState.isOutOfBounds(destinationX, destinationY))
			throw new OutOfBoundsException(destinationX, destinationY);
		
		int startX = gameState.getPlayerX();
		int startY = gameState.getPlayerY();
		Vector2[] path = calculatePath(startX, startY, destinationX, destinationY);
		
		if(path.length > 0) // A path exists
		{
			int targetX = path[0].getX();
			int targetY = path[0].getY();
			
			if(targetX > startX)
				return Action.MoveRight;
			else if(targetX < startX)
				return Action.MoveLeft;
			else if(targetY > startY)
				return Action.MoveDown;
			else if(targetY < startY)
				return Action.MoveUp;
			else
				return Action.NoAction;
		}
		else // Unreachable
		{
			return Action.NoAction;
		}
	}
	
	/**
	 * Returns an action that shoots a projectile to the specified location.
	 * 
	 * <p>
	 * The position is zero-based, meaning the the minimum coordinate is <code>(0, 0)</code> (top-left) and the maximum coordinate is <code>({@link GameState#getMapWidth()}-1, {@link GameState#getMapHeight()}-1)</code> (bottom-right).
	 * </p>
	 * 
	 * <p>
	 * Note: The destination position must share either the x or the y position with the player. If this isn't true, the method will return {@link Action#NoAction}.
	 * </p>
	 * 
	 * @param destinationX - the destination zero-based x-position
	 * @param destinationY - the destination zero-based y-position
	 * 
	 * @throws OutOfBoundsException when the position is out of bounds
	 * 
	 * @return an action to shoot a projectile to the destination location.
	 */
	public final Action shootTowards(int destinationX, int destinationY) throws OutOfBoundsException
	{
		if(gameState.isOutOfBounds(destinationX, destinationY))
			throw new OutOfBoundsException(destinationX, destinationY);
		
		int startX = gameState.getPlayerX();
		int startY = gameState.getPlayerY();
		
		if(destinationX > startX)
			return Action.ShootRight;
		else if(destinationX < startX)
			return Action.ShootLeft;
		else if(destinationY > startY)
			return Action.ShootDown;
		else if(destinationY < startY)
			return Action.ShootUp;
		else
			return Action.NoAction;
	}
	
	/**
	 * Returns an action that places a mine towards the specified location. Will return {@link Action#NoAction} if the player cannot place a mine due to its cooldown.
	 * 
	 * <p>
	 * The position is zero-based, meaning the the minimum coordinate is <code>(0, 0)</code> (top-left) and the maximum coordinate is <code>({@link GameState#getMapWidth()}-1, {@link GameState#getMapHeight()}-1)</code> (bottom-right).
	 * </p>
	 * 
	 * <p>
	 * Note: The destination position must share either the x or the y position with the player. If this isn't true, the method will return {@link Action#NoAction}.
	 * </p>
	 * 
	 * @param destinationX - the destination zero-based x-position
	 * @param destinationY - the destination zero-based y-position
	 * 
	 * @throws OutOfBoundsException when the position is out of bounds
	 * 
	 * @return an action to place a mine towards the destination location.
	 */
	public final Action placeMineTowards(int destinationX, int destinationY) throws OutOfBoundsException
	{
		if(gameState.isOutOfBounds(destinationX, destinationY))
			throw new OutOfBoundsException(destinationX, destinationY);

		if(!gameState.canPlaceMine())
			return Action.NoAction;
		
		int startX = gameState.getPlayerX();
		int startY = gameState.getPlayerY();
		
		if(destinationX > startX)
			return Action.PlaceMineRight;
		else if(destinationX < startX)
			return Action.PlaceMineLeft;
		else if(destinationY > startY)
			return Action.PlaceMineDown;
		else if(destinationY < startY)
			return Action.PlaceMineUp;
		else
			return Action.NoAction;
	}
	
	/**
	 * Calculates the manhattan distance between the specified locations.
	 * 
	 * <pre>
	 * Manhattan distance = dx + dy,
	 * 
	 * 	where dx = absolute(destinationX - startX)
	 * 	and   dy = absolute(destinationY - startY)
	 * </pre>
	 * 
	 * <p>
	 * The positions are zero-based, meaning the the minimum coordinate is <code>(0, 0)</code> (top-left) and the maximum coordinate is <code>({@link GameState#getMapWidth()}-1, {@link GameState#getMapHeight()}-1)</code> (bottom-right).
	 * </p>
	 * 
	 * @param startX - the start zero-based x-position
	 * @param startY - the start zero-based y-position
	 * @param destinationX - the destination zero-based x-position
	 * @param destinationY - the destination zero-based y-position
	 * 
	 * @return the manhattan distance between the specified locations
	 */
	public final int manhattanDistance(int startX, int startY, int destinationX, int destinationY)
	{
		int dx = startX - destinationX;
		int dy = startY - destinationY; 
		return Math.abs(dx) + Math.abs(dy);
	}
	
	/**
	 * Calculates the euclidian distance between the specified locations.
	 * 
	 * <pre>
	 * Euclidian distance = sqrt(dx*dx + dy*dy),
	 * 
	 * 	where dx = absolute(destinationX - startX)
	 * 	and   dy = absolute(destinationY - startY)
	 * </pre>
	 * 
	 * <p>
	 * The positions are zero-based, meaning the the minimum coordinate is <code>(0, 0)</code> (top-left) and the maximum coordinate is <code>({@link GameState#getMapWidth()}-1, {@link GameState#getMapHeight()}-1)</code> (bottom-right).
	 * </p>
	 * 
	 * @param startX - the start zero-based x-position
	 * @param startY - the start zero-based y-position
	 * @param destinationX - the destination zero-based x-position
	 * @param destinationY - the destination zero-based y-position
	 * 
	 * @return the manhattan distance between the specified locations
	 */
	public final double euclidianDistance(int startX, int startY, int destinationX, int destinationY)
	{
		int dx = startX - destinationX;
		int dy = startY - destinationY; 
		return Math.sqrt(dx*dx + dy*dy);
	}
	
	/**
	 * Finds and returns the position of the nearest entity as specified by <code>criterion</code>. If no entities are found, returns null.
	 * 
	 * <p>
	 * The following code snippet finds the position of the nearest health pack:
	 * </p>
	 * <pre>
	 * // Assume have reference to gameUtility and gameState
	 * int playerX = gameState.getPlayerX();
	 * int playerY = gameState.getPlayerY();
	 * Vector2 nearestHealthPack = gameUtility.findNearest(playerX, playerY, EntityType.HealthPack);
	 * if(nearestHealthPack != null) // check if found a health pack
	 * {
	 *   // do things such as moving towards it...
	 * }
	 * </pre>
	 * 
	 * <p>
	 * The position is zero-based, meaning the the minimum coordinate is <code>(0, 0)</code> (top-left) and the maximum coordinate is <code>({@link GameState#getMapWidth()}-1, {@link GameState#getMapHeight()}-1)</code> (bottom-right).
	 * </p>
	 * 
	 * @param x - a zero-based x-position
	 * @param y - a zero-based y-position
	 * @param criterion - the entity type to search for
	 * 
	 * @throws OutOfBoundsException when the position is out of bounds
	 * 
	 * @return the position of the nearest entity, null otherwise
	 */
	public final Vector2 findNearest(int x, int y, EntityType criterion) throws OutOfBoundsException
	{
		if(gameState.isOutOfBounds(x, y))
			throw new OutOfBoundsException(x, y);
		
		Vector2 closest = new Vector2();
		int distance = Integer.MAX_VALUE;
		boolean anyMatches = false;
		for(int yy = 0; yy < gameState.getMapHeight(); yy++)
		{
			for(int xx = 0; xx < gameState.getMapWidth(); xx++)
			{
				EntityType entityType = gameState.getEntityAt(xx, yy);
				if(entityType != criterion)
					continue;
				int calculatedDistance = manhattanDistance(x, y, xx, yy);
				if(calculatedDistance < distance)
				{
					closest.setX(xx);
					closest.setY(yy);
					distance = calculatedDistance;
					anyMatches = true;
				}
			}
		}
		return (anyMatches)? closest : null;
	}
	
	/**
	 * Finds and returns the position of the furthest entity as specified by <code>criterion</code>. If no entities are found, returns null.
	 * 
	 * <p>
	 * The following code snippet finds the position of the furthest health pack:
	 * </p>
	 * <pre>
	 * // Assume have reference to gameUtility and gameState
	 * int playerX = gameState.getPlayerX();
	 * int playerX = gameState.getPlayerY();
	 * Vector2 furthestHealthPack = gameUtility.findFurthest(playerX, playerY, EntityType.HealthPack);
	 * if(furthestHealthPack != null) // check if found a health pack
	 * {
	 *   // do things such as moving towards it...
	 * }
	 * </pre>
	 * 
	 * <p>
	 * The position is zero-based, meaning the the minimum coordinate is <code>(0, 0)</code> (top-left) and the maximum coordinate is <code>({@link GameState#getMapWidth()}-1, {@link GameState#getMapHeight()}-1)</code> (bottom-right).
	 * </p>
	 * 
	 * @param x - a zero-based x-position
	 * @param y - a zero-based y-position
	 * @param criterion - the entity type to search for
	 * 
	 * @throws OutOfBoundsException when the position is out of bounds
	 * 
	 * @return the position of the furthest entity, null otherwise
	 */
	public final Vector2 findFurthest(int x, int y, EntityType criterion) throws OutOfBoundsException
	{
		if(gameState.isOutOfBounds(x, y))
			throw new OutOfBoundsException(x, y);
		
		Vector2 furthest = new Vector2();
		int distance = Integer.MIN_VALUE;
		boolean anyMatches = false;
		for(int yy = 0; yy < gameState.getMapHeight(); yy++)
		{
			for(int xx = 0; xx < gameState.getMapWidth(); xx++)
			{
				EntityType entityType = gameState.getEntityAt(xx, yy);
				if(entityType != criterion)
					continue;
				int calculatedDistance = manhattanDistance(x, y, xx, yy);
				if(calculatedDistance > distance)
				{
					furthest.setX(xx);
					furthest.setY(yy);
					distance = calculatedDistance;
					anyMatches = true;
				}
			}
		}
		return (anyMatches)? furthest : null;
	}
	
	/**
	 * Checks if the specified position is within the storm, given a storm size.
	 * 
	 * <p>
	 * The current storm size can be retrieved by {@link GameState#getStormSize()} and the maximum storm size by {@link GameState#getStormMaxSize()}. 
	 * The size of the storm is the amount of diagonal blocks from the map's edges.
	 * </p>
	 * 
	 * <p>
	 * The position is zero-based, meaning the the minimum coordinate is <code>(0, 0)</code> (top-left) and the maximum coordinate is <code>({@link GameState#getMapWidth()}-1, {@link GameState#getMapHeight()}-1)</code> (bottom-right).
	 * </p>
	 * 
	 * @param x - a zero-based x-position
	 * @param y - a zero-based y-position
	 * @param stormSize - the size of the storm
	 * 
	 * @throws OutOfBoundsException when the position is out of bounds
	 * 
	 * @return true if the position is within the storm radius, false otherwise
	 */
	public final boolean isWithinStorm(int x, int y, int stormSize) throws OutOfBoundsException
	{
		if(gameState.isOutOfBounds(x, y))
			throw new OutOfBoundsException(x, y);
		
		double minX = Math.min(x + 1, Math.abs(x - gameState.getMapWidth()));
		double minY = Math.min(y + 1, Math.abs(y - gameState.getMapHeight()));
		double distance = Math.min(minX, minY);
		return distance <= stormSize;
	}
	
	/**
	 * Chooses randomly from the specified actions. If no actions are specified, {@link Action#NoAction} is returned.
	 * 
	 * <p>
	 * For example, the following snippet will randomly choose a move action:
	 * </p>
	 * <pre>
	 * Action randomMove = chooseRandomly(Action.MoveUp, Action.MoveDown, Action.MoveLeft, Action.MoveRight);
	 * </pre>
	 * 
	 * @param actions - the set of actions to randomly choose from
	 * 
	 * @return a random action from the specified actions
	 */
	public final Action chooseRandomly(Action... actions)
	{
		if(actions == null || actions.length == 0)
			return Action.NoAction;
		
		int index = (int) (actions.length * Math.random());
		return actions[index];
	}
}