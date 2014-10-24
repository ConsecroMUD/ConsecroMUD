package com.suscipio_solutions.consecro_mud.Abilities.Druid;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Libraries.interfaces.TrackingLibrary;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;




@SuppressWarnings({"unchecked","rawtypes"})
public class Chant_MassFungalGrowth extends Chant_SummonFungus
{
	@Override public String ID() { return "Chant_MassFungalGrowth"; }
	private final static String localizedName = CMLib.lang().L("Mass Fungal Growth");
	@Override public String name() { return localizedName; }
	@Override public int classificationCode(){return Ability.ACODE_CHANT|Ability.DOMAIN_PLANTGROWTH;}
	@Override public int abstractQuality(){return Ability.QUALITY_INDIFFERENT;}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		final Vector V=new Vector();
		TrackingLibrary.TrackingFlags flags;
		flags = new TrackingLibrary.TrackingFlags()
				.plus(TrackingLibrary.TrackingFlag.OPENONLY)
				.plus(TrackingLibrary.TrackingFlag.AREAONLY)
				.plus(TrackingLibrary.TrackingFlag.NOEMPTYGRIDS)
				.plus(TrackingLibrary.TrackingFlag.NOAIR)
				.plus(TrackingLibrary.TrackingFlag.NOWATER);
		CMLib.tracking().getRadiantRooms(mob.location(),V,flags,null,adjustedLevel(mob,asLevel),null);
		for(int v=V.size()-1;v>=0;v--)
		{
			final Room R=(Room)V.elementAt(v);
			if((R.domainType()!=Room.DOMAIN_INDOORS_CAVE)
			||(R==mob.location()))
				V.removeElementAt(v);
		}
		if(V.size()>0)
		{
			mob.location().show(mob,null,CMMsg.MASK_ALWAYS|CMMsg.TYP_NOISE,L("The faint sound of fungus popping into existence can be heard."));
			int done=0;
			for(int v=0;v<V.size();v++)
			{
				final Room R=(Room)V.elementAt(v);
				if(R==mob.location()) continue;
				buildMyThing(mob,R);
				if((done++)==adjustedLevel(mob,asLevel))
					break;
			}
		}

		return true;
	}
}
