package com.suscipio_solutions.consecro_mud.core.database;
import java.sql.ResultSet;
import java.util.List;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Libraries.interfaces.DatabaseEngine;
import com.suscipio_solutions.consecro_mud.core.Log;



public class GRaceLoader
{
	protected DBConnector DB=null;
	public GRaceLoader(DBConnector newDB)
	{
		DB=newDB;
	}
	public void DBDeleteRace(String raceID)
	{
		DB.update("DELETE FROM CMGRAC WHERE CMRCID='"+raceID+"'");
	}
	public void DBCreateRace(String raceID, String data)
	{
		DB.updateWithClobs(
		 "INSERT INTO CMGRAC ("
		 +"CMRCID, "
		 +"CMRDAT "
		 +") values ("
		 +"'"+raceID+"',"
		 +"?"
		 +")",
		 data+" ");
	}
	public List<DatabaseEngine.AckRecord> DBReadRaces()
	{
		DBConnection D=null;
		final List<DatabaseEngine.AckRecord> rows=new Vector<DatabaseEngine.AckRecord>();
		try
		{
			D=DB.DBFetch();
			final ResultSet R=D.query("SELECT * FROM CMGRAC");
			while(R.next())
			{
				final DatabaseEngine.AckRecord ack=new DatabaseEngine.AckRecord(
						DBConnections.getRes(R,"CMRCID"),
						DBConnections.getRes(R,"CMRDAT"),
						"GenRace");
				rows.add(ack);
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

}
