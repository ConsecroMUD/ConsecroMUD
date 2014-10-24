package com.suscipio_solutions.consecro_mud.core.database;
import java.sql.ResultSet;
import java.util.List;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Libraries.interfaces.DatabaseEngine;
import com.suscipio_solutions.consecro_mud.core.Log;



public class PollLoader
{
	protected DBConnector DB=null;
	public PollLoader(DBConnector newDB)
	{
		DB=newDB;
	}
	public DatabaseEngine.PollData DBRead(String name)
	{
		DBConnection D=null;
		try
		{
			D=DB.DBFetch();
			final ResultSet R=D.query("SELECT * FROM CMPOLL WHERE CMNAME='"+name+"'");
			while(R.next())
			{
				final DatabaseEngine.PollData data = new DBInterface.PollData();
				data.name=DBConnections.getRes(R,"CMNAME");
				data.byName=DBConnections.getRes(R,"CMBYNM");
				data.subject=DBConnections.getRes(R,"CMSUBJ");
				data.description=DBConnections.getRes(R,"CMDESC");
				data.options=DBConnections.getRes(R,"CMOPTN");
				data.flag=DBConnections.getLongRes(R,"CMFLAG");
				data.qual=DBConnections.getRes(R,"CMQUAL");
				data.results=DBConnections.getRes(R,"CMRESL");
				data.expiration=DBConnections.getLongRes(R,"CMEXPI");
				return data;
			}
		}
		catch(final Exception sqle)
		{
			Log.errOut("PollLoader",sqle);
		}
		finally
		{
			DB.DBDone(D);
		}
		// log comment
		return null;
	}


	public List<DatabaseEngine.PollData> DBReadList()
	{
		DBConnection D=null;
		final Vector<DatabaseEngine.PollData> rows=new Vector<DatabaseEngine.PollData>();
		try
		{
			D=DB.DBFetch();
			final ResultSet R=D.query("SELECT * FROM CMPOLL");
			while(R.next())
			{
				final DatabaseEngine.PollData data = new DBInterface.PollData();
				data.name=DBConnections.getRes(R,"CMNAME");
				data.flag=DBConnections.getLongRes(R,"CMFLAG");
				data.qual=DBConnections.getRes(R,"CMQUAL");
				data.expiration=DBConnections.getLongRes(R,"CMEXPI");
				rows.addElement(data);
			}
		}
		catch(final Exception sqle)
		{
			Log.errOut("PollLoader",sqle);
		}
		finally
		{
			DB.DBDone(D);
		}
		// log comment
		return rows;
	}

	public void DBUpdate(String OldName,
								String name,
								String player,
								String subject,
								String description,
								String optionXML,
								int flag,
								String qualZapper,
								String results,
								long expiration)
	{
		DB.updateWithClobs(
				"UPDATE CMPOLL SET"
				+" CMRESL=?"
				+" WHERE CMNAME='"+OldName+"'", results+" ");

		DB.updateWithClobs(
			"UPDATE CMPOLL SET"
			+"  CMNAME='"+name+"'"
			+", CMBYNM='"+player+"'"
			+", CMSUBJ='"+subject+"'"
			+", CMDESC=?"
			+", CMOPTN=?"
			+", CMFLAG="+flag
			+", CMQUAL='"+qualZapper+"'"
			+", CMEXPI="+expiration
			+"  WHERE CMNAME='"+OldName+"'", new String[][]{{description+" ", optionXML+" "}});

	}

	public void DBUpdate(String name,  String results)
	{
		DB.updateWithClobs(
		"UPDATE CMPOLL SET"
		+" CMRESL=?"
		+" WHERE CMNAME='"+name+"'", results+" ");
	}

	public void DBDelete(String name)
	{
		DB.update("DELETE FROM CMPOLL WHERE CMNAME='"+name+"'");
		try{Thread.sleep(500);}catch(final Exception e){}
		if(DB.queryRows("SELECT * FROM CMPOLL WHERE CMNAME='"+name+"'")>0)
			Log.errOut("Failed to delete data from poll "+name+".");
	}

	public void DBCreate(String name,
								String player,
								String subject,
								String description,
								String optionXML,
								int flag,
								String qualZapper,
								String results,
								long expiration)
	{
		DB.updateWithClobs(
		 "INSERT INTO CMPOLL ("
		 +"CMNAME, "
		 +"CMBYNM, "
		 +"CMSUBJ, "
		 +"CMDESC, "
		 +"CMOPTN, "
		 +"CMFLAG, "
		 +"CMQUAL, "
		 +"CMRESL, "
		 +"CMEXPI "
		 +") values ("
		 +"'"+name+"',"
		 +"'"+player+"',"
		 +"'"+subject+"',"
		 +"?, "
		 +"?,"
		 +""+flag+","
		 +"'"+qualZapper+"',"
		 +"?,"
		 +""+expiration+""
		 +")", new String[][]{{description,optionXML,results+" "}});
	}
}
