package com.suscipio_solutions.consecro_mud.core.database;
import java.sql.ResultSet;
import java.util.List;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Libraries.interfaces.DatabaseEngine;
import com.suscipio_solutions.consecro_mud.core.Log;



public class GCClassLoader
{
	protected DBConnector DB=null;
	public GCClassLoader(DBConnector newDB)
	{
		DB=newDB;
	}

	public List<DatabaseEngine.AckRecord> DBReadClasses()
	{
		DBConnection D=null;
		final Vector<DatabaseEngine.AckRecord> rows=new Vector<DatabaseEngine.AckRecord>();
		try
		{
			D=DB.DBFetch();
			final ResultSet R=D.query("SELECT * FROM CMCCAC");
			while(R.next())
			{
				final DatabaseEngine.AckRecord ack = new DatabaseEngine.AckRecord(
					DBConnections.getRes(R,"CMCCID"),
					DBConnections.getRes(R,"CMCDAT"),
					"GenCharClass");
				rows.addElement(ack);
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

	public void DBDeleteClass(String classID)
	{
		DB.update("DELETE FROM CMCCAC WHERE CMCCID='"+classID+"'");
	}

	public void DBCreateClass(String classID, String data)
	{
		DB.updateWithClobs(
		 "INSERT INTO CMCCAC ("
		 +"CMCCID, "
		 +"CMCDAT "
		 +") values ("
		 +"'"+classID+"',"
		 +"?"
		 +")",
		 data+" ");
	}
}
