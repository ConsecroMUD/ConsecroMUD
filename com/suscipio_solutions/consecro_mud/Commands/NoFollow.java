package com.suscipio_solutions.consecro_mud.Commands;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMParms;


@SuppressWarnings("rawtypes")
public class NoFollow extends Follow
{
	public NoFollow(){}

	private final String[] access=I(new String[]{"NOFOLLOW","NOFOL"});
	@Override public String[] getAccessWords(){return access;}

	@Override
	public boolean execute(MOB mob, Vector commands, int metaFlags)
		throws java.io.IOException
	{
		if((commands.size()>1)&&(commands.elementAt(0) instanceof String))
		{
			if(((String)commands.elementAt(0)).equalsIgnoreCase("UNFOLLOW"))
			{
				unfollow(mob,((commands.size()>1)&&(commands.elementAt(1) instanceof String)&&(((String)commands.elementAt(1)).equalsIgnoreCase("QUIETLY"))));
				return false;
			}
			MOB M=mob.fetchFollower(CMParms.combine(commands,1));
			if((M==null)&&(mob.location()!=null))
			{
				M=mob.location().fetchInhabitant(CMParms.combine(commands,1));
				if(M!=null)
					mob.tell(L("@x1 is not following you!",M.name(mob)));
				else
					mob.tell(L("There is noone here called '@x1' following you!",CMParms.combine(commands,1)));
				return false;
			}
			if((mob.location()!=null)&&(M!=null)&&(M.amFollowing()==mob))
			{
				nofollow(M,true,false);
				return true;
			}
			mob.tell(L("There is noone called '@x1' following you!",CMParms.combine(commands,1)));
			return false;
		}
		if(!mob.isAttribute(MOB.Attrib.NOFOLLOW))
		{
			mob.setAttribute(MOB.Attrib.NOFOLLOW,true);
			//unfollow(mob,false);
			mob.tell(L("You are no longer accepting new followers."));
		}
		else
		{
			mob.setAttribute(MOB.Attrib.NOFOLLOW,false);
			mob.tell(L("You are now accepting new followers."));
		}
		return false;
	}

	@Override public boolean canBeOrdered(){return true;}


}
