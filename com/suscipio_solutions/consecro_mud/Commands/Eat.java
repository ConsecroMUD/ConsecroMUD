package com.suscipio_solutions.consecro_mud.Commands;
import java.util.List;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Food;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Wearable;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.Races.interfaces.Race;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.CMProps;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;


@SuppressWarnings("rawtypes")
public class Eat extends StdCommand
{
	public Eat(){}

	private final String[] access=I(new String[]{"EAT"});
	@Override public String[] getAccessWords(){return access;}
	@Override
	public boolean execute(MOB mob, Vector commands, int metaFlags)
		throws java.io.IOException
	{
		if(commands.size()<2)
		{
			mob.tell(L("Eat what?"));
			return false;
		}
		commands.removeElementAt(0);

		Environmental thisThang=null;
		thisThang=mob.location().fetchFromMOBRoomFavorsItems(mob,null,CMParms.combine(commands,0),Wearable.FILTER_ANY);
		if((thisThang==null)
		||(!CMLib.flags().canBeSeenBy(thisThang,mob)))
		{
			mob.tell(L("You don't see '@x1' here.",CMParms.combine(commands,0)));
			return false;
		}
		final boolean hasHands=mob.charStats().getBodyPart(Race.BODY_HAND)>0;
		if((thisThang instanceof Food)&&(!mob.isMine(thisThang))&&(hasHands))
		{
			mob.tell(L("You don't seem to have '@x1'.",CMParms.combine(commands,0)));
			return false;
		}
		final String eatSound=CMLib.protocol().msp("gulp.wav",10);
		final String eatMsg="<S-NAME> eat(s) <T-NAMESELF>."+eatSound;
		final CMMsg newMsg=CMClass.getMsg(mob,thisThang,null,hasHands?CMMsg.MSG_EAT:CMMsg.MSG_EAT_GROUND,eatMsg);
		if(mob.location().okMessage(mob,newMsg))
		{
			if((thisThang instanceof Food)
			&&(newMsg.value()>0)
			&&(newMsg.value()<((Food)thisThang).nourishment())
			&&(newMsg.othersMessage()!=null)
			&&(newMsg.othersMessage().startsWith(eatMsg))
			&&(newMsg.sourceMessage().equalsIgnoreCase(newMsg.othersMessage()))
			&&(newMsg.targetMessage().equalsIgnoreCase(newMsg.othersMessage())))
			{
				final String biteMsg="<S-NAME> take(s) a bite of <T-NAMESELF>."+eatSound;
				newMsg.setSourceMessage(biteMsg);
				newMsg.setTargetMessage(biteMsg);
				newMsg.setOthersMessage(biteMsg);
			}
			mob.location().send(mob,newMsg);
		}
		return false;
	}
	@Override public double combatActionsCost(final MOB mob, final List<String> cmds){return CMProps.getCommandCombatActionCost(ID());}
	@Override public double actionsCost(final MOB mob, final List<String> cmds){return CMProps.getCommandActionCost(ID());}
	@Override public boolean canBeOrdered(){return true;}


}
