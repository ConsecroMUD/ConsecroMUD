package com.suscipio_solutions.consecro_mud.Commands;
import java.util.List;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.CMSecurity;
import com.suscipio_solutions.consecro_mud.core.CMStrings;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_mud.core.interfaces.Tickable;


@SuppressWarnings("rawtypes")
public class After extends StdCommand implements Tickable
{
	@Override public String name(){return "SysOpSkills";} // for tickables use
	@Override public int getTickStatus(){return Tickable.STATUS_NOT;}

	public List<AfterCommand> afterCmds=new Vector<AfterCommand>();

	public After(){}

	private static class AfterCommand
	{
		long start=0;
		long duration=0;
		boolean every=false;
		MOB M=null;
		Vector command=null;
		int metaFlags=0;
	}

	private final String[] access=I(new String[]{"AFTER"});
	@Override public String[] getAccessWords(){return access;}
	@Override
	public boolean execute(MOB mob, Vector commands, int metaFlags)
		throws java.io.IOException
	{
		boolean every=false;
		commands.removeElementAt(0);

		final String afterErr="format: after (every) [X] [TICKS/MINUTES/SECONDS/HOURS] [COMMAND]";
		if(commands.size()==0){ mob.tell(afterErr); return false;}
		if(((String)commands.elementAt(0)).equalsIgnoreCase("stop"))
		{
			afterCmds.clear();
			CMLib.threads().deleteTick(this,Tickable.TICKID_AREA);
			mob.tell(L("Ok."));
			return false;
		}
		if(((String)commands.elementAt(0)).equalsIgnoreCase("list"))
		{
			//afterCmds.clear();
			int s=0;
			final StringBuffer str=new StringBuffer(L("^xCurrently scheduled AFTERs: ^?^.^?\n\r"));
			str.append(L("@x1 @x2 @x3 Command\n\r",CMStrings.padRight(L("Next run"),20),CMStrings.padRight(L(" Interval"),20),CMStrings.padRight(L("Who"),10)));
			while(s<afterCmds.size())
			{
				final AfterCommand V=afterCmds.get(s);
				every=V.every;
				str.append(CMStrings.padRight(CMLib.time().date2String(V.start+V.duration),20)+" ");
				str.append((every?"*":" ")+CMStrings.padRight(CMLib.english().returnTime(V.duration,0),20)+" ");
				str.append(CMStrings.padRight(V.M.Name(),10)+" ");
				str.append(CMStrings.limit(CMParms.combine(V.command,0),25)+"\n\r");
				s++;
			}
			mob.tell(str.toString());
			return false;
		}
		if(((String)commands.elementAt(0)).equalsIgnoreCase("every"))
		{ every=true; commands.removeElementAt(0);}
		if(commands.size()==0){ mob.tell(afterErr); return false;}
		long time=CMath.s_long((String)commands.elementAt(0));
		if(time==0) { mob.tell(L("Time may not be 0.@x1",afterErr)); return false;}
		commands.removeElementAt(0);
		if(commands.size()==0){ mob.tell(afterErr); return false;}
		final String s=(String)commands.elementAt(0);
		final long multiplier=CMLib.english().getMillisMultiplierByName(s);
		if(multiplier<0)
		{
			mob.tell(L("'@x1 Time may not be 0. @x2",s,afterErr));
			return false;
		}
		else
			time=time*multiplier;
		commands.removeElementAt(0);
		if(commands.size()==0){ mob.tell(afterErr); return false;}
		final AfterCommand V=new AfterCommand();
		V.start=System.currentTimeMillis();
		V.duration=time;
		V.every=every;
		V.M=mob;
		V.command=commands;
		V.metaFlags=metaFlags;
		afterCmds.add(V);
		CMLib.threads().startTickDown(this,Tickable.TICKID_AREA,1);
		mob.tell(L("Ok."));
		return false;
	}

	@Override public boolean canBeOrdered(){return false;}
	@Override public boolean securityCheck(MOB mob){return CMSecurity.isAllowed(mob,mob.location(),CMSecurity.SecFlag.AFTER);}



	@Override
	public boolean tick(Tickable ticking, int tickID)
	{
		if(afterCmds.size()==0) return false;
		int s=0;
		while(s<afterCmds.size())
		{
			final AfterCommand cmd=afterCmds.get(s);
			if(System.currentTimeMillis()>(cmd.start+cmd.duration))
			{
				final boolean every=cmd.every;
				if(every)
				{
					cmd.start=System.currentTimeMillis();
					s++;
				}
				else
					afterCmds.remove(s);
				cmd.M.doCommand((Vector)cmd.command.clone(),cmd.metaFlags);
			}
			else
				s++;
		}
		return true;
	}
}
