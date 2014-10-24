package com.suscipio_solutions.consecro_mud.Commands;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Commands.interfaces.Command;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Items.interfaces.DeadBody;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.Directions;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Rideable;


@SuppressWarnings("rawtypes")
public class Enter extends Go
{
	public Enter(){}

	private final String[] access=I(new String[]{"ENTER","EN"});
	@Override public String[] getAccessWords(){return access;}
	@Override
	public boolean execute(MOB mob, Vector commands, int metaFlags)
		throws java.io.IOException
	{
		if(commands.size()<=1)
		{
			mob.tell(L("Enter what or where? Try LOOK or EXITS."));
			return false;
		}
		Environmental enterThis=null;
		final String enterWhat=CMParms.combine(commands,1);
		int dir=Directions.getGoodDirectionCode(enterWhat.toUpperCase());
		if(dir<0)
		{
			enterThis=mob.location().fetchFromRoomFavorItems(null,enterWhat.toUpperCase());
			if(enterThis!=null)
			{
				if(enterThis instanceof Rideable)
				{
					final Command C=CMClass.getCommand("Sit");
					if(C!=null) return C.execute(mob,commands,metaFlags);
				}
				else
				if((enterThis instanceof DeadBody)
				&&(mob.phyStats().height()<=0)
				&&(mob.phyStats().weight()<=0))
				{
					final String enterStr=L("<S-NAME> enter(s) <T-NAME>.");
					final CMMsg msg=CMClass.getMsg(mob,enterThis,null,CMMsg.MSG_SIT,enterStr);
					if(mob.location().okMessage(mob,msg))
						mob.location().send(mob,msg);
					return true;
				}
			}
			dir=CMLib.tracking().findExitDir(mob,mob.location(),enterWhat);
			if(dir<0)
			{
				mob.tell(L("You don't see '@x1' here.",enterWhat.toLowerCase()));
				return false;
			}
		}
		CMLib.tracking().walk(mob,dir,false,false,false);
		return false;
	}
	@Override public boolean canBeOrdered(){return true;}


}
