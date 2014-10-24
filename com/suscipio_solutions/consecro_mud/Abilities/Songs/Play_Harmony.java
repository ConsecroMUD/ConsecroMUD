package com.suscipio_solutions.consecro_mud.Abilities.Songs;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;



public class Play_Harmony extends Play
{
	@Override public String ID() { return "Play_Harmony"; }
	private final static String localizedName = CMLib.lang().L("Harmony");
	@Override public String name() { return localizedName; }
	@Override public int abstractQuality(){ return Ability.QUALITY_BENEFICIAL_OTHERS;}
	@Override protected int canAffectCode(){return 0;}
	@Override protected boolean persistantSong(){return false;}
	@Override protected boolean HAS_QUANTITATIVE_ASPECT(){return false;}

	@Override
	protected void inpersistantAffect(MOB mob)
	{
		final MOB victim=mob.getVictim();
		if(victim!=null) victim.makePeace();
		mob.makePeace();
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
