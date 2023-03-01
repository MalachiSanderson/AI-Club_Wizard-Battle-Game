package arena.agents;

import java.util.Random;

import arena.core.*;
import arena.core.GameState.EntityType;

public class ConnorAI extends PlayerAI
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
		Action chosen = Action.NoAction;
		
		try {
			nearestStormDist = gameUtility.manhattanDistance(playerX, playerY, nearestStorm.getX(), nearestStorm.getY());
		} catch (Exception e){
			nearestStormDist = 99;
		}
		
		if(queuedBomb) {
			queuedBomb = false;
			return gameUtility.placeMineTowards(playerX, playerY);
		}
		
		if((nearestStormDist <= 1 || gameUtility.findNearest(playerX, playerY, EntityType.Storm) == null )&& gameState.getRoundsTillNextStormAdvance() < 6) {
			return gameUtility.moveTowards(gameState.getMapHeight()/2, gameState.getMapWidth()/2);
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
		
		if(haveLineOfSight && distance > 1) {
			return gameUtility.shootTowards(opponentX, opponentY);
		}
		
		if (!(gameUtility.findNearest(playerX, playerY, EntityType.Mine) == null) && gameUtility.manhattanDistance(playerX, playerY, 
				gameUtility.findNearest(playerX, playerY, EntityType.Mine).getX(), gameUtility.findNearest(playerX, playerY, EntityType.Mine).getX()) > 2
				&& gameState.canPlaceMine()) {
			return gameUtility.placeMineTowards(opponentX, opponentY);
		} else {
			chosen = gameUtility.moveTowards(opponentX, opponentY);
		}
		
		boolean mineCheck = true;
		while (mineCheck) {
			switch (chosen) {
				case MoveDown:
					if (gameState.getEntityAt(playerX, playerY-1) == EntityType.Mine || gameState.getEntityAt(playerX, playerY-1) == EntityType.Projectile) {
						chosen = gameUtility.chooseRandomly(Action.MoveUp, Action.MoveLeft, Action.MoveRight);
					} else {
						mineCheck = false;
					}
					break;
				case MoveUp:
					if (gameState.getEntityAt(playerX, playerY+1) == EntityType.Mine || gameState.getEntityAt(playerX, playerY+1) == EntityType.Projectile) {
						chosen = gameUtility.chooseRandomly(Action.MoveDown, Action.MoveLeft, Action.MoveRight);
					} else {
						mineCheck = false;
					}
					break;
				case MoveLeft:
					if (gameState.getEntityAt(playerX-1, playerY) == EntityType.Mine || gameState.getEntityAt(playerX-1, playerY) == EntityType.Projectile) {
						chosen = gameUtility.chooseRandomly(Action.MoveDown, Action.MoveUp, Action.MoveRight);
					} else {
						mineCheck = false;
					}
					break;
				case MoveRight:
					if (gameState.getEntityAt(playerX+1, playerY) == EntityType.Mine || gameState.getEntityAt(playerX+1, playerY) == EntityType.Projectile) {
						chosen = gameUtility.chooseRandomly(Action.MoveDown, Action.MoveUp, Action.MoveLeft);
					} else {
						mineCheck = false;
					}
					break;
				default:
					mineCheck = true;
					break;
			}
		}
		return chosen;
	}

}
