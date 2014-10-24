package com.suscipio_solutions.consecro_mud.Commands;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMProps;


@SuppressWarnings("rawtypes")
public class Ver extends StdCommand
{
	public Ver(){}

	private final String[] access=I(new String[]{"VERSION","VER"});
	@Override public String[] getAccessWords(){return access;}
	@Override
	public boolean execute(MOB mob, Vector commands, int metaFlags)
		throws java.io.IOException
	{
		mob.tell(L("ConsecroMUD v@x1",CMProps.getVar(CMProps.Str.MUDVER)));
		mob.tell(L("(C) 2009-2014 Suscipio Solutions"));
		mob.tell(L("^<A HREF=\"http://www.consecromud.org\"^>http://www.consecromud.org^</A^>"));
		return false;
	}

	@Override public boolean canBeOrdered(){return true;}


}
