package com.suscipio_solutions.consecro_mud.Commands;
import java.util.List;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Common.interfaces.Session;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.CMSecurity;


@SuppressWarnings({"unchecked","rawtypes"})
public class Snoop extends StdCommand
{
	public Snoop(){}

	private final String[] access=I(new String[]{"SNOOP"});
	@Override public String[] getAccessWords(){return access;}

	protected List<Session> snoopingOn(Session S)
	{
		final List<Session> V=new Vector();
		for(final Session S2 : CMLib.sessions().allIterable())
			if(S2.isBeingSnoopedBy(S))
				V.add(S2);
		return V;
	}


	@Override
	public boolean execute(MOB mob, Vector commands, int metaFlags)
		throws java.io.IOException
	{
		commands.removeElementAt(0);
		if(mob.session()==null) return false;
		boolean doneSomething=false;
		for(final Session S : CMLib.sessions().allIterable())
			if(S.isBeingSnoopedBy(mob.session()))
			{
				if(S.mob()!=null)
					mob.tell(L("You stop snooping on @x1.",S.mob().name()));
				else
					mob.tell(L("You stop snooping on someone."));
				doneSomething=true;
				S.setBeingSnoopedBy(mob.session(),false);
			}
		if(commands.size()==0)
		{
			if(!doneSomething)
				mob.tell(L("Snoop on whom?"));
			return false;
		}
		final String whom=CMParms.combine(commands,0);
		Session SnoopOn=null;
		final Session S=CMLib.sessions().findPlayerSessionOnline(whom,false);
		if(S!=null)
		{
			if(S==mob.session())
			{
				mob.tell(L("no."));
				return false;
			}
			else
			if(CMSecurity.isAllowed(mob,S.mob().location(),CMSecurity.SecFlag.SNOOP))
				SnoopOn=S;
		}
		if(SnoopOn==null)
			mob.tell(L("You can't find anyone to snoop on by that name."));
		else
		if(!CMLib.flags().isInTheGame(SnoopOn.mob(),true))
			mob.tell(L("@x1 is not yet fully in the game.",SnoopOn.mob().Name()));
		else
		if(CMSecurity.isASysOp(SnoopOn.mob())&&(!CMSecurity.isASysOp(mob)))
			mob.tell(L("Only another Immortal can snoop on @x1.",SnoopOn.mob().name()));
		else
		{
			final Vector snoop=new Vector();
			snoop.addElement(SnoopOn);
			for(int v=0;v<snoop.size();v++)
			{
				if(snoop.elementAt(v)==mob.session())
				{
					mob.tell(L("This would create a snoop loop!"));
					return false;
				}
				final List<Session> V=snoopingOn((Session)snoop.elementAt(v));
				for(int v2=0;v2<V.size();v2++)
				{
					final Session S2=V.get(v2);
					if(!snoop.contains(S2))
						snoop.addElement(S2);
				}
			}
			mob.tell(L("You start snooping on @x1.",SnoopOn.mob().name()));
			SnoopOn.setBeingSnoopedBy(mob.session(), true);
		}
		return false;
	}

	@Override public boolean canBeOrdered(){return true;}
	@Override public boolean securityCheck(MOB mob){return CMSecurity.isAllowed(mob,mob.location(),CMSecurity.SecFlag.SNOOP);}


}
