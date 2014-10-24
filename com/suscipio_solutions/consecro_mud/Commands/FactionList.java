package com.suscipio_solutions.consecro_mud.Commands;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Common.interfaces.Faction;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMStrings;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_mud.core.collections.XVector;

@SuppressWarnings("rawtypes")
public class FactionList extends StdCommand
{
	public FactionList(){}

	private final String[] access=I(new String[]{"FACTIONS","FAC"});
	@Override public String[] getAccessWords(){return access;}
	@Override
	public boolean execute(MOB mob, Vector commands, int metaFlags)
		throws java.io.IOException
	{
		final StringBuffer msg=new StringBuffer(L("\n\r^HFaction Standings:^?^N\n\r"));
		boolean none=true;
		final XVector<String> list=new XVector<String>(mob.fetchFactions());
		list.sort();
		for (final String name : list)
		{
			final Faction F=CMLib.factions().getFaction(name);
			if((F!=null)&&(F.showInFactionsCommand()))
			{
				none=false;
				msg.append(formatFactionLine(name,mob.fetchFaction(name)));
			}
		}
		if(!mob.isMonster())
			if(none)
				mob.session().colorOnlyPrintln(L("\n\r^HNo factions apply.^?^N"));
			else
				mob.session().colorOnlyPrintln(msg.toString());
		return false;
	}

	public String formatFactionLine(String name,int faction)
	{
		final StringBuffer line=new StringBuffer();
		line.append("  "+CMStrings.padRight(CMStrings.capitalizeAndLower(CMLib.factions().getName(name).toLowerCase()),21)+" ");
		final Faction.FRange FR=CMLib.factions().getRange(name,faction);
		if(FR==null)
			line.append(CMStrings.padRight(""+faction,17)+" ");
		else
			line.append(CMStrings.padRight(FR.name(),17)+" ");
		line.append("[");
		line.append(CMStrings.padRight(calcRangeBar(name,faction),25));
		line.append("]\n\r");
		return line.toString();
	}

	public String calcRangeBar(String factionID, int faction)
	{
		final StringBuffer bar=new StringBuffer();
		final Double fill=Double.valueOf(CMath.div(CMLib.factions().getRangePercent(factionID,faction),4));
		for(int i=0;i<fill.intValue();i++)
		{
			bar.append("*");
		}
		return bar.toString();
	}


	@Override public boolean canBeOrdered(){return true;}


}
