package com.suscipio_solutions.consecro_mud.Abilities.Druid;
import java.util.List;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CharStats;
import com.suscipio_solutions.consecro_mud.Libraries.interfaces.TrackingLibrary;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMStrings;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;




@SuppressWarnings("rawtypes")
public class Chant_SenseSentience extends Chant
{
	@Override public String ID() { return "Chant_SenseSentience"; }
	private final static String localizedName = CMLib.lang().L("Sense Sentience");
	@Override public String name() { return localizedName; }
	@Override public int classificationCode(){return Ability.ACODE_CHANT|Ability.DOMAIN_BREEDING;}
	@Override public int abstractQuality(){ return Ability.QUALITY_OK_SELF;}
	@Override protected int canTargetCode(){return 0;}
	@Override protected int canAffectCode(){return 0;}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		final boolean success=proficiencyCheck(mob,0,auto);

		if(success)
		{
			final CMMsg msg=CMClass.getMsg(mob,null,this,verbalCastCode(mob,null,auto),auto?"":L("^S<S-NAME> chant(s) softly to <S-HIM-HERSELF>!^?"));
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				final StringBuffer lines=new StringBuffer("^x");
				lines.append(CMStrings.padRight(L("Name"),25)+"| ");
				lines.append(CMStrings.padRight(L("Location"),17)+"^.^N\n\r");
				TrackingLibrary.TrackingFlags flags;
				flags = new TrackingLibrary.TrackingFlags()
						.plus(TrackingLibrary.TrackingFlag.AREAONLY);
				final List<Room> checkSet=CMLib.tracking().getRadiantRooms(mob.location(),flags,50);
				if(!checkSet.contains(mob.location())) checkSet.add(mob.location());
				for (final Room room : checkSet)
				{
					final Room R=CMLib.map().getRoom(room);
					if((((R.domainType()&Room.INDOORS)==0)
						&&(R.domainType()!=Room.DOMAIN_OUTDOORS_CITY)
						&&(R.domainType()!=Room.DOMAIN_OUTDOORS_SPACEPORT))
					||(R==mob.location()))
					for(int m=0;m<R.numInhabitants();m++)
					{
						final MOB M=R.fetchInhabitant(m);
						if((M!=null)&&(M.charStats().getStat(CharStats.STAT_INTELLIGENCE)>=2))
						{
							lines.append("^!"+CMStrings.padRight(M.name(mob),25)+"^?| ");
							lines.append(R.displayText(mob));
							lines.append("\n\r");
						}
					}
				}
				mob.tell(lines.toString()+"^.");
			}
		}
		else
			beneficialVisualFizzle(mob,null,L("<S-NAME> chant(s) softly to <S-HIM-HERSELF>, but the magic fades."));

		return success;
	}
}
