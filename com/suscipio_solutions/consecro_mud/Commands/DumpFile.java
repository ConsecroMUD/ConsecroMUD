package com.suscipio_solutions.consecro_mud.Commands;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Common.interfaces.Session;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMFile;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMSecurity;
import com.suscipio_solutions.consecro_mud.core.Resources;


@SuppressWarnings("rawtypes")
public class DumpFile extends StdCommand
{
	public DumpFile(){}

	private final String[] access=I(new String[]{"DUMPFILE"});
	@Override public String[] getAccessWords(){return access;}
	@Override
	public boolean execute(MOB mob, Vector commands, int metaFlags)
		throws java.io.IOException
	{
		if(commands.size()<3)
		{
			mob.tell(L("dumpfile {raw} username|all {filename1 ...}"));
			return false;
		}
		commands.removeElementAt(0);

		int numFiles = 0;
		int numSessions = 0;
		boolean rawMode=false;

		if(((String)commands.elementAt(0)).equalsIgnoreCase("raw"))
		{
			rawMode = true;
			commands.removeElementAt(0);
		}

		final String targetName = (String)commands.elementAt(0);
		final boolean allFlag=(targetName.equalsIgnoreCase("all"));

		commands.removeElementAt(0);

		// so they can do dumpfile (username) RAW filename too
		if(!rawMode && ( ((String)commands.elementAt(0)).equalsIgnoreCase("raw")) )
		{
			rawMode = true;
			commands.removeElementAt(0);
		}

		final StringBuffer fileText = new StringBuffer("");
		while (commands.size() > 0)
		{
			boolean wipeAfter = true;
			final String fn = (String)commands.elementAt(0);

			if (Resources.getResource(fn) != null)
				wipeAfter = false;

			final StringBuffer ft = new CMFile(fn,mob,CMFile.FLAG_LOGERRORS).text();
			if (ft != null && ft.length() > 0)
			{
				fileText.append("\n\r");
				fileText.append(ft);
				++numFiles;
			}

			if (wipeAfter)
				Resources.removeResource(fn);
			commands.removeElementAt(0);

		}
		if (fileText.length() > 0)
		{
			for(final Session S : CMLib.sessions().localOnlineIterable())
			{
				if(!CMSecurity.isAllowed(mob,S.mob().location(),CMSecurity.SecFlag.DUMPFILE))
					continue;
				if (allFlag || S.mob().name().equalsIgnoreCase(targetName))
				{
					if (rawMode)
						S.rawPrintln(fileText.toString());
					else
						S.colorOnlyPrintln(fileText.toString());
					++numSessions;
				}
			}
		}
		mob.tell(L("dumped @x1 files to @x2 user(s)",""+numFiles,""+numSessions));
		return false;
	}

	@Override public boolean canBeOrdered(){return true;}
	@Override public boolean securityCheck(MOB mob){return CMSecurity.isAllowed(mob,mob.location(),CMSecurity.SecFlag.DUMPFILE);}


}
