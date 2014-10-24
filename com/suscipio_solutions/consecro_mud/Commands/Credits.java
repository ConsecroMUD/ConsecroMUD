package com.suscipio_solutions.consecro_mud.Commands;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMFile;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.Resources;


@SuppressWarnings("rawtypes")
public class Credits extends StdCommand
{
	public Credits(){}

	private final String[] access=I(new String[]{"CREDITS"});
	@Override public String[] getAccessWords(){return access;}
	@Override
	public boolean execute(MOB mob, Vector commands, int metaFlags)
		throws java.io.IOException
	{
		StringBuffer credits=new CMFile(Resources.buildResourcePath("text")+"credits.txt",null,CMFile.FLAG_LOGERRORS).text();
		try { credits = CMLib.webMacroFilter().virtualPageFilter(credits);}catch(final Exception ex){}
		if((credits!=null)&&(mob.session()!=null)&&(credits.length()>0))
			mob.session().colorOnlyPrintln(credits.toString());
		else
			mob.tell(L(""));
		return false;
	}

	@Override public boolean canBeOrdered(){return true;}


}
