package com.suscipio_solutions.consecro_mud.Libraries;
import java.util.Calendar;

import com.suscipio_solutions.consecro_mud.Common.interfaces.CMTableRow;
import com.suscipio_solutions.consecro_mud.Libraries.interfaces.StatisticsLibrary;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMProps;
import com.suscipio_solutions.consecro_mud.core.CMSecurity;
import com.suscipio_solutions.consecro_mud.core.CMSecurity.DbgFlag;
import com.suscipio_solutions.consecro_mud.core.Log;
import com.suscipio_solutions.consecro_mud.core.interfaces.CMObject;
import com.suscipio_solutions.consecro_mud.core.interfaces.MudHost;
import com.suscipio_solutions.consecro_mud.core.interfaces.Tickable;


public class CMTables extends StdLibrary implements StatisticsLibrary
{
	@Override public String ID(){return "CMTables";}
	public CMTableRow todays=null;

	@Override
	public void update()
	{
		if(CMSecurity.isDisabled(CMSecurity.DisFlag.STATS))
			return;
		if(todays!=null)
			CMLib.database().DBUpdateStat(todays.startTime(),todays.data());
	}

	@Override
	public void bump(CMObject E, int type)
	{
		if(!CMProps.getBoolVar(CMProps.Bool.MUDSTARTED))
			return;
		if(CMSecurity.isDisabled(CMSecurity.DisFlag.STATS))
			return;
		if(todays==null)
		{
			final Calendar S=Calendar.getInstance();
			S.set(Calendar.HOUR_OF_DAY,0);
			S.set(Calendar.MINUTE,0);
			S.set(Calendar.SECOND,0);
			S.set(Calendar.MILLISECOND,0);
			todays=(CMTableRow)CMLib.database().DBReadStat(S.getTimeInMillis());
			if(todays==null)
			{
				synchronized(this)
				{
					if(todays==null)
					{
						final Calendar C=Calendar.getInstance();
						C.set(Calendar.HOUR_OF_DAY,23);
						C.set(Calendar.MINUTE,59);
						C.set(Calendar.SECOND,59);
						C.set(Calendar.MILLISECOND,999);
						todays=(CMTableRow)CMClass.getCommon("DefaultCMTableRow");
						todays.setStartTime(S.getTimeInMillis());
						todays.setEndTime(C.getTimeInMillis());
						CMLib.database().DBCreateStat(todays.startTime(),todays.endTime(),todays.data());
					}
				}
			}
			return;
		}
		final long now=System.currentTimeMillis();
		if((now>todays.endTime())
		&&(!CMLib.time().date2MonthDateString(now, true).equals(CMLib.time().date2MonthDateString(todays.endTime(), true))))
		{
			synchronized(this)
			{
				if((now>todays.endTime())
				&&(!CMLib.time().date2MonthDateString(now, true).equals(CMLib.time().date2MonthDateString(todays.endTime(), true))))
				{
					CMLib.database().DBUpdateStat(todays.startTime(),todays.data());
					final Calendar S=Calendar.getInstance();
					S.set(Calendar.HOUR_OF_DAY,0);
					S.set(Calendar.MINUTE,0);
					S.set(Calendar.SECOND,0);
					S.set(Calendar.MILLISECOND,0);
					final Calendar C=Calendar.getInstance();
					C.set(Calendar.HOUR_OF_DAY,23);
					C.set(Calendar.MINUTE,59);
					C.set(Calendar.SECOND,59);
					C.set(Calendar.MILLISECOND,999);
					todays=(CMTableRow)CMClass.getCommon("DefaultCMTableRow");
					todays.setStartTime(S.getTimeInMillis());
					todays.setEndTime(C.getTimeInMillis());
					final CMTableRow testRow=(CMTableRow)CMLib.database().DBReadStat(todays.startTime());
					if(testRow!=null)
						todays=testRow;
					else
					if(!CMLib.database().DBCreateStat(todays.startTime(),todays.endTime(),todays.data()))
					{
						Log.errOut("CMTables","Unable to manage daily-stat transition");
					}
				}
			}
		}
		todays.bumpVal(E,type);
	}

	@Override
	public boolean activate()
	{
		if(serviceClient==null)
		{
			name="THStats"+Thread.currentThread().getThreadGroup().getName().charAt(0);
			serviceClient=CMLib.threads().startTickDown(this, Tickable.TICKID_SUPPORT|Tickable.TICKID_SOLITARYMASK, MudHost.TIME_SAVETHREAD_SLEEP, 1);
		}
		return true;
	}

	@Override public boolean tick(Tickable ticking, int tickID)
	{
		try
		{
			if((!CMSecurity.isDisabled(CMSecurity.DisFlag.SAVETHREAD))
			&&(!CMSecurity.isDisabled(CMSecurity.DisFlag.STATSTHREAD)))
			{
				tickStatus=Tickable.STATUS_ALIVE;
				isDebugging=CMSecurity.isDebugging(DbgFlag.STATSTHREAD);
				setThreadStatus(serviceClient,"checking database health");
				final String ok=CMLib.database().errorStatus();
				if((ok.length()!=0)&&(!ok.startsWith("OK")))
				{
					Log.errOut(serviceClient.getName(),"DB: "+ok);
					CMLib.s_sleep(100000);
				}
				else
				{
					CMLib.coffeeTables().bump(null,CMTableRow.STAT_SPECIAL_NUMONLINE);
					CMLib.coffeeTables().update();
				}
			}
		}
		finally
		{
			tickStatus=Tickable.STATUS_NOT;
			setThreadStatus(serviceClient,"sleeping");
		}
		return true;
	}

	@Override
	public boolean shutdown()
	{
		if(CMLib.threads().isTicking(this, TICKID_SUPPORT|Tickable.TICKID_SOLITARYMASK))
		{
			CMLib.threads().deleteTick(this, TICKID_SUPPORT|Tickable.TICKID_SOLITARYMASK);
			serviceClient=null;
		}
		return true;
	}
}
