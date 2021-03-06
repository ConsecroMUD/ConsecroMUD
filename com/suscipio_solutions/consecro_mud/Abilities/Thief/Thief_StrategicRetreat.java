package com.suscipio_solutions.consecro_mud.Abilities.Thief;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Exits.interfaces.Exit;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.Directions;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;


@SuppressWarnings({"unchecked","rawtypes"})
public class Thief_StrategicRetreat extends ThiefSkill
{
	@Override public String ID() { return "Thief_StrategicRetreat"; }
	private final static String localizedName = CMLib.lang().L("Strategic Retreat");
	@Override public String name() { return localizedName; }
	@Override public String displayText(){ return "";}
	@Override protected int canAffectCode(){return 0;}
	@Override protected int canTargetCode(){return 0;}
	@Override public int abstractQuality(){return Ability.QUALITY_OK_SELF;}
	private static final String[] triggerStrings =I(new String[] {"FREEFLEE","STRATEGICRETREAT"});
	@Override public String[] triggerStrings(){return triggerStrings;}
	@Override public int usageType(){return USAGE_MOVEMENT;}
	@Override public int classificationCode(){return Ability.ACODE_THIEF_SKILL|Ability.DOMAIN_DIRTYFIGHTING;}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		if(!mob.isInCombat())
		{
			mob.tell(L("You can only retreat from combat!"));
			return false;
		}

		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		final boolean success=proficiencyCheck(mob,0,auto);

		String where=CMParms.combine(commands,0);
		if(!success)
		{
			mob.tell(L("Your attempt to flee with grace and honor FAILS!"));
			CMLib.commands().postFlee(mob,where);
		}
		else
		{
			int directionCode=-1;
			if(!where.equals("NOWHERE"))
			{
				if(where.length()==0)
				{
					final Vector directions=new Vector();
					for(int d=Directions.NUM_DIRECTIONS()-1;d>=0;d--)
					{
						final Exit thisExit=mob.location().getExitInDir(d);
						final Room thisRoom=mob.location().getRoomInDir(d);
						if((thisRoom!=null)&&(thisExit!=null)&&(thisExit.isOpen()))
							directions.addElement(Integer.valueOf(d));
					}
					// up is last resort
					if(directions.size()>1)
						directions.removeElement(Integer.valueOf(Directions.UP));
					if(directions.size()>0)
					{
						directionCode=((Integer)directions.elementAt(CMLib.dice().roll(1,directions.size(),-1))).intValue();
						where=Directions.getDirectionName(directionCode);
					}
				}
				else
					directionCode=Directions.getGoodDirectionCode(where);
				if(directionCode<0)
				{
					mob.tell(L("Flee where?!"));
					return false;
				}
				mob.makePeace();
				CMLib.tracking().walk(mob,directionCode,true,false);
			}
		}
		return success;
	}
}
