package com.suscipio_solutions.consecro_mud.Abilities.Spells;
import java.util.Enumeration;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Areas.interfaces.Area;
import com.suscipio_solutions.consecro_mud.Behaviors.interfaces.Behavior;
import com.suscipio_solutions.consecro_mud.Behaviors.interfaces.LegalBehavior;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CharStats;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;


@SuppressWarnings("rawtypes")
public class Spell_Telepathy extends Spell
{
	@Override public String ID() { return "Spell_Telepathy"; }
	private final static String localizedName = CMLib.lang().L("Telepathy");
	@Override public String name() { return localizedName; }
	@Override public int abstractQuality(){ return Ability.QUALITY_INDIFFERENT;}
	@Override protected int canAffectCode(){return 0;}
	@Override public int classificationCode(){ return Ability.ACODE_SPELL|Ability.DOMAIN_DIVINATION;}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		final MOB target=this.getTarget(mob,commands,givenTarget);
		if(target==null) return false;

		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		final boolean success=proficiencyCheck(mob,0,auto);

		if(success)
		{
			final CMMsg msg=CMClass.getMsg(mob,target,this,somanticCastCode(mob,target,auto),auto?"":L("^S<S-NAME> concentrate(s) on <T-NAMESELF>!^?"));
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
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
					thoughts.append(prefix).append(accounting).append(".  ");
				}
				String adjective="";
				if(target.charStats().getStat(CharStats.STAT_INTELLIGENCE)>=18)
					adjective+="massively intelligent, ";
				else
				if(target.charStats().getStat(CharStats.STAT_INTELLIGENCE)>=13)
					adjective+="very intelligent, ";
				else
				if(target.charStats().getStat(CharStats.STAT_INTELLIGENCE)>10)
					adjective+="intelligent, ";
				else
				if(target.charStats().getStat(CharStats.STAT_INTELLIGENCE)<=3)
					adjective+="instinctual, ";
				else
				if(target.charStats().getStat(CharStats.STAT_INTELLIGENCE)<7)
					adjective+="stupid, ";
				else
				if(target.charStats().getStat(CharStats.STAT_INTELLIGENCE)<10)
					adjective+="slow thinking, ";

				if(target.charStats().getStat(CharStats.STAT_WISDOM)>=18)
					adjective+="incredibly wise, ";
				else
				if(target.charStats().getStat(CharStats.STAT_WISDOM)>=13)
					adjective+="very wise, ";
				else
				if(target.charStats().getStat(CharStats.STAT_WISDOM)>10)
					adjective+="wise, ";
				else
				if(target.charStats().getStat(CharStats.STAT_WISDOM)<=3)
					adjective+="extremely naieve, ";
				else
				if(target.charStats().getStat(CharStats.STAT_WISDOM)<7)
					adjective+="naieve, ";
				else
				if(target.charStats().getStat(CharStats.STAT_WISDOM)<10)
					adjective+="unwise, ";

				mob.tell(L("@x1 is a @x2@x3 @x4.",target.Name(),adjective,target.charStats().getMyRace().name(),target.charStats().getCurrentClass().name()));
				if(thoughts.length()==0)
					mob.tell(L("You don't detect any other thoughts."));
				else
					mob.tell(thoughts.toString());
			}
		}
		else
			return beneficialVisualFizzle(mob,target,L("<S-NAME> concentrate(s) on <T-NAMESELF>, but look(s) frustrated."));

		// return whether it worked
		return success;
	}
}
