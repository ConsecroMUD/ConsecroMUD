package com.suscipio_solutions.consecro_mud.Commands;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMParms;


@SuppressWarnings("rawtypes")
public class Description extends StdCommand
{
	public Description(){}

	private final String[] access=I(new String[]{"DESCRIPTION"});
	
	private final int CHAR_LIMIT = 128 * 1024;
	
	@Override public String[] getAccessWords(){return access;}
	@Override
	public boolean execute(MOB mob, Vector commands, int metaFlags)
		throws java.io.IOException
	{
		if(commands.size()<2)
		{
			mob.tell(L("^xYour current description:^?\n\r@x1",mob.description()));
			mob.tell(L("\n\rEnter DESCRIPTION [NEW TEXT] to change."));
			return false;
		}
		
		final String s=CMParms.combine(commands,1);
		if(s.length()>CHAR_LIMIT)
			mob.tell(L("Your description exceeds @x1 characters in length.  Please re-enter a shorter one.",""+CHAR_LIMIT));
		else
		{
			mob.setDescription(s);
			mob.tell(L("Your description has been changed."));
		}
		return false;
	}

	@Override public boolean canBeOrdered(){return false;}


}
