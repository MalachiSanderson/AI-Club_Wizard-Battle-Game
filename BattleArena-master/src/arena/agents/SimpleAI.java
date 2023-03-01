package arena.agents;

import arena.core.Action;
import arena.core.GameState;
import arena.core.GameUtility;
import arena.core.PlayerAI;

/**
 * An example of a simple AI that chases the opponent if close and has line of sight using A* pathfinding.
 * Also, it will shoot the opponent and randomly places mines whenever it can.
 * 
 * @author ERAU AI Club
 */
public class SimpleAI extends PlayerAI
{
	@Override
	protected Action getNextAction(GameState gameState, GameUtility gameUtility)
	{
		// Cache positions of player and opponent
		int playerX = gameState.getPlayerX();
		int playerY = gameState.getPlayerY();
		int opponentX = gameState.getOpponentX();
		int opponentY = gameState.getOpponentY();
		
		// Calculate the manhattan distance between them
		int distance = gameUtility.manhattanDistance(playerX, playerY, opponentX, opponentY);
		
		// If the opponent is close, and we have a line of sight...
		if(distance <= 5 && gameUtility.haveLineOfSight(playerX, playerY, opponentX, opponentY))
		{
			// ... shoot the opponent
			return gameUtility.shootTowards(opponentX, opponentY);
		}
		// We dont have line of sight, and opponent is far ...
		else
		{
			// ... place a mine randomly if we can
			if(gameState.canPlaceMine())
			{
				// Can also do:
				// return gameUtility.placeMineTowards(opponentX, opponentY);
				
				return gameUtility.chooseRandomly(Action.PlaceMineDown, Action.PlaceMineLeft, Action.PlaceMineRight, Action.PlaceMineUp);
			}
			// ... move towards the opponent
			else
			{
				return gameUtility.moveTowards(opponentX, opponentY);
			}
		}
	}
}