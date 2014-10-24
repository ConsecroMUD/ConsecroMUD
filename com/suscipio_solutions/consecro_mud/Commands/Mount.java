package com.suscipio_solutions.consecro_mud.Commands;
import java.util.List;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.CMProps;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Rideable;
import com.suscipio_solutions.consecro_mud.core.interfaces.Rider;


@SuppressWarnings({"unchecked","rawtypes"})
public class Mount extends StdCommand
{
	public Mount(){}

	private final String[] access=I(new String[]{"MOUNT","BOARD","RIDE","M"});
	@Override public String[] getAccessWords(){return access;}
	@Override
	public boolean execute(MOB mob, Vector commands, int metaFlags)
		throws java.io.IOException
	{
		if(commands.size()<2)
		{
			mob.tell(L("@x1 what?",((String)commands.elementAt(0))));
			return false;
		}
		commands.removeElementAt(0);
		Environmental recipient=null;
		final Vector possRecipients=new Vector();
		for(int m=0;m<mob.location().numInhabitants();m++)
		{
			final MOB M=mob.location().fetchInhabitant(m);
			if((M!=null)&&(M instanceof Rideable))
				possRecipients.addElement(M);
		}
		for(int i=0;i<mob.location().numItems();i++)
		{
			final Item I=mob.location().getItem(i);
			if((I!=null)&&(I instanceof Rideable))
				possRecipients.addElement(I);
		}
		Rider RI=null;
		if(commands.size()>1)
		{
			final Item I=mob.location().findItem(null,(String)commands.firstElement());
			if(I!=null)
			{
				commands.removeElementAt(0);
				I.setRiding(null);
				RI=I;
			}
			if(RI==null)
			{
				final MOB M=mob.location().fetchInhabitant((String)commands.firstElement());
				if(M!=null)
				{
					if(!CMLib.flags().canBeSeenBy(M,mob))
					{
						mob.tell(L("You don't see @x1 here.",((String)commands.firstElement())));
						return false;
					}
					if((!CMLib.flags().isBoundOrHeld(M))&&(!M.willFollowOrdersOf(mob)))
					{
						mob.tell(L("Only the bound or servants can be mounted unwillingly."));
						return false;
					}
					RI=M;
					RI.setRiding(null);
					commands.removeElementAt(0);
				}
			}
		}
		recipient=CMLib.english().fetchEnvironmental(possRecipients,CMParms.combine(commands,0),true);
		if(recipient==null)
			recipient=CMLib.english().fetchEnvironmental(possRecipients,CMParms.combine(commands,0),false);
		if(recipient==null)
			recipient=mob.location().fetchFromRoomFavorMOBs(null,CMParms.combine(commands,0));
		if((recipient==null)||(!CMLib.flags().canBeSeenBy(recipient,mob)))
		{
			mob.tell(L("You don't see '@x1' here.",CMParms.combine(commands,0)));
			return false;
		}
		String mountStr=null;
		if(recipient instanceof Rideable)
		{
			if(RI!=null)
				mountStr=L("<S-NAME> mount(s) <O-NAME> onto <T-NAMESELF>.");
			else
				mountStr="<S-NAME> "+((Rideable)recipient).mountString(CMMsg.TYP_MOUNT,mob)+" <T-NAMESELF>.";
		}
		else
		{
			if(RI!=null)
				mountStr=L("<S-NAME> mount(s) <O-NAME> to <T-NAMESELF>.");
			else
				mountStr=L("<S-NAME> mount(s) <T-NAMESELF>.");
		}
		final CMMsg msg=CMClass.getMsg(mob,recipient,RI,CMMsg.MSG_MOUNT,mountStr);
		if(mob.location().okMessage(mob,msg))
			mob.location().send(mob,msg);
		return false;
	}
	@Override public double combatActionsCost(final MOB mob, final List<String> cmds){return CMProps.getCommandCombatActionCost(ID());}
	@Override public double actionsCost(final MOB mob, final List<String> cmds){return CMProps.getCommandActionCost(ID());}
	@Override public boolean canBeOrdered(){return true;}
}
