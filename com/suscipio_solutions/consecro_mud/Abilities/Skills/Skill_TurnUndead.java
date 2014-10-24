package com.suscipio_solutions.consecro_mud.Abilities.Skills;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;


@SuppressWarnings("rawtypes")
public class Skill_TurnUndead extends StdSkill
{
	@Override public String ID() { return "Skill_TurnUndead"; }
	private final static String localizedName = CMLib.lang().L("Turn Undead");
	@Override public String name() { return localizedName; }
	private final static String localizedStaticDisplay = CMLib.lang().L("(Turned)");
	@Override public String displayText() { return localizedStaticDisplay; }
	@Override protected int canAffectCode(){return 0;}
	@Override protected int canTargetCode(){return CAN_MOBS;}
	@Override public int classificationCode(){return Ability.ACODE_SKILL|Ability.DOMAIN_DEATHLORE;}
	@Override public int abstractQuality(){return Ability.QUALITY_MALICIOUS;}
	private static final String[] triggerStrings =I(new String[] {"TURN"});
	@Override public String[] triggerStrings(){return triggerStrings;}

	@Override
	public int castingQuality(MOB mob, Physical target)
	{
		if((mob!=null)&&(target!=null))
		{
			if(!(target instanceof MOB)) return Ability.QUALITY_INDIFFERENT;
			final MOB targetM=(MOB)target;
			if((targetM.baseCharStats().getMyRace()==null)
			||(!targetM.baseCharStats().getMyRace().racialCategory().equals("Undead")))
				return Ability.QUALITY_INDIFFERENT;
			if(CMLib.flags().isEvil(mob))
				return Ability.QUALITY_INDIFFERENT;
		}
		return super.castingQuality(mob,target);
	}


	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		final MOB target=this.getTarget(mob,commands,givenTarget);
		if(target==null) return false;

		if((target.baseCharStats().getMyRace()==null)
		   ||(!target.baseCharStats().getMyRace().racialCategory().equals("Undead")))
		{
			mob.tell(auto?L("Only the undead can be turned."):L("You can only turn the undead."));
			return false;
		}

		if(CMLib.flags().isEvil(mob))
		{
			mob.tell(L("Only the riteous may turn the undead."));
			return false;
		}

		// the invoke method for spells receives as
		// parameters the invoker, and the REMAINING
		// command line parameters, divided into words,
		// and added as String objects to a vector.
		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		final boolean success=proficiencyCheck(mob,((mob.phyStats().level()+(4*getXLEVELLevel(mob)))-target.phyStats().level())*30,auto);

		if(success)
		{
			// it worked, so build a copy of this ability,
			// and add it to the affects list of the
			// affected MOB.  Then tell everyone else
			// what happened.
			final CMMsg msg=CMClass.getMsg(mob,target,this,CMMsg.MSG_CAST_ATTACK_SOMANTIC_SPELL|(auto?CMMsg.MASK_ALWAYS:0),auto?L("<T-NAME> turn(s) away."):L("^S<S-NAME> turn(s) <T-NAMESELF>.^?"));
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				if(msg.value()<=0)
				{
					if((mob.phyStats().level()-target.phyStats().level())>6)
					{
						mob.location().show(mob,target,CMMsg.MSG_OK_ACTION,L("<T-NAME> wither(s)"+(auto?".":" under <S-HIS-HER> holy power!")));
						CMLib.combat().postDamage(mob,target,this,target.curState().getHitPoints(),CMMsg.MASK_ALWAYS|CMMsg.TYP_CAST_SPELL,-1,null);
					}
					else
					{
						mob.location().show(target,null,CMMsg.MSG_OK_VISUAL,L("<S-NAME> shake(s) in fear!"));
						CMLib.commands().postFlee(target,"");
					}
					invoker=mob;
				}
			}
		}
		else
			return maliciousFizzle(mob,target,L("<S-NAME> attempt(s) to turn <T-NAMESELF>, but fail(s)."));


		// return whether it worked
		return success;
	}
}
