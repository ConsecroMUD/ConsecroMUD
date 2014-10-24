package com.suscipio_solutions.consecro_mud.Abilities.Spells;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Wearable;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;


@SuppressWarnings("rawtypes")
public class Spell_Alarm extends Spell
{
	@Override public String ID() { return "Spell_Alarm"; }
	private final static String localizedName = CMLib.lang().L("Alarm");
	@Override public String name() { return localizedName; }
	@Override protected int canAffectCode(){return CAN_ITEMS;}
	@Override protected int canTargetCode(){return CAN_ITEMS;}
	@Override public int abstractQuality(){ return Ability.QUALITY_INDIFFERENT;}
	@Override public int classificationCode(){	return Ability.ACODE_SPELL | Ability.DOMAIN_ENCHANTMENT;}
	Room myRoomContainer=null;
	boolean waitingForLook=false;

	@Override
	public void executeMsg(final Environmental myHost, final CMMsg msg)
	{
		super.executeMsg(myHost,msg);

		if((affected==null)||(invoker==null))
		{
			unInvoke();
			return;
		}

		if(msg.source()!=null)
		{
			myRoomContainer=msg.source().location();
			if(msg.source()==invoker) return;
		}

		if(msg.amITarget(affected))
		{
			myRoomContainer.showHappens(CMMsg.MSG_NOISE,L("A HORRENDOUS ALARM GOES OFF, WHICH SEEMS TO BE COMING FROM @x1!!!",affected.name().toUpperCase()));
			invoker.tell(L("The alarm on your @x1 has gone off.",affected.name()));
			unInvoke();
		}
	}


	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		final Physical target=getTarget(mob,mob.location(),givenTarget,commands,Wearable.FILTER_UNWORNONLY);
		if(target==null) return false;

		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		final boolean success=proficiencyCheck(mob,0,auto);
		if(success)
		{
			final CMMsg msg=CMClass.getMsg(mob,target,this,verbalCastCode(mob,target,auto),auto?L("<T-NAME> glow(s) faintly for a short time."):L("^S<S-NAME> touch(es) <T-NAMESELF> very lightly.^?"));
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				myRoomContainer=mob.location();
				beneficialAffect(mob,target,asLevel,0);
			}

		}
		else
			beneficialWordsFizzle(mob,target,L("<S-NAME> speak(s) and touch(es) <T-NAMESELF> very lightly, but the spell fizzles."));


		// return whether it worked
		return success;
	}
}
