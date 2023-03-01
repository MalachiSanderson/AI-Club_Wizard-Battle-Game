package arena.agents;

import arena.core.*;

public class KamrynAI extends PlayerAI
{

	@Override
	protected Action getNextAction(GameState gameState, GameUtility gameUtility)
	{
		// Write your agent's code here.
		
		int health = gameState.getOpponentHealth();
		int opponentX = gameState.getOpponentX();
		int opponentY = gameState.getOpponentY();
		int playerX = gameState.getPlayerX();
		int playerY = gameState.getPlayerY();
		
		
		if (health == 1 && gameState.canShoot())
		{
			gameUtility.shootTowards(opponentX, opponentY);
		}
		
		gameUtility.moveTowards(opponentX, opponentY);

		// Use gameState to retrieve the game's current state
		// Use gameUtility to assist you in common tasks such as path-finding
		// Return your action from the enum Action.*
		
		return Action.NoAction;
	}

}
