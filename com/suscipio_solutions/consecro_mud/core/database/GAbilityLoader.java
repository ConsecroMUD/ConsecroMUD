package com.suscipio_solutions.consecro_mud.core.database;
import java.sql.ResultSet;
import java.util.List;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Libraries.interfaces.DatabaseEngine;
import com.suscipio_solutions.consecro_mud.core.Log;



public class GAbilityLoader
{
	protected DBConnector DB=null;
	public GAbilityLoader(DBConnector newDB)
	{
		DB=newDB;
	}
	public List<DatabaseEngine.AckRecord> DBReadAbilities()
	{
		DBConnection D=null;
		final Vector<DatabaseEngine.AckRecord> rows=new Vector<DatabaseEngine.AckRecord>();
		try
		{
			D=DB.DBFetch();
			final ResultSet R=D.query("SELECT * FROM CMGAAC");
			while(R.next())
				rows.addElement(new DatabaseEngine.AckRecord(DBConnections.getRes(R,"CMGAID"), DBConnections.getRes(R,"CMGAAT"), DBConnections.getRes(R,"CMGACL")));
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
	public void DBCreateAbility(String classID, String typeClass, String data)
	{
		DB.updateWithClobs(
		 "INSERT INTO CMGAAC ("
		 +"CMGAID, "
		 +"CMGAAT, "
		 +"CMGACL "
		 +") values ("
		 +"'"+classID+"',"
		 +"?,"
		 +"'"+typeClass+"'"
		 +")",
		 data+" ");
	}
	public void DBDeleteAbility(String classID)
	{
		DB.update("DELETE FROM CMGAAC WHERE CMGAID='"+classID+"'");
	}
}
