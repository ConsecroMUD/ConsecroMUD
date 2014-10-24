package com.suscipio_solutions.consecro_mud.Commands;
import java.util.List;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Libraries.interfaces.PlayerLibrary;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMStrings;


@SuppressWarnings("rawtypes")
public class Vassals extends StdCommand
{
	public Vassals(){}

	private final String[] access=I(new String[]{"VASSALS"});
	@Override public String[] getAccessWords(){return access;}
	@Override
	public boolean execute(MOB mob, Vector commands, int metaFlags)
		throws java.io.IOException
	{
		mob.tell(L("The following players are in your service:"));
		List<PlayerLibrary.ThinPlayer> players=CMLib.database().vassals(mob,mob.Name());
		final StringBuilder str=new StringBuilder("");
		str.append("[");
		str.append(CMStrings.padRight(L("Race"),8)+" ");
		str.append(CMStrings.padRight(L("Class"),10)+" ");
		str.append(CMStrings.padRight(L("Lvl"),4)+" ");
		str.append(CMStrings.padRight(L("Exp/Lvl"),17));
		str.append(L("] Character name\n\r"));
		for(PlayerLibrary.ThinPlayer tM : players)
		{
			final MOB M=CMLib.players().getPlayer(tM.name);
			if(M==null)
			{
				str.append("[");
				str.append(CMStrings.padRight(tM.race,8)+" ");
				str.append(CMStrings.padRight(tM.charClass,10)+" ");
				str.append(CMStrings.padRight(Integer.toString(tM.level),4)+" ");
				str.append(CMStrings.padRight(tM.exp+"/"+tM.expLvl,17));
				str.append("] "+CMStrings.padRight(tM.name,15));
				str.append("\n\r");
			}
			else
			{
				str.append("[");
				str.append(CMStrings.padRight(M.charStats().getMyRace().name(),8)+" ");
				str.append(CMStrings.padRight(M.charStats().getCurrentClass().name(M.charStats().getCurrentClassLevel()),10)+" ");
				str.append(CMStrings.padRight(""+M.phyStats().level(),4)+" ");
				str.append(CMStrings.padRight(M.getExperience()+"/"+M.getExpNextLevel(),17));
				str.append("] "+CMStrings.padRight(M.name(),15));
				str.append("\n\r");
			}
		}
		mob.tell(str.toString());
		return false;
	}

	@Override public boolean canBeOrdered(){return true;}


}
