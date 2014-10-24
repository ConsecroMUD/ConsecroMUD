package com.suscipio_solutions.consecro_mud.Abilities.Fighter;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;



@SuppressWarnings("rawtypes")
public class Fighter_Spring extends FighterSkill
{
	@Override public String ID() { return "Fighter_Spring"; }
	private final static String localizedName = CMLib.lang().L("Spring Attack");
	@Override public String name() { return localizedName; }
	private static final String[] triggerStrings =I(new String[] {"SPRINGATTACK","SPRING","SATTACK"});
	@Override public int abstractQuality(){return Ability.QUALITY_MALICIOUS;}
	@Override public String[] triggerStrings(){return triggerStrings;}
	@Override protected int canAffectCode(){return 0;}
	@Override protected int canTargetCode(){return Ability.CAN_MOBS;}
	@Override public int classificationCode(){return Ability.ACODE_SKILL|Ability.DOMAIN_ACROBATIC;}
	@Override public int usageType(){return USAGE_MOVEMENT;}

	@Override
	public int castingQuality(MOB mob, Physical target)
	{
		if((mob!=null)&&(target!=null))
		{
			if(mob.isInCombat()&&(mob.rangeToTarget()>0))
				return Ability.QUALITY_INDIFFERENT;
			if(mob.curState().getMovement()<50)
				return Ability.QUALITY_INDIFFERENT;
			if(mob.rangeToTarget()>=mob.location().maxRange())
				return Ability.QUALITY_INDIFFERENT;
		}
		return super.castingQuality(mob,target);
	}


	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		if(mob.isInCombat()&&(mob.rangeToTarget()>0))
		{
			mob.tell(L("You are too far away to make a spring attack!"));
			return false;
		}
		if(mob.curState().getMovement()<50)
		{
			mob.tell(L("You are too tired to make a spring attack."));
			return false;
		}
		if(mob.rangeToTarget()>=mob.location().maxRange())
		{
			mob.tell(L("There is no more room to spring back!"));
			return false;
		}

		final MOB target=this.getTarget(mob,commands,givenTarget);
		if(target==null) return false;

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
			invoker=mob;
			CMMsg msg=CMClass.getMsg(mob,target,this,CMMsg.MSK_MALICIOUS_MOVE|CMMsg.TYP_JUSTICE|(auto?CMMsg.MASK_ALWAYS:0),null);
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				CMLib.combat().postAttack(mob,target,mob.fetchWieldedItem());
				if(mob.getVictim()==target)
				{
					msg=CMClass.getMsg(mob,target,this,CMMsg.MSG_RETREAT,L("^F^<FIGHT^><S-NAME> spring(s) back!^</FIGHT^>^?"));
					CMLib.color().fixSourceFightColor(msg);
					if(mob.location().okMessage(mob,msg))
					{
						mob.location().send(mob,msg);
						if(mob.rangeToTarget()<mob.location().maxRange())
						{
							msg=CMClass.getMsg(mob,target,this,CMMsg.MSG_RETREAT,null);
							if(mob.location().okMessage(mob,msg))
								mob.location().send(mob,msg);
						}
					}
				}
			}
		}
		else
			return maliciousFizzle(mob,target,L("<S-NAME> fail(s) to spring attack <T-NAMESELF>."));

		// return whether it worked
		return success;
	}
}
