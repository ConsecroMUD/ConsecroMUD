package com.suscipio_solutions.consecro_mud.Commands;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Commands.interfaces.Command;
import com.suscipio_solutions.consecro_mud.Libraries.interfaces.ListingLibrary;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMStrings;


@SuppressWarnings({"unchecked","rawtypes"})
public class Commands extends StdCommand
{
	public Commands(){}

	private final String[] access=I(new String[]{"COMMANDS"});
	@Override public String[] getAccessWords(){return access;}
	@Override
	public boolean execute(MOB mob, Vector commands, int metaFlags)
		throws java.io.IOException
	{
		if(!mob.isMonster())
		{
			if ((commands!=null) && (commands.size()>0) && ("CLEAR".startsWith(commands.get(0).toString().toUpperCase())))
			{
				mob.clearCommandQueue();
				mob.tell(L("Command queue cleared."));
				return false;
			}
			final StringBuffer commandList=new StringBuffer("");
			final Vector commandSet=new Vector();
			int col=0;
			final HashSet done=new HashSet();
			for(final Enumeration e=CMClass.commands();e.hasMoreElements();)
			{
				final Command C=(Command)e.nextElement();
				final String[] access=C.getAccessWords();
				if((access!=null)
				&&(access.length>0)
				&&(access[0].length()>0)
				&&(!done.contains(access[0]))
				&&(C.securityCheck(mob)))
				{
					done.add(access[0]);
					commandSet.add(access[0]);
				}
			}
			for(final Enumeration<Ability> a=mob.allAbilities();a.hasMoreElements();)
			{
				final Ability A=a.nextElement();
				if((A!=null)&&(A.triggerStrings()!=null)&&(A.triggerStrings().length>0)&&(!done.contains(A.triggerStrings()[0])))
				{
					done.add(A.triggerStrings()[0]);
					commandSet.add(A.triggerStrings()[0]);
				}
			}
			Collections.sort(commandSet);
			final int COL_LEN=ListingLibrary.ColFixer.fixColWidth(19.0,mob);
			for(final Iterator i=commandSet.iterator();i.hasNext();)
			{
				final String s=(String)i.next();
				if(++col>3){ commandList.append("\n\r"); col=0;}
				commandList.append(CMStrings.padRight("^<HELP^>"+s+"^</HELP^>",COL_LEN));
			}
			commandList.append("\n\r\n\rEnter HELP 'COMMAND' for more information on these commands.\n\r");
			mob.session().colorOnlyPrintln(L("^HComplete commands list:^?\n\r@x1",commandList.toString()),false);
		}
		return false;
	}

	@Override public boolean canBeOrdered(){return true;}
}
