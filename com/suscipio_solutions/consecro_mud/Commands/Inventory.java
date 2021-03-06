package com.suscipio_solutions.consecro_mud.Commands;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Items.interfaces.Coins;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Wearable;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMParms;


@SuppressWarnings({"unchecked","rawtypes"})
public class Inventory extends StdCommand
{
	public Inventory(){}

	private final String[] access=I(new String[]{"INVENTORY","INV","I"});
	@Override public String[] getAccessWords(){return access;}

	public static class InventoryList
	{
		public boolean foundAndSeen=false;
		public boolean foundButUnseen=false;
		public Vector<Item> viewItems=new Vector<Item>();
		public Hashtable<String,Vector<Coins>> moneyItems=new Hashtable<String,Vector<Coins>>();
	}

	public static InventoryList fetchInventory(MOB seer, MOB mob)
	{
		final InventoryList lst = new InventoryList();
		Vector<Coins> coinsV=null;
		int insertAt=-1;
		CMLib.beanCounter().getTotalAbsoluteNativeValue(mob);
		for(final Enumeration<Item> i=mob.items();i.hasMoreElements();)
		{
			final Item thisItem=i.nextElement();
			if(thisItem==null) continue;
			if((thisItem.container()==null)
			&&(thisItem.amWearingAt(Wearable.IN_INVENTORY)))
			{
				if(CMLib.flags().canBeSeenBy(thisItem,seer))
					lst.foundAndSeen=true;
				else
					lst.foundButUnseen=true;
				if((!(thisItem instanceof Coins))||(((Coins)thisItem).getDenomination()==0.0))
					lst.viewItems.addElement(thisItem);
				else
				{
					coinsV=lst.moneyItems.get(((Coins)thisItem).getCurrency());
					if(coinsV==null)
					{
						coinsV=new Vector<Coins>();
						lst.moneyItems.put(((Coins)thisItem).getCurrency(),coinsV);
					}
					for(insertAt=0;insertAt<coinsV.size();insertAt++)
						if(coinsV.elementAt(insertAt).getDenomination()>((Coins)thisItem).getDenomination())
							break;
					if(insertAt>=coinsV.size())
						coinsV.addElement((Coins)thisItem);
					else
						coinsV.insertElementAt((Coins)thisItem,insertAt);
				}
			}
		}
		return lst;
	}

	protected String getShowableMoney(InventoryList list)
	{
		final StringBuilder msg=new StringBuilder("");
		if(list.moneyItems.size()>0)
		{
			msg.append(L("\n\r^HMoney:^N\n\r"));
			Item I=null;
			for(final Enumeration e=list.moneyItems.keys();e.hasMoreElements();)
			{
				final String key=(String)e.nextElement();
				final Vector<Coins> V=list.moneyItems.get(key);
				double totalValue=0.0;
				for(int v=0;v<V.size();v++)
				{
					I=V.elementAt(v);
					if(I!=null)
					{
						if(v>0) msg.append(", ");
						if(I instanceof Coins)
							totalValue+=((Coins)I).getTotalValue();
						msg.append(I.name());
					}
				}
				msg.append(" ^N("+CMLib.beanCounter().abbreviatedPrice(key,totalValue)+")");
				if(e.hasMoreElements()) msg.append("\n\r");
			}
		}
		return msg.toString();
	}

	public StringBuilder getInventory(MOB seer, MOB mob, String mask)
	{
		final StringBuilder msg=new StringBuilder("");
		final InventoryList list = fetchInventory(seer,mob);
		if(((list.viewItems.size()>0)||(list.moneyItems.size()>0))
		&&(!list.foundAndSeen))
		{
			list.viewItems.clear();
			list.moneyItems.clear();
			list.foundButUnseen=true;
		}
		else
		if((mask!=null)&&(mask.trim().length()>0))
		{
			mask=mask.trim().toUpperCase();
			if(!mask.startsWith("all")) mask="all "+mask;
			final Vector<Item> V=(Vector<Item>)list.viewItems.clone();
			list.viewItems.clear();
			Item I=(V.size()>0)?(Item)V.firstElement():null;
			while(I!=null)
			{
				I=(Item)CMLib.english().fetchEnvironmental(V,mask,false);
				if(I!=null)
				{
					list.viewItems.addElement(I);
					V.remove(I);
				}
			}
		}
		if((list.viewItems.size()==0)&&(list.moneyItems.size()==0))
		{
			if((mask!=null)&&(mask.trim().length()>0))
				msg.append(L("(nothing like that you can see right now)"));
			else
				msg.append(L("(nothing you can see right now)"));
		}
		else
		{
			if(list.viewItems.size()>0)
				msg.append(CMLib.lister().lister(seer,list.viewItems,true,"MItem","",false,seer.isAttribute(MOB.Attrib.COMPRESS)));
			if(list.foundButUnseen)
				msg.append(L("(stuff you can't see right now)"));

			msg.append(getShowableMoney(list));
		}
		return msg;
	}


	@Override
	public boolean execute(MOB mob, Vector commands, int metaFlags)
		throws java.io.IOException
	{
		if((commands.size()==1)&&(commands.firstElement() instanceof MOB))
		{
			commands.addElement(getInventory((MOB)commands.firstElement(),mob,null));
			return true;
		}
		final StringBuilder msg=getInventory(mob,mob,CMParms.combine(commands,1));
		if(msg.length()==0)
			mob.tell(L("^HYou are carrying:\n\r^!Nothing!^?\n\r"));
		else
		if(!mob.isMonster())
			mob.session().wraplessPrintln(L("^HYou are carrying:^?\n\r@x1",msg.toString()));
		return false;
	}

	@Override public boolean canBeOrdered(){return true;}


}
