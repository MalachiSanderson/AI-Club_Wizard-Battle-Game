package arena.agents;

import arena.core.*;
import arena.core.GameState.EntityType;

public class ExampleAI extends PlayerAI
{
	@Override
	protected Action getNextAction(GameState gameState, GameUtility gameUtility)
	{
		int playerX = gameState.getPlayerX();
		int playerY = gameState.getPlayerY();
		int playerHealth = gameState.getPlayerHealth();
		int opponentX = gameState.getOpponentX();
		int opponentY = gameState.getOpponentY();
		int opponentHealth = gameState.getOpponentHealth();
		
		int distance = gameUtility.manhattanDistance(playerX, playerY, opponentX, opponentY);
		boolean haveLineOfSight = gameUtility.haveLineOfSight(playerX, playerY, opponentX, opponentY);
		
		// If I have lower health than the opponent...
		if(playerHealth < opponentHealth)
		{
			// ... run away to the nearest health pack (if there are any)
			Vector2 nearestHealthPack = gameUtility.findNearest(playerX, playerY, EntityType.HealthPack);
			
			if(nearestHealthPack != null) // means there is a health pack
			{
				int healthPackX = nearestHealthPack.getX();
				int healthPackY = nearestHealthPack.getY();
				return gameUtility.moveTowards(healthPackX, healthPackY);
			}
			else // no healths pack are available
			{
				if(haveLineOfSight && gameState.canShoot()) // shoot if have LoS and I can shoot
				{
					return gameUtility.shootTowards(opponentX, opponentY);
				}
				else // move to opponent if I dont have LoS
				{
					return gameUtility.moveTowards(opponentX, opponentY);
				}
			}
		}
		else // I have same/more health than opponent
		{
			// If i'm close and can shoot
			if(distance <= 5 && gameState.canShoot()) 
			{
				return gameUtility.shootTowards(opponentX, opponentY);
			}
			// otherwise, run to opponent and face death like a man
			else
			{
				return gameUtility.moveTowards(opponentX, opponentY);
			}
		}
	}
}