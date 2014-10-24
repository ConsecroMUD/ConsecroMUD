package com.suscipio_solutions.consecro_mud.Abilities.Spells;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CharStats;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;
import com.suscipio_solutions.consecro_mud.core.interfaces.Tickable;


@SuppressWarnings("rawtypes")
public class Spell_MindLight extends Spell
{
	@Override public String ID() { return "Spell_MindLight"; }
	private final static String localizedName = CMLib.lang().L("Mind Light");
	@Override public String name() { return localizedName; }
	private final static String localizedStaticDisplay = CMLib.lang().L("(Mind Light spell)");
	@Override public String displayText() { return localizedStaticDisplay; }
	@Override protected int canAffectCode(){return CAN_ROOMS;}
	@Override protected int canTargetCode(){return CAN_ROOMS;}
	@Override public int classificationCode(){	return Ability.ACODE_SPELL|Ability.DOMAIN_ENCHANTMENT;}
	@Override public int abstractQuality(){ return Ability.QUALITY_INDIFFERENT;}

	@Override
	public void unInvoke()
	{
		// undo the affects of this spell
		if(affected==null)
			return;
		if(!(affected instanceof Room))
			return;
		final Room room=(Room)affected;
		super.unInvoke();
		if(canBeUninvoked())
		{
			room.showHappens(CMMsg.MSG_OK_VISUAL, L("The mind light starts to fade."));
		}
	}

	@Override
	public boolean tick(Tickable ticking, int tickID)
	{
		if(!super.tick(ticking,tickID)) return false;
		if(!(affected instanceof Room)) return true;
		final Room R=(Room)affected;
		if((invoker()!=null)&&(canBeUninvoked()))
		{
			if(!R.isInhabitant(invoker()))
			   unInvoke();
			return false;
		}
		for(int m=0;m<R.numInhabitants();m++)
		{
			final MOB M=R.fetchInhabitant(m);
			if(M!=null)
			{
				if(invoker()!=null)
					M.curState().adjMana((invoker().charStats().getStat(CharStats.STAT_INTELLIGENCE)+invoker().charStats().getStat(CharStats.STAT_WISDOM))/2,M.maxState());
				else
					M.curState().adjMana((M.charStats().getStat(CharStats.STAT_INTELLIGENCE)+M.charStats().getStat(CharStats.STAT_WISDOM))/2,M.maxState());
			}
		}
		return true;
	}


	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		// the invoke method for spells receives as
		// parameters the invoker, and the REMAINING
		// command line parameters, divided into words,
		// and added as String objects to a vector.
		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		final Physical target = mob.location();

		if(target.fetchEffect(this.ID())!=null)
		{
			mob.tell(mob,null,null,L("The Mind Light is already here!"));
			return false;
		}


		final boolean success=proficiencyCheck(mob,0,auto);

		if(success)
		{
			// it worked, so build a copy of this ability,
			// and add it to the affects list of the
			// affected MOB.  Then tell everyone else
			// what happened.

			final CMMsg msg = CMClass.getMsg(mob, target, this, verbalCastCode(mob,target,auto), L((auto?"T":"^S<S-NAME> incant(s) and gesture(s) and t")+"he mind light envelopes everyone.^?"));
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				beneficialAffect(mob,mob.location(),asLevel,0);
				mob.location().recoverRoomStats();
			}
		}
		else
			return beneficialWordsFizzle(mob,null,L("<S-NAME> incant(s) lightly, but the spell fizzles."));

		// return whether it worked
		return success;
	}
}
