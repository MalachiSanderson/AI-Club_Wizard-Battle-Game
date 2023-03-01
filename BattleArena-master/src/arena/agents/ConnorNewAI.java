package arena.agents;

import java.util.Random;

import arena.core.*;
import arena.core.GameState.EntityType;

public class ConnorNewAI extends PlayerAI
{

	boolean run = false;
	boolean queuedBomb = false;
	
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
		boolean inDangerOfWall;
		
		Vector2 nearestStorm = gameUtility.findNearest(playerX, playerY, EntityType.Storm);
		int nearestStormDist = 0;
		try {
			nearestStormDist = gameUtility.manhattanDistance(playerX, playerY, nearestStorm.getX(), nearestStorm.getY());
		} catch (Exception e){
			nearestStormDist = 99;
		}
		
		if(queuedBomb) {
			queuedBomb = false;
			return gameUtility.placeMineTowards(playerX, playerY);
		}
		
		if(nearestStormDist <= 1) {
			switch (gameUtility.moveTowards(nearestStorm.getX(), nearestStorm.getY()))	{
				case MoveDown:
					return Action.MoveUp;
				case MoveUp:
					return Action.MoveDown;
				case MoveLeft:
					return Action.MoveRight;
				case MoveRight:
					return Action.MoveLeft;
			}
		}
		
		if(run || distance <= 1) {
			run = false;
			switch (gameUtility.moveTowards(opponentX, opponentY))	{
				case MoveDown:
					return gameUtility.chooseRandomly(Action.MoveLeft, Action.MoveRight, gameUtility.shootTowards(opponentX, opponentY));
				case MoveUp:
					return gameUtility.chooseRandomly(Action.MoveLeft, Action.MoveRight, gameUtility.shootTowards(opponentX, opponentY));
				case MoveLeft:
					return gameUtility.chooseRandomly(Action.MoveUp, Action.MoveDown, gameUtility.shootTowards(opponentX, opponentY));
				case MoveRight:
					return gameUtility.chooseRandomly(Action.MoveUp, Action.MoveDown, gameUtility.shootTowards(opponentX, opponentY));
			}
		}
		
		
		if(playerHealth <= opponentHealth)
		{
			if (!haveLineOfSight) {
				// ... run away to the nearest health pack (if there are any)
				Vector2 nearestHealthPack = gameUtility.findNearest(playerX, playerY, EntityType.HealthPack);
			
				if(nearestHealthPack != null) // means there is a health pack
				{
					int healthPackX = nearestHealthPack.getX();
					int healthPackY = nearestHealthPack.getY();
					return gameUtility.moveTowards(healthPackX, healthPackY);
				}
			} else {
				Vector2 nearestMine = gameUtility.findNearest(playerX, playerY, EntityType.Mine);
				if (gameState.canPlaceMine() && distance < 5 && nearestMine == null) {
					run = true;
					return gameUtility.placeMineTowards(opponentX, opponentY);
				}
			}
		} else {
			if (haveLineOfSight && distance < 6) {
				return gameUtility.shootTowards(opponentX, opponentY);
			}
			else if (distance < 2) {
				queuedBomb = true;
				return gameUtility.moveTowards(playerX - (opponentX - playerX), playerY - (opponentY - playerY));
			}
		}

		if(haveLineOfSight) {
			return gameUtility.shootTowards(opponentX, opponentY);
		}
		
		return gameUtility.moveTowards(opponentX, opponentY);
	}

}
