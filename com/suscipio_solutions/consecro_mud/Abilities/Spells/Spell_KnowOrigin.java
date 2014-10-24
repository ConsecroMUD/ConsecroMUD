package com.suscipio_solutions.consecro_mud.Abilities.Spells;
import java.util.NoSuchElementException;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Wearable;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.LandTitle;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;



@SuppressWarnings("rawtypes")
public class Spell_KnowOrigin extends Spell
{
	@Override public String ID() { return "Spell_KnowOrigin"; }
	private final static String localizedName = CMLib.lang().L("Know Origin");
	@Override public String name() { return localizedName; }
	@Override public int abstractQuality(){return Ability.QUALITY_INDIFFERENT;}
	@Override protected int canAffectCode(){return 0;}
	@Override protected int canTargetCode(){return Ability.CAN_MOBS|Ability.CAN_ITEMS;}
	@Override public int classificationCode(){ return Ability.ACODE_SPELL|Ability.DOMAIN_DIVINATION;}

	public Room origin(MOB mob, Environmental meThang)
	{
		if(meThang instanceof LandTitle)
			return ((LandTitle)meThang).getAllTitledRooms().get(0);
		else
		if(meThang instanceof MOB)
			return ((MOB)meThang).getStartRoom();
		else
		if(meThang instanceof Item)
		{
			final Item me=(Item)meThang;
			try
			{
				// check mobs worn items first!
				final String srchStr="$"+me.Name()+"$";
				Environmental E=CMLib.map().findFirstShopStocker(CMLib.map().rooms(), mob, srchStr, 10);
				if(E!=null) return CMLib.map().getStartRoom(E);
				E=CMLib.map().findFirstInventory(CMLib.map().rooms(), mob, srchStr, 10);
				if(E!=null) return CMLib.map().getStartRoom(E);
				return CMLib.map().findWorldRoomLiberally(mob,srchStr, "I",10,600000);
			}catch(final NoSuchElementException nse){}
		}
		return null;
	}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		final Physical target=getAnyTarget(mob,commands,givenTarget,Wearable.FILTER_ANY);
		if(target==null) return false;

		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		final Room R=origin(mob,target);
		final boolean success=proficiencyCheck(mob,0,auto);
		if((success)&&(R!=null))
		{
			final CMMsg msg=CMClass.getMsg(mob,target,this,verbalCastCode(mob,target,auto),auto?"":L("^S<S-NAME> incant(s), divining the origin of <T-NAMESELF>.^?"));
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				mob.tell(L("@x1 seems to come from '@x2'.",target.name(mob),R.displayText(mob)));
			}
		}
		else
			beneficialWordsFizzle(mob,target,L("<S-NAME> attempt(s) to divine something, but fail(s)."));

		return success;
	}
}
