package com.suscipio_solutions.consecro_mud.Abilities.Spells;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;
import com.suscipio_solutions.consecro_mud.core.interfaces.Tickable;


@SuppressWarnings("rawtypes")
public class Spell_GhostSound extends Spell
{
	@Override public String ID() { return "Spell_GhostSound"; }
	private final static String localizedName = CMLib.lang().L("Ghost Sound");
	@Override public String name() { return localizedName; }
	private final static String localizedStaticDisplay = CMLib.lang().L("(Ghost Sound spell)");
	@Override public String displayText() { return localizedStaticDisplay; }
	@Override protected int canAffectCode(){return CAN_ROOMS;}
	@Override protected int canTargetCode(){return 0;}
	@Override public int abstractQuality(){ return Ability.QUALITY_MALICIOUS;}
	@Override public int classificationCode(){ return Ability.ACODE_SPELL|Ability.DOMAIN_ILLUSION;}

	@Override
	public boolean tick(Tickable ticking, int tickID)
	{
		if((tickID==Tickable.TICKID_MOB)
		&&(CMLib.dice().rollPercentage()<10)
		&&(affected!=null)
		&&(invoker!=null)
		&&(affected instanceof Room))
		switch(CMLib.dice().roll(1,14,0))
		{
		case 1:	((Room)affected).showHappens(CMMsg.MSG_NOISE,
				L("You hear something coming up behind you."));
				break;
		case 2:	((Room)affected).showHappens(CMMsg.MSG_NOISE,
				L("You hear somebody screaming in the distance."));
				break;
		case 3:	((Room)affected).showHappens(CMMsg.MSG_NOISE,
				L("You hear the snarl of a large ferocious beast."));
				break;
		case 4:	((Room)affected).showHappens(CMMsg.MSG_NOISE,
				L("You hear complete silence."));
				break;
		case 5:	((Room)affected).showHappens(CMMsg.MSG_NOISE,
				L("CLANK! Someone just dropped their sword."));
				break;
		case 6:	((Room)affected).showHappens(CMMsg.MSG_NOISE,
				L("You hear a bird singing."));
				break;
		case 7:	((Room)affected).showHappens(CMMsg.MSG_NOISE,
				L("You hear a cat dying."));
				break;
		case 8:	((Room)affected).showHappens(CMMsg.MSG_NOISE,
				L("You hear some people talking."));
				break;
		case 9:	((Room)affected).showHappens(CMMsg.MSG_NOISE,
				L("You hear singing."));
				break;
		case 10:((Room)affected).showHappens(CMMsg.MSG_NOISE,
				L("You hear a cow mooing."));
				break;
		case 11:((Room)affected).showHappens(CMMsg.MSG_NOISE,
				L("You hear your shadow."));
				break;
		case 12:((Room)affected).showHappens(CMMsg.MSG_NOISE,
				L("You hear someone trying to sneak by you."));
				break;
		case 13:((Room)affected).showHappens(CMMsg.MSG_NOISE,
				L("You hear an annoying beeping sound."));
				break;
		case 14:((Room)affected).showHappens(CMMsg.MSG_NOISE,
				L("You hear your heart beating in your chest."));
				break;
		}
		return super.tick(ticking,tickID);
	}

	@Override
	public int castingQuality(MOB mob, Physical target)
	{
		if(mob!=null)
		{
			if((mob.isMonster())&&(mob.isInCombat()))
				return Ability.QUALITY_INDIFFERENT;
			if(target instanceof MOB)
			{
			}
		}
		return super.castingQuality(mob,target);
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
			mob.tell(mob,null,null,L("There are already ghost sounds here."));
			return false;
		}


		final boolean success=proficiencyCheck(mob,0,auto);

		if(success)
		{
			// it worked, so build a copy of this ability,
			// and add it to the affects list of the
			// affected MOB.  Then tell everyone else
			// what happened.

			final CMMsg msg = CMClass.getMsg(mob, target, this, verbalCastCode(mob,target,auto),auto?"":L("^S<S-NAME> scream(s) loudly, then fall(s) silent.^?"));
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				beneficialAffect(mob,mob.location(),asLevel,0);
			}
		}
		else
			return beneficialWordsFizzle(mob,null,L("<S-NAME> scream(s) loudly, but then feel(s) disappointed."));

		// return whether it worked
		return success;
	}
}
