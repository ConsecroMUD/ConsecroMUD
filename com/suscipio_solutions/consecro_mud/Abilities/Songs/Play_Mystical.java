package com.suscipio_solutions.consecro_mud.Abilities.Songs;
import com.suscipio_solutions.consecro_mud.Abilities.StdAbility;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;
import com.suscipio_solutions.consecro_mud.core.interfaces.Tickable;



public class Play_Mystical extends Play
{
	@Override public String ID() { return "Play_Mystical"; }
	private final static String localizedName = CMLib.lang().L("Mystical");
	@Override public String name() { return localizedName; }
	@Override public int abstractQuality(){ return Ability.QUALITY_BENEFICIAL_OTHERS;}
	@Override protected String songOf(){return L("Mystical Music");}
	@Override protected boolean HAS_QUANTITATIVE_ASPECT(){return false;}

	@Override
	public boolean tick(Tickable ticking, int tickID)
	{
		if(!super.tick(ticking,tickID))
			return false;
		final Room R=CMLib.map().roomLocation(affected);
		if(R!=null)
		for(int m=0;m<R.numInhabitants();m++)
		{
			final MOB mob=R.fetchInhabitant(m);
			if(mob!=null)
			for(int i=0;i<mob.numEffects();i++) // personal
			{
				final Ability A=mob.fetchEffect(i);
				if((A!=null)
				&&(A instanceof StdAbility)
				&&(A.abstractQuality()!=Ability.QUALITY_MALICIOUS)
				&&((A.classificationCode()&Ability.ALL_ACODES)==Ability.ACODE_SPELL)
				&&(((StdAbility)A).getTickDownRemaining()==1))
					((StdAbility)A).setTickDownRemaining(2);
			}
		}
		return true;
	}

	@Override
	public int castingQuality(MOB mob, Physical target)
	{
		if(mob!=null)
		{
			if(mob.isInCombat())
				return Ability.QUALITY_INDIFFERENT;
		}
		return super.castingQuality(mob,target);
	}

}
