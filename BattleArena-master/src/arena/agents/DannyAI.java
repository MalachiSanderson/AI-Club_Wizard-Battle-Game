package arena.agents;

import arena.core.*;
import arena.core.GameState.EntityType;

public class DannyAI extends PlayerAI
{
	@Override
	protected Action getNextAction(GameState gameState, GameUtility gameUtility)
	{
		int playerX = gameState.getPlayerX();
		int playerY = gameState.getPlayerY();
		int opponentX = gameState.getOpponentX();
		int opponentY = gameState.getOpponentY();
		
		int distance = gameUtility.manhattanDistance(playerX, playerY, opponentX, opponentY);
		boolean haveLineOfSight = gameUtility.haveLineOfSight(playerX, playerY, opponentX, opponentY);
		
		// ... run away to the nearest health pack (if there are any)
		Vector2 nearestHealthPack = gameUtility.findNearest(playerX, playerY, EntityType.HealthPack);
		if(haveLineOfSight && gameState.canShoot()) // shoot if have LoS and I can shoot
		{
			return gameUtility.shootTowards(opponentX, opponentY);
		}
		else if (gameState.canPlaceMine()) {
			return gameUtility.chooseRandomly(Action.PlaceMineDown, Action.PlaceMineUp, Action.PlaceMineLeft, Action.PlaceMineRight);
		}
		else if(nearestHealthPack != null) // means there is a health pack
		{
			int healthPackX = nearestHealthPack.getX(); 
			int healthPackY = nearestHealthPack.getY();
			return gameUtility.moveTowards(healthPackX, healthPackY);
		}
		return gameUtility.moveTowards(gameState.getMapWidth()/2, gameState.getMapHeight()/2);
		
	}
}