package arena.agents;

import arena.core.*;

public class JakobAI extends PlayerAI
{

	@Override
	protected Action getNextAction(GameState gameState, GameUtility gameUtility)
	{
		// Write your agent's code here.
		
		// Use gameState to retrieve the game's current state
		// Use gameUtility to assist you in common tasks such as path-finding
		// Return your action from the enum Action.*
		
		
		
		if(distanceToStorm(gameState,gameUtility) > 3 && gameUtility.haveLineOfSight(gameState.getPlayerX(), gameState.getPlayerY(), gameState.getOpponentX(), gameState.getOpponentY())) {
			return gameUtility.shootTowards(gameState.getOpponentX(), gameState.getOpponentY());
		}
		
		Vector2 nearHealthPos = gameUtility.findNearest(gameState.getPlayerX(), gameState.getPlayerY(), GameState.EntityType.HealthPack);
		
		
		int mapCenter = 10;
		
		boolean isClose = false;
		if(gameUtility.euclidianDistance(gameState.getPlayerX(), gameState.getPlayerY(), gameState.getOpponentX(), gameState.getOpponentY()) < 4) {
			isClose = true;
		}
		
		boolean isMapCenterEmpty = gameState.isEmpty(mapCenter, mapCenter);
		
		
		if(!isClose && nearHealthPos != null && gameUtility.isReachable(gameState.getPlayerX(), gameState.getPlayerY(), nearHealthPos.getX(), nearHealthPos.getY()) && gameState.getPlayerHealth() < 5) {
			return gameUtility.moveTowards(nearHealthPos.getX(), nearHealthPos.getY());
		}
		
		
		if((isMapCenterEmpty && !isClose) || distanceToStorm(gameState,gameUtility) < 3) {
			
			return gameUtility.moveTowards(mapCenter,mapCenter);	
			
		}
		if(!isMapCenterEmpty && !isClose) {
			
			if(Math.random() < 0.5) {
				return gameUtility.moveTowards(mapCenter - 1,mapCenter);
			}else {
				return gameUtility.moveTowards(mapCenter,mapCenter - 1);
			}
		}
		
		if(isClose &&  gameState.getOpponentHealth() > gameState.getPlayerHealth() && nearHealthPos != null) {
			
			return gameUtility.moveTowards(nearHealthPos.getX(), nearHealthPos.getY());
			
		}
		
		
		return gameUtility.shootTowards(gameState.getOpponentX(), gameState.getOpponentY());
	}
	
	public static int distanceToStorm(GameState gameState, GameUtility gameUtility) {
		int distance = 0;
		
		int xDistance = 0;
		if(gameState.getPlayerX() < 10) {
			xDistance = Math.abs(gameState.getPlayerX() - gameState.getStormSize());
		}else {
			xDistance = Math.abs(20 - gameState.getStormSize()- gameState.getPlayerX() );
		}
		
		int yDistance = 0;
		if(gameState.getPlayerY() < 10) {
			yDistance = Math.abs(gameState.getPlayerY() - gameState.getStormSize());
		}else {
			yDistance = Math.abs(20 - gameState.getStormSize() - gameState.getPlayerY());
		}
		
		if(xDistance > yDistance) {
			distance = yDistance;
		}else {
			distance = xDistance;
		}
		
		return distance;
	}
		
	

}
