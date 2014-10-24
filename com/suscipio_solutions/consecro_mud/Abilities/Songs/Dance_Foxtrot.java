package com.suscipio_solutions.consecro_mud.Abilities.Songs;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;
import com.suscipio_solutions.consecro_mud.core.interfaces.Tickable;



public class Dance_Foxtrot extends Dance
{
	@Override public String ID() { return "Dance_Foxtrot"; }
	private final static String localizedName = CMLib.lang().L("Foxtrot");
	@Override public String name() { return localizedName; }
	@Override public int abstractQuality(){ return Ability.QUALITY_BENEFICIAL_OTHERS;}
	protected int ticks=1;
	protected int increment=1;

	@Override
	public int castingQuality(MOB mob, Physical target)
	{
		if(mob!=null)
		{
			if(target instanceof MOB)
			{
				if((((MOB)target).curState().getMana()>=((MOB)target).maxState().getMana()/2)
				&&(((MOB)target).curState().getMovement()>=((MOB)target).maxState().getMovement()/2))
					return Ability.QUALITY_INDIFFERENT;
			}
		}
		return super.castingQuality(mob,target);
	}

	@Override
	public boolean tick(Tickable ticking, int tickID)
	{
		if(!super.tick(ticking,tickID))
			return false;

		final MOB mob=(MOB)affected;
		if(mob==null)
			return false;

		mob.curState().adjMovement((invokerManaCost/15)+increment,mob.maxState());
		mob.curState().adjMana(increment,mob.maxState());
		if(increment<=1+(int)Math.round(CMath.div(adjustedLevel(invoker(),0),3)))
		{
			if((++ticks)>2)
			{
				increment++;
				ticks=1;
			}
		}
		return true;
	}

}
