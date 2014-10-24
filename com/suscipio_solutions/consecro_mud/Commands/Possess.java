package com.suscipio_solutions.consecro_mud.Commands;
import java.util.Enumeration;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.Session;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.CMSecurity;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;


@SuppressWarnings("rawtypes")
public class Possess extends StdCommand
{
	public Possess(){}

	private final String[] access=I(new String[]{"POSSESS","POSS"});
	@Override public String[] getAccessWords(){return access;}

	public MOB getTarget(MOB mob, Vector commands, boolean quiet)
	{
		String targetName=CMParms.combine(commands,0);
		MOB target=null;
		if(targetName.length()>0)
		{
			target=mob.location().fetchInhabitant(targetName);
			if(target==null)
			{
				final Environmental t=mob.location().fetchFromRoomFavorItems(null,targetName);
				if((t!=null)&&(!(t instanceof MOB)))
				{
					if(!quiet)
						mob.tell(mob,t,null,L("You can't do that to <T-NAMESELF>."));
					return null;
				}
			}
		}

		if(target!=null)
			targetName=target.name();

		if((target==null)||((!CMLib.flags().canBeSeenBy(target,mob))&&((!CMLib.flags().canBeHeardMovingBy(target,mob))||(!target.isInCombat()))))
		{
			if(!quiet)
			{
				if(targetName.trim().length()==0)
					mob.tell(L("You don't see them here."));
				else
					mob.tell(L("You don't see '@x1' here.",targetName));
			}
			return null;
		}

		return target;
	}

	@Override
	public boolean execute(MOB mob, Vector commands, int metaFlags)
		throws java.io.IOException
	{
		if(mob.soulMate()!=null)
		{
			mob.tell(L("You are already possessing someone.  Quit back to your body first!"));
			return false;
		}
		commands.removeElementAt(0);
		final String MOBname=CMParms.combine(commands,0);
		MOB target=getTarget(mob,commands,true);
		if((target==null)||(!target.isMonster()))
			target=mob.location().fetchInhabitant(MOBname);
		if((target==null)||(!target.isMonster()))
		{
			final Enumeration<Room> r=mob.location().getArea().getProperMap();
			for(;r.hasMoreElements();)
			{
				final Room R=r.nextElement();
				final MOB mob2=R.fetchInhabitant(MOBname);
				if((mob2!=null)&&(mob2.isMonster()))
				{
					target=mob2;
					break;
				}
			}
		}
		if((target==null)||(!target.isMonster()))
		{
			try
			{
				final List<MOB> inhabs=CMLib.map().findInhabitants(CMLib.map().rooms(), mob,MOBname,100);
				for(final MOB mob2 : inhabs)
					if((mob2.isMonster())&&(CMSecurity.isAllowed(mob,mob2.location(),CMSecurity.SecFlag.POSSESS)))
					{
						target=mob2;
						break;
					}
			}catch(final NoSuchElementException e){}
		}
		if((target==null)||(!target.isMonster())||(!CMLib.flags().isInTheGame(target,true)))
		{
			mob.tell(L("You can't possess '@x1' right now.",MOBname));
			return false;
		}
		if(!CMSecurity.isAllowed(mob,target.location(),CMSecurity.SecFlag.POSSESS))
		{
			mob.tell(L("You can not possess @x1.",target.Name()));
			return false;
		}

		if((!CMSecurity.isASysOp(mob))&&(CMSecurity.isASysOp(target)))
		{
			mob.tell(L("You may not possess '@x1'.",MOBname));
			return false;
		}
		final CMMsg msg=CMClass.getMsg(mob,target,null, CMMsg.MSG_POSSESS, L("<S-NAME> get(s) a far away look, then seem(s) to fall limp."));
		final Room room=mob.location();
		if((room==null)||(room.okMessage(mob, msg)))
		{
			if(room!=null) room.send(mob, msg);
			final Session s=mob.session();
			s.setMob(target);
			target.setSession(s);
			target.setSoulMate(mob);
			mob.setSession(null);
			CMLib.commands().postLook(target,true);
			target.tell(L("^HYour spirit has changed bodies@x1, use QUIT to return to yours.",(mob.isAttribute(MOB.Attrib.SYSOPMSGS)?" and SECURITY mode is ON":"")));
		}
		return false;
	}

	@Override public boolean canBeOrdered(){return true;}
	@Override public boolean securityCheck(MOB mob){return CMSecurity.isAllowed(mob,mob.location(),CMSecurity.SecFlag.POSSESS);}


}
