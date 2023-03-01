package arena.agents;

import arena.core.*;

public class FrankymonkeyAI extends PlayerAI
{
			@Override
			protected Action getNextAction(GameState gameState, GameUtility gameUtility)
			{

				int playerX = gameState.getPlayerX();
				int playerY = gameState.getPlayerY();
				int opponentX = gameState.getOpponentX();
				int opponentY = gameState.getOpponentY();
				int randomselector = 0;
				

				int distance = gameUtility.manhattanDistance(playerX, playerY, opponentX, opponentY);
				

				if(distance <= 2 && gameUtility.haveLineOfSight(playerX, playerY, opponentX, opponentY))
				{
					randomselector =(int)(Math.random() * 10);
					switch(randomselector)  {
					
					case 1:
						return gameUtility.shootTowards(opponentX, opponentY);
					case 2:
						return gameUtility.chooseRandomly(Action.MoveUp, Action.MoveDown, Action.MoveLeft,Action.MoveRight);
					default : 
						return gameUtility.shootTowards(opponentX, opponentY);
					
					}

				}

				else
				{

					if(gameState.canPlaceMine() && distance >= 5)
					{

						 gameUtility.chooseRandomly(Action.MoveUp, Action.MoveDown, Action.MoveLeft,Action.MoveRight);
						 return	gameUtility.chooseRandomly(Action.PlaceMineDown, Action.PlaceMineLeft, Action.PlaceMineRight, Action.PlaceMineUp);
					}

					else
					{
						return gameUtility.moveTowards(opponentX, opponentY);
					}
				}
	}

}
