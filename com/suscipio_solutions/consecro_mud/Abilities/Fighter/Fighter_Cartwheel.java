package com.suscipio_solutions.consecro_mud.Abilities.Fighter;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.Races.interfaces.Race;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;



@SuppressWarnings("rawtypes")
public class Fighter_Cartwheel extends FighterSkill
{
	@Override public String ID() { return "Fighter_Cartwheel"; }
	private final static String localizedName = CMLib.lang().L("Cartwheel");
	@Override public String name() { return localizedName; }
	private static final String[] triggerStrings =I(new String[] {"CARTWHEEL"});
	@Override public int abstractQuality(){return Ability.QUALITY_OK_SELF;}
	@Override public String[] triggerStrings(){return triggerStrings;}
	@Override protected int canAffectCode(){return 0;}
	@Override protected int canTargetCode(){return Ability.CAN_MOBS;}
	@Override public int classificationCode(){ return Ability.ACODE_SKILL|Ability.DOMAIN_ACROBATIC;}
	@Override public int usageType(){return USAGE_MOVEMENT;}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		final MOB victim=mob.getVictim();
		if(victim==null)
		{
			mob.tell(L("You can only do this in combat!"));
			return false;
		}
		if(mob.rangeToTarget()>=mob.location().maxRange())
		{
			mob.tell(L("You can not get any further away here!"));
			return false;
		}
		if((mob.charStats().getBodyPart(Race.BODY_LEG)<=1)
		||(mob.charStats().getBodyPart(Race.BODY_ARM)<=1))
		{
			mob.tell(L("You need arms and legs to do this."));
			return false;
		}

		// the invoke method for spells receives as
		// parameters the invoker, and the REMAINING
		// command line parameters, divided into words,
		// and added as String objects to a vector.
		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		// now see if it worked
		final boolean success=proficiencyCheck(mob,0,auto);
		if(success)
		{
			// it worked, so build a copy of this ability,
			// and add it to the affects list of the
			// affected MOB.  Then tell everyone else
			// what happened.
			CMMsg msg=CMClass.getMsg(mob,victim,this,CMMsg.MSG_RETREAT,L("<S-NAME> cartwheel(s) away from <T-NAMESELF>!"));
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				int tries=2;
				while(((--tries)>=0)&&(mob.rangeToTarget()<mob.location().maxRange()))
				{
					msg=CMClass.getMsg(mob,victim,this,CMMsg.MSG_RETREAT,null);
					if(mob.location().okMessage(mob,msg))
						mob.location().send(mob,msg);
				}
			}
		}
		else
			return beneficialVisualFizzle(mob,null,L("<S-NAME> attempt(s) to cartwheel and fail(s)."));

		// return whether it worked
		return success;
	}
}
