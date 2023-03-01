package arena.agents;

import java.util.Random;

import arena.core.Action;
import arena.core.GameState;
import arena.core.GameUtility;
import arena.core.PlayerAI;

public class RandomAI extends PlayerAI
{
	// Generate a random generator
	private final Random rand;
	
	// Store all possible actions in an array for future use
	private final Action[] actions;
	
	public RandomAI()
	{
		rand = new Random();
		actions = Action.values();
	}
	
	@Override
	protected Action getNextAction(GameState gameState, GameUtility gameUtility)
	{
		// Returns a random action from the actions array
		return actions[rand.nextInt(actions.length)];
	}
}