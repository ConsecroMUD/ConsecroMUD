package com.suscipio_solutions.consecro_mud.Items.MiscMagic;
import java.util.List;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.PhyStats;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Container;
import com.suscipio_solutions.consecro_mud.Items.interfaces.ImmortalOnly;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMSecurity;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;


@SuppressWarnings({"unchecked","rawtypes"})
public class BagOfEndlessness extends BagOfHolding implements ImmortalOnly
{
	@Override public String ID(){	return "BagOfEndlessness";}
	public BagOfEndlessness()
	{
		super();

		setName("a small sack");
		setDisplayText("a small black sack is crumpled up here.");
		setDescription("A nice silk sack to put your things in.");
		secretIdentity="The Bag of Endless Stuff";
		basePhyStats().setLevel(1);
		capacity=Integer.MAX_VALUE-1000;

		baseGoldValue=10000;
		basePhyStats().setDisposition(basePhyStats().disposition()|PhyStats.IS_BONUS);
		recoverPhyStats();
	}

	@Override
	public boolean okMessage(final Environmental myHost, final CMMsg msg)
	{
		if(!super.okMessage(myHost, msg))
			return false;
		if(msg.amITarget(this)&&(msg.tool() instanceof Item))
		{
			final Item newitem=(Item)msg.tool();
			if((newitem.container()==this)&&(newitem.owner() !=null))
			{
				if((!CMSecurity.isAllowedAnywhere(msg.source(), CMSecurity.SecFlag.COPYITEMS))
				&&(!CMSecurity.isAllowedAnywhere(msg.source(), CMSecurity.SecFlag.CMDITEMS)))
				{
					msg.source().tell(L("You aren't allowed to do that."));
					return false;
				}
			}
		}
		return true;
	}

	@Override
	public void executeMsg(final Environmental myHost, final CMMsg msg)
	{
		if(msg.amITarget(this)&&(msg.tool() instanceof Item))
		{
			final Item newitem=(Item)msg.tool();
			if((newitem.container()==this)
			&&(newitem.owner() !=null))
			{
				Item neweritem=(Item)newitem.copyOf();
				final Vector allStuff=new Vector();
				allStuff.addElement(neweritem);
				if(newitem instanceof Container)
				{
					final List<Item> V=((Container)newitem).getContents();
					for(int v=0;v<V.size();v++)
					{
						final Item I=(Item)V.get(v).copyOf();
						I.setContainer((Container)neweritem);
						allStuff.addElement(I);
					}
				}
				neweritem.setContainer(this);
				for(int i=0;i<allStuff.size();i++)
				{
					neweritem=(Item)allStuff.elementAt(i);
					if(newitem.owner() instanceof MOB)
						((MOB)newitem.owner()).addItem(neweritem);
					else
					if(newitem.owner() instanceof Room)
					{
						((Room)newitem.owner()).addItem(neweritem);
						neweritem.setExpirationDate(expirationDate());
					}
					neweritem.recoverPhyStats();
				}
			}
		}
		super.executeMsg(myHost,msg);
	}
}
