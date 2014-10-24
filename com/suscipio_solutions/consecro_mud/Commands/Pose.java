package com.suscipio_solutions.consecro_mud.Commands;
import java.util.List;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.PlayerStats;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.CMProps;


@SuppressWarnings("rawtypes")
public class Pose extends StdCommand
{
	public Pose(){}

	private final String[] access=I(new String[]{"POSE","NOPOSE"});
	@Override public String[] getAccessWords(){return access;}
	@Override
	public boolean execute(MOB mob, Vector commands, int metaFlags)
		throws java.io.IOException
	{
		if((commands.size()>0)&&(commands.firstElement().toString().equalsIgnoreCase("NOPOSE")))
		{
			final PlayerStats pstats = mob.playerStats();
			if(pstats != null)
			{
				if((pstats.getSavedPose()==null)||(pstats.getSavedPose().length()==0))
				{
					mob.tell(L("You are not currently posing."));
					return false;
				}
				pstats.setSavedPose("");
				mob.setDisplayText("");
				mob.tell(L("You stop posing."));
			}
			return false;
		}
		if(commands.size()<2)
		{
			if(mob.displayText().length()==0)
				mob.tell(L("POSE how?"));
			else
				mob.tell(L("Your current pose is: @x1",mob.displayText(mob)));
			return false;
		}
		String combinedCommands=CMParms.combine(commands,1);
		combinedCommands=CMProps.applyINIFilter(combinedCommands,CMProps.Str.POSEFILTER);
		if(combinedCommands.trim().startsWith("'")||combinedCommands.trim().startsWith("`"))
			combinedCommands=combinedCommands.trim();
		else
			combinedCommands=" "+combinedCommands.trim();
		final String emote="^E<S-NAME>"+combinedCommands+" ^?";
		final CMMsg msg=CMClass.getMsg(mob,null,null,CMMsg.MSG_EMOTE | CMMsg.MASK_ALWAYS,L("^E@x1@x2 ^?",mob.name(),combinedCommands),emote,emote);
		if(mob.location().okMessage(mob,msg))
		{
			mob.location().send(mob,msg);
			mob.setDisplayText(mob.Name()+combinedCommands);
			final PlayerStats pstats = mob.playerStats();
			if(pstats != null)
				pstats.setSavedPose(mob.Name()+combinedCommands);
		}
		return false;
	}
	@Override public double combatActionsCost(final MOB mob, final List<String> cmds){return CMProps.getCommandCombatActionCost(ID());}
	@Override public double actionsCost(final MOB mob, final List<String> cmds){return CMProps.getCommandActionCost(ID());}
	@Override public boolean canBeOrdered(){return true;}


}
