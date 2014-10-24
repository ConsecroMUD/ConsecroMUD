package com.suscipio_solutions.consecro_mud.Abilities.Songs;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Tickable;



public class Song_Mana extends Song
{
	@Override public String ID() { return "Song_Mana"; }
	private final static String localizedName = CMLib.lang().L("Mana");
	@Override public String name() { return localizedName; }
	@Override public int abstractQuality(){ return Ability.QUALITY_OK_OTHERS;}

	@Override
	public boolean tick(Tickable ticking, int tickID)
	{
		if(!super.tick(ticking,tickID))
			return false;

		final MOB mob=(MOB)affected;
		if(mob==null) return true;
		if(invoker==null) return true;
		//int level=invoker.phyStats().level();
		//int mana=(int)Math.round(Integer.valueOf(level).doubleValue()/2.0);
		mob.curState().adjMana((adjustedLevel(invoker(),0)*3),mob.maxState());
		return true;
	}
}
