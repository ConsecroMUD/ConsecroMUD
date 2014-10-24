package com.suscipio_solutions.consecro_mud.Commands;
import java.util.List;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.CMSecurity;
import com.suscipio_solutions.consecro_mud.core.Resources;


@SuppressWarnings({"unchecked","rawtypes"})
public class Goto extends At
{
	public Goto(){}

	private final String[] access=I(new String[]{"GOTO"});
	@Override public String[] getAccessWords(){return access;}
	@Override
	public boolean execute(MOB mob, Vector commands, int metaFlags)
		throws java.io.IOException
	{
		Room room=null;
		if(commands.size()<2)
		{
			mob.tell(L("Go where? Try a Room ID, player name, area name, or room text!"));
			return false;
		}
		commands.removeElementAt(0);
		final StringBuffer cmd = new StringBuffer(CMParms.combine(commands,0));
		List<String> stack=(List<String>)Resources.getResource("GOTOS_FOR_"+mob.Name().toUpperCase());
		if(stack==null)
		{
			stack=new Vector();
			Resources.submitResource("GOTOS_FOR_"+mob.Name().toUpperCase(),stack);
		}
		else
		if(stack.size()>10)
			stack.remove(0);
		final Room curRoom=mob.location();
		if("PREVIOUS".startsWith(cmd.toString().toUpperCase()))
		{
			if(stack.size()==0)
				mob.tell(L("Your previous room stack is empty."));
			else
			{
				room=CMLib.map().getRoom(stack.get(stack.size()-1));
				stack.remove(stack.size()-1);
			}
		}
		else
		if(CMLib.map().findArea(cmd.toString())!=null)
			room=CMLib.map().findArea(cmd.toString()).getRandomProperRoom();
		else
		if(cmd.toString().toUpperCase().startsWith("AREA "))
			room=CMLib.map().findAreaRoomLiberally(mob,curRoom.getArea(),CMParms.combine(commands,1),"RIPM",100);
		else
			room=CMLib.map().findWorldRoomLiberally(mob,cmd.toString(),"RIPMA",100,120000);

		if(room==null)
		{
			mob.tell(L("Goto where? Try a Room ID, player name, area name, room text, or PREVIOUS!"));
			return false;
		}
		if(!CMSecurity.isAllowed(mob,room,CMSecurity.SecFlag.GOTO))
		{
			mob.tell(L("You aren't powerful enough to do that. Try 'GO'."));
			return false;
		}
		if(curRoom==room)
		{
			mob.tell(L("Done."));
			return false;
		}
		if(!"PREVIOUS".startsWith(cmd.toString().toUpperCase()))
		{
			if((stack.size()==0)||(stack.get(stack.size()-1)!=mob.location().roomID()))
				stack.add(CMLib.map().getExtendedRoomID(mob.location()));
		}
		if(mob.playerStats().getPoofOut().length()>0)
			mob.location().show(mob,null,CMMsg.MSG_OK_VISUAL,mob.playerStats().getPoofOut());
		room.bringMobHere(mob,true);
		if(mob.playerStats().getPoofIn().length()>0)
			room.show(mob,null,CMMsg.MSG_OK_VISUAL,mob.playerStats().getPoofIn());
		CMLib.commands().postLook(mob,true);
		return false;
	}

	@Override public boolean canBeOrdered(){return true;}
	@Override public boolean securityCheck(MOB mob){return CMSecurity.isAllowedAnywhere(mob,CMSecurity.SecFlag.GOTO);}


}
