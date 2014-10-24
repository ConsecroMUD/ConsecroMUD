package com.suscipio_solutions.consecro_mud.Abilities.Songs;
import java.util.Enumeration;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Areas.interfaces.Area;
import com.suscipio_solutions.consecro_mud.Behaviors.interfaces.LegalBehavior;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;


@SuppressWarnings("rawtypes")
public class Skill_FalseArrest extends BardSkill
{
	@Override public String ID() { return "Skill_FalseArrest"; }
	private final static String localizedName = CMLib.lang().L("False Arrest");
	@Override public String name() { return localizedName; }
	@Override protected int canAffectCode(){return Ability.CAN_MOBS;}
	@Override protected int canTargetCode(){return Ability.CAN_MOBS;}
	@Override public int abstractQuality(){return Ability.QUALITY_INDIFFERENT;}
	private static final String[] triggerStrings =I(new String[] {"FALSEARREST"});
	@Override public String[] triggerStrings(){return triggerStrings;}
	@Override protected int overrideMana(){return 50;}
	@Override public int classificationCode(){return Ability.ACODE_SKILL|Ability.DOMAIN_DECEPTIVE;}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		final MOB target=getTarget(mob,commands,givenTarget);
		if(target==null) return false;
		if(mob==target)
		{
			mob.tell(L("Arrest whom?!"));
			return false;
		}
		if(mob.isInCombat())
		{
			mob.tell(L("Not while you are fighting!"));
			return false;
		}

		LegalBehavior B=null;
		Area A2=null;
		if(mob.location()!=null)
		{
			B=CMLib.law().getLegalBehavior(mob.location());
			if((B==null)||(!B.hasWarrant(CMLib.law().getLegalObject(mob.location()),target)))
				B=null;
			else
				A2=CMLib.law().getLegalObject(mob.location());
		}

		if(B==null)
		for(final Enumeration e=CMLib.map().areas();e.hasMoreElements();)
		{
			final Area A=(Area)e.nextElement();
			if(CMLib.flags().canAccess(mob,A))
			{
				B=CMLib.law().getLegalBehavior(A);
				if((B!=null)
				&&(B.hasWarrant(CMLib.law().getLegalObject(A),target)))
				{
					A2=CMLib.law().getLegalObject(A);
					break;
				}
			}
			B=null;
			A2=null;
		}

		if(B==null)
		{
			mob.tell(L("@x1 is not wanted for anything, anywhere.",target.name(mob)));
			return false;
		}

		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		int levelDiff=target.phyStats().level()-(mob.phyStats().level()+abilityCode()+(2*super.getXLEVELLevel(mob)));
		if(levelDiff>0)
			levelDiff=levelDiff*5;
		else
			levelDiff=0;

		final boolean success=proficiencyCheck(mob,-levelDiff,auto);

		if(!success)
		{
			beneficialWordsFizzle(mob,target,L("<S-NAME> frown(s) at <T-NAMESELF>, but lose(s) the nerve."));
			return false;
		}
		final CMMsg msg=CMClass.getMsg(mob,target,this,CMMsg.MSG_DELICATE_HANDS_ACT,L("<S-NAME> frown(s) at <T-NAMESELF>."),CMMsg.NO_EFFECT,null,CMMsg.NO_EFFECT,null);
		if(mob.location().okMessage(mob,msg))
		{
			mob.location().send(mob,msg);
			if(!B.arrest(A2,mob,target))
			{
				mob.tell(L("You are not able to arrest @x1 at this time.",target.name(mob)));
				return false;
			}
		}
		return success;
	}

}
