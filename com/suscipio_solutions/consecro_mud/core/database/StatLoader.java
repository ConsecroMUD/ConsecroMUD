package com.suscipio_solutions.consecro_mud.core.database;
import java.sql.ResultSet;
import java.util.List;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Common.interfaces.CMTableRow;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMSecurity;
import com.suscipio_solutions.consecro_mud.core.Log;



public class StatLoader
{
	protected DBConnector DB=null;
	public StatLoader(DBConnector newDB)
	{
		DB=newDB;
	}
	public CMTableRow DBRead(long startTime)
	{
		if(Log.debugChannelOn()&&(CMSecurity.isDebugging(CMSecurity.DbgFlag.CMSTAT)))
			Log.debugOut("StatLoader","Reading content of Stat  "+CMLib.time().date2String(startTime));
		DBConnection D=null;
		CMTableRow T=null;
		try
		{
			D=DB.DBFetch();
			final ResultSet R=D.query("SELECT * FROM CMSTAT WHERE CMSTRT="+startTime);
			if(R.next())
			{
				T=(CMTableRow)CMClass.getCommon("DefaultCMTableRow");
				final long endTime=DBConnections.getLongRes(R,"CMENDT");
				final String data=DBConnections.getRes(R,"CMDATA");
				T.populate(startTime,endTime,data);
			}
		}
		catch(final Exception sqle)
		{
			Log.errOut("DataLoader",sqle);
		}
		finally
		{
			DB.DBDone(D);
		}
		// log comment
		return T;
	}

	public List<CMTableRow> DBReadAfter(long startTime)
	{
		if(Log.debugChannelOn()&&(CMSecurity.isDebugging(CMSecurity.DbgFlag.CMSTAT)))
			Log.debugOut("StatLoader","Reading content of Stats since "+CMLib.time().date2String(startTime));
		DBConnection D=null;
		CMTableRow T=null;
		final List<CMTableRow> rows=new Vector<CMTableRow>();
		try
		{
			D=DB.DBFetch();
			final ResultSet R=D.query("SELECT * FROM CMSTAT WHERE CMSTRT>"+startTime);
			while(R.next())
			{
				T=(CMTableRow)CMClass.getCommon("DefaultCMTableRow");
				final long strTime=DBConnections.getLongRes(R,"CMSTRT");
				final long endTime=DBConnections.getLongRes(R,"CMENDT");
				final String data=DBConnections.getRes(R,"CMDATA");
				T.populate(strTime,endTime,data);
				rows.add(T);
			}
		}
		catch(final Exception sqle)
		{
			Log.errOut("DataLoader",sqle);
		}
		finally
		{
			DB.DBDone(D);
		}
		// log comment
		return rows;
	}

	public void DBDelete(long startTime)
	{
		if(Log.debugChannelOn()&&(CMSecurity.isDebugging(CMSecurity.DbgFlag.CMSTAT)))
			Log.debugOut("StatLoader","Deleting Stat  "+CMLib.time().date2String(startTime));
		try
		{
			DB.update("DELETE FROM CMSTAT WHERE CMSTRT="+startTime);
		}
		catch(final Exception sqle)
		{
			Log.errOut("DataLoader",sqle);
		}
	}
	public boolean DBUpdate(long startTime, String data)
	{
		if(Log.debugChannelOn()&&(CMSecurity.isDebugging(CMSecurity.DbgFlag.CMSTAT)))
			Log.debugOut("StatLoader","Updating Stat  "+CMLib.time().date2String(startTime));
		int result=-1;
		try
		{
			result=DB.updateWithClobs("UPDATE CMSTAT SET CMDATA=? WHERE CMSTRT="+startTime, data);
		}
		catch(final Exception sqle)
		{
			Log.errOut("DataLoader",sqle);
		}
		return (result != -1);
	}
	public boolean DBCreate(long startTime, long endTime, String data)
	{
		if(Log.debugChannelOn()&&(CMSecurity.isDebugging(CMSecurity.DbgFlag.CMSTAT)))
			Log.debugOut("StatLoader","Creating Stat  "+CMLib.time().date2String(startTime));
		final int result = DB.updateWithClobs(
		 "INSERT INTO CMSTAT ("
		 +"CMSTRT, "
		 +"CMENDT, "
		 +"CMDATA "
		 +") values ("
		 +""+startTime+","
		 +""+endTime+","
		 +"?"
		 +")", data);
		return (result != -1);
	}
}
