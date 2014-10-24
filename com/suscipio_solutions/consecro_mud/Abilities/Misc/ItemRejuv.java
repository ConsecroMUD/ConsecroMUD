package com.suscipio_solutions.consecro_mud.Abilities.Misc;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.StdAbility;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.ItemTicker;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Container;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Tickable;



@SuppressWarnings({"unchecked","rawtypes"})
public class ItemRejuv extends StdAbility implements ItemTicker
{
	@Override public String ID() { return "ItemRejuv"; }
	private final static String localizedName = CMLib.lang().L("ItemRejuv");
	@Override public String name() { return localizedName; }
	private final static String localizedStaticDisplay = CMLib.lang().L("(ItemRejuv)");
	@Override public String displayText() { return localizedStaticDisplay; }
	@Override protected int canAffectCode(){return CAN_ITEMS;}
	@Override protected int canTargetCode(){return 0;}
	@Override public int abstractQuality(){return Ability.QUALITY_MALICIOUS;}
	protected Room myProperLocation=null;
	protected Vector contents=new Vector();
	protected Vector ccontents=new Vector();

	public synchronized void loadContent(ItemTicker ticker, Item item, Room room)
	{
		if(ticker instanceof ItemRejuv)
		{
			final ItemRejuv ability=(ItemRejuv)ticker;
			ability.contents.addElement(item);

			final Item newItem=(Item)item.copyOf();
			newItem.stopTicking();
			newItem.setContainer(item.container());
			ability.ccontents.addElement(newItem);

			for(int r=0;r<room.numItems();r++)
			{
				final Item content=room.getItem(r);
				if((content!=null)&&(content.container()==item))
					loadContent(ability,content,room);
			}
		}
	}

	@Override public Room properLocation(){return myProperLocation;}
	@Override
	public void setProperLocation(Room room)
	{ myProperLocation=room; }
	@Override
	public void loadMeUp(Item item, Room room)
	{
		unloadIfNecessary(item);
		contents=new Vector();
		ccontents=new Vector();
		final ItemRejuv ability=new ItemRejuv();
		ability.myProperLocation=room;
		if(item.fetchEffect(ability.ID())==null)
			item.addEffect(ability);
		ability.setSavable(false);
		loadContent(ability,item,room);
		contents.trimToSize();
		ccontents.trimToSize();
		CMLib.threads().startTickDown(ability,Tickable.TICKID_ROOM_ITEM_REJUV,item.phyStats().rejuv());
	}

	@Override
	public void unloadIfNecessary(Item item)
	{
		final ItemRejuv a=(ItemRejuv)item.fetchEffect(new ItemRejuv().ID());
		if(a!=null)
			a.unInvoke();
	}

	@Override
	public String accountForYourself()
	{ return ""; }

	@Override
	public boolean isVerifiedContents(Item item)
	{
		if(item==null) return false;
		return contents.contains(item);
	}

	public synchronized void verifyFixContents()
	{
		final Room R=myProperLocation;
		for(int i=0;i<contents.size();i++)
		{
			final Item thisItem=(Item)contents.elementAt(i);
			if(thisItem!=null)
			{
				final Container thisContainer=((Item)ccontents.elementAt(i)).container();
				if((!R.isContent(thisItem))
				&&((!CMLib.flags().isMobile(thisItem)) || (!CMLib.flags().isInTheGame(thisItem,true))))
				{
					final Item newThisItem=(Item)((Item)ccontents.elementAt(i)).copyOf();
					contents.setElementAt(newThisItem,i);
					for(int c=0;c<ccontents.size();c++)
					{
						final Item thatItem=(Item)ccontents.elementAt(c);
						if((thatItem.container()==thisItem)&&(newThisItem instanceof Container))
							thatItem.setContainer((Container)newThisItem);
					}
					if(newThisItem instanceof Container)
					{
						final Container C=(Container)newThisItem;
						final boolean locked=C.defaultsLocked();
						final boolean open=(!locked) && (!C.defaultsClosed());
						C.setDoorsNLocks(C.hasADoor(),open,C.defaultsClosed(),C.hasALock(),locked,C.defaultsLocked());
					}
					newThisItem.setExpirationDate(0);
					R.addItem(newThisItem);
					
					newThisItem.setContainer(thisContainer);
				}
				else
					thisItem.setContainer(thisContainer);
			}
		}
	}

	@Override
	public boolean tick(Tickable ticking, int tickID)
	{
		if(!super.tick(ticking,tickID))
			return false;

		final Item item=(Item)affected;
		final Room R=myProperLocation;
		if((item==null)||(R==null))
			return false;

		if(tickID==Tickable.TICKID_ROOM_ITEM_REJUV)
		{
			if((CMLib.flags().canNotBeCamped(item)||CMLib.flags().canNotBeCamped(R))
			&& (R.numPCInhabitants() > 0) 
			&& (!CMLib.tracking().isAnAdminHere(R,false)))
			{
				CMLib.threads().setTickPending(ticking,Tickable.TICKID_ROOM_ITEM_REJUV);
				return true; // it will just come back next time
			}
			verifyFixContents();
			if((!R.isContent(item))
			&&((!CMLib.flags().isMobile(item)) || (!CMLib.flags().isInTheGame(item,true))))
			{
				unloadIfNecessary(item);
				loadMeUp((Item)contents.elementAt(0),R);
				return false;
			}
			if(item instanceof Container)
			{
				final Container C=(Container)item;
				final boolean locked=C.defaultsLocked();
				final boolean open=(!locked) && (!C.defaultsClosed());
				C.setDoorsNLocks(C.hasADoor(),open,C.defaultsClosed(),C.hasALock(),locked,C.defaultsClosed());
			}
		}
		return true;
	}
}
