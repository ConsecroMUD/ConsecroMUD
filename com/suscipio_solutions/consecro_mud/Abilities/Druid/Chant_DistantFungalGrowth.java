package com.suscipio_solutions.consecro_mud.Abilities.Druid;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;



@SuppressWarnings("rawtypes")
public class Chant_DistantFungalGrowth extends Chant
{
	@Override public String ID() { return "Chant_DistantFungalGrowth"; }
	private final static String localizedName = CMLib.lang().L("Distant Fungal Growth");
	@Override public String name() { return localizedName; }
	@Override public int classificationCode(){return Ability.ACODE_CHANT|Ability.DOMAIN_PLANTGROWTH;}
	@Override public int abstractQuality(){return Ability.QUALITY_INDIFFERENT;}
	@Override protected int canAffectCode(){return 0;}
	@Override protected int canTargetCode(){return CAN_ROOMS;}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{

		if(commands.size()<1)
		{
			mob.tell(L("Grow plants where?"));
			return false;
		}

		final String areaName=CMParms.combine(commands,0).trim().toUpperCase();
		Room anyRoom=null;
		Room newRoom=null;
		try
		{
			final List<Room> rooms=CMLib.map().findRooms(CMLib.map().rooms(), mob, areaName, true, 10);
			for(final Room R : rooms)
			{
				anyRoom=R;
				if(R.domainType()==Room.DOMAIN_INDOORS_CAVE)
				{
					newRoom=R;
					break;
				}
			}
		}catch(final NoSuchElementException e){}

		if(newRoom==null)
		{
			if(anyRoom==null)
				mob.tell(L("You don't know of a place called '@x1'.",CMParms.combine(commands,0)));
			else
			if((anyRoom.domainType()&Room.INDOORS)==0)
				mob.tell(L("There IS such a place, but it is outdoors, where your fungus will not grow."));
			else
			if((anyRoom.domainType()==Room.DOMAIN_INDOORS_UNDERWATER)
			||(anyRoom.domainType()==Room.DOMAIN_INDOORS_WATERSURFACE))
				mob.tell(L("There IS such a place, but it is on or in the water, so your magic would fail."));
			else
				mob.tell(L("There IS such a place, but it is not in a cave, so your magic would fail."));
			return false;
		}

		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		final boolean success=proficiencyCheck(mob,0,auto);

		if(success)
		{
			final CMMsg msg=CMClass.getMsg(mob,null,this,verbalCastCode(mob,null,auto),L("^S<S-NAME> chant(s) about a far away place.^?"));
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				final Item newItem=new Chant_SummonFungus().buildFungus(mob,newRoom);
				mob.tell(L("You feel a distant connection with @x1",newItem.name()));
			}
		}
		else
			beneficialWordsFizzle(mob,null,L("<S-NAME> chant(s) about a far away place, but the magic fades."));


		// return whether it worked
		return success;
	}
}
