package com.suscipio_solutions.consecro_mud.Abilities.Spells;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.PhyStats;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.Races.interfaces.Race;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;


@SuppressWarnings("rawtypes")
public class Spell_LightSensitivity extends Spell
{
	@Override public String ID() { return "Spell_LightSensitivity"; }
	private final static String localizedName = CMLib.lang().L("Light Sensitivity");
	@Override public String name() { return localizedName; }
	private final static String localizedStaticDisplay = CMLib.lang().L("(Light Sensitivity)");
	@Override public String displayText() { return localizedStaticDisplay; }
	@Override public int abstractQuality(){return Ability.QUALITY_MALICIOUS;}
	@Override protected int canAffectCode(){return CAN_MOBS;}
	@Override public int classificationCode(){return Ability.ACODE_SPELL|Ability.DOMAIN_TRANSMUTATION;}

	@Override
	public void affectPhyStats(Physical affected, PhyStats affectableStats)
	{
		super.affectPhyStats(affected,affectableStats);
		if(!(affected instanceof MOB)) return;
		if(((MOB)affected).location()==null) return;
		if(CMLib.flags().isInDark(((MOB)affected).location()))
			affectableStats.setSensesMask(affectableStats.sensesMask()|PhyStats.CAN_SEE_DARK);
		else
			affectableStats.setSensesMask(affectableStats.sensesMask()|PhyStats.CAN_NOT_SEE);
	}


	@Override
	public void unInvoke()
	{
		// undo the affects of this spell
		if(!(affected instanceof MOB))
			return;
		final MOB mob=(MOB)affected;
		super.unInvoke();

		if(canBeUninvoked())
			mob.tell(L("Your light sensitivity returns to normal."));
	}

	@Override
	public int castingQuality(MOB mob, Physical target)
	{
		if(mob!=null)
		{
			if(mob.isInCombat())
			{
				if(CMLib.flags().isInDark(mob.location()))
					return Ability.QUALITY_INDIFFERENT;
				if(target instanceof MOB)
				{
					if(((MOB)target).charStats().getBodyPart(Race.BODY_EYE)==0)
						return Ability.QUALITY_INDIFFERENT;
					if(!CMLib.flags().canSee((MOB)target))
						return Ability.QUALITY_INDIFFERENT;
				}
			}
		}
		return super.castingQuality(mob,target);
	}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		final MOB target=this.getTarget(mob,commands,givenTarget);
		if(target==null) return false;

		if((!auto)&&(target.charStats().getBodyPart(Race.BODY_EYE)==0))
		{
			mob.tell(L("@x1 has no eyes, and would not be affected.",target.name(mob)));
			return false;
		}

		// the invoke method for spells receives as
		// parameters the invoker, and the REMAINING
		// command line parameters, divided into words,
		// and added as String objects to a vector.
		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		boolean success=proficiencyCheck(mob,0,auto);

		if(success)
		{
			// it worked, so build a copy of this ability,
			// and add it to the affects list of the
			// affected MOB.  Then tell everyone else
			// what happened.
			invoker=mob;
			final String autoStr=L("A flashing light blazes in the eyes of <T-NAME>!");
			final CMMsg msg=CMClass.getMsg(mob,target,this,verbalCastCode(mob,target,auto),auto?autoStr:L("^SYou invoke a sensitive light into <T-NAME>s eyes.^?"),verbalCastCode(mob,target,auto),auto?autoStr:L("^S<S-NAME> invoke(s) a sensitive light into your eyes.^?"),CMMsg.MSG_CAST_ATTACK_VERBAL_SPELL,auto?autoStr:L("^S<S-NAME> invokes a sensitive light into <T-NAME>s eyes.^?"));
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				if(msg.value()<=0)
				{
					if(CMLib.flags().isInDark(mob.location()))
						mob.location().show(target,null,CMMsg.MSG_OK_VISUAL,L("<S-NAME> become(s) extremely sensitive to light."));
					else
						mob.location().show(target,null,CMMsg.MSG_OK_VISUAL,L("<S-NAME> become(s) blinded by the light."));
					if(castingQuality(mob,target)==Ability.QUALITY_MALICIOUS)
						success=maliciousAffect(mob,target,asLevel,0,-1)!=null;
					else
						success=beneficialAffect(mob,target,asLevel,0)!=null;
				}
			}
		}
		else
		if(castingQuality(mob,target)==Ability.QUALITY_MALICIOUS)
			return maliciousFizzle(mob,target,L("<S-NAME> invoke(s) at <T-NAMESELF>, but the spell fizzles."));
		else
			return beneficialVisualFizzle(mob,target,L("<S-NAME> invoke(s) at <T-NAMESELF>, but the spell fizzles."));

		// return whether it worked
		return success;
	}
}
