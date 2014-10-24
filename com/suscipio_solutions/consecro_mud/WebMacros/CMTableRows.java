package com.suscipio_solutions.consecro_mud.WebMacros;

import java.util.Calendar;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.CharClasses.interfaces.CharClass;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMTableRow;
import com.suscipio_solutions.consecro_mud.Common.interfaces.Quest;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMProps;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_mud.core.collections.PairSVector;
import com.suscipio_solutions.consecro_web.interfaces.HTTPRequest;


@SuppressWarnings({"unchecked","rawtypes"})
public class CMTableRows extends StdWebMacro
{
	@Override public String name() { return "CMTableRows"; }

	//HEADER, FOOTER, DATERANGE, DATESTART, DATEEND, LEVELSUP, DIVORCES, BIRTHS, MARRIAGES, PURGES, CLASSCHANGES, PKDEATHS, DEATHS, NEWPLAYERS, TOTALHOURS, AVERAGETICKS, AVERAGEONLINE, MOSTONLINE, LOGINS,
	@Override
	public String runMacro(HTTPRequest httpReq, String parm)
	{
		if(parm.length()==0) parm="DATERANGE&LOGINS&MOSTONLINE&AVERAGEONLINE&TOTALHOURS&NEWPLAYERS&DEATHS&PKDEATHS&CLASSCHANGES&PURGES&MARRIAGES&BIRTHS&DIVORCES";
		final java.util.Map<String,String> parms=parseParms(parm);
		final PairSVector<String,String> orderedParms=parseOrderedParms(parm,false);
		String header=parms.get("HEADER");
		if(header==null) header="";
		String footer=parms.get("FOOTER");
		if(footer==null) footer="";
		int scale=CMath.s_int(httpReq.getUrlParameter("SCALE"));
		if(scale<=0) scale=1;
		int days=CMath.s_int(httpReq.getUrlParameter("DAYS"));
		days=days*scale;
		if(days<=0) days=0;
		String code=httpReq.getUrlParameter("CODE");
		if((code==null)||(code.length()==0)) code="*";

		final Calendar ENDQ=Calendar.getInstance();
		ENDQ.add(Calendar.DATE,-days);
		ENDQ.set(Calendar.HOUR_OF_DAY,23);
		ENDQ.set(Calendar.MINUTE,59);
		ENDQ.set(Calendar.SECOND,59);
		ENDQ.set(Calendar.MILLISECOND,000);
		CMLib.coffeeTables().update();
		final List<CMTableRow> V=CMLib.database().DBReadStats(ENDQ.getTimeInMillis()-1);
		if(V.size()==0){return "";}
		final StringBuffer table=new StringBuffer("");
		final Calendar C=Calendar.getInstance();
		C.set(Calendar.HOUR_OF_DAY,23);
		C.set(Calendar.MINUTE,59);
		C.set(Calendar.SECOND,59);
		C.set(Calendar.MILLISECOND,999);
		long curTime=C.getTimeInMillis();
		long lastCur=0;
		String colspan="";
		if(parms.containsKey("SKILLUSE"))
		{
			CharClass CharC=null;
			if(code.length()>1)
				CharC=CMClass.getCharClass(code.substring(1));
			final Vector allSkills=new Vector();
			for(final Enumeration<Ability> e=CMClass.abilities();e.hasMoreElements();)
			{
				final Ability A=e.nextElement();
				if((CharC==null)||(CMLib.ableMapper().getQualifyingLevel(CharC.ID(),true,A.ID())>=0))
					allSkills.addElement(A);
			}
			final long[][] totals=new long[allSkills.size()][CMTableRow.STAT_TOTAL];
			while((V.size()>0)&&(curTime>(ENDQ.getTimeInMillis())))
			{
				lastCur=curTime;
				final Calendar C2=Calendar.getInstance();
				C2.setTimeInMillis(curTime);
				C2.add(Calendar.DATE,-(scale));
				C2.set(Calendar.HOUR_OF_DAY,23);
				C2.set(Calendar.MINUTE,59);
				C2.set(Calendar.SECOND,59);
				C2.set(Calendar.MILLISECOND,999);
				curTime=C2.getTimeInMillis();
				final Vector set=new Vector();
				if(V.size()==1)
				{
					final CMTableRow T=V.get(0);
					set.addElement(T);
					V.remove(0);
				}
				else
				for(int v=V.size()-1;v>=0;v--)
				{
					final CMTableRow T=V.get(v);
					if((T.startTime()>curTime)&&(T.endTime()<=lastCur))
					{
						set.addElement(T);
						V.remove(v);
					}
				}
				for(int s=0;s<set.size();s++)
				{
					final CMTableRow T=(CMTableRow)set.elementAt(s);
					for(int x=0;x<allSkills.size();x++)
						T.totalUp("A"+((Ability)allSkills.elementAt(x)).ID().toUpperCase(),totals[x]);
				}
				if(scale==0) break;
			}
			int x=-1;
			Ability A=null;
			while(x<allSkills.size())
			{
				table.append("<TR>");
				for(int i=0;i<orderedParms.size();i++)
				{
					final String key=orderedParms.getFirst(i);
					if(key.equals("COLSPAN"))
						colspan=" COLSPAN="+orderedParms.getSecond(i);
					else
					if(key.equalsIgnoreCase("NEXTSKILLID"))
					{
						x++;
						if(x>=allSkills.size())
							A=null;
						else
						{
							A=(Ability)allSkills.elementAt(x);
							table.append("<TD"+colspan+">"+header+A.ID()+footer+"</TD>");
						}
					}
					else
					if(key.equalsIgnoreCase("SKILLUSE"))
					{
						if(A!=null)
							table.append("<TD"+colspan+">"+header+totals[x][CMTableRow.STAT_SKILLUSE]+footer+"</TD>");
					}
				}
				table.append("</TR>");
			}
		}
		else
		if(parms.containsKey("QUESTNAME")||parms.containsKey("QUESTRPT"))
		{
			final long[][] totals=new long[CMLib.quests().numQuests()][CMTableRow.STAT_TOTAL];
			while((V.size()>0)&&(curTime>(ENDQ.getTimeInMillis())))
			{
				lastCur=curTime;
				final Calendar C2=Calendar.getInstance();
				C2.setTimeInMillis(curTime);
				C2.add(Calendar.DATE,-(scale));
				C2.set(Calendar.HOUR_OF_DAY,23);
				C2.set(Calendar.MINUTE,59);
				C2.set(Calendar.SECOND,59);
				C2.set(Calendar.MILLISECOND,999);
				curTime=C2.getTimeInMillis();
				final Vector set=new Vector();
				if(V.size()==1)
				{
					final CMTableRow T=V.get(0);
					set.addElement(T);
					V.remove(0);
				}
				else
				for(int v=V.size()-1;v>=0;v--)
				{
					final CMTableRow T=V.get(v);
					if((T.startTime()>curTime)&&(T.endTime()<=lastCur))
					{
						set.addElement(T);
						V.remove(v);
					}
				}
				if(set.size()==0){ set.addAll(V); V.clear();}
				for(int s=0;s<set.size();s++)
				{
					final CMTableRow T=(CMTableRow)set.elementAt(s);
					for(int x=0;x<CMLib.quests().numQuests();x++)
						T.totalUp("U"+T.tagFix(CMLib.quests().fetchQuest(x).name()),totals[x]);
				}
				if(scale==0) break;
			}
			Quest Q=null;
			for(int x=0;x<CMLib.quests().numQuests();x++)
			{
				Q=CMLib.quests().fetchQuest(x);
				table.append("<TR>");
				for(int i=0;i<orderedParms.size();i++)
				{
					final String key=orderedParms.getFirst(i);
					if(key.equals("COLSPAN"))
						colspan=" COLSPAN="+orderedParms.getSecond(i);
					else if(key.equalsIgnoreCase("QUESTNAME")) table.append("<TD"+colspan+">"+header+Q.name()+footer+"</TD>");
					else if(key.equalsIgnoreCase("DATERANGE")) table.append("<TD"+colspan+">"+header+CMLib.time().date2DateString(curTime+1)+" - "+CMLib.time().date2DateString(lastCur-1)+footer+"</TD>");
					else if(key.equalsIgnoreCase("DATESTART")) table.append("<TD"+colspan+">"+header+CMLib.time().date2DateString(curTime+1)+footer+"</TD>");
					else if(key.equalsIgnoreCase("DATEEND")) table.append("<TD"+colspan+">"+header+CMLib.time().date2DateString(lastCur)+footer+"</TD>");
					else if(key.equalsIgnoreCase("FAILEDSTART")) table.append("<TD"+colspan+">"+header+totals[x][CMTableRow.STAT_QUESTFAILEDSTART]+footer+"</TD>");
					else if(key.equalsIgnoreCase("TIMESTART")) table.append("<TD"+colspan+">"+header+totals[x][CMTableRow.STAT_QUESTTIMESTART]+footer+"</TD>");
					else if(key.equalsIgnoreCase("TIMESTOP")) table.append("<TD"+colspan+">"+header+totals[x][CMTableRow.STAT_QUESTTIMESTOP]+footer+"</TD>");
					else if(key.equalsIgnoreCase("STOP")) table.append("<TD"+colspan+">"+header+totals[x][CMTableRow.STAT_QUESTSTOP]+footer+"</TD>");
					else if(key.equalsIgnoreCase("ACCEPTED")) table.append("<TD"+colspan+">"+header+totals[x][CMTableRow.STAT_QUESTACCEPTED]+footer+"</TD>");
					else if(key.equalsIgnoreCase("FAILED")) table.append("<TD"+colspan+">"+header+totals[x][CMTableRow.STAT_QUESTFAILED]+footer+"</TD>");
					else if(key.equalsIgnoreCase("SUCCESS")) table.append("<TD"+colspan+">"+header+totals[x][CMTableRow.STAT_QUESTSUCCESS]+footer+"</TD>");
					else if(key.equalsIgnoreCase("DROPPED")) table.append("<TD"+colspan+">"+header+totals[x][CMTableRow.STAT_QUESTDROPPED]+footer+"</TD>");
					else if(key.equalsIgnoreCase("STARTATTEMPT")) table.append("<TD"+colspan+">"+header+totals[x][CMTableRow.STAT_QUESTSTARTATTEMPT]+footer+"</TD>");
				}
				table.append("</TR>");
			}
		}
		else
		while((V.size()>0)&&(curTime>(ENDQ.getTimeInMillis())))
		{
			lastCur=curTime;
			final Calendar C2=Calendar.getInstance();
			C2.setTimeInMillis(curTime);
			C2.add(Calendar.DATE,-scale);
			curTime=C2.getTimeInMillis();
			C2.set(Calendar.HOUR_OF_DAY,23);
			C2.set(Calendar.MINUTE,59);
			C2.set(Calendar.SECOND,59);
			C2.set(Calendar.MILLISECOND,999);
			curTime=C2.getTimeInMillis();
			final Vector set=new Vector();
			for(int v=V.size()-1;v>=0;v--)
			{
				final CMTableRow T=V.get(v);
				if((T.startTime()>curTime)&&(T.endTime()<=lastCur))
				{
					set.addElement(T);
					V.remove(v);
				}
			}
			final long[] totals=new long[CMTableRow.STAT_TOTAL];
			long highestOnline=0;
			long numberOnlineTotal=0;
			long numberOnlineCounter=0;
			for(int s=0;s<set.size();s++)
			{
				final CMTableRow T=(CMTableRow)set.elementAt(s);
				T.totalUp(code,totals);
				if(T.highestOnline()>highestOnline) highestOnline=T.highestOnline();
				numberOnlineTotal+=T.numberOnlineTotal();
				numberOnlineCounter+=T.numberOnlineCounter();
			}
			final long minsOnline=(totals[CMTableRow.STAT_TICKSONLINE]*CMProps.getTickMillis())/(1000*60);
			totals[CMTableRow.STAT_TICKSONLINE]=(totals[CMTableRow.STAT_TICKSONLINE]*CMProps.getTickMillis())/(1000*60*60);
			double avgOnline=(numberOnlineCounter>0)?CMath.div(numberOnlineTotal,numberOnlineCounter):0.0;
			avgOnline=CMath.div(Math.round(avgOnline*10.0),10.0);
			table.append("<TR>");
			for(int i=0;i<orderedParms.size();i++)
			{
				final String key=orderedParms.getFirst(i);
				if(key.equals("COLSPAN")) colspan=" COLSPAN="+orderedParms.getSecond(i);
				else if(key.equalsIgnoreCase("DATERANGE")) table.append("<TD"+colspan+">"+header+CMLib.time().date2DateString(curTime+1)+" - "+CMLib.time().date2DateString(lastCur-1)+footer+"</TD>");
				else if(key.equalsIgnoreCase("DATESTART")) table.append("<TD"+colspan+">"+header+CMLib.time().date2DateString(curTime+1)+footer+"</TD>");
				else if(key.equalsIgnoreCase("DATEEND")) table.append("<TD"+colspan+">"+header+CMLib.time().date2DateString(lastCur)+footer+"</TD>");
				else if(key.equalsIgnoreCase("LOGINS")) table.append("<TD"+colspan+">"+header+totals[CMTableRow.STAT_LOGINS]+footer+"</TD>");
				else if(key.equalsIgnoreCase("MOSTONLINE")) table.append("<TD"+colspan+">"+header+highestOnline+footer+"</TD>");
				else if(key.equalsIgnoreCase("AVERAGEONLINE")) table.append("<TD"+colspan+">"+header+avgOnline+footer+"</TD>");
				else if(key.equalsIgnoreCase("AVERAGETICKS")) table.append("<TD"+colspan+">"+header+((totals[CMTableRow.STAT_LOGINS]>0)?(minsOnline/totals[CMTableRow.STAT_LOGINS]):0)+"</TD>");
				else if(key.equalsIgnoreCase("TOTALHOURS")) table.append("<TD"+colspan+">"+header+totals[CMTableRow.STAT_TICKSONLINE]+footer+"</TD>");
				else if(key.equalsIgnoreCase("NEWPLAYERS")) table.append("<TD"+colspan+">"+header+totals[CMTableRow.STAT_NEWPLAYERS]+footer+"</TD>");
				else if(key.equalsIgnoreCase("DEATHS")) table.append("<TD"+colspan+">"+header+totals[CMTableRow.STAT_DEATHS]+footer+"</TD>");
				else if(key.equalsIgnoreCase("PKDEATHS")) table.append("<TD"+colspan+">"+header+totals[CMTableRow.STAT_PKDEATHS]+footer+"</TD>");
				else if(key.equalsIgnoreCase("CLASSCHANGES")) table.append("<TD"+colspan+">"+header+totals[CMTableRow.STAT_CLASSCHANGE]+footer+"</TD>");
				else if(key.equalsIgnoreCase("PURGES")) table.append("<TD"+colspan+">"+header+totals[CMTableRow.STAT_PURGES]+footer+"</TD>");
				else if(key.equalsIgnoreCase("MARRIAGES")) table.append("<TD"+colspan+">"+header+totals[CMTableRow.STAT_MARRIAGES]+footer+"</TD>");
				else if(key.equalsIgnoreCase("BIRTHS")) table.append("<TD"+colspan+">"+header+totals[CMTableRow.STAT_BIRTHS]+footer+"</TD>");
				else if(key.equalsIgnoreCase("DIVORCES")) table.append("<TD"+colspan+">"+header+totals[CMTableRow.STAT_DIVORCES]+footer+"</TD>");
				else if(key.equalsIgnoreCase("LEVELSUP")) table.append("<TD"+colspan+">"+header+totals[CMTableRow.STAT_LEVELSGAINED]+footer+"</TD>");
			}
			table.append("</TR>");
			if(scale==0) break;
		}
		return clearWebMacros(table);
	}
}
