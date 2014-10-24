package com.suscipio_solutions.consecro_mud.Libraries.interfaces;
import java.util.List;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMShop;
import com.suscipio_solutions.consecro_mud.Items.interfaces.InnKey;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.Auctioneer;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.Auctioneer.AuctionData;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.ShopKeeper;

public interface ShoppingLibrary extends CMLibrary
{
	public ShopKeeper getShopKeeper(Environmental E);
	public List<Environmental> getAllShopkeepers(Room here, MOB notMOB);
	public String getViewDescription(MOB viewerM, Environmental E);
	public double rawSpecificGoldPrice(Environmental product,  CMShop shop, double numberOfThem);
	public double prejudiceValueFromPart(MOB customer, boolean sellTo, String part);
	public double prejudiceFactor(MOB customer, String factors, boolean sellTo);
	public ShopKeeper.ShopPrice sellingPrice(MOB seller, MOB buyer, Environmental product, ShopKeeper shop, boolean includeSalesTax);
	public double devalue(ShopKeeper shop, Environmental product);
	public ShopKeeper.ShopPrice pawningPrice(MOB seller, MOB buyer, Environmental product, ShopKeeper shop);
	public double getSalesTax(Room homeRoom, MOB seller);
	public boolean standardSellEvaluation(MOB seller, MOB buyer, Environmental product, ShopKeeper shop, double maxToPay, double maxEverPaid, boolean sellNotValue);
	public boolean standardBuyEvaluation(MOB seller, MOB buyer, Environmental product, ShopKeeper shop, boolean buyNotView);
	public String getListInventory(MOB seller,  MOB buyer, List<? extends Environmental> inventory, int limit, ShopKeeper shop, String mask);
	public String findInnRoom(InnKey key, String addThis, Room R);
	public MOB parseBuyingFor(MOB buyer, String message);
	public double transactPawn(MOB shopkeeper, MOB pawner, ShopKeeper shop, Environmental product);
	public void transactMoneyOnly(MOB seller, MOB buyer, ShopKeeper shop, Environmental product, boolean sellerGetsPaid);
	public boolean purchaseItems(Item baseProduct, List<Environmental> products, MOB seller, MOB mobFor);
	public boolean purchaseMOB(MOB product, MOB seller, ShopKeeper shop, MOB mobFor);
	public void purchaseAbility(Ability A,  MOB seller, ShopKeeper shop, MOB mobFor);
	public List<Environmental> addRealEstateTitles(List<Environmental> V, MOB buyer, CMShop shop, Room myRoom);
	public boolean ignoreIfNecessary(MOB mob, String ignoreMask, MOB whoIgnores);
	public String storeKeeperString(CMShop shop);
	public boolean doISellThis(Environmental thisThang, ShopKeeper shop);
	public String[] bid(MOB mob, double bid, String bidCurrency, Auctioneer.AuctionData auctionData, Item I, List<String> auctionAnnounces);
	public void returnMoney(MOB to, String currency, double amt);
	public String getAuctionInventory(MOB seller,MOB buyer,Auctioneer auction,String mask);
	public String getListForMask(String targetMessage);
	public List<AuctionData> getAuctions(Object ofLike, String auctionHouse);
	public Auctioneer.AuctionData getEnumeratedAuction(String named, String auctionHouse);
	public void auctionNotify(MOB M, String resp, String regardingItem);
	public void cancelAuction(String auctionHouse, Auctioneer.AuctionData data);
	public void saveAuction(Auctioneer.AuctionData data, String auctionHouse, boolean updateOnly);
}
