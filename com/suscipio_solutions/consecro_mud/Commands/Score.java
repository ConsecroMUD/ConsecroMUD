package com.suscipio_solutions.consecro_mud.Commands;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.CharClasses.interfaces.CharClass;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CharStats;
import com.suscipio_solutions.consecro_mud.Common.interfaces.Clan;
import com.suscipio_solutions.consecro_mud.Common.interfaces.Faction;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.CMProps;
import com.suscipio_solutions.consecro_mud.core.CMSecurity;
import com.suscipio_solutions.consecro_mud.core.CMStrings;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_mud.core.collections.Pair;


@SuppressWarnings({"unchecked","rawtypes"})
public class Score extends Affect
{
	public Score(){}

	private final String[] access=I(new String[]{"SCORE","SC"});
	@Override public String[] getAccessWords(){return access;}

	public StringBuilder getScore(MOB mob){return getScore(mob,"");}
	public StringBuilder getScore(MOB mob, String parm)
	{
		final StringBuilder msg=new StringBuilder("^N");

		final int classLevel=mob.charStats().getClassLevel(mob.charStats().getCurrentClass());
		if((!CMSecurity.isDisabled(CMSecurity.DisFlag.CLASSES))
		&&(!mob.charStats().getMyRace().classless())
		&&(!CMSecurity.isDisabled(CMSecurity.DisFlag.LEVELS))
		&&(!mob.charStats().getMyRace().leveless())
		&&(!mob.charStats().getCurrentClass().leveless()))
		{
			String levelStr=null;
			if(classLevel>=mob.phyStats().level())
				levelStr=L("level ")+mob.phyStats().level()+" "+mob.charStats().getCurrentClass().name(mob.charStats().getCurrentClassLevel());
			else
				levelStr=mob.charStats().getCurrentClass().name(mob.charStats().getCurrentClassLevel())+" "+classLevel+"/"+mob.phyStats().level();
			msg.append(L("You are ^H@x1^? the ^H@x2^?.\n\r",mob.Name(),levelStr));
		}
		else
		if((!CMSecurity.isDisabled(CMSecurity.DisFlag.LEVELS))
		&&(!mob.charStats().getCurrentClass().leveless())
		&&(!mob.charStats().getMyRace().leveless()))
		{
			String levelStr=null;
			if(classLevel>=mob.phyStats().level())
				levelStr=L(", level ")+mob.phyStats().level();
			else
				levelStr=L(", level ")+classLevel+"/"+mob.phyStats().level();
			msg.append(L("You are ^H@x1^?^H@x2^?.\n\r",mob.Name(),levelStr));
		}
		else
		if((!CMSecurity.isDisabled(CMSecurity.DisFlag.CLASSES))
		&&(!mob.charStats().getMyRace().classless()))
			msg.append(L("You are ^H@x1^? the ^H@x2^?.\n\r",mob.Name(),mob.charStats().getCurrentClass().name(mob.charStats().getCurrentClassLevel())));
		else
			msg.append(L("You are ^H@x1^?.\n\r",mob.Name()));

		if((!CMSecurity.isDisabled(CMSecurity.DisFlag.CLASSES))
		&&(classLevel<mob.phyStats().level()))
		{
			msg.append(L("You also have levels in: "));
			final StringBuilder classList=new StringBuilder("");
			for(int c=0;c<mob.charStats().numClasses()-1;c++)
			{
				final CharClass C=mob.charStats().getMyClass(c);
				if(C!=mob.charStats().getCurrentClass())
				{
					if(classList.length()>0)
						if(c==mob.charStats().numClasses()-2)
							classList.append(L(", and "));
						else
							classList.append(", ");
					classList.append(C.name(mob.charStats().getClassLevel(C))+" ("+mob.charStats().getClassLevel(C)+") ");
				}
			}
			msg.append(classList.toString()+".\n\r");
		}

		if(CMProps.getBoolVar(CMProps.Bool.ACCOUNTEXPIRATION)&&(mob.playerStats()!=null))
			msg.append(L("Your account is Registered and Active until: @x1!\n\r",CMLib.time().date2String(mob.playerStats().getAccountExpiration())));

		String genderName=L("^!neuter");
		if(mob.charStats().getStat(CharStats.STAT_GENDER)=='M') 
			genderName=L("^!male");
		else
		if(mob.charStats().getStat(CharStats.STAT_GENDER)=='F') 
			genderName=L("^!female");
		msg.append(L("You are a "));
		if((mob.baseCharStats().getStat(CharStats.STAT_AGE)>0)&&(!CMSecurity.isDisabled(CMSecurity.DisFlag.ALL_AGEING)))
			msg.append(L("^!@x1^? year old ",""+mob.baseCharStats().getStat(CharStats.STAT_AGE)));
		msg.append(genderName);
		if((!CMSecurity.isDisabled(CMSecurity.DisFlag.RACES))
		&&(!mob.charStats().getCurrentClass().raceless()))
			msg.append(" "+mob.charStats().getMyRace().name() + "^?");
		else
			msg.append("^?");
		if(mob.getLiegeID().length()>0)
		{
			if(mob.isMarriedToLiege())
				msg.append(L(" who is married to ^H@x1^?",mob.getLiegeID()));
			else
				msg.append(L(" who serves ^H@x1^?",mob.getLiegeID()));
		}
		if(mob.getWorshipCharID().length()>0)
			msg.append(L(" worshipping ^H@x1^?",mob.getWorshipCharID()));
		msg.append(".\n\r");
		if(mob.clans().iterator().hasNext())
		{
			msg.append(L("You are "));
			for(final Iterator<Pair<Clan,Integer>> c = mob.clans().iterator();c.hasNext();)
			{
				final Pair<Clan,Integer> p=c.next();
				final Clan C=p.first;
				String role=C.getRoleName(p.second.intValue(),true,false);
				role=CMLib.english().startWithAorAn(role);
				msg.append(L("@x1 of ^H@x2^?^.",role,C.getName()));
				if(c.hasNext())
					msg.append(", ");
			}
			msg.append("\n\r");
		}
		if(!CMSecurity.isDisabled(CMSecurity.DisFlag.ATTRIBS))
		{
			msg.append(L("\n\r^NYour stats are: "));
			msg.append(CMLib.protocol().mxpImage(mob," ALIGN=RIGHT H=70 W=70"));
			msg.append("\n\r");
			CharStats CT=mob.charStats();
			if(parm.equalsIgnoreCase("BASE")) 
				CT=mob.baseCharStats();
			msg.append("^N^!");
			for(final int i : CharStats.CODES.BASECODES())
				msg.append(CMStrings.padRight("^<HELP^>" + CMStrings.capitalizeAndLower(CharStats.CODES.NAME(i))+"^</HELP^>",15)
						+": "
						+CMStrings.padRight(Integer.toString(CT.getStat(i)),2)
						+"/"
						+(CT.getMaxStat(i))+"\n\r");
			msg.append("^?\n\r");
		}
		msg.append(L("You have ^H@x1^? ^<HELP^>hit points^</HELP^>, ^H",mob.curState().getHitPoints()+"/"+mob.maxState().getHitPoints()));
		msg.append(L("@x1^? ^<HELP^>mana^</HELP^>, and ^H",mob.curState().getMana()+"/"+mob.maxState().getMana()));
		msg.append(L("@x1^? ^<HELP^>movement^</HELP^>.\n\r",mob.curState().getMovement()+"/"+mob.maxState().getMovement()));
		if(mob.phyStats().height()<0)
			msg.append(L("You are incorporeal, but still weigh ^!@x1^? pounds.\n\r",""+mob.baseWeight()));
		else
			msg.append(L("You are ^!@x1^? inches tall and weigh ^!@x2^? pounds.\n\r",""+mob.phyStats().height(),""+mob.baseWeight()));
		if(CMSecurity.isAllowed(mob,mob.location(),CMSecurity.SecFlag.CARRYALL))
			msg.append(L("You are carrying ^!@x1^? items weighing ^!@x2^? pounds.\n\r",""+mob.numItems(),""+mob.phyStats().weight()));
		else
			msg.append(L("You are carrying ^!@x1^?/^!@x2^? items weighing ^!@x3^? pounds.\n\r",""+mob.numItems(),""+mob.maxItems(),mob.phyStats().weight()+"^?/^!"+mob.maxCarry()));
		msg.append(L("You have ^!@x1^? ^<HELP^>practices^</HELP^>, ^!@x2^? ^<HELP^>training sessions^</HELP^>, and ^!@x3^? ^<HELP^>quest points^</HELP^>.\n\r",""+mob.getPractices(),""+mob.getTrains(),""+mob.getQuestPoint()));
		if((!CMSecurity.isDisabled(CMSecurity.DisFlag.EXPERIENCE))
		&&!mob.charStats().getCurrentClass().expless()
		&&!mob.charStats().getMyRace().expless())
		{
			if((!CMSecurity.isDisabled(CMSecurity.DisFlag.LEVELS))
			&&(!mob.charStats().getCurrentClass().leveless())
			&&(!mob.charStats().getMyRace().leveless()))
			{
				if(((CMProps.getIntVar(CMProps.Int.LASTPLAYERLEVEL)>0)
					&&(mob.basePhyStats().level()>CMProps.getIntVar(CMProps.Int.LASTPLAYERLEVEL)))
				||(mob.getExpNeededLevel()==Integer.MAX_VALUE)
				||(mob.charStats().isLevelCapped(mob.charStats().getCurrentClass())))
					msg.append(L("You have scored ^!@x1^? ^<HELP^>experience points^</HELP^>, ^!@x2^? over your last level.\n\r",""+mob.getExperience(),""+mob.getExpNeededDelevel()));
				else
					msg.append(L("You have scored ^!@x1^? ^<HELP^>experience points^</HELP^>, and need ^!@x2^? to advance.\n\r",""+mob.getExperience(),""+mob.getExpNeededLevel()));
			}
			else
				msg.append(L("You have scored ^!@x1^? ^<HELP^>experience points^</HELP^>.\n\r",""+mob.getExperience()));
		}
		msg.append(L("You have been online for ^!@x1^? hours.\n\r",""+Math.round(CMath.div(mob.getAgeMinutes(),60.0))));
		for(final Enumeration e=mob.fetchFactions();e.hasMoreElements();)
		{
			final String factionID=(String)e.nextElement();
			final Faction F=CMLib.factions().getFaction(factionID);
			if(F!=null)
			{
				final int factionAmt=mob.fetchFaction(factionID);
				final Faction.FRange FR=CMLib.factions().getRange(factionID,factionAmt);
				if((FR!=null)&&(F.showInScore()))
					msg.append(L("Your ")+CMStrings.padRight(L("^<HELP^>@x1^</HELP^> is",F.name()),18)+": ^H"+FR.name()+" ^.("+factionAmt+").\n\r");
			}
		}
		msg.append(L("Your ^<HELP^>armored defence^</HELP^> is: ^H@x1^..\n\r",CMLib.combat().armorStr(mob)));
		msg.append(L("Your ^<HELP^>combat prowess^</HELP^> is : ^H@x1^..\n\r",CMLib.combat().fightingProwessStr(mob)));
		//if(CMLib.flags().canSeeHidden(mob))
		//    msg.append(L("Your ^<HELP^>observation score^</HELP^> : ^H@x1^?.\n\r",CMLib.flags().getDetectScore(mob)));
		msg.append(L("Wimpy is set to ^!@x1^? hit points.\n\r",""+mob.getWimpHitPoint()));

		msg.append(getMOBState(mob));
		msg.append(getAffects(mob.session(),mob,false,false));
		return msg;
	}

	@Override
	public boolean execute(MOB mob, Vector commands, int metaFlags)
		throws java.io.IOException
	{
		String parm="";
		if(commands.size()>1)
			parm=CMParms.combine(commands,1);
		final StringBuilder msg=getScore(mob,parm);
		if(commands.size()==0)
		{
			commands.addElement(msg);
			return false;
		}
		if(!mob.isMonster())
			mob.session().wraplessPrintln(msg.toString());
		return false;
	}

	@Override public boolean canBeOrdered(){return true;}


}
