package arena.core;

/**
 * The <code>Action</code> enum represents a set of actions that a player AI agent may perform.
 * This is closely related to the {@link PlayerAI#getNextAction(GameState, GameUtility)} method.
 * Note that at each turn, the agent must take an action within this set; if no action is needed, there exists a {@link #NoAction}.
 * 
 * <p>
 * Possible actions are:
 * </p>
 * <ul>
 * <li>{@link #NoAction}</li>
 * <li>{@link #MoveUp}</li>
 * <li>{@link #MoveDown}</li>
 * <li>{@link #MoveLeft}</li>
 * <li>{@link #MoveRight}</li>
 * <li>{@link #ShootUp}</li>
 * <li>{@link #ShootDown}</li>
 * <li>{@link #ShootLeft}</li>
 * <li>{@link #ShootRight}</li>
 * <li>{@link #PlaceMineUp}</li>
 * <li>{@link #PlaceMineDown}</li>
 * <li>{@link #PlaceMineLeft}</li>
 * <li>{@link #PlaceMineRight}</li>
 * </ul>
 * 
 * @author ERAU AI Club
 */
public enum Action
{
	/** The agent performs no action in this turn. */
	NoAction,
	
	/** The agent moves up in this turn if no obstacles and objects are above it. */
	MoveUp,
	/** The agent moves down in this turn if no obstacles and objects are below it. */
	MoveDown,
	/** The agent moves to the left in this turn if no obstacles and objects are on its left. */
	MoveLeft,
	/** The agent moves to the right in this turn if no obstacles and objects are on its right. */
	MoveRight,
	
	/** The agent shoots a projectile upwards. */
	ShootUp,
	/** The agent shoots a projectile downwards. */
	ShootDown,
	/** The agent shoots a projectile towards the left. */
	ShootLeft,
	/** The agent shoots a projectile towards the right. */
	ShootRight,
	
	/** The agent places a mine above itself. Cannot place mines unless the tile is unoccupied. */
	PlaceMineUp,
	/** The agent places a mine below itself. Cannot place mines unless the tile is unoccupied. */
	PlaceMineDown,
	/** The agent places a mine to the left of itself. Cannot place mines unless the tile is unoccupied. */
	PlaceMineLeft,
	/** The agent places a mine to the right of itself. Cannot place mines unless the tile is unoccupied. */
	PlaceMineRight
}