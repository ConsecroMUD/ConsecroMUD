package com.suscipio_solutions.consecro_mud.Abilities.Songs;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Tickable;



public class Dance_CanCan extends Dance
{
	@Override public String ID() { return "Dance_CanCan"; }
	private final static String localizedName = CMLib.lang().L("Can-Can");
	@Override public String name() { return localizedName; }
	@Override public int abstractQuality(){ return Ability.QUALITY_BENEFICIAL_OTHERS;}
	public static Ability kick=null;

	@Override
	public boolean tick(Tickable ticking, int tickID)
	{
		if(!super.tick(ticking,tickID))
			return false;

		final MOB mob=(MOB)affected;
		if(mob==null)
			return false;

		if(mob.isInCombat())
		{
			if(kick==null)
			{
				kick=CMClass.getAbility("Fighter_Kick");
				kick.setProficiency(100);
			}
			final int oldMana=mob.curState().getMana();
			kick.invoke(mob,mob.getVictim(),false,adjustedLevel(invoker(),0));
			mob.curState().setMana(oldMana);
		}
		return true;
	}

}
