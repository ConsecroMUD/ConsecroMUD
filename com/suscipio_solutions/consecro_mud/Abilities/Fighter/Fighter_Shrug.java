package com.suscipio_solutions.consecro_mud.Abilities.Fighter;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Weapon;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;



@SuppressWarnings("rawtypes")
public class Fighter_Shrug extends FighterSkill
{
	@Override public String ID() { return "Fighter_Shrug"; }
	private final static String localizedName = CMLib.lang().L("Shrug Off");
	@Override public String name() { return localizedName; }
	private final static String localizedStaticDisplay = CMLib.lang().L("(Braced for a hit)");
	@Override public String displayText() { return localizedStaticDisplay; }
	private static final String[] triggerStrings =I(new String[] {"BRACE"});
	@Override public int abstractQuality(){return Ability.QUALITY_BENEFICIAL_SELF;}
	@Override public String[] triggerStrings(){return triggerStrings;}
	@Override protected int canAffectCode(){return Ability.CAN_MOBS;}
	@Override protected int canTargetCode(){return 0;}
	@Override public int classificationCode() {   return Ability.ACODE_SKILL|Ability.DOMAIN_FITNESS; }
	@Override public int usageType(){return USAGE_MOVEMENT;}

	@Override
	public boolean okMessage(final Environmental myHost, final CMMsg msg)
	{
		if((affected!=null)
		&&(affected instanceof MOB)
		&&(msg.amITarget(affected))
		&&(msg.targetMinor()==CMMsg.TYP_DAMAGE)
		&&(!msg.amISource((MOB)affected))
		&&(CMLib.flags().aliveAwakeMobile((MOB)affected,true))
		&&(msg.tool()!=null)
		&&(msg.tool() instanceof Weapon))
		{
			final MOB mob=(MOB)affected;
			if(mob.location().show(mob,msg.source(),this,CMMsg.MSG_OK_ACTION,L("<S-NAME> shrug(s) off the attack from <T-NAME>.")))
			{
				unInvoke();
				return false;
			}
		}
		return super.okMessage(myHost,msg);
	}

	@Override
	public int castingQuality(MOB mob, Physical target)
	{
		if(mob!=null)
		{
			if(!mob.isInCombat())
				return Ability.QUALITY_INDIFFERENT;
		}
		return super.castingQuality(mob,target);
	}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		if((!auto)&&(!mob.isInCombat()))
		{
			mob.tell(L("You must be in combat first!"));
			return false;
		}

		MOB target=mob;
		if((auto)&&(givenTarget!=null)&&(givenTarget instanceof MOB))
			target=(MOB)givenTarget;

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
			final CMMsg msg=CMClass.getMsg(mob,target,this,CMMsg.MSG_QUIETMOVEMENT,auto?L("<T-NAME> is braced for an attack!"):L("<S-NAME> brace(s) for an attack!"));
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				beneficialAffect(mob,target,asLevel,0);
			}
		}
		else
			return beneficialVisualFizzle(mob,null,L("<S-NAME> attempt(s) to brace <S-HIM-HERSELF>, but get(s) distracted."));

		// return whether it worked
		return success;
	}
}
