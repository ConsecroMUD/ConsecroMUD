package com.suscipio_solutions.consecro_mud.Abilities.Songs;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;
import com.suscipio_solutions.consecro_mud.core.interfaces.Tickable;



public class Song_Disgust extends Song
{
	@Override public String ID() { return "Song_Disgust"; }
	private final static String localizedName = CMLib.lang().L("Disgust");
	@Override public String name() { return localizedName; }
	@Override public int abstractQuality(){ return Ability.QUALITY_MALICIOUS;}
	@Override protected boolean HAS_QUANTITATIVE_ASPECT(){return false;}

	@Override
	public boolean tick(Tickable ticking, int tickID)
	{
		if(!super.tick(ticking,tickID))
			return false;

		final MOB mob=(MOB)affected;
		if(mob==null) return true;
		if(mob==invoker) return true;
		if(invoker==null) return true;
		final Room room=invoker.location();
		if((!mob.isInCombat())&&(room!=null))
		{
			final MOB newMOB=room.fetchRandomInhabitant();
			if(newMOB!=mob)
			{
				room.show(mob,newMOB,CMMsg.MSG_OK_ACTION,L("<S-NAME> appear(s) disgusted with <T-NAMESELF>."));
				CMLib.combat().postAttack(mob,newMOB,mob.fetchWieldedItem());
			}
		}
		return true;
	}

	@Override
	public int castingQuality(MOB mob, Physical target)
	{
		if(mob!=null)
		{
			if(mob.location().numInhabitants()<3)
				return Ability.QUALITY_INDIFFERENT;
		}
		return super.castingQuality(mob,target);
	}

}
