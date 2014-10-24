package com.suscipio_solutions.consecro_mud.Abilities.Spells;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.GridLocale;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;


@SuppressWarnings("rawtypes")
public class Spell_Mirage extends Spell
{
	@Override public String ID() { return "Spell_Mirage"; }
	private final static String localizedName = CMLib.lang().L("Mirage");
	@Override public String name() { return localizedName; }
	private final static String localizedStaticDisplay = CMLib.lang().L("(Mirage spell)");
	@Override public String displayText() { return localizedStaticDisplay; }
	@Override protected int canAffectCode(){return CAN_ROOMS;}
	@Override protected int canTargetCode(){return CAN_ROOMS;}
	@Override public int abstractQuality(){ return Ability.QUALITY_MALICIOUS;}
	@Override public int classificationCode(){ return Ability.ACODE_SPELL|Ability.DOMAIN_ILLUSION;}

	Room newRoom=null;

	@Override
	public void unInvoke()
	{
		// undo the affects of this spell
		if(affected==null)
			return;
		if(!(affected instanceof Room))
			return;
		final Room room=(Room)affected;
		if(canBeUninvoked())
			room.showHappens(CMMsg.MSG_OK_VISUAL, L("The appearance of this place changes..."));
		super.unInvoke();
	}

	protected Room room()
	{
		if(newRoom==null)
		{
			newRoom=CMLib.map().getRoom(text());
			if(newRoom==null)
			{
				if(!(affected instanceof Room))
					return null;
				newRoom=((Room)affected).getArea().getRandomProperRoom();
			}
		}
		return newRoom;
	}


	@Override
	public boolean okMessage(final Environmental myHost, final CMMsg msg)
	{
		if((affected!=null)
		&&(affected instanceof Room)
		&&(msg.amITarget(affected))
		&&(room().fetchEffect(ID())==null)
		&&((msg.targetMinor()==CMMsg.TYP_LOOK)||(msg.targetMinor()==CMMsg.TYP_EXAMINE)))
		{
			final CMMsg msg2=CMClass.getMsg(msg.source(),room(),msg.tool(),
						  msg.sourceCode(),msg.sourceMessage(),
						  msg.targetCode(),msg.targetMessage(),
						  msg.othersCode(),msg.othersMessage());
			if(room().okMessage(msg.source(),msg2))
			{
				room().executeMsg(msg.source(),msg2);
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
			if((mob.isInCombat())&&(mob.isMonster()))
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
		if(mob.location().getArea().properSize()<2)
		{
			mob.tell(L("This area is too small to cast this spell."));
			return false;
		}

		// the invoke method for spells receives as
		// parameters the invoker, and the REMAINING
		// command line parameters, divided into words,
		// and added as String objects to a vector.
		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		final Physical target = mob.location();
		final boolean success=proficiencyCheck(mob,0,auto);

		if(success)
		{
			// it worked, so build a copy of this ability,
			// and add it to the affects list of the
			// affected MOB.  Then tell everyone else
			// what happened.

			final CMMsg msg = CMClass.getMsg(mob, target, this, verbalCastCode(mob,target,auto), auto?"":L("^S<S-NAME> speak(s) and gesture(s) dramatically!^?"));
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				mob.location().showHappens(CMMsg.MSG_OK_VISUAL,L("The appearance of this place changes..."));
				if(CMLib.law().doesOwnThisProperty(mob,mob.location()))
				{
					final Ability A=(Ability)copyOf();
					A.setInvoker(mob);
					newRoom=mob.location().getArea().getRandomProperRoom();
					if((newRoom!=null)&&(newRoom.roomID().length()>0)&&(!(newRoom instanceof GridLocale)))
					{
						A.setMiscText(CMLib.map().getExtendedRoomID(newRoom));
						mob.location().addNonUninvokableEffect(A);
						CMLib.database().DBUpdateRoom(mob.location());
					}
				}
				else
					beneficialAffect(mob,mob.location(),asLevel,0);
			}
		}
		else
			return beneficialWordsFizzle(mob,null,L("<S-NAME> speak(s) and gesture(s) dramatically, but the spell fizzles."));

		// return whether it worked
		return success;
	}
}
