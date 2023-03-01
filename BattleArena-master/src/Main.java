import arena.agents.*;
import arena.core.BattleArena;

public class Main
{
	public static void main(String[] args)
	{
		// Start the game with RandomAI vs TemplateAI
		BattleArena.startArena(GageAI.class, DannyAI.class);
	}
}