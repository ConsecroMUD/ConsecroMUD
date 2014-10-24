package com.suscipio_solutions.consecro_mud.Commands;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Libraries.interfaces.ListingLibrary;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMSecurity;
import com.suscipio_solutions.consecro_mud.core.CMStrings;


@SuppressWarnings("rawtypes")
public class Group extends StdCommand
{
	public Group(){}

	private final String[] access=I(new String[]{"GROUP","GR"});
	@Override public String[] getAccessWords(){return access;}

	public static StringBuffer showWhoLong(MOB seer, MOB who)
	{

		final StringBuffer msg=new StringBuffer("");
		msg.append("[");
		final int[] cols={
				ListingLibrary.ColFixer.fixColWidth(7,seer.session()),
				ListingLibrary.ColFixer.fixColWidth(7,seer.session()),
				ListingLibrary.ColFixer.fixColWidth(5,seer.session()),
				ListingLibrary.ColFixer.fixColWidth(13,seer.session()),
				ListingLibrary.ColFixer.fixColWidth(3,seer.session()),
				ListingLibrary.ColFixer.fixColWidth(12,seer.session())
			};
		if(!CMSecurity.isDisabled(CMSecurity.DisFlag.RACES))
		{
			if(who.charStats().getCurrentClass().raceless())
				msg.append(CMStrings.padRight(" ",cols[0])+" ");
			else
				msg.append(CMStrings.padRight(who.charStats().raceName(),cols[0])+" ");
		}

		String levelStr=who.charStats().displayClassLevel(who,true).trim();
		final int x=levelStr.lastIndexOf(' ');
		if(x>=0) levelStr=levelStr.substring(x).trim();
		if(!CMSecurity.isDisabled(CMSecurity.DisFlag.CLASSES))
		{
			if(who.charStats().getMyRace().classless())
				msg.append(CMStrings.padRight(" ",cols[1])+" ");
			else
				msg.append(CMStrings.padRight(who.charStats().displayClassName(),cols[1])+" ");
		}
		if(!CMSecurity.isDisabled(CMSecurity.DisFlag.LEVELS))
		{
			if(who.charStats().getCurrentClass().leveless()
			||who.charStats().getMyRace().leveless())
				msg.append(CMStrings.padRight(" ",cols[2]));
			else
				msg.append(CMStrings.padRight(levelStr,cols[2]));
		}
		msg.append("] "+CMStrings.padRight(who.name(),cols[3])+" ");
		msg.append(CMStrings.padRightPreserve(CMLib.lang().L("hp(@x1/@x2)",CMStrings.padRightPreserve(""+who.curState().getHitPoints(),cols[4]),CMStrings.padRightPreserve(""+who.maxState().getHitPoints(),cols[4])),cols[5]));
		msg.append(CMStrings.padRightPreserve(CMLib.lang().L("mn(@x1/@x2)",CMStrings.padRightPreserve(""+who.curState().getMana(),cols[4]),CMStrings.padRightPreserve(""+who.maxState().getMana(),cols[4])),cols[5]));
		msg.append(CMStrings.padRightPreserve(CMLib.lang().L("mv(@x1/@x2)",CMStrings.padRightPreserve(""+who.curState().getMovement(),cols[4]),CMStrings.padRightPreserve(""+who.maxState().getMovement(),cols[4])),cols[5]));
		msg.append("\n\r");
		return msg;
	}

	@Override
	public boolean execute(MOB mob, Vector commands, int metaFlags)
		throws java.io.IOException
	{
		mob.tell(L("@x1's group:\n\r",mob.name()));
		final Set<MOB> group=mob.getGroupMembers(new HashSet<MOB>());
		final StringBuffer msg=new StringBuffer("");
		for (final Object element : group)
		{
			final MOB follower=(MOB)element;
			msg.append(showWhoLong(mob,follower));
		}
		mob.tell(msg.toString());
		return false;
	}

	@Override public boolean canBeOrdered(){return true;}


}
