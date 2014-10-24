package com.suscipio_solutions.consecro_mud.Abilities.Immortal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.StdAbility;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;

@SuppressWarnings("rawtypes")
public class Immortal_Skill extends StdAbility
{
	@Override public String ID() { return "Immortal_Skill"; }
	private final static String localizedName = CMLib.lang().L("an Immortal Skill");
	@Override public String name() { return localizedName; }
	private final static String localizedStaticDisplay = CMLib.lang().L("(in the realms of greatest power)");
	@Override public String displayText() { return localizedStaticDisplay; }
	@Override public boolean putInCommandlist(){return false;}
	@Override protected int canAffectCode(){return 0;}
	@Override protected int canTargetCode(){return 0;}

	@Override
	public int classificationCode()
	{ return Ability.ACODE_SKILL|Ability.DOMAIN_IMMORTAL;	}

	public MOB getTargetAnywhere(MOB mob, Vector commands, Environmental givenTarget, boolean playerOnly)
	{ return getTargetAnywhere(mob,commands,givenTarget,false,false,playerOnly);	}

	public MOB getTargetAnywhere(MOB mob, Vector commands, Environmental givenTarget, boolean quiet, boolean alreadyAffOk, boolean playerOnly)
	{
		MOB target=super.getTarget(mob,commands,givenTarget,true,alreadyAffOk);
		if(target!=null) return target;

		String targetName=CMParms.combine(commands,0);
		if((givenTarget!=null)&&(givenTarget instanceof MOB))
			target=(MOB)givenTarget;
		else
		if((targetName.length()==0)&&(mob.isInCombat())&&(castingQuality(mob,mob.getVictim())==Ability.QUALITY_MALICIOUS))
		   target=mob.getVictim();
		else
		if((targetName.length()==0)&&(castingQuality(mob,mob)==Ability.QUALITY_BENEFICIAL_SELF))
			target=mob;
		else
		if((targetName.length()==0)&&(abstractQuality()!=Ability.QUALITY_MALICIOUS))
			target=mob;
		else
		if(targetName.equalsIgnoreCase("self")||targetName.equalsIgnoreCase("me"))
		   target=mob;
		else
		if(targetName.length()>0)
		{
			try
			{
				final List<MOB> targets=CMLib.map().findInhabitants(CMLib.map().rooms(), mob, targetName, 50);
				if(targets.size()>0)
					target=targets.get(CMLib.dice().roll(1,targets.size(),-1));
			}
			catch(final NoSuchElementException e){}
		}

		if((target==null)||((playerOnly)&&(target.isMonster())))
		{
			if(CMLib.players().playerExists(targetName))
				target=CMLib.players().getLoadPlayer(targetName);
		}

		if((target!=null)&&((!playerOnly)||(!target.isMonster())))
			targetName=target.name();


		if(((target==null)||((playerOnly)&&(target.isMonster())))
		||((givenTarget==null)&&(!CMLib.flags().canBeSeenBy(target,mob))&&((!CMLib.flags().canBeHeardMovingBy(target,mob))||(!target.isInCombat()))))
		{
			if(!quiet)
			{
				if(targetName.trim().length()==0)
					mob.tell(L("You don't know of anyone called '@x1'.",targetName));
				else
					mob.tell(L("You don't know of anyone called '@x1' here.",targetName));
			}
			return null;
		}

		if((!alreadyAffOk)&&(!isAutoInvoked())&&(target.fetchEffect(this.ID())!=null))
		{
			if((givenTarget==null)&&(!quiet))
			{
				if(target==mob)
					mob.tell(L("You are already affected by @x1.",name()));
				else
					mob.tell(target,null,null,L("<S-NAME> is already affected by @x1.",name()));
			}
			return null;
		}
		return target;
	}

}
