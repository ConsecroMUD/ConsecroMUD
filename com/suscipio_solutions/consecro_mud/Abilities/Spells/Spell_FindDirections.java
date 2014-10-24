package com.suscipio_solutions.consecro_mud.Abilities.Spells;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Areas.interfaces.Area;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;


@SuppressWarnings("rawtypes")
public class Spell_FindDirections extends Spell
{
	@Override public String ID() { return "Spell_FindDirections"; }
	private final static String localizedName = CMLib.lang().L("Find Directions");
	@Override public String name() { return localizedName; }
	@Override public int abstractQuality(){ return Ability.QUALITY_INDIFFERENT;}
	@Override protected int canTargetCode(){return Ability.CAN_AREAS;}
	@Override protected int canAffectCode(){return 0;}
	@Override public int classificationCode(){ return Ability.ACODE_SPELL|Ability.DOMAIN_DIVINATION;}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		final Room targetR=mob.location();
		if(targetR==null)
			return false;

		if((commands.size()>0)
		&&(commands.firstElement() instanceof String)
		&&(((String)commands.firstElement()).toLowerCase().startsWith("direction")))
			commands.remove(0);
		Area A=null;
		if(commands.size()>0)
		{
			A=CMLib.map().findArea(CMParms.combine(commands));
			if(A!=null)
			{
				if(!CMLib.flags().canAccess(mob, A))
					A=null;
				else
				{
					boolean foundOne=false;
					for(int i=0;i<10;i++)
						if(CMLib.flags().canAccess(mob, A.getRandomProperRoom()))
						{
							foundOne=true;
							break;
						}
					if(!foundOne)
						A=null;
				}
			}
		}

		if(A==null)
		{
			mob.tell(L("You know of nowhere called \"@x1\".",CMParms.combine(commands)));
			return false;
		}

		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		final boolean success=proficiencyCheck(mob,0,auto);
		if(success)
		{
			final CMMsg msg=CMClass.getMsg(mob,targetR,this,somanticCastCode(mob,targetR,auto),auto?"":L("^S<S-NAME> wave(s) <S-HIS-HER> hands around, pointing towards '@x1'.^?",A.name()));
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				mob.tell(L("The directions are taking shape in your mind: \n\r@x1",CMLib.tracking().getTrailToDescription(targetR, new Vector<Room>(), A.Name(), false, false, 100, null,1)));
			}
		}
		else
			beneficialVisualFizzle(mob,targetR,L("<S-NAME> wave(s) <S-HIS-HER> hands around, looking more frustrated every second."));


		// return whether it worked
		return success;
	}
}
