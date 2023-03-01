package arena.agents;

import arena.core.*;
import arena.core.GameState.Direction;
import arena.core.GameState.EntityType;
import arena.core.GameState.ProjectileData;

/**
 * 
 * (Last Updated ALL Documentation: 10/26/21)
 * <p>
 * As of V1.0 this AI specializes in primarily avoiding combat and making 
 * it difficult for the opponent to hit them. Additionally, the AI is good at avoiding the 
 * storm. The {@link #dodgeProjectile  dodge method} isn't perfect and the player will still walk into projectiles but 
 * generally is pretty solid at avoiding them and good at being slippery. <b>Current MAJOR WEAKNESS is 
 * being blocked/trapped in corners</b>. The AI tends to do best when it has an open areas in the center of 
 * the map to access and walk around. It likes to drift back towards the center of the map unless it needs to 
 * grab a health kit or avoid the opponent/projectiles. It majorly chokes when the opponent is right on top of them.
 * </p>
 * @author Malachi Gage Sanderson
 * @since 10/26/21 
 * @version 
 * <b>Current Version: 1.1...</b>
 * <p>
 * ADDED (11/9/21): 
 * Added a {@link #stuck  is player stuck} variable. Updated in every {@link #startInfoSetup()  start} via the {@link #isStuck()  stuck checker} 
 * method and if they are stuck, it tells the AI to not bother with any movement methods and just attack if they can.
 * Improved the {@link #isPositionSafeToBeIn  method for determining position safety} to identify if the position may have lava in it in the near future.
 * Made it so that the method that makes me move towards center and avoid the storm will stop triggering when storm size is >= 60% its max size.
 * Made it so that if {@link #moveAwayFromLocation() run from } method returns false, {@link #stuck  is player stuck} is set true.
 * Also made it so that if player is stuck, they will shoot player if can or will shoot mine if can, or lastly if can't do either will try and walk on mine;
 * and if none of that works, just scream. Also got more accurate center of map from Luke's code (vector2 (10,10)) and set that as 
 * {@link #centerOfMap map center}
 * </p>
 * <p>
 * <b>In summary:</b> some new polish but generally the main focus of this version was to make him a bit more aggressive and 
 * make him more able to react intelligently when stuck.
 * </p>
 * 
 * 
 * <p>
 * [TODO] MAKE IT SO THAT, JUST LIKE WITH SEEING A PLAYER, I WILL ALWAYS SHOOT ANY HP I HAVE LINE OF SIGHT OF IMMEDIATELY.
 * [TODO] Make it so that AI recognizes when it's impossible to enter center of map and instead uses the move away from storm method.
 * [TODO] FIX HIS PROBLEM OF GETTING STUCK ON SIDES!!!
 * 	Added {@link #escapePriority  escape priority level} variable. Does nothing yet.
 *  Will fix the agent so they are better suited for attacking and can recognize when they are blocked and 
 *  make appropriate decisions when blocked. Improve aggression functionality. 
 *  Design and implement a method for identifying/predicting the basic style of opponent such as aggressive chaser 
 *  versus avoider; then make it so this can be used to make decisions as to how to best deal with opponent.
 * </p>
 * 
 * <pre>
 * <b>Previous Versions:</b> 
 * 	1.0 [10/25/21] -- <i>Implemented most basic stuff such as: the main dodging function and methods 
 * 	for deciding where/where not to move.</i> As of V1.0 this AI specializes in primarily avoiding combat and making 
 * 	it difficult for the opponent to hit them. Additionally, the AI is good at avoiding the 
 * 	storm. The dodge method isn't perfect and the player will still walk into projectiles but 
 * 	generally is pretty solid at avoiding them and good at being slippery. <b>Current MAJOR WEAKNESS is 
 * 	being blocked/trapped in corners</b>. The AI tends to do best when it has an open areas in the center of 
 * 	the map to access and walk around. It likes to drift back towards the center of the map unless it needs to 
 * 	grab a health kit or avoid the opponent/projectiles. It majorly chokes when the opponent is right on top of them.
 * </pre>
 * 
 * @apiNote <b> KNOWN PROBLEMS IN CURRENT BUILD...</b>
 * <pre>
 * 	1.) AI gets stuck on side walls and has difficulty when being told that a side position is "unsafe" yet has to move through another side position to get to safety.
 * 	2.) Sometimes when no health packs are available at start of map, the AI breaks.
 * 	3.) USUALLY when there is no path to center from starting position, the AI breaks.
 * 	4.) Figure out why {@link #avoidStorm() } doesn't work/breaks all the time.
 * 	5.) Clean up code. (For example a bunch of Action things can be put in their own methods and just put an (if Method() != Action.NoAction) before calling it.)
 * 
 * 
 * </pre>
 */
public class GageAI extends PlayerAI
{
	private static final int minAcceptableStormDistance = 3;
	private static final int minAcceptableEnemyDistance = 0;
	private static final double stormIgnoreRatio = 0.45;
	private static final boolean prefireActive = false;
	private static final int lowHealthThreshhold = 3;
	private GameState gs;
	private GameUtility gu;
	private Vector2 myPos;
	private Vector2 oppPos;
	private Vector2 nearestStorm;
	private Vector2 nearestHP;
	private int enemyDistance;
	private static Vector2 centerOfMap = new Vector2(10,10); //new Vector2((int)(gs.getMapWidth()*0.5),(int)(gs.getMapHeight()*0.5));
	private static boolean inCenter;
	private boolean stuck;
	//private int escapePriority; //this is intended to serve as a priority level indicator for telling the AI how NECESSARY it is to change position rather than shooting.
	//private static enum GageState {Retreat, Stuck, Low_Health, Bored };


	/**
	 * <p><b>GAGE's CORE AI ROUTINE...</b></p>
	 * As of 11/9/21 The basic procedure can be described as...
	 * (Grab and load all starting info and variables)
	 * <pre>
	 * 1.) If enemy is within minimum acceptable distance, attack using mine or shot if possible.
	 * 2.) If stuck, do {@link #actionsToDoWhenStuck() stuck actions}  which include and start with a collection of {@link #stuckInCenterActions(boolean) actions to do when "stuck" in the center}.
	 * 3.) If storm is getting too close and storm size less than a certain percent of its max size, move towards center.
	 * 4.) If have eyes on opponent and can shoot, shoot!
	 * 5.) If there's a health pack in line of sight, shoot it.
	 * 6.) If player health is below a set threshold, move towards nearest health pack.
	 * 7.) If enemy is within a minimum acceptable distance, run from them.
	 * 8.) If my position isn't safe to be in, try and dodge any projectile coming my way.
	 * 9.) If in center and can place a mine, start placing mines around myself
	 * 10.) If none of the above triggered, try to move to center after doing one more stuck check.
	 * </pre>
	 * @author Gage
	 * @since 11/9/21
	 */
	@Override
	protected Action getNextAction(GameState gameState, GameUtility gameUtility)
	{
		startInfoSetup(gameState, gameUtility);
		Action chosenAction = Action.NoAction;
		if(enemyDistance <= minAcceptableEnemyDistance) //attack enemy if they are too close and you can attack in some way.
		{
			if(canSeeOpponent() && gs.canShoot())
			{
				return chosenAction = shootOpponent();
			}
			if(gs.canPlaceMine())
			{
				try
				{
					gu.placeMineTowards(oppPos.getX(), oppPos.getY());	//[TODO] place a return here! (didn't do for current build because didn't want to accidentally break anything).
				}
				catch(Exception e)
				{
					//?
				}
			}
		}


		if(stuck) //if I'm stuck, just try and shoot opponent/do other stuck actions.
		{
			return actionsToDoWhenStuck();
		}
		
		
		//if storm is getting too close and storm size less than a certain percent of its max size , move towards center. 
		if(closestStormDistance() <= minAcceptableStormDistance && (gs.getStormSize()/gs.getStormMaxSize()) <= stormIgnoreRatio)  
		{
			try
			{
				if(canMoveAlongPath(centerOfMap))
				{
					//System.out.println("\n\n\tMove to center of map\n\n");
					//return gu.moveTowards(centerOfMap.getX(), centerOfMap.getY());
					return gu.moveTowards(centerOfMap.getX(), centerOfMap.getY());
				}
			}
			catch(ArrayIndexOutOfBoundsException e1)
			{
				System.out.println("\n\n\tGAGE: [ERROR -- Unable to resolve a way to move closer to center.\n\tWill try to just run avoid storm method.]");
				if(!inCenter)
					return avoidStorm(); 		//[TODO] figure out why this method causes problems so often

			}
			catch(Exception e)
			{
				System.out.println("GAGE: [ERROR -- Unable to resolve a way to move towards center (but this time it broke really bad)]");
			}
			//return chosenAction;
			//return avoidStorm();
		}

		if(canSeeOpponent() && gs.canShoot())
		{
			return chosenAction = shootOpponent();
		}


		if(nearestHP != null)
		{
			//tells player to shoot any HP in sight.
			if(!isNearestHealthPackAboutToBeShot()) 
			{
				if(gu.haveLineOfSight(myPos.getX(), myPos.getY(), nearestHP.getX(), nearestHP.getY()) && gs.canShoot()) //are conditions good for shooting it?
				{
					return gu.shootTowards(nearestHP.getX(), nearestHP.getY());
				}

			}


			if(gs.getPlayerHealth() <= lowHealthThreshhold)
			{
				if(!gu.isWithinStorm(nearestHP.getX(), nearestHP.getY(), gs.getStormSize()))
				{
					Vector2 hpBoundProj = gu.findNearest(nearestHP.getX(), nearestHP.getY(), EntityType.Projectile);
					if(hpBoundProj != null) //this checks if a projectile is "near" the HP.
					{
						if(isNearestHealthPackAboutToBeShot()) //if a projectile is going to hit HP...
						{
							//Do nothing and ignore the HP...
						}
						else
							return chosenAction = goToHealthPack();
					}
					return chosenAction = goToHealthPack();
				}
			}
		}

		if(enemyDistance <= minAcceptableEnemyDistance)
		{
			//System.out.println("\n\n\tRUN AWAY FROM BOY\n\n");
			return chosenAction = moveAwayFromLocation(oppPos);
		}

		if(!isPositionSafeToBeIn(myPos))
		{
			Vector2 nearestEnemyProjectile = gu.findNearest(myPos.getX(), myPos.getY(), EntityType.Projectile);
			if(nearestEnemyProjectile != null && gs.isHostileProjectile(nearestEnemyProjectile.getX(), nearestEnemyProjectile.getY()))
			{
				ProjectileData projectile = getProjectileData(nearestEnemyProjectile, gs.isHostileProjectile(nearestEnemyProjectile.getX(), nearestEnemyProjectile.getY()));
				if(willProjectileHitLocation(projectile, myPos))
				{
					//System.out.println("\n\n\tDodge Projectile\n\n");
					//return chosenAction;
					return dodgeProjectile(nearestEnemyProjectile, gs.isHostileProjectile(nearestEnemyProjectile.getX(), nearestEnemyProjectile.getY()));
				}
			}
		}


		if(centerOfMap != null && inCenter && gs.canPlaceMine())
		{
			try
			{
				if(!gs.isMine(myPos.getX()-1, myPos.getY()) && gu.isWalkable(myPos.getX()-1, myPos.getY())) //if can place mine to left...
					chosenAction =  gu.placeMineTowards(myPos.getX()-1, myPos.getY());
				else if(!gs.isMine(myPos.getX(), myPos.getY()+1) && gu.isWalkable(myPos.getX(), myPos.getY()+1)  )//if can place mine below
					chosenAction =  gu.placeMineTowards(myPos.getX(), myPos.getY()+1);
				else if(!gs.isMine(myPos.getX()+1, myPos.getY())   && gu.isWalkable(myPos.getX()+1, myPos.getY())  )//if can place mine to right
					chosenAction =  gu.placeMineTowards(myPos.getX()+1, myPos.getY());

				else if(!gs.isMine(myPos.getX(), myPos.getY()-1) && gu.isWalkable(myPos.getX(), myPos.getY()-1)  )//if can place mine above
					chosenAction =  gu.placeMineTowards(myPos.getX(), myPos.getY()-1);
			}
			catch(arena.core.OutOfBoundsException e)
			{
				//Why would you try and place mines outside the arena?
			}
			catch(Exception E)
			{
				//Oh boy fucked up big here...
			}

		}


		//DO this if cannot do anything else.
		if(centerOfMap != null)
		{
			try
			{
				if(canMoveAlongPath(centerOfMap))
				{
					//return Action.NoAction;
					chosenAction = gu.moveTowards(centerOfMap.getX(), centerOfMap.getY());
				}
				//System.out.println("\n\n\tMove to center of map\n\n");

			}
			catch(ArrayIndexOutOfBoundsException e1)
			{
				//can't move closer to center
			}
			catch(Exception e)
			{
				System.out.println("GAGE: [ERROR -- Unable to resolve a way to move towards center (but this time it broke really bad)]");
			}	
		}
		
		if(stuck) //Final stuck check...
		{
			return actionsToDoWhenStuck();
		}

		
		return chosenAction;
	}

	/**
	 * Just updates/sets all variables saved in class.
	 * @author Gage
	 * @since 11/8/21
	 * @param gameState
	 * @param gameUtility
	 */
	private void startInfoSetup(GameState gameState, GameUtility gameUtility)
	{
		gs = gameState;
		gu = gameUtility;
		myPos = new Vector2(gameState.getPlayerX(),gameState.getPlayerY());
		oppPos = new Vector2(gameState.getOpponentX(),gameState.getOpponentY());
		nearestStorm = gu.findNearest(myPos.getX(), myPos.getY(), EntityType.Storm);
		nearestHP = findNearestHealthPack();
		enemyDistance = gu.manhattanDistance(myPos.getX(), myPos.getY(), oppPos.getX(), oppPos.getY());
		//escapePriority = 0;
		stuck = isStuck();
		if(centerOfMap != null)
		{
			if(myPos.getX() == centerOfMap.getX() && myPos.getY() == centerOfMap.getY())
			{
				//System.out.println("\n\n\t\tI'M IN THE CENTER OF THE MAP!!.\n");
				inCenter = true;
			}
			else
				inCenter = false;
		}	
	}


	/**
	 * @author Gage
	 * @since 10/26/21
	 * @return
	 */
	private Action goToHealthPack()
	{
		if(nearestHP != null)
		{
			if(isPositionSafeToBeIn(nearestHP))
				return gu.moveTowards(nearestHP.getX(), nearestHP.getY());
		}
		return Action.NoAction;
	}

	/**
	 * Shoots in direction of opponent if they are in line of sight.
	 * @author Gage
	 * @since 10/26/21
	 */
	private Action shootOpponent()
	{
		if(canSeeOpponent() && gs.canShoot())
		{
			return gu.shootTowards(oppPos.getX(), oppPos.getY());
		}
		else
			return Action.NoAction;
	}

	
	/**
	 * When called this method tells the AI to take an action that will
	 * make them move in a direction away from the nearest part of the storm.
	 * @author Gage
	 * @since 10/26/21
	 * @return
	 */
	private Action avoidStorm()
	{
		//System.out.println("\n\n\n\t\tGAGE: TRIED MOVING AWAY FROM STORM\n\n\n");
		if(nearestStorm == null)
		{
			System.out.println("\n\n\n\t\tGAGE: NO STORM DETECTED\n\n\n");
			return Action.NoAction;
		}
		else if(willPositionHaveStormInSoon(myPos))
			return moveAwayFromLocation(nearestStorm);
		else return Action.NoAction;
	}

	
	/**
	 * Grab's the x and y position of the {@link #nearestStorm nearest storm} and then calculates and returns
	 * the {@link GameUtility#manhattanDistance distance} between me and it.
	 * @author Gage
	 * @since 10/26/21
	 * @return distance between me and the position of closest storm location.
	 */
	private int closestStormDistance()
	{
		int nearestStormDist = 0;
		try 
		{
			nearestStormDist = gu.manhattanDistance(myPos.getX(), myPos.getY(), nearestStorm.getX(), nearestStorm.getY());
		} 
		catch (Exception e)
		{
			nearestStormDist = 99;
		}
		return nearestStormDist;
	}

	/**
	 * just checks if has line of sight on opponent.
	 * @author Gage
	 * @since 10/26/21
	 * @return
	 */
	public boolean canSeeOpponent()
	{
		return gu.haveLineOfSight(myPos.getX(), myPos.getY(), oppPos.getX(), oppPos.getY());
	}

	/**
	 * Finds nearest HP and returns its vector position. 
	 * @apiNote NOTE: make sure to check if the returned vector is
	 * null or not before returning.
	 * @author Gage
	 * @since 10/26/21
	 * @return
	 */
	private Vector2 findNearestHealthPack()
	{
		return gu.findNearest(myPos.getX(), myPos.getY(), EntityType.HealthPack);
	}



	/**
	 * [TODO]
	 * @author Gage
	 * @since 11/8/21
	 * @return true if the position is determined as safe to be in.
	 */
	private boolean isPositionSafeToBeIn(Vector2 pos)
	{

		if(gs.isOutOfBounds(pos.getX(), pos.getY()) || gs.isStorm(pos.getX(), pos.getY()) || gs.isWall(pos.getX(), pos.getY()) 
				|| gs.isHostilePlayer(pos.getX(), pos.getY()) || gs.isMine(pos.getX(), pos.getY()))
		{
			//System.out.println("\n\n\t\tCANNOT ENTER GIVEN POSITION: ("+pos.getX()+", "+pos.getY()+")." );
			return false;
		}


		Vector2 nearestEnemyProjectile = gu.findNearest(pos.getX(), pos.getY(), EntityType.Projectile);
		if(nearestEnemyProjectile != null)
		{
			ProjectileData projectile = getProjectileData(nearestEnemyProjectile, gs.isHostileProjectile(nearestEnemyProjectile.getX(), nearestEnemyProjectile.getY()));
			if(projectile != null && willProjectileHitLocation(projectile, pos))
				return false;
		}

		if(gs.isMine(pos.getX(), pos.getY())) //if position contains a mine.
		{
			return false;
		}

		if(willPositionHaveStormInSoon(pos)) 
		{
			return false;
		}

		return true;
	}

	
	/**
	 * Epic Gamer Method. Self-Explanatory. Give a projectile that you want to try to dodge 
	 * (and a boolean just confirming that it is actually an enemy projectile) and it will do 
	 * its best to move to the the first position it detects as save to move to. 
	 * NOTE: When checking if this new position is safe to be in, it should -- ideally --
	 * check to see if an existing enemy projectile will be in it in a future.
	 * 
	 * @author Gage
	 * @since 10/26/21
	 * @param projectileLoc
	 * @param isEnemyProjectile
	 * @return
	 */
	private Action dodgeProjectile(Vector2 projectileLoc, boolean isEnemyProjectile)
	{ 
		ProjectileData projectile = getProjectileData(projectileLoc, isEnemyProjectile);
		//System.out.println("\n\n\n\t\tGAGE: RUNNING DODGE METHOD\n\n\n");
		Action moveAction = Action.NoAction;
		switch(projectile.getMoveDirection())
		{
		case Up:
			if(myPos.getY() < projectile.getY() && myPos.getX() == projectile.getX())//if I'm above and on the same X row.
			{
				//Move left (if can't, move right). If cannot move L/R, move Up. 
				if(isPositionSafeToBeIn(new Vector2(myPos.getX(),myPos.getY()-1)))
				{
					return Action.MoveUp;
				}
				if(isPositionSafeToBeIn(new Vector2(myPos.getX()+1,myPos.getY())))
				{
					return Action.MoveRight;
				}
				if(isPositionSafeToBeIn(new Vector2(myPos.getX()-1,myPos.getY())))
				{
					return Action.MoveLeft;
				}

			}
			break;

		case Down:
			if(myPos.getY() > projectile.getY() && myPos.getX() == projectile.getX())//if I'm below and on the same X row.
			{
				//Move left (if can't, move right). If cannot move L/R, move Down. 
				if(isPositionSafeToBeIn(new Vector2(myPos.getX(),myPos.getY()+1)))
				{
					return Action.MoveDown;
				}
				if(isPositionSafeToBeIn(new Vector2(myPos.getX()+1,myPos.getY())))
				{
					return Action.MoveRight;
				}
				if(isPositionSafeToBeIn(new Vector2(myPos.getX()-1,myPos.getY())))
				{
					return Action.MoveLeft;
				}

			}
			break;

		case Left:
			if(myPos.getY() == projectile.getY() && myPos.getX() < projectile.getX())//if I'm in front of (left) and on the same Y row.
			{
				//Move Up (if can't, move Down). If cannot move U/D, move Left. 
				if(isPositionSafeToBeIn(new Vector2(myPos.getX(),myPos.getY()-1)))
				{
					return Action.MoveUp;
				}
				if(isPositionSafeToBeIn(new Vector2(myPos.getX(),myPos.getY()+1)))
				{
					return Action.MoveDown;
				}
				if(isPositionSafeToBeIn(new Vector2(myPos.getX()-1,myPos.getY())))
				{
					return Action.MoveLeft;
				}
			}
			break;

		case Right:
			if(myPos.getY() == projectile.getY() && myPos.getX() > projectile.getX())//if I'm in front of (right) and on the same Y row.
			{
				//Move Up (if can't, move Down). If cannot move U/D, move Right. 
				if(isPositionSafeToBeIn(new Vector2(myPos.getX(),myPos.getY()-1)))
				{
					return Action.MoveUp;
				}
				if(isPositionSafeToBeIn(new Vector2(myPos.getX(),myPos.getY()+1)))
				{
					return Action.MoveDown;
				}
				if(isPositionSafeToBeIn(new Vector2(myPos.getX()+1,myPos.getY())))
				{
					return Action.MoveRight;
				}
			}
			break;
		}
		System.out.println("\n\n\n\t\tGAGE: FAILED TO FIND A NEW POS FOR DODGE PROJECTILE\n\n\n");
		return moveAction;
	}

	/**
	 * Rad method that tells you if a projectile will ever be hitting a certain location.
	 * @author Gage
	 * @since 10/26/21
	 * @param projectile
	 * @param pos
	 * @return
	 */
	private boolean willProjectileHitLocation(ProjectileData projectile, Vector2 pos)
	{
		boolean willItHit = false;
		if(projectile != null)
		{
			switch(projectile.getMoveDirection())
			{
			case Up:
				if(pos.getY() <= projectile.getY() && pos.getX() == projectile.getX())//if I'm above and on the same X row.
				{
					//System.out.println("PROJECTILE WILL HIT LOC: ("+ pos.getX()+ ", "+ pos.getY() +") " +" True");
					return true;
				}
				break;

			case Down:
				if(pos.getY() >= projectile.getY() && pos.getX() == projectile.getX())//if I'm below and on the same X row.
				{
					//System.out.println("PROJECTILE WILL HIT LOC: ("+ pos.getX()+ ", "+ pos.getY() +") " +" True");
					return true;
				}
				break;

			case Left:
				if(pos.getY() == projectile.getY() && pos.getX() <= projectile.getX())//if I'm in front of (left) and on the same Y row.
				{
					//System.out.println("PROJECTILE WILL HIT LOC: ("+ pos.getX()+ ", "+ pos.getY() +") " +" True");
					return true;
				}
				break;

			case Right:
				if(pos.getY() == projectile.getY() && pos.getX() >= projectile.getX())//if I'm in front of (right) and on the same Y row.
				{
					//System.out.println("PROJECTILE WILL HIT LOC: ("+ pos.getX()+ ", "+ pos.getY() +") " +" True");
					return true;
				}
				break;
			}
		}

		//System.out.println("PROJECTILE WILL HIT LOC: ("+ pos.getX()+ ", "+ pos.getY() +") " +willItHit);
		return willItHit;
	}


	/**
	 * Clever method that should tell me where a projectile will be in the next round based on 
	 * it's direction and position.
	 * @author Gage
	 * @since 11/9/21
	 * @param projectile
	 * @param pos
	 * @return
	 */
	private boolean willProjectileHitPositionNextRound(ProjectileData projectile, Vector2 pos) throws arena.core.OutOfBoundsException
	{
		boolean willItHit = false;
		if(projectile != null)
		{
			switch(projectile.getMoveDirection())
			{
			case Up:
				if(pos.getY() <= projectile.getY() && pos.getX() == projectile.getX())//if I'm above and on the same X row.
				{
					if(pos.getY() == (projectile.getY() - 1)) //if projectile moving one step means it'll be in pos...
					{
						return true;
					}
					//System.out.println("PROJECTILE WILL HIT LOC: ("+ pos.getX()+ ", "+ pos.getY() +") " +" True");
					return false;
				}
				break;

			case Down:
				if(pos.getY() >= projectile.getY() && pos.getX() == projectile.getX())//if I'm below and on the same X row.
				{
					if(pos.getY() == (projectile.getY() + 1)) //if projectile moving one step means it'll be in pos...
					{
						return true;
					}
					//System.out.println("PROJECTILE WILL HIT LOC: ("+ pos.getX()+ ", "+ pos.getY() +") " +" True");
					return false;
				}
				break;

			case Left:
				if(pos.getY() == projectile.getY() && pos.getX() <= projectile.getX())//if I'm in front of (left) and on the same Y row.
				{
					if(pos.getX() == (projectile.getX() - 1)) //if projectile moving one step means it'll be in pos...
					{
						return true;
					}
					//System.out.println("PROJECTILE WILL HIT LOC: ("+ pos.getX()+ ", "+ pos.getY() +") " +" True");
					return false;
				}
				break;

			case Right:
				if(pos.getY() == projectile.getY() && pos.getX() >= projectile.getX())//if I'm in front of (right) and on the same Y row.
				{
					if(pos.getX() == (projectile.getX() + 1)) //if projectile moving one step means it'll be in pos...
					{
						return true;
					}
					//System.out.println("PROJECTILE WILL HIT LOC: ("+ pos.getX()+ ", "+ pos.getY() +") " +" True");
					return false;
				}
				break;
			}
		}

		//System.out.println("PROJECTILE WILL HIT LOC: ("+ pos.getX()+ ", "+ pos.getY() +") " +willItHit);
		return willItHit;
	}

	/**
	 * This method should tell me the direction that something is relative to the player's current position...
	 * @author Gage
	 * @since 11/9/21
	 * @param pos the something that the player will compare positions with.
	 * @return
	 */
	private Direction directionToPosition(Vector2 pos)
	{
		Direction dir = null;
		switch(gu.moveTowards(pos.getX(), pos.getY()))
		{
		case MoveDown: //if location is below
			return Direction.Down;

		case MoveUp: //if location is above
			return Direction.Up;

		case MoveRight: //if location is to the right
			return Direction.Right;

		case MoveLeft: //if location is to the left.
			return Direction.Left;
		}
		if(dir == null)
			System.out.println("\n\n\n\t\tBIG OOPSIE IN DETERMINING DIRECTION: DIRECTION == NULL\n");
		return dir;
	}


	/**
	 * easy way to get a projectile's data using a known projectile's location vector.
	 * @author Gage
	 * @since 10/26/21
	 * @param projectileLoc
	 * @param isEnemyProjectile
	 * @return
	 */
	public ProjectileData getProjectileData(Vector2 projectileLoc, boolean isEnemyProjectile)
	{
		//System.out.println("\n\n\n\t\tGAGE: Running Projectile Data get\n\n\n");
		if(!isEnemyProjectile)
		{
			//System.out.println("\n\n\n\t\tGAGE: NOT ENEMY PROJECTILE\n\n\n");
			return null;
		}


		ProjectileData projectile = null;
		ProjectileData[] projectileArr = gs.getProjectileDatas();
		for(ProjectileData d : projectileArr)  
		{

			Vector2 confirmedProjectileLoc = new Vector2(d.getX(),d.getY());
			if(confirmedProjectileLoc.getX() == projectileLoc.getX())
			{
				projectile = d;
				//System.out.println("\n\n\n\t\tGAGE: SENT PROJECTILE\n\n\n("+ d.getX()+ ", "+ d.getY() +") ("+ projectileLoc.getX()+", "+projectileLoc.getY()+").\n");
				return d;
			}	
		}
		//System.out.println("\n\n\n\t\tGAGE: FAILED TO FIND PROJECTILE\n\n\n");
		return projectile;
	}

	/**
	 * [TODO] easy way to get a projectile's data using a known projectile's location vector.
	 * @author Gage
	 * @since 10/26/21
	 * @param projectileLoc
	 * @param isEnemyProjectile
	 * @return
	 */
	public ProjectileData getMyOwnProjectileData(Vector2 projectileLoc, boolean isMyProjectile)
	{
		return null;
		/*
		//System.out.println("\n\n\n\t\tGAGE: Running Projectile Data get\n\n\n");
		if(!isMyProjectile)
		{
			//System.out.println("\n\n\n\t\tGAGE: NOT ENEMY PROJECTILE\n\n\n");
			return null;
		}


		ProjectileData projectile = null;
		ProjectileData[] projectileArr = gs.getSelfProjectileDatas();
		for(ProjectileData d : projectileArr)  
		{

			Vector2 confirmedProjectileLoc = new Vector2(d.getX(),d.getY());
			if(confirmedProjectileLoc.getX() == projectileLoc.getX())
			{
				projectile = d;
				//System.out.println("\n\n\n\t\tGAGE: SENT PROJECTILE\n\n\n("+ d.getX()+ ", "+ d.getY() +") ("+ projectileLoc.getX()+", "+projectileLoc.getY()+").\n");
				return d;
			}	
		}
		//System.out.println("\n\n\n\t\tGAGE: FAILED TO FIND PROJECTILE\n\n\n");
		return projectile;
		 */
	}


	/**
	 * [TODO MAKE IT SO THAT THIS METHOD CHECKS TO MAKE SURE EACH DIRECTION IS NOT OUT OF BOUNDS...]
	 * Complicated, hopefully smart, method for telling player to move away from a location 
	 * while still avoiding being in a dangerous location.
	 * @author Gage
	 * @since 10/26/21
	 * @param dangerLoc
	 * @return
	 */
	private Action moveAwayFromLocation(Vector2 dangerLoc)
	{
		switch (gu.moveTowards(dangerLoc.getX(), dangerLoc.getY()))	
		{
		case MoveDown: //if danger is below

			if(isPositionSafeToBeIn(new Vector2(myPos.getX()+1,myPos.getY()))) //is right safe
			{
				return Action.MoveRight;
			}
			if(isPositionSafeToBeIn(new Vector2(myPos.getX(),myPos.getY()-1))) //is up safe
			{
				return Action.MoveUp;
			}
			if(isPositionSafeToBeIn(new Vector2(myPos.getX()-1,myPos.getY()))) //is left safe
			{
				return Action.MoveLeft;
			}
		case MoveUp: //if danger is above

			if(isPositionSafeToBeIn(new Vector2(myPos.getX()+1,myPos.getY()))) 
			{
				return Action.MoveRight;
			}
			if(isPositionSafeToBeIn(new Vector2(myPos.getX(),myPos.getY()+1)))
			{
				return Action.MoveDown;
			}
			if(isPositionSafeToBeIn(new Vector2(myPos.getX()-1,myPos.getY())))
			{
				return Action.MoveLeft;
			}
		case MoveLeft: //if danger is to the left
			if(isPositionSafeToBeIn(new Vector2(myPos.getX(),myPos.getY()-1)))
			{
				return Action.MoveUp;
			}
			if(isPositionSafeToBeIn(new Vector2(myPos.getX(),myPos.getY()+1)))
			{
				return Action.MoveDown;
			}
			if(isPositionSafeToBeIn(new Vector2(myPos.getX()+1,myPos.getY())))
			{
				return Action.MoveRight;
			}
		case MoveRight: //if danger is to the right
			if(isPositionSafeToBeIn(new Vector2(myPos.getX(),myPos.getY()-1)))
			{
				return Action.MoveUp;
			}
			if(isPositionSafeToBeIn(new Vector2(myPos.getX(),myPos.getY()+1)))
			{
				return Action.MoveDown;
			}
			if(isPositionSafeToBeIn(new Vector2(myPos.getX()-1,myPos.getY())))
			{
				return Action.MoveLeft;
			}
		}
		System.out.println("\n\n\tCannot Move in Any Direction To avoid the given danger Loc: "+ dangerLoc.toString()+ "\n\tRunning On Stuck Actions..\n");
		stuck = true;
		return actionsToDoWhenStuck();
	}



	/**
	 * Do all the necessary checks to see if nearestHP is about to be shot.
	 * @author Gage
	 * @since 11/8/21
	 * @return
	 */
	private boolean isNearestHealthPackAboutToBeShot()
	{
		try
		{
			if(nearestHP != null) 
			{
				Vector2 projectileAboutToHitHP = gu.findNearest(nearestHP.getX(), nearestHP.getY(), EntityType.Projectile);
				if(projectileAboutToHitHP != null)
				{	
					if(!willProjectileHitLocation(getMyOwnProjectileData(projectileAboutToHitHP, gs.isFriendlyProjectile(projectileAboutToHitHP.getX(), projectileAboutToHitHP.getY())) , nearestHP)) //Make sure HP not already about to be shot.
					{
						return true;
					}	
				}
			}

		}
		catch(Exception e)
		{
			System.out.println("GAGE: [ERROR -- PROBLEM TRYING TO FIND A PROJECTILE TO HIT NEAREST HP]");
			return false;
		}

		return false;
	}

	
	
	
	//+++++++++++++++++++++++++++++++++++++++++++++++++++STUCK STUFF+++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	
	/**
	 * If the player is unable to move to a "safe" location in any direction, 
	 * call this to check if player is stuck so that you 
	 * can tell player to just try and attack or do other actions 
	 * defined in {@link #actionsToDoWhenStuck()}.
	 * @author Gage
	 * @since 11/9/21
	 * @return if the player is "stuck"
	 */
	private boolean isStuck()
	{
		//Can move up?
		if(gu.isWalkable(myPos.getX(),myPos.getY()-1) && isPositionSafeToBeIn(new Vector2(myPos.getX(),myPos.getY()-1)))
		{
			return false;
		}

		//Can move down?
		if(gu.isWalkable(myPos.getX(),myPos.getY()+1)  && isPositionSafeToBeIn(new Vector2(myPos.getX(),myPos.getY()+1)))
		{
			return false;
		}

		//Can move left?
		if(gu.isWalkable(myPos.getX()-1,myPos.getY())  && isPositionSafeToBeIn(new Vector2(myPos.getX()-1,myPos.getY())))
		{
			return false;
		}

		//Can move right?
		if(gu.isWalkable(myPos.getX()+1,myPos.getY()) && isPositionSafeToBeIn(new Vector2(myPos.getX()+1,myPos.getY())))
		{
			return false;
		}

		System.out.println("\n\n\n\n\t\tAHHHH HELP I'M STUCKKKK!!!!\n\n\n\n");
		return true;
	}


	/**
	 * Just set of actions to do when the boolean value stuck is true...
	 * <p><i>First make sure he's actually stuck and not just happy and in the center where this will then branch to {@link #stuckInCenterActions() } </i></p>
	 * 
	 * Basically he will:
	 * <pre>
	 * 1.) Try to shoot opponent if can.
	 * 2.) Try to shoot mine if can.
	 * 3.) Try to step on mine if can. 
	 * 4.) Just wait if cannot do anything...
	 * </pre>
	 * @author Gage
	 * @since 11/8/21
	 * @return
	 */
	public Action actionsToDoWhenStuck()
	{
		if(inCenter)
		{
			//System.out.println("\n\n\t\tSTUCK! BUT I'M IN THE CENTER OF THE MAP!!.\n");
			return stuckInCenterActions(prefireActive);
		}

		Vector2 nearestMinePos = gu.findNearest(myPos.getX(), myPos.getY(), EntityType.Mine);
		if(gs.canShoot())
		{
			if(canSeeOpponent()) //shoot player if can
			{
				return shootOpponent();
			}
			else if(nearestMinePos != null ) //shoot mine if can (and not in center)
			{
				if(gu.haveLineOfSight(myPos.getX(), myPos.getY(), nearestMinePos.getX(), nearestMinePos.getY()))
				{
					return gu.shootTowards(nearestMinePos.getX(), nearestMinePos.getY());
				}
			}
		}
		//If cannot shoot or another factor is in way, and can see a mine; just walk on mine.
		try
		{
			if(nearestMinePos != null )
			{
				if( !isPositionSafeToBeIn(myPos) && gu.isWalkable(nearestMinePos.getX(), nearestMinePos.getY()))
				{
					System.out.println("\n\n\t\tStuck and can't shoot. Only option is to step on Mine!!!!.\n\n\n");
					return gu.moveTowards(nearestMinePos.getX(), nearestMinePos.getY()); //This is done with no risk checks...
				}
			}
		}
		catch(Exception e)
		{
			System.out.println("GAGE: [ERROR IN STUCK -- Unable to resolve a way to do anything (but this time it broke really bad)]");
		}
		if(willPositionHaveStormInSoon(myPos))
		{
			try
			{
				if(canMoveAlongPath(centerOfMap))
				{
					//System.out.println("\n\n\tMove to center of map\n\n");
					//return gu.moveTowards(centerOfMap.getX(), centerOfMap.getY());
					return gu.moveTowards(centerOfMap.getX(), centerOfMap.getY());
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
			
		else
			System.out.println("\n\n\t\tSTUCK! CAN'T SHOOT OR JUST STEP ON MINE, SO JUST GOTTA WAIT.\n");
		return Action.NoAction;
	}


	/**
	 * This is used as often when player is just sitting still in center he can enter a state where he is considered stuck, 
	 * which would normally be a bad thing but in the case that he's in the center of the map he doesn't actually want to move anywhere.
	 * <p>
	 * Basically, I want the player to try and surround himself with mines and 
	 * any time there's an open spot without a mine try and place the mine there but if there's a spot that's open
	 * and you have line of sight on player in that opening, just blast his ass.
	 * </p>
	 * Note: this method also has a feature I'm calling <b>"Prefire"</b> that is supposed to be
	 * a really sneaky/smart feature that allows the AI to predict when an opponent's projectile will hit
	 * a mine that I'm hiding behind and thus hopefully shoot preemptively so that as soon as mine is broken my projectile is already traveling towards enemy.
	 * [TODO]<i> Needs further testing before can be considered actually functional.</i>
	 * 
	 * <p>
	 * Major priority of this method was to prevent the problem where my AI would just shoot his own defensive mines.
	 * </p>
	 * 
	 * @author Gage
	 * @since 11/9/21
	 * @param preFire as of 11/9/21 this enables the experimental "prefire" feature when set true.
	 */
	public Action stuckInCenterActions(boolean preFire)
	{
		Direction oppDirection = directionToPosition(oppPos);


		if(oppDirection != null)
		{
			switch(oppDirection) //if player is in opening in mine barrier, shoot them.
			{
			case Up: //player above...
				Vector2 upPos = new Vector2(myPos.getX(), myPos.getY()-1);
				if(!gs.isMine(upPos.getX(), upPos.getY())  && gu.isWalkable(upPos.getX(), upPos.getY())) //if no mine above
				{
					if(gs.canShoot() && canSeeOpponent()) 
						return shootOpponent();
				}
				else if(preFire && gs.isMine(upPos.getX(), upPos.getY())  && gu.isWalkable(upPos.getX(), upPos.getY())) //if mine above
				{
					if(preFireMine(upPos)!=Action.NoAction)
						return preFireMine(upPos);
				}

				break;

			case Down: //player below...
				Vector2 downPos = new Vector2(myPos.getX(), myPos.getY()+1);
				if(!gs.isMine(myPos.getX(), myPos.getY()+1) && gu.isWalkable(myPos.getX(), myPos.getY()+1)) //if no mine below
				{
					if(gs.canShoot() && canSeeOpponent()) 
						return shootOpponent();
				}
				else if(preFire && gs.isMine(downPos.getX(), downPos.getY())  && gu.isWalkable(downPos.getX(), downPos.getY())) //if mine below
				{
					if(preFireMine(downPos)!=Action.NoAction)
						return preFireMine(downPos);
				}

				break;

			case Right: //player to the right...
				Vector2 rightPos = new Vector2(myPos.getX()+1, myPos.getY());
				if(!gs.isMine(rightPos.getX(), rightPos.getY())   && gu.isWalkable(rightPos.getX(), rightPos.getY())) //if no mine right
				{
					if(gs.canShoot() && canSeeOpponent()) 
						return shootOpponent();
				}
				else if(preFire && gs.isMine(rightPos.getX(), rightPos.getY())  && gu.isWalkable(rightPos.getX(), rightPos.getY())) //if mine right
				{
					if(preFireMine(rightPos)!=Action.NoAction)
						return preFireMine(rightPos);
				}

				break;

			case Left: //player to the left...
				Vector2 leftPos = new Vector2(myPos.getX()-1, myPos.getY());
				if(!gs.isMine(leftPos.getX(), leftPos.getY()) && gu.isWalkable(leftPos.getX(), leftPos.getY())) //if no mine left
				{
					if(gs.canShoot() && canSeeOpponent()) 
						return shootOpponent();
				}
				else if(preFire && gs.isMine(leftPos.getX(), leftPos.getY())  && gu.isWalkable(leftPos.getX(), leftPos.getY())) //if mine above
				{
					if(preFireMine(leftPos)!=Action.NoAction)
						return preFireMine(leftPos);
				}
					
				break;

			}
		}

		
		if(gs.canPlaceMine()) //if you can surround yourself with a mine, do so.
		{
			try
			{
				if(!gs.isMine(myPos.getX()-1, myPos.getY()) && gu.isWalkable(myPos.getX()-1, myPos.getY())) //if can place mine to left...
					return  gu.placeMineTowards(myPos.getX()-1, myPos.getY());
				else if(!gs.isMine(myPos.getX(), myPos.getY()+1) && gu.isWalkable(myPos.getX(), myPos.getY()+1)  )//if can place mine below
					return  gu.placeMineTowards(myPos.getX(), myPos.getY()+1);
				else if(!gs.isMine(myPos.getX()+1, myPos.getY())   && gu.isWalkable(myPos.getX()+1, myPos.getY())  )//if can place mine to right
					return  gu.placeMineTowards(myPos.getX()+1, myPos.getY());
				else if(!gs.isMine(myPos.getX(), myPos.getY()-1) && gu.isWalkable(myPos.getX(), myPos.getY()-1)  )//if can place mine above
					return  gu.placeMineTowards(myPos.getX(), myPos.getY()-1);
				//else if(gs.canShoot() && canSeeOpponent()) 
				//return shootOpponent();
			}
			catch(arena.core.OutOfBoundsException e)
			{
				//Why would you try and place mines outside the arena?
			}
			catch(Exception E)
			{
				//Oh boy fucked up big here...
			}
		}
		return Action.NoAction;
	}

	//-----------------------------------------------------------------------------------------------------------------------

	
	
	/**
	 * Identifies if a given position may have storm in it soon
	 * based on if the position is 1 block away from lava or not.
	 * This function is primarily used in {@link #isPositionSafeToBeIn() position safety checker}.
	 * @author Gage
	 * @since 11/8/21
	 * @return
	 */
	private boolean willPositionHaveStormInSoon(Vector2 pos)
	{
		Vector2 closestStormToPos = gu.findNearest(pos.getX(), pos.getY(), EntityType.Storm);
		//[TODO] Make it so he knows to avoid the edges of the map.

		if(closestStormToPos == null) //storm doesn't exist yet...
		{
			/*
			if(pos.getX() == 0 || pos.getX() == gs.getMapWidth()) //don't stand in the sides...
			{
				return true;
			}
			if(pos.getY() == 0 || pos.getY() == gs.getMapHeight()) //don't stand in the sides...
			{
				return true;
			}
			 */
		}

		if(closestStormToPos != null)
		{
			if(1 >= gu.manhattanDistance(pos.getX(), pos.getY(), closestStormToPos.getX(), closestStormToPos.getY()) )//if the dist between pos and nearest storm from pos is equal to 1 or less...
			{
				System.out.println("\n\n\t\tPOSITION WILL HAVE STORM IN IT SOON.\n");
				return true;
			}
			return false;
		}
		//System.out.println("\n\n\t\tSTORM == Null.\n");
		return false;
	}


	/**
	 * [TODO]
	 * @author Gage
	 * @since 10/26/21
	 * @param locToMoveTowards
	 * @return
	 */
	private boolean canMoveAlongPath(Vector2 locToMoveTowards) throws ArrayIndexOutOfBoundsException
	{
		Vector2 path = gu.calculatePath(myPos.getX(), myPos.getY(), locToMoveTowards.getX(), locToMoveTowards.getY())[0];
		Vector2 newPos = new Vector2(path.getX(),path.getY());
		if(newPos.getX() == 0 || newPos.getY() == 0 ) 
		{
			System.out.println("\n\n\t\tPOSITION PATH VECTOR FAILED TO CACULATE PATH.\n");
			return false;
		}
		if(gu.isWalkable(newPos.getX(),newPos.getY()) && isPositionSafeToBeIn(newPos) )
		{
			return true;
		}
		return false;
	}

	
	/**
	 * Used to identify if a mine is about to be shot and if it is, return the action of shoot towards opponent.
	 * Created mostly to be used in {@link #stuckInCenterActions() center stuck} method for the "prefire" functionality.
	 * @author Gage
	 * @since 11/9/21
	 * @return
	 */
	public Action preFireMine(Vector2 minePos)
	{
		Vector2 nearestEnemyProjectile = gu.findNearest(minePos.getX(), minePos.getY(), EntityType.Projectile);
		try
		{
			if(nearestEnemyProjectile != null)
			{
				ProjectileData projectile = getProjectileData(nearestEnemyProjectile, gs.isHostileProjectile(nearestEnemyProjectile.getX(), nearestEnemyProjectile.getY()));
				if(projectile != null && willProjectileHitPositionNextRound(projectile, minePos))
					if(gs.canShoot() && canSeeOpponent()) 
						return shootOpponent();
			}
		}
		catch(arena.core.OutOfBoundsException e)
		{

		}
		return Action.NoAction;
	}


	
	//[TODO] This should help me with avoiding the sides of the map when storm == null and my AI gets stuck in sides...
	/**
	 * 
	 * @author Gage
	 * @since 10/26/21
	 * @param minDistance
	 * @return
	 */
	private boolean tooCloseToEdge(int minDistance)
	{
		//[TODO]
		return true;
	}

	//[TODO]
	/**
	 * A method that will try to determine what general approach the opponent is using based off of saved info 
	 * their previous moves. If they are non-aggressive (shoot very little), act very aggressively.
	 * If they are hyper-aggressive, mostly run, place mines and dodge. 
	 * @author Gage
	 * @since 10/26/21
	 */
	private void identifyPlayerApproach()
	{
		//[TODO]
	}





}


