package com.suscipio_solutions.consecro_mud.Commands;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMFile;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.Resources;


@SuppressWarnings("rawtypes")
public class Rules extends StdCommand
{
	public Rules(){}

	private final String[] access=I(new String[]{"RULES"});
	@Override public String[] getAccessWords(){return access;}
	@Override
	public boolean execute(MOB mob, Vector commands, int metaFlags)
		throws java.io.IOException
	{
		StringBuffer credits=new CMFile(Resources.buildResourcePath("text")+"rules.txt",null,CMFile.FLAG_LOGERRORS).text();
		try { credits = CMLib.webMacroFilter().virtualPageFilter(credits); } catch(final Exception e){}
		if((credits!=null)&&(mob.session()!=null)&&(credits.length()>0))
			mob.session().colorOnlyPrintln(credits.toString());
		else
			mob.tell(L("This mud has no rules.  Welcome to chaos."));
		return false;
	}

	@Override public boolean canBeOrdered(){return true;}


}
