package com.suscipio_solutions.consecro_mud.Abilities.Songs;
import java.util.HashSet;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CharStats;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;



@SuppressWarnings("rawtypes")
public class Song_Dexterity extends Song
{
	@Override public String ID() { return "Song_Dexterity"; }
	private final static String localizedName = CMLib.lang().L("Dexterity");
	@Override public String name() { return localizedName; }
	@Override public int abstractQuality(){ return Ability.QUALITY_BENEFICIAL_OTHERS;}
	protected int amount=0;

	@Override
	public void affectCharStats(MOB affected, CharStats affectableStats)
	{
		super.affectCharStats(affected,affectableStats);
		if(invoker==null) return;
		if(affected==invoker)
			affectableStats.setStat(CharStats.STAT_DEXTERITY,affectableStats.getStat(CharStats.STAT_DEXTERITY)-amount);
		else
			affectableStats.setStat(CharStats.STAT_DEXTERITY,affectableStats.getStat(CharStats.STAT_DEXTERITY)+(amount+super.getXLEVELLevel(invoker())));
	}

	@Override
	public int castingQuality(MOB mob, Physical target)
	{
		if(mob!=null)
		{
			if(target instanceof MOB)
			{
				if(mob.getGroupMembers(new HashSet<MOB>()).size()==0)
					return Ability.QUALITY_INDIFFERENT;
			}
		}
		return super.castingQuality(mob,target);
	}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		amount=CMath.s_int(CMParms.combine(commands,0));

		if(amount<=0)
		{
			if(mob.isMonster())
				amount=mob.charStats().getStat(CharStats.STAT_DEXTERITY)/2;
			else
			{
				mob.tell(L("Sing about how much dexterity?"));
				return false;
			}
		}

		if(amount>=mob.charStats().getStat(CharStats.STAT_DEXTERITY))
		{
			mob.tell(L("You can't sing away that much dexterity."));
			return false;
		}

		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;
		return true;
	}
}
