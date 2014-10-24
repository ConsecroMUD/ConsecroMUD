package com.suscipio_solutions.consecro_mud.Abilities.Spells;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;


@SuppressWarnings("rawtypes")
public class Spell_DistantVision extends Spell
{
	@Override public String ID() { return "Spell_DistantVision"; }
	private final static String localizedName = CMLib.lang().L("Distant Vision");
	@Override public String name() { return localizedName; }
	@Override protected int canTargetCode(){return 0;}
	@Override public int classificationCode(){ return Ability.ACODE_SPELL|Ability.DOMAIN_DIVINATION;}
	@Override public int abstractQuality(){ return Ability.QUALITY_INDIFFERENT;}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		if(commands.size()<1)
		{
			mob.tell(L("Divine a vision of where?"));
			return false;
		}
		final String areaName=CMParms.combine(commands,0).trim().toUpperCase();
		Room thisRoom=null;
		try
		{
			final List<Room> rooms=CMLib.map().findRooms(CMLib.map().rooms(), mob, areaName, true, 10);
			if(rooms.size()>0)
				thisRoom=rooms.get(CMLib.dice().roll(1,rooms.size(),-1));
		}catch(final NoSuchElementException nse){}

		if(thisRoom==null)
		{
			mob.tell(L("You can't seem to fixate on a place called '@x1'.",CMParms.combine(commands,0)));
			return false;
		}

		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		final boolean success=proficiencyCheck(mob,0,auto);

		if(success)
		{
			final CMMsg msg=CMClass.getMsg(mob,null,this,verbalCastCode(mob,thisRoom,auto),auto?"":L("^S<S-NAME> close(s) <S-HIS-HER> eyes, and invoke(s) a vision.^?"));
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				mob.tell(L("\n\r\n\r"));
				final CMMsg msg2=CMClass.getMsg(mob,thisRoom,CMMsg.MSG_LOOK,null);
				thisRoom.executeMsg(mob,msg2);
			}

		}
		else
			beneficialWordsFizzle(mob,null,L("<S-NAME> close(s) <S-HIS-HER> eyes, incanting, but then open(s) them in frustration."));


		// return whether it worked
		return success;
	}
}
