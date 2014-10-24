package com.suscipio_solutions.consecro_mud.Abilities.Songs;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Tickable;



public class Dance_Tap extends Dance
{
	@Override public String ID() { return "Dance_Tap"; }
	private final static String localizedName = CMLib.lang().L("Tap");
	@Override public String name() { return localizedName; }
	@Override public int abstractQuality(){ return Ability.QUALITY_MALICIOUS;}
	@Override protected String danceOf(){return name()+" Dance";}

	@Override
	public boolean tick(Tickable ticking, int tickID)
	{
		if(!super.tick(ticking,tickID))
			return false;

		final MOB mob=(MOB)affected;
		if(mob==null)
			return false;

		if(mob!=invoker())
		{
			mob.curState().adjMovement(-(adjustedLevel(invoker(),0)/3),mob.maxState());
			mob.curState().adjMana(-adjustedLevel(invoker(),0),mob.maxState());
		}
		return true;
	}
}
