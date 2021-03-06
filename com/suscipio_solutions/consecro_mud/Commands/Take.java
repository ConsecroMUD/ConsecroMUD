package com.suscipio_solutions.consecro_mud.Commands;
import java.util.List;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Commands.interfaces.Command;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Coins;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Wearable;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.CMProps;
import com.suscipio_solutions.consecro_mud.core.CMSecurity;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;


@SuppressWarnings({"unchecked","rawtypes"})
public class Take extends StdCommand
{
	public Take(){}

	private final String[] access=I(new String[]{"TAKE"});
	@Override public String[] getAccessWords(){return access;}
	@Override
	public boolean execute(MOB mob, Vector commands, int metaFlags)
		throws java.io.IOException
	{
		if(CMSecurity.isAllowed(mob,mob.location(),CMSecurity.SecFlag.ORDER)
		||CMSecurity.isAllowed(mob,mob.location(),CMSecurity.SecFlag.CMDMOBS)
		||CMSecurity.isAllowed(mob,mob.location(),CMSecurity.SecFlag.CMDROOMS))
		{
			if(commands.size()<3)
			{
				mob.tell(L("Take what from whom?"));
				return false;
			}
			commands.removeElementAt(0);
			if(commands.size()<2)
			{
				mob.tell(L("From whom should I take the @x1",(String)commands.elementAt(0)));
				return false;
			}

			final MOB victim=mob.location().fetchInhabitant((String)commands.lastElement());
			if((victim==null)||(!CMLib.flags().canBeSeenBy(victim,mob)))
			{
				mob.tell(L("I don't see anyone called @x1 here.",(String)commands.lastElement()));
				return false;
			}
			if((!victim.isMonster())&&(!CMSecurity.isAllowedEverywhere(mob,CMSecurity.SecFlag.ORDER)))
			{
				mob.tell(L("@x1 is a player!",victim.Name()));
				return false;
			}
			commands.removeElementAt(commands.size()-1);
			if((commands.size()>0)&&(((String)commands.lastElement()).equalsIgnoreCase("from")))
				commands.removeElementAt(commands.size()-1);

			final int maxToGive=CMLib.english().calculateMaxToGive(mob,commands,true,victim,false);
			if(maxToGive<0) return false;

			String thingToGive=CMParms.combine(commands,0);
			int addendum=1;
			String addendumStr="";
			final Vector V=new Vector();
			boolean allFlag=((String)commands.elementAt(0)).equalsIgnoreCase("all");
			if(thingToGive.toUpperCase().startsWith("ALL.")){ allFlag=true; thingToGive="ALL "+thingToGive.substring(4);}
			if(thingToGive.toUpperCase().endsWith(".ALL")){ allFlag=true; thingToGive="ALL "+thingToGive.substring(0,thingToGive.length()-4);}

			if((thingToGive.equalsIgnoreCase("qp"))
			||(thingToGive.toUpperCase().endsWith(" QP"))
			||(thingToGive.toUpperCase().endsWith(".QP")))
			{
				int numToTake=1;
				if(allFlag) numToTake=victim.getQuestPoint();
				if(numToTake>maxToGive) numToTake=maxToGive;
				if((victim.getQuestPoint()<=0)||(victim.getQuestPoint()<numToTake))
				{
					if(victim.getQuestPoint()<=0)
						mob.tell(L("@x1 has no quest points!",victim.name()));
					else
						mob.tell(L("@x1 has only @x2 quest points!",victim.name(),""+victim.getQuestPoint()));
					return false;
				}
				mob.tell(L("You silently take @x1 quest points from @x2.",""+numToTake,victim.name()));
				victim.setQuestPoint(victim.getQuestPoint()-numToTake);
				return false;
			}

			boolean doBugFix = true;
			while(doBugFix || ((allFlag)&&(addendum<=maxToGive)))
			{
				doBugFix=false;
				Environmental giveThis=CMLib.english().bestPossibleGold(victim,null,thingToGive);

				if(giveThis!=null)
				{
					if(((Coins)giveThis).getNumberOfCoins()<CMLib.english().numPossibleGold(victim,thingToGive))
						return false;
					allFlag=false;
				}
				else
					giveThis=victim.fetchItem(null,Wearable.FILTER_UNWORNONLY,thingToGive+addendumStr);
				if((giveThis==null)
				&&(V.size()==0)
				&&(addendumStr.length()==0)
				&&(!allFlag))
					giveThis=victim.findItem(thingToGive);
				if(giveThis==null) break;
				if(giveThis instanceof Item)
				{
					((Item)giveThis).unWear();
					((Item)giveThis).setContainer(null);
					V.addElement(giveThis);
				}
				addendumStr="."+(++addendum);
			}

			if(V.size()==0)
				mob.tell(L("@x1 does not seem to be carrying that.",victim.name()));
			else
			for(int i=0;i<V.size();i++)
			{
				final Item giveThis=(Item)V.elementAt(i);
				final CMMsg newMsg=CMClass.getMsg(victim,mob,giveThis,CMMsg.MASK_ALWAYS|CMMsg.MSG_GIVE,L("<T-NAME> take(s) <O-NAME> from <S-NAMESELF>."));
				if(victim.location().okMessage(victim,newMsg))
					victim.location().send(victim,newMsg);
				if(!mob.isMine(giveThis)) mob.moveItemTo(giveThis);
				if(giveThis instanceof Coins)
					((Coins)giveThis).putCoinsBack();
				if(giveThis instanceof RawMaterial)
					((RawMaterial)giveThis).rebundle();
			}
		}
		else
		{
			if(((String)commands.lastElement()).equalsIgnoreCase("off"))
			{
				commands.removeElementAt(commands.size()-1);
				final Command C=CMClass.getCommand("Remove");
				if(C!=null) C.execute(mob,commands,metaFlags);
			}
			else
			if((commands.size()>1)&&(((String)commands.elementAt(1)).equalsIgnoreCase("off")))
			{
				commands.removeElementAt(1);
				final Command C=CMClass.getCommand("Remove");
				if(C!=null) C.execute(mob,commands,metaFlags);
			}
			else
			{
				final Command C=CMClass.getCommand("Get");
				if(C!=null) C.execute(mob,commands,metaFlags);
			}
		}
		return false;
	}
	@Override public double combatActionsCost(final MOB mob, final List<String> cmds){return CMProps.getCommandCombatActionCost(ID());}
	@Override public double actionsCost(final MOB mob, final List<String> cmds){return CMProps.getCommandActionCost(ID());}
	@Override public boolean canBeOrdered(){return true;}


}
