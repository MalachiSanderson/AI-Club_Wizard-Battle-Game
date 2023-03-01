package arena.core;

/**
 * The <code>PlayerAI</code> class represents the base class for all artificial intelligence agents for the Battle Arena game.
 * All AI agents inherit from this class and extends its {@link #getNextAction(GameState, GameUtility)} method to define
 * the agent's logic for decision making.
 * 
 * @author ERAU AI Club
 */
public abstract class PlayerAI
{
	/**
	 * Computes the next action based on the logic defined in this method.
	 * The parameters <code>gameState</code> and <code>gameUtility</code> allow the user to
	 * examine the environment and perform specific actions.
	 * The action to perform is done by returning a value from the enum, {@link Action}.
	 * 
	 * <p>
	 * The following is an example to have the AI randomly perform any action:
	 * <pre>
	 * public class RandomAI extends PlayerAI
	 * {
	 *	private final Random rand;
	 *	private final Action[] actions;
	 *
	 *	public RandomAI()
	 *	{
	 *		rand = new Random();
	 *		actions = Action.values();
	 *	}
	 *
	 *	&#64;Override
	 *	protected Action getNextAction(GameState gameState, GameUtility gameUtility)
	 *	{
	 *		return actions[rand.nextInt(actions.length)];
	 *	}
	 * }
	 * </pre>
	 * </p>
	 * 
	 * @param gameState - the state of the game at this moment
	 * @param gameUtility - a helper utility class to perform common functions
	 * 
	 * @return the next action to perform as defined by {@link Action}
	 */
	protected abstract Action getNextAction(GameState gameState, GameUtility gameUtility);
	
	final void playRound(Game game, Player player, Player otherPlayer)
	{
		if(player.isDead() || player.isDestroyed())
			return;
		
		GameState gameState = new GameState(game, player, otherPlayer);
		GameUtility gameUtility = new GameUtility(gameState);
		
		Action action = getNextAction(gameState, gameUtility);
		
		if(action == null)
			action = Action.NoAction;
		
		boolean successful = true;
		
		switch(action)
		{
			case NoAction:
				break;
				
			case MoveUp:
				successful = player.moveUp();
				break;
				
			case MoveDown:
				successful = player.moveDown();
				break;
				
			case MoveLeft:
				successful = player.moveLeft();
				break;
				
			case MoveRight:
				successful = player.moveRight();
				break;
				
			case ShootUp:
				successful = player.shootUp();
				break;
				
			case ShootDown:
				successful = player.shootDown();
				break;
				
			case ShootLeft:
				successful = player.shootLeft();
				break;
				
			case ShootRight:
				successful = player.shootRight();
				break;
				
			case PlaceMineUp:
				successful = player.placeMineUp();
				break;
				
			case PlaceMineDown:
				successful = player.placeMineDown();
				break;
				
			case PlaceMineLeft:
				successful = player.placeMineLeft();
				break;
				
			case PlaceMineRight:
				successful = player.placeMineRight();
				break;
				
			default:
				throw new IllegalStateException("Unknown action: " + action);
		}
		
		// Logging what happened
		if(successful)
		{
			System.out.println("\t[" + player.getName() + " - " + this.getClass().getSimpleName() + "] Succesfully performed " + action.toString());
		}
		else
		{
			System.out.println("\t[" + player.getName() + " - " + this.getClass().getSimpleName() + "] Failed to perform " + action.toString());
		}
	}
}