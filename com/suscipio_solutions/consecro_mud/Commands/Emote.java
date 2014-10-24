package com.suscipio_solutions.consecro_mud.Commands;
import java.util.List;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Libraries.interfaces.TelnetFilter.Pronoun;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.CMProps;
import com.suscipio_solutions.consecro_mud.core.CMStrings;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;


@SuppressWarnings("rawtypes")
public class Emote extends StdCommand
{
	public Emote(){}

	private final String[] access=I(new String[]{"EMOTE",",",";",":"});
	@Override public String[] getAccessWords(){return access;}

	@Override
	public boolean execute(MOB mob, Vector commands, int metaFlags)
		throws java.io.IOException
	{
		if(commands.size()<2)
		{
			mob.tell(L(" EMOTE what?"));
			return false;
		}
		String combinedCommands=CMParms.combine(commands,1);
		combinedCommands=CMProps.applyINIFilter(combinedCommands,CMProps.Str.EMOTEFILTER);
		if(combinedCommands.trim().startsWith("'")||combinedCommands.trim().startsWith("`"))
			combinedCommands=combinedCommands.trim();
		else
			combinedCommands=" "+combinedCommands.trim();
		Environmental target=null;
		int x=combinedCommands.indexOf('/');
		while(x>0)
		{
			int y=CMStrings.indexOfEndOfWord(combinedCommands,x+1);
			if(y<0) y=combinedCommands.length();
			String rest=combinedCommands.substring(x+1,y);
			Pronoun P=Pronoun.NAME;
			for(final Pronoun p : Pronoun.values())
			{
				if((p.emoteSuffix!=null)&&(rest.endsWith(p.emoteSuffix)))
				{
					P=p;
					rest=rest.substring(0,rest.length()-p.emoteSuffix.length());
					break;
				}
			}
			if(rest.length()>0)
			{
				final Environmental E=mob.location().fetchFromRoomFavorMOBs(null, rest);
				if((E!=null)&&(CMLib.flags().canBeSeenBy(E, mob)))
				{
					target=E;
					combinedCommands=combinedCommands.substring(0,x)+"<T"+P.suffix+">"+combinedCommands.substring(y);
				}
			}
			x=combinedCommands.indexOf('/',x+1);
		}
		final String emote="^E<S-NAME>"+combinedCommands+" ^?";
		final CMMsg msg=CMClass.getMsg(mob,target,null,CMMsg.MSG_EMOTE,emote);
		if(mob.location().okMessage(mob,msg))
			mob.location().send(mob,msg);
		return false;
	}
	@Override public double combatActionsCost(final MOB mob, final List<String> cmds){return CMProps.getCommandCombatActionCost(ID());}
	@Override public double actionsCost(final MOB mob, final List<String> cmds){return CMProps.getCommandActionCost(ID());}
	@Override public boolean canBeOrdered(){return true;}


}
