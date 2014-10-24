package com.suscipio_solutions.consecro_mud.Common;
import java.util.Enumeration;
import java.util.List;

import com.suscipio_solutions.consecro_mud.Common.interfaces.CMCommon;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Container;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Wearable;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.collections.SVector;
import com.suscipio_solutions.consecro_mud.core.interfaces.CMObject;
import com.suscipio_solutions.consecro_mud.core.interfaces.EachApplicable;
import com.suscipio_solutions.consecro_mud.core.interfaces.ItemCollection;


/**
 * Abstract collection of item objects, complete with some
 * finders and various accessors.  Also, the copyOf method
 * does a deep copy.
 */
public class DefaultItemCollection implements ItemCollection, CMCommon
{
	private final SVector<Item> contents = new SVector<Item>(0);

	@Override public String ID() { return "DefaultItemCollection"; }
	@Override public String name() { return ID();}

	@Override
	public CMObject copyOf()
	{
		final DefaultItemCollection c=(DefaultItemCollection)newInstance();
		for(int i=0;i<contents.size();i++)
		{
			final Item I=contents.get(i);
			final Item I2=(Item)I.copyOf();
			I2.setOwner(I.owner());
			c.contents.add(I2);
		}
		for(int i=0;i<contents.size();i++)
		{
			final Item I=contents.get(i);
			final Item I2=c.contents.get(i);
			if(I.container() != null)
				for(int i2=0;i2<contents.size();i2++)
					if(I.container() == contents.get(i2))
					{
						I2.setContainer((Container)c.contents.get(i2));
						break;
					}
		}
		return c;
	}

	@Override public void initializeClass() {}
	@Override public CMObject newInstance() { return new DefaultItemCollection(); }
	@Override public int compareTo(CMObject o) { return o==this?0:1; }

	@Override
	public Item findItem(String itemID)
	{
		Item item=(Item)CMLib.english().fetchEnvironmental(contents,itemID,true);
		if(item==null) item=(Item)CMLib.english().fetchEnvironmental(contents,itemID,false);
		return item;
	}
	@Override public Enumeration<Item> items() { return contents.elements();}

	@Override
	public Item findItem(Item goodLocation, String itemID)
	{
		Item item=CMLib.english().fetchAvailableItem(contents,itemID,goodLocation,Wearable.FILTER_ANY,true);
		if(item==null) item=CMLib.english().fetchAvailableItem(contents,itemID,goodLocation,Wearable.FILTER_ANY,false);
		return item;
	}

	@Override
	public List<Item> findItems(Item goodLocation, String itemID)
	{
		List<Item> items=CMLib.english().fetchAvailableItems(contents,itemID,goodLocation,Wearable.FILTER_ANY,true);
		if(items.size()==0)
			items=CMLib.english().fetchAvailableItems(contents,itemID,goodLocation,Wearable.FILTER_ANY,false);
		return items;
	}

	@Override @SuppressWarnings({"unchecked","rawtypes"})
	public List<Item> findItems(String itemID)
	{
		List items=CMLib.english().fetchEnvironmentals(contents,itemID,true);
		if(items.size()==0)
			items=CMLib.english().fetchEnvironmentals(contents,itemID, false);
		return items;
	}

	@Override
	public void addItem(Item item)
	{
		if((item!=null)&&(!item.amDestroyed()))
			contents.addElement(item);
	}

	@Override
	public void delItem(Item item)
	{
		contents.removeElement(item);
	}

	@Override
	public int numItems()
	{
		return contents.size();
	}

	@Override
	public boolean isContent(Item item)
	{
		return contents.contains(item);
	}

	@Override
	public void delAllItems(boolean destroy)
	{
		if(destroy)
			for(int i=numItems()-1;i>=0;i--)
			{
				final Item I=getItem(i);
				if(I!=null) I.destroy();
			}
		contents.clear();
	}
	@Override
	public void eachItem(final EachApplicable<Item> applier)
	{
		final List<Item> contents=this.contents;
		if(contents!=null)
		try
		{
			for(int a=0;a<contents.size();a++)
			{
				final Item I=contents.get(a);
				if(I!=null) applier.apply(I);
			}
		}
		catch(final ArrayIndexOutOfBoundsException e){}
	}

	@Override
	public Item getItem(int i)
	{
		try
		{
			return contents.elementAt(i);
		}
		catch(final java.lang.ArrayIndexOutOfBoundsException x){}
		return null;
	}
  @Override
public Item getRandomItem()
  {
	  if(numItems()==0) return null;
	  return getItem(CMLib.dice().roll(1,numItems(),-1));
  }
}
