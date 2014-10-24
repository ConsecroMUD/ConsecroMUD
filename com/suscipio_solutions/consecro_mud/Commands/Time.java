package com.suscipio_solutions.consecro_mud.Commands;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Common.interfaces.PlayerStats;
import com.suscipio_solutions.consecro_mud.Common.interfaces.TimeClock;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMStrings;
import com.suscipio_solutions.consecro_mud.core.CMath;


@SuppressWarnings("rawtypes")
public class Time extends StdCommand
{
	public Time(){}

	private final String[] access=I(new String[]{"TIME","DATE"});
	@Override public String[] getAccessWords(){return access;}


	@Override
	public boolean execute(MOB mob, Vector commands, int metaFlags)
		throws java.io.IOException
	{
		final Room room=mob.location();
		if(room==null) return false;
		mob.tell(room.getArea().getTimeObj().timeDescription(mob,room));
		if((mob.playerStats()!=null)&&(mob.playerStats().getBirthday()!=null))
		{
			final TimeClock C=CMLib.time().localClock(mob.getStartRoom());
			final int day=C.getDayOfMonth();
			final int month=C.getMonth();
			int year=C.getYear();
			final int bday=mob.playerStats().getBirthday()[PlayerStats.BIRTHDEX_DAY];
			final int bmonth=mob.playerStats().getBirthday()[PlayerStats.BIRTHDEX_MONTH];
			if((month>bmonth)||((month==bmonth)&&(day>bday)))
				year++;

			final StringBuffer timeDesc=new StringBuffer("");
			if(C.getDaysInWeek()>0)
			{
				long x=((long)year)*((long)C.getMonthsInYear())*C.getDaysInMonth();
				x=x+((long)(bmonth-1))*((long)C.getDaysInMonth());
				x=x+bmonth;
				timeDesc.append(C.getWeekNames()[(int)(x%C.getDaysInWeek())]+", ");
			}
			timeDesc.append("the "+bday+CMath.numAppendage(bday));
			timeDesc.append(" day of "+C.getMonthNames()[bmonth-1]);
			if(C.getYearNames().length>0)
				timeDesc.append(", "+CMStrings.replaceAll(C.getYearNames()[year%C.getYearNames().length],"#",""+year));
			mob.tell(L("Your next birthday is @x1.",timeDesc.toString()));
		}
		return false;
	}

	@Override public boolean canBeOrdered(){return true;}


}
