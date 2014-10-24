package com.suscipio_solutions.consecro_mud.Abilities.Spells;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.Libraries.interfaces.TrackingLibrary;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;


@SuppressWarnings("rawtypes")
public class Spell_NaturalCommunion extends Spell
{
	@Override public String ID() { return "Spell_NaturalCommunion"; }
	private final static String localizedName = CMLib.lang().L("Natural Communion");
	@Override public String name() { return localizedName; }
	@Override public int abstractQuality(){ return Ability.QUALITY_INDIFFERENT;}
	@Override protected int canTargetCode(){return 0;}
	@Override protected int canAffectCode(){return 0;}
	@Override public int classificationCode(){ return Ability.ACODE_SPELL|Ability.DOMAIN_DIVINATION;}

	public void communeWithThisRoom(MOB mob, Room room, List<String> stuff)
	{
		if(!CMLib.flags().canAccess(mob, room))
			return;
		if((room.domainType()&Room.INDOORS)==0)
		{
			try
			{
				final String desc=Room.outdoorDomainDescs[room.domainType()].toLowerCase();
				if(!stuff.contains(desc))
					stuff.add(desc);
			}
			catch(final Exception t) { }
		}
		final int resource=room.myResource()&RawMaterial.RESOURCE_MASK;
		if(RawMaterial.CODES.IS_VALID(resource))
		{
			final Physical found=CMLib.materials().makeResource(room.myResource(),Integer.toString(room.domainType()),false,null);
			if(found!=null)
			{
				final String name;
				if(found instanceof RawMaterial)
					name=RawMaterial.CODES.NAME(((RawMaterial) found).material()).toLowerCase();
				else
					name=found.name();
				if(!stuff.contains(name))
					stuff.add(name);
				found.destroy();
			}
		}
		for(final Enumeration<MOB> m = room.inhabitants(); m.hasMoreElements(); )
		{
			final MOB M=m.nextElement();
			if((CMLib.flags().isVegetable(M))&&(!stuff.contains(M.name())))
				stuff.add(M.name());
			else
			if((CMLib.flags().isAnimalIntelligence(M))&&(!stuff.contains(M.name())))
				stuff.add(M.name());
		}
	}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		final Room targetR=mob.location();
		if(targetR==null)
			return false;
		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		int chance=0;
		if((mob.location().domainType()&Room.INDOORS)>0)
			chance-=25;
		final boolean success=proficiencyCheck(mob,chance,auto);
		if(success)
		{
			final CMMsg msg=CMClass.getMsg(mob,targetR,this,somanticCastCode(mob,targetR,auto),auto?"":L("^S<S-NAME> commune(s) with <S-HIS-HER> natural surroundings.^?"));
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				final int radius=3 + super.getXLEVELLevel(mob);
				final List<Room> rooms=CMLib.tracking().getRadiantRooms(mob.location(), new TrackingLibrary.TrackingFlags(), radius);
				final List<String> stuff=new Vector<String>();
				communeWithThisRoom(mob,mob.location(),stuff);
				for(final Room R : rooms)
					communeWithThisRoom(mob,R,stuff);
				mob.tell(L("Your surroundings show the following natural signs: @x1.",CMLib.english().toEnglishStringList(stuff.toArray(new String[0]))));
			}
		}
		else
			beneficialVisualFizzle(mob,targetR,L("<S-NAME> attempt(s) to commune with nature, and fail(s)."));


		// return whether it worked
		return success;
	}
}
