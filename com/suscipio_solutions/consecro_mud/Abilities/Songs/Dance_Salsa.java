package com.suscipio_solutions.consecro_mud.Abilities.Songs;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Commands.interfaces.Command;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CharStats;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Wearable;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;
import com.suscipio_solutions.consecro_mud.core.interfaces.Tickable;



@SuppressWarnings({"unchecked","rawtypes"})
public class Dance_Salsa extends Dance
{
	@Override public String ID() { return "Dance_Salsa"; }
	private final static String localizedName = CMLib.lang().L("Salsa");
	@Override public String name() { return localizedName; }
	@Override public int abstractQuality(){ return Ability.QUALITY_BENEFICIAL_SELF;}

	@Override
	public boolean tick(Tickable ticking, int tickID)
	{
		if(!super.tick(ticking,tickID))
			return false;

		final MOB mob=(MOB)affected;
		if(mob==null)
			return false;

		final Vector choices=new Vector();
		for(int i=0;i<mob.location().numInhabitants();i++)
		{
			final MOB M=mob.location().fetchInhabitant(i);
			if((M!=null)
			&&(M!=mob)
			&&(CMLib.flags().canBeSeenBy(M,mob))
			&&(M.charStats().getStat(CharStats.STAT_GENDER)!=mob.charStats().getStat(CharStats.STAT_GENDER))
			&&(M.charStats().getStat(CharStats.STAT_GENDER)!='N')
			&&(M.charStats().getSave(CharStats.STAT_CHARISMA)>14))
				choices.addElement(M);
		}
		if(choices.size()>0)
		{
			final MOB M=(MOB)choices.elementAt(CMLib.dice().roll(1,choices.size(),-1));
			if(CMLib.dice().rollPercentage()==1)
			{
				Item I=mob.fetchFirstWornItem(Wearable.WORN_WAIST);
				if(I!=null)	CMLib.commands().postRemove(mob,I,false);
				I=mob.fetchFirstWornItem(Wearable.WORN_LEGS);
				if(I!=null)	CMLib.commands().postRemove(mob,I,false);
				mob.doCommand(CMParms.parse("MATE "+M.Name()),Command.METAFLAG_FORCED);
			}
			else
			if(CMLib.dice().rollPercentage()>10)
				switch(CMLib.dice().roll(1,5,0))
				{
				case 1:
					mob.tell(L("You feel strange urgings towards @x1.",M.name(mob)));
					break;
				case 2:
					mob.tell(L("You have strong happy feelings towards @x1.",M.name(mob)));
					break;
				case 3:
					mob.tell(L("You feel very appreciative of @x1.",M.name(mob)));
					break;
				case 4:
					mob.tell(L("You feel very close to @x1.",M.name(mob)));
					break;
				case 5:
					mob.tell(L("You feel lovingly towards @x1.",M.name(mob)));
					break;
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

	@Override
	public void affectCharStats(MOB affected, CharStats affectableStats)
	{
		super.affectCharStats(affected,affectableStats);
		affectableStats.setStat(CharStats.STAT_CHARISMA,affectableStats.getStat(CharStats.STAT_CHARISMA)+4+getXLEVELLevel(invoker()));
	}

}
