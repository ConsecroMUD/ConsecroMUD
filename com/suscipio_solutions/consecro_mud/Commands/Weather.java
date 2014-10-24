package com.suscipio_solutions.consecro_mud.Commands;
import java.util.Enumeration;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Areas.interfaces.Area;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMStrings;
import com.suscipio_solutions.consecro_mud.core.CMath;


@SuppressWarnings("rawtypes")
public class Weather extends StdCommand
{
	public Weather(){}

	private final String[] access=I(new String[]{"WEATHER"});
	@Override public String[] getAccessWords(){return access;}
	@Override
	public boolean execute(MOB mob, Vector commands, int metaFlags)
		throws java.io.IOException
	{
		final Room room=mob.location();
		if(room==null) return false;
		if((commands.size()>1)&&((room.domainType()&Room.INDOORS)==0)&&(((String)commands.elementAt(1)).equalsIgnoreCase("WORLD")))
		{
			final StringBuffer tellMe=new StringBuffer("");
			for(final Enumeration a=CMLib.map().areas();a.hasMoreElements();)
			{
				final Area A=(Area)a.nextElement();
				if((CMLib.flags().canAccess(mob,A))
				&&(!CMath.bset(A.flags(),Area.FLAG_INSTANCE_CHILD)))
					tellMe.append(CMStrings.padRight(A.name(),20)+": "+A.getClimateObj().weatherDescription(room)+"\n\r");
			}
			mob.tell(tellMe.toString());
			return false;
		}
		mob.tell(room.getArea().getClimateObj().weatherDescription(room));
		return false;
	}

	@Override public boolean canBeOrdered(){return true;}


}
