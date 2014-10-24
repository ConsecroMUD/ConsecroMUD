package com.suscipio_solutions.consecro_mud.Commands;
import java.util.List;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Container;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Weapon;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Wearable;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.CMProps;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;


@SuppressWarnings({"unchecked","rawtypes"})
public class Draw extends Get
{
	public Draw(){}

	private final String[] access=I(new String[]{"DRAW"});
	@Override public String[] getAccessWords(){return access;}

	public Vector getSheaths(MOB mob)
	{
		final Vector sheaths=new Vector();
		if(mob!=null)
		for(int i=0;i<mob.numItems();i++)
		{
			final Item I=mob.getItem(i);
			if((I!=null)
			&&(!I.amWearingAt(Wearable.IN_INVENTORY))
			&&(I instanceof Container)
			&&(((Container)I).capacity()>0)
			&&(((Container)I).containTypes()!=Container.CONTAIN_ANYTHING))
			{
				final List<Item> contents=((Container)I).getContents();
				for(int c=0;c<contents.size();c++)
					if(contents.get(c) instanceof Weapon)
					{
						sheaths.addElement(I);
						break;
					}
			}
		}
		return sheaths;
	}

	@Override
	public boolean execute(MOB mob, Vector commands, int metaFlags)
		throws java.io.IOException
	{
		boolean quiet=false;
		boolean noerrors=false;
		boolean ifNecessary=false;
		if((commands.size()>0)&&(((String)commands.lastElement()).equalsIgnoreCase("IFNECESSARY")))
		{
			quiet=true;
			noerrors=true;
			commands.removeElementAt(commands.size()-1);
			if((commands.size()>0)
			&&(((String)commands.lastElement()).equalsIgnoreCase("HELD")))
			{
				commands.removeElementAt(commands.size()-1);
				if(mob.fetchHeldItem()!=null)
					return false;
			}
			else
			if(mob.fetchWieldedItem()!=null)
				return false;
		}
		else
		{
			if((commands.size()>0)&&(((String)commands.lastElement()).equalsIgnoreCase("QUIETLY")))
			{
				commands.removeElementAt(commands.size()-1);
				quiet=true;
			}
			if((commands.size()>0)&&(((String)commands.lastElement()).equalsIgnoreCase("IFNECESSARY")))
			{
				ifNecessary=true;
				commands.removeElementAt(commands.size()-1);
				noerrors=true;
			}
		}

		boolean allFlag=false;
		List<Container> containers=new Vector();
		String containerName="";
		String whatToGet="";
		int c=0;
		final Vector sheaths=getSheaths(mob);
		if(commands.size()>0)
			commands.removeElementAt(0);
		if(commands.size()==0)
		{
			if(sheaths.size()>0)
				containerName=((Item)sheaths.elementAt(0)).name();
			else
				containerName="a weapon";
			for(int i=0;i<mob.numItems();i++)
			{
				final Item I=mob.getItem(i);
				if((I instanceof Weapon)
				   &&(I.container()!=null)
				   &&(sheaths.contains(I.container())))
				{
					containers.add(I.container());
					whatToGet=I.name();
					break;
				}
			}
			if(whatToGet.length()==0)
				for(int i=0;i<mob.numItems();i++)
				{
					final Item I=mob.getItem(i);
					if(I instanceof Weapon)
					{
						whatToGet=I.name();
						break;
					}
				}
		}
		else
		{
			containerName=(String)commands.lastElement();
			commands.insertElementAt("all",0);
			containers=CMLib.english().possibleContainers(mob,commands,Wearable.FILTER_WORNONLY,true);
			if(containers.size()==0) containers=sheaths;
			whatToGet=CMParms.combine(commands,0);
			allFlag=((String)commands.elementAt(0)).equalsIgnoreCase("all");
			if(whatToGet.toUpperCase().startsWith("ALL.")){ allFlag=true; whatToGet="ALL "+whatToGet.substring(4);}
			if(whatToGet.toUpperCase().endsWith(".ALL")){ allFlag=true; whatToGet="ALL "+whatToGet.substring(0,whatToGet.length()-4);}
		}
		boolean doneSomething=false;
		while((c<containers.size())||(containers.size()==0))
		{
			final Vector V=new Vector();
			Container container=null;
			if(containers.size()>0) container=containers.get(c++);
			int addendum=1;
			String addendumStr="";
			boolean doBugFix = true;
			while(doBugFix || allFlag)
			{
				doBugFix=false;
				Environmental getThis=null;
				if((container!=null)&&(mob.isMine(container)))
				   getThis=mob.findItem(container,whatToGet+addendumStr);
				if(getThis==null) break;
				if(getThis instanceof Weapon)
					V.addElement(getThis);
				addendumStr="."+(++addendum);
			}

			for(int i=0;i<V.size();i++)
			{
				final Item getThis=(Item)V.elementAt(i);
				long wearCode=0;
				if(container!=null)	wearCode=container.rawWornCode();
				if((ifNecessary)
				&&(mob.freeWearPositions(Wearable.WORN_WIELD,(short)0,(short)0)==0)
				&&(mob.freeWearPositions(Wearable.WORN_HELD,(short)0,(short)0)==0))
					break;
				if(get(mob,container,getThis,quiet,"draw",false))
				{
					if(getThis.container()==null)
					{
						if(mob.freeWearPositions(Wearable.WORN_WIELD,(short)0,(short)0)==0)
						{
							final CMMsg newMsg=CMClass.getMsg(mob,getThis,null,CMMsg.MSG_HOLD,null);
							if(mob.location().okMessage(mob,newMsg))
								mob.location().send(mob,newMsg);
						}
						else
						{
							final CMMsg newMsg=CMClass.getMsg(mob,getThis,null,CMMsg.MSG_WIELD,null);
							if(mob.location().okMessage(mob,newMsg))
								mob.location().send(mob,newMsg);
						}
					}
				}
				if(container!=null)	container.setRawWornCode(wearCode);
				doneSomething=true;
			}

			if(containers.size()==0) break;
		}
		if((!doneSomething)&&(!noerrors))
		{
			if(containers.size()>0)
			{
				final Container container=containers.get(0);
				if(container.isOpen())
					mob.tell(L("You don't see that in @x1.",container.name()));
				else
					mob.tell(L("@x1 is closed.",container.name()));
			}
			else
				mob.tell(L("You don't see @x1 here.",containerName));
		}
		return false;
	}
	@Override public double actionsCost(final MOB mob, final List<String> cmds){return CMProps.getCommandActionCost(ID());}
	@Override public boolean canBeOrdered(){return true;}


}
