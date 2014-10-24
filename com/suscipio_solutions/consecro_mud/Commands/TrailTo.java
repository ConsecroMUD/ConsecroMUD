package com.suscipio_solutions.consecro_mud.Commands;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Areas.interfaces.Area;
import com.suscipio_solutions.consecro_mud.Libraries.interfaces.TrackingLibrary;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.CMSecurity;
import com.suscipio_solutions.consecro_mud.core.CMStrings;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_mud.core.Log;
import com.suscipio_solutions.consecro_mud.core.interfaces.SpaceObject;


@SuppressWarnings({"unchecked","rawtypes"})
public class TrailTo extends StdCommand
{
	public TrailTo(){}

	private final String[] access=I(new String[]{"TRAILTO"});
	@Override public String[] getAccessWords(){return access;}


	public String trailTo(Room R1, Vector commands)
	{
		int radius=Integer.MAX_VALUE;
		HashSet<Room> ignoreRooms=null;
		final TrackingLibrary.TrackingFlags flags = new TrackingLibrary.TrackingFlags();
		for(int c=0;c<commands.size();c++)
		{
			String s=(String)commands.elementAt(c);
			if(s.toUpperCase().startsWith("RADIUS"))
			{
				s=s.substring(("RADIUS").length()).trim();
				if(!s.startsWith("=")) continue;
				s=s.substring(1);
				commands.removeElementAt(c);
				radius=CMath.s_int(s);
			}
			else
			if(s.toUpperCase().startsWith("IGNOREROOMS"))
			{
				s=s.substring(("IGNOREROOMS").length()).trim();
				if(!s.startsWith("=")) continue;
				s=s.substring(1);
				commands.removeElementAt(c);
				final List<String> roomList=CMParms.parseCommas(s,true);
				ignoreRooms=new HashSet<Room>();
				for(int v=0;v<roomList.size();v++)
				{
					final Room R=CMLib.map().getRoom(roomList.get(v));
					if(R==null){ return "Ignored room "+roomList.get(v)+" is unknown!";}
					if(!ignoreRooms.contains(R))ignoreRooms.add(R);
				}
			}
			else
			if(s.toUpperCase().startsWith("NOHOME"))
			{
				commands.removeElementAt(c);
				flags.plus(TrackingLibrary.TrackingFlag.NOHOMES);
			}
		}
		String where=CMParms.combine(commands,1);
		if(where.length()==0) return "Trail to where? Try a Room ID, 'everyroom', or 'everyarea'.  You can also use the 'areanames', 'nohomes', 'ignorerooms=', and 'confirm!' flags.";
		if(R1==null) return "Where are you?";
		boolean confirm=false;
		boolean areaNames=false;
		boolean justTheFacts=false;
		if(where.toUpperCase().endsWith(" AREANAMES"))
		{
			where=where.substring(0,where.length()-10).trim();
			areaNames=true;
		}
		if(where.toUpperCase().endsWith(" JUSTTHEFACTS"))
		{
			where=where.substring(0,where.length()-13).trim();
			justTheFacts=true;
		}
		if(where.toUpperCase().endsWith(" CONFIRM!"))
		{
			where=where.substring(0,where.length()-9).trim();
			confirm=true;
		}
		final Vector<Room> set=new Vector<Room>();
		CMLib.tracking().getRadiantRooms(R1,set,flags,null,radius,ignoreRooms);
		if(where.equalsIgnoreCase("everyarea"))
		{
			final StringBuffer str=new StringBuffer("");
			for(final Enumeration<Area> a=CMLib.map().areas();a.hasMoreElements();)
			{
				final Area A=a.nextElement();
				if(!(A instanceof SpaceObject))
				{
					final String trail = CMLib.tracking().getTrailToDescription(R1,set,A.name(),areaNames,confirm,radius,ignoreRooms,5);
					str.append(CMStrings.padRightPreserve(A.name(),30)+": "+trail+"\n\r");
				}
			}
			if(confirm) Log.rawSysOut(str.toString());
			return str.toString();
		}
		else
		if(where.equalsIgnoreCase("everyroom"))
		{
			final StringBuffer str=new StringBuffer("");
			try
			{
				for(final Enumeration<Room> r=CMLib.map().rooms();r.hasMoreElements();)
				{
					final Room R=r.nextElement();
					if((R!=R1)&&(R.roomID().length()>0))
					{
						final String trail = CMLib.tracking().getTrailToDescription(R1,set,R.roomID(),areaNames,confirm,radius,ignoreRooms,5);
						str.append(CMStrings.padRightPreserve(R.roomID(),30)+": "+trail+"\n\r");
					}
				}
			}catch(final NoSuchElementException nse){}
			if(confirm) Log.rawSysOut(str.toString());
			return str.toString();
		}
		else
		{
			String str=CMLib.tracking().getTrailToDescription(R1,set,where,areaNames,confirm,radius,ignoreRooms,5);
			if(!justTheFacts)str=CMStrings.padRightPreserve(where,30)+": "+str;
			if(confirm) Log.rawSysOut(str);
			return str;
		}
	}

	@Override
	public boolean execute(MOB mob, Vector commands, int metaFlags)
		throws java.io.IOException
	{
		if((commands.size()>0)&&(((String)commands.lastElement()).equalsIgnoreCase("QUIETLY")))
		{
			commands.removeElementAt(commands.size()-1);
			commands.setElementAt(trailTo(mob.location(),commands),0);
		}
		else
		if(!mob.isMonster())
			mob.session().rawPrintln(trailTo(mob.location(),commands));
		return false;
	}

	@Override public boolean canBeOrdered(){return true;}
	@Override public boolean securityCheck(MOB mob){return CMSecurity.isAllowed(mob,mob.location(),CMSecurity.SecFlag.TRAILTO);}


}
