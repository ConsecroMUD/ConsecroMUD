package com.suscipio_solutions.consecro_mud.core.database;
import java.sql.ResultSet;

import com.suscipio_solutions.consecro_mud.Common.interfaces.Clan;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMProps;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_mud.core.Log;

public class ClanLoader
{
	protected DBConnector DB=null;
	public ClanLoader(DBConnector newDB)
	{
		DB=newDB;
	}
	protected int currentRecordPos=1;
	protected int recordCount=0;

	public void updateBootStatus(String loading)
	{
		CMProps.setUpLowVar(CMProps.Str.MUDSTATUS,"Booting: Loading "+loading+" ("+currentRecordPos+" of "+recordCount+")");
	}

	public void DBRead()
	{
		DBConnection D=null;
		Clan C=null;
		try
		{
			D=DB.DBFetch();
			final ResultSet R=D.query("SELECT * FROM CMCLAN");
			recordCount=DB.getRecordCount(D,R);
			while(R.next())
			{
				currentRecordPos=R.getRow();
				final String name=DBConnections.getRes(R,"CMCLID");
				C=(Clan)CMClass.getCommon("DefaultClan");
				C.setName(name);
				C.setPremise(DBConnections.getRes(R,"CMDESC"));
				C.setAcceptanceSettings(DBConnections.getRes(R,"CMACPT"));
				C.setPolitics(DBConnections.getRes(R,"CMPOLI"));
				C.setRecall(DBConnections.getRes(R,"CMRCLL"));
				C.setDonation(DBConnections.getRes(R,"CMDNAT"));
				C.setStatus(CMath.s_int(DBConnections.getRes(R, "CMSTAT")));
				C.setMorgue(DBConnections.getRes(R,"CMMORG"));
				C.setTrophies(CMath.s_int(DBConnections.getRes(R, "CMTROP")));
				CMLib.clans().addClan(C);
				updateBootStatus("Clans");
			}
		}
		catch(final Exception sqle)
		{
			Log.errOut("Clan",sqle);
		}
		finally
		{
			DB.DBDone(D);
		}
		// log comment
	}

	public void DBUpdate(Clan C)
	{
		final String sql="UPDATE CMCLAN SET "
				+"CMDESC='"+C.getPremise()+"',"
				+"CMACPT='"+C.getAcceptanceSettings()+"',"
				+"CMPOLI=?,"
				+"CMRCLL='"+C.getRecall()+"',"
				+"CMDNAT='"+C.getDonation()+"',"
				+"CMSTAT="+C.getStatus()+","
				+"CMMORG='"+C.getMorgue()+"',"
				+"CMTROP="+C.getTrophies()+""
				+" WHERE CMCLID='"+C.clanID()+"'";
		DB.updateWithClobs(sql, C.getPolitics());
	}

	public void DBCreate(Clan C)
	{
		if(C.clanID().length()==0) return;
		final String sql="INSERT INTO CMCLAN ("
			+"CMCLID,"
			+"CMTYPE,"
			+"CMDESC,"
			+"CMACPT,"
			+"CMPOLI,"
			+"CMRCLL,"
			+"CMDNAT,"
			+"CMSTAT,"
			+"CMMORG,"
			+"CMTROP"
			+") values ("
			+"'"+C.clanID()+"',"
			+"0,"
			+"'"+C.getPremise()+"',"
			+"'"+C.getAcceptanceSettings()+"',"
			+"?,"
			+"'"+C.getRecall()+"',"
			+"'"+C.getDonation()+"',"
			+""+C.getStatus()+","
			+"'"+C.getMorgue()+"',"
			+""+C.getTrophies()
			+")";
			DB.updateWithClobs(sql, C.getPolitics());
	}

	public void DBDelete(Clan C)
	{
		DB.update("DELETE FROM CMCLAN WHERE CMCLID='"+C.clanID()+"'");
	}

}
