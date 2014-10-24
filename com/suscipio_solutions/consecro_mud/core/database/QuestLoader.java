package com.suscipio_solutions.consecro_mud.core.database;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Common.interfaces.Quest;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.Log;
import com.suscipio_solutions.consecro_mud.core.interfaces.MudHost;


public class QuestLoader
{
	protected DBConnector DB=null;
	public QuestLoader(DBConnector newDB)
	{
		DB=newDB;
	}
	public void DBRead(MudHost myHost)
	{
		CMLib.quests().shutdown();
		DBConnection D=null;
		try
		{
			D=DB.DBFetch();
			final ResultSet R=D.query("SELECT * FROM CMQUESTS");
			while(R.next())
			{
				final String questName=DBConnections.getRes(R,"CMQUESID");
				final String questScript=DBConnections.getRes(R,"CMQSCRPT");
				final String questWinners=DBConnections.getRes(R,"CMQWINNS");
				final long flags=DBConnections.getLongRes(R, "CMQFLAGS");
				final Quest Q=(Quest)CMClass.getCommon("DefaultQuest");
				Q.setFlags(flags);
				final boolean loaded=Q.setScript(questScript,!Q.suspended());
				Q.setFlags(flags);
				Q.setWinners(questWinners);
				if(Q.name().length()==0)
					Q.setName(questName);
				if(!loaded)
				{
					if(!Q.suspended())
					{
						Log.sysOut("QuestLoader","Unable to load Quest '"+questName+"'.  Suspending.");
						Q.setSuspended(true);
					}
					if(CMLib.quests().fetchQuest(Q.name())==null)
						CMLib.quests().addQuest(Q);
					continue;
				}
				if(Q.name().length()==0)
					Log.sysOut("QuestLoader","Unable to load Quest '"+questName+"' due to blank name.");
				else
				if(Q.duration()<0)
					Log.sysOut("QuestLoader","Unable to load Quest '"+questName+"' due to duration "+Q.duration()+".");
				else
				if(CMLib.quests().fetchQuest(Q.name())!=null)
					Log.sysOut("QuestLoader","Unable to load Quest '"+questName+"' due to it already being loaded.");
				else
					CMLib.quests().addQuest(Q);
			}
		}
		catch(final SQLException sqle)
		{
			Log.errOut("Quest",sqle);
		}
		finally
		{
			DB.DBDone(D);
		}
	}


	public void DBUpdateQuest(Quest Q)
	{
		if(Q==null) return;
		DB.update("DELETE FROM CMQUESTS WHERE CMQUESID='"+Q.name()+"'");
		DB.updateWithClobs(
		"INSERT INTO CMQUESTS ("
		+"CMQUESID, "
		+"CMQUTYPE, "
		+"CMQFLAGS, "
		+"CMQSCRPT, "
		+"CMQWINNS "
		+") values ("
		+"'"+Q.name()+"',"
		+"'"+CMClass.classID(Q)+"',"
		+Q.getFlags()+","
		+"?,"
		+"?"
		+")", new String[][]{{Q.script()+" ",Q.getWinnerStr()+" "}});
	}
	public void DBUpdateQuests(List<Quest> quests)
	{
		if(quests==null) quests=new Vector<Quest>();
		String quType="DefaultQuest";
		if(quests.size()>0) quType=CMClass.classID(quests.get(0));
		DBConnection D=null;
		DB.update("DELETE FROM CMQUESTS WHERE CMQUTYPE='"+quType+"'");
		try{Thread.sleep((1000+(quests.size()*100)));}catch(final Exception e){}
		if(DB.queryRows("SELECT * FROM CMQUESTS WHERE CMQUTYPE='"+quType+"'")>0)
			Log.errOut("Failed to delete quest typed '"+quType+"'.");
		DB.update("DELETE FROM CMQUESTS WHERE CMQUTYPE='Quests'");
		try{Thread.sleep((1000+(quests.size()*100)));}catch(final Exception e){}
		if(DB.queryRows("SELECT * FROM CMQUESTS WHERE CMQUTYPE='Quests'")>0)
			Log.errOut("Failed to delete quest typed 'Quests'.");
		D=DB.DBFetchEmpty();
		for(int m=0;m<quests.size();m++)
		{
			final Quest Q=quests.get(m);
			if(Q.isCopy()) continue;
			try
			{
				D.rePrepare(
				"INSERT INTO CMQUESTS ("
				+"CMQUESID, "
				+"CMQUTYPE, "
				+"CMQFLAGS, "
				+"CMQSCRPT, "
				+"CMQWINNS "
				+") values ("
				+"'"+Q.name()+"',"
				+"'"+CMClass.classID(Q)+"',"
				+Q.getFlags()+","
				+"?,"
				+"?"
				+")");
				D.setPreparedClobs(new String[]{Q.script()+" ",Q.getWinnerStr()+" "});
				D.update("",0);
			}
			catch(final java.sql.SQLException sqle)
			{
				Log.errOut("Quest",sqle);
			}
		}
		if(D!=null) DB.DBDone(D);
	}

}
