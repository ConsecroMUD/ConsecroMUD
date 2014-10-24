package com.suscipio_solutions.consecro_mud.Common;
import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Common.interfaces.CMShop;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.Auctioneer;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.Auctioneer.AuctionData;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.CMObject;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.ShopKeeper;


@SuppressWarnings({"unchecked","rawtypes"})
public class AuctionCMShop implements CMShop
{
	@Override public String ID(){return "AuctionCMShop";}
	@Override public String name() { return ID();}
	@Override public int compareTo(CMObject o){ return CMClass.classID(this).compareToIgnoreCase(CMClass.classID(o));}
	public static final Vector emptyV=new Vector();
	public String auctionShop="";
	protected WeakReference<ShopKeeper> shopKeeper=null;

	@Override
	public CMObject copyOf()
	{
		try
		{
			final Object O=this.clone();
			return (CMObject)O;
		}
		catch(final CloneNotSupportedException e)
		{
			return new AuctionCMShop();
		}
	}
	@Override public CMObject newInstance(){try{return getClass().newInstance();}catch(final Exception e){return new AuctionCMShop();}}
	@Override public void initializeClass(){}


	@Override
	public CMShop build(ShopKeeper SK)
	{
		shopKeeper=new WeakReference(SK);
		return this;
	}

	@Override public ShopKeeper shopKeeper(){ return (shopKeeper==null)?null:shopKeeper.get();}
	@Override public boolean isSold(int code){final ShopKeeper SK=shopKeeper(); return (SK==null)?false:SK.isSold(code);}

	@Override
	public boolean inEnumerableInventory(Environmental thisThang)
	{
		return false;
	}

	@Override public Environmental addStoreInventory(Environmental thisThang){ return addStoreInventory(thisThang,1,-1);}
	@Override public int enumerableStockSize(){ return 0;}
	@Override public int totalStockSize(){ return 0;}
	@Override public Iterator<Environmental> getStoreInventory(){ return emptyV.iterator();}
	@Override public Iterator<Environmental> getStoreInventory(String srchStr){ return emptyV.iterator();}
	@Override public Iterator<Environmental> getEnumerableInventory(){ return emptyV.iterator();}

	@Override
	public Environmental addStoreInventory(Environmental thisThang,
										   int number,
										   int price)
	{
		if(shopKeeper() instanceof Auctioneer)
			auctionShop=((Auctioneer)shopKeeper()).auctionHouse();
		return thisThang;
	}

	@Override public int totalStockWeight(){return 0;}

	@Override public int totalStockSizeIncludingDuplicates(){ return 0;}
	@Override public void delAllStoreInventory(Environmental thisThang){}

	@Override public boolean doIHaveThisInStock(String name, MOB mob){return getStock(name,mob)!=null;}

	@Override
	public int stockPrice(Environmental likeThis)
	{
		return -1;
	}
	@Override public int numberInStock(Environmental likeThis){ return 1;}
	@Override public void resubmitInventory(List<Environmental> V){}

	@Override
	public Environmental getStock(String name, MOB mob)
	{
		final List<AuctionData> auctions=CMLib.coffeeShops().getAuctions(null,auctionShop);
		final Vector auctionItems=new Vector();
		for(int a=0;a<auctions.size();a++)
		{
			final Item I=auctions.get(a).auctioningI;
			auctionItems.addElement(I);
		}
		for(int a=0;a<auctionItems.size();a++)
		{
			final Item I=(Item)auctionItems.elementAt(a);
			I.setExpirationDate(CMLib.english().getContextNumber(auctionItems,I));
		}
		Environmental item=CMLib.english().fetchEnvironmental(auctionItems,name,true);
		if(item==null)
			item=CMLib.english().fetchEnvironmental(auctionItems,name,false);
		return item;
	}

	@Override
	public void destroyStoreInventory()
	{
	}


	@Override
	public Environmental removeStock(String name, MOB mob)
	{
		return null;
	}

	@Override public void emptyAllShelves(){}

	@Override
	public List<Environmental> removeSellableProduct(String named, MOB mob)
	{
		return emptyV;
	}

	@Override public String makeXML(){return "";}
	@Override public void buildShopFromXML(String text){}
}
