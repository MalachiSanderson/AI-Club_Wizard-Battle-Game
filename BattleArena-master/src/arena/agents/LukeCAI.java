package arena.agents;

import arena.core.Action;
import arena.core.GameState;
import arena.core.GameState.EntityType;
import arena.core.GameUtility;
import arena.core.PlayerAI;
import arena.core.Vector2;

public class LukeCAI extends PlayerAI
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
		if(playerHealth <= opponentHealth)
		{
			// ... run away to the nearest health pack (if there are any)
			/*
			if() // means there is a health pack
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
				}*/
			//}
			return gameUtility.moveTowards(10,10);
		}
		else // I have same/more health than opponent
		{
			// If i'm close and can shoo
				return gameUtility.shootTowards(opponentX, opponentY);
			// otherwise, run to opponent and face death like a man
		}
	}
}
