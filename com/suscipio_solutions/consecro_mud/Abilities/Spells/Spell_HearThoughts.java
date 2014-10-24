package com.suscipio_solutions.consecro_mud.Abilities.Spells;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Areas.interfaces.Area;
import com.suscipio_solutions.consecro_mud.Behaviors.interfaces.Behavior;
import com.suscipio_solutions.consecro_mud.Behaviors.interfaces.LegalBehavior;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CharStats;
import com.suscipio_solutions.consecro_mud.Libraries.interfaces.TrackingLibrary;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;


@SuppressWarnings("rawtypes")
public class Spell_HearThoughts extends Spell
{
	@Override public String ID() { return "Spell_HearThoughts"; }
	private final static String localizedName = CMLib.lang().L("Hear Thoughts");
	@Override public String name() { return localizedName; }
	@Override public int abstractQuality(){ return Ability.QUALITY_INDIFFERENT;}
	@Override protected int canAffectCode(){return 0;}
	@Override public int classificationCode(){ return Ability.ACODE_SPELL|Ability.DOMAIN_DIVINATION;}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		final boolean success=proficiencyCheck(mob,0,auto);

		if(success)
		{
			final CMMsg msg=CMClass.getMsg(mob,null,this,somanticCastCode(mob,null,auto),auto?"":L("^S<S-NAME> concentrate(s) and listen(s) carefully!^?"));
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				final List<Room> rooms=CMLib.tracking().getRadiantRooms(mob.location(), new TrackingLibrary.TrackingFlags(), 50);
				final List<MOB> mobs=new LinkedList<MOB>();
				int numMobs= 8 + super.getXLEVELLevel(mob);
				for(final Room R : rooms)
				{
					for(final Enumeration<MOB> m=R.inhabitants();m.hasMoreElements();)
					{
						final MOB M=m.nextElement();
						if((numMobs>0)&&(M!=mob))
						{
							mobs.add(M);
							numMobs--;
						}
					}
					if(numMobs<=0)
						break;
				}
				rooms.clear();
				for(final MOB target : mobs)
				{
					final Room room=target.location();
					if(room==null) continue;
					String adjective="";
					if(target.charStats().getStat(CharStats.STAT_INTELLIGENCE)>=18)
						adjective+="massively intelligent, ";
					else
					if(target.charStats().getStat(CharStats.STAT_INTELLIGENCE)>=13)
						adjective+="very intelligent, ";
					else
					if(target.charStats().getStat(CharStats.STAT_INTELLIGENCE)>=10)
						adjective+="intelligent, ";
					if(target.charStats().getStat(CharStats.STAT_WISDOM)>=18)
						adjective+="incredibly wise, ";
					else
					if(target.charStats().getStat(CharStats.STAT_WISDOM)>=13)
						adjective+="very wise, ";
					else
					if(target.charStats().getStat(CharStats.STAT_WISDOM)>=10)
						adjective+="wise, ";
					mob.tell(L("Regarding @x1, a @x2@x3 @x4 at @x5:",target.Name(),adjective,target.charStats().getMyRace().name(),target.charStats().getCurrentClass().name(),room.displayText(mob)));
					final StringBuilder thoughts=new StringBuilder("");
					final LegalBehavior LB=CMLib.law().getLegalBehavior(target.location());
					final Area AO=CMLib.law().getLegalObject(target.location());
					if((LB!=null)&&(AO!=null))
					{
						if(LB.isJudge(AO, target))
							thoughts.append("You detect the legalese thoughts of a judge.  ");
						else
						if(LB.isAnyOfficer(AO, target))
							thoughts.append("You detect the stern thoughts of a law officer.  ");
					}
					for(final Enumeration<Behavior> b=target.behaviors();b.hasMoreElements();)
					{
						final Behavior B=b.nextElement();
						final String accounting=B.accountForYourself();
						if(accounting.length()==0) continue;
						String prefix;
						switch(CMLib.dice().roll(1, 4, 0))
						{
						case 1: prefix="You sense thoughts of "; break;
						case 2: prefix="You hear thoughts of "; break;
						case 3: prefix="You detect thoughts of "; break;
						default: prefix="You can see thoughts of "; break;
						}
						thoughts.append(prefix).append(accounting).append("  ");
					}
					if(thoughts.length()==0)
						mob.tell(L("You don't detect any other thoughts.\n\r"));
					else
						mob.tell(thoughts.append("\n\r").toString());
				}
			}
		}
		else
			return beneficialVisualFizzle(mob,null,L("<S-NAME> concentrate(s), but look(s) frustrated."));

		// return whether it worked
		return success;
	}
}
