package com.suscipio_solutions.consecro_mud.MOBS.interfaces;
import java.util.List;

import com.suscipio_solutions.consecro_mud.Common.interfaces.Clan;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Libraries.interfaces.DatabaseEngine.PlayerData;
import com.suscipio_solutions.consecro_mud.Libraries.interfaces.MoneyLibrary;
import com.suscipio_solutions.consecro_mud.core.interfaces.ShopKeeper;



public interface Banker extends ShopKeeper
{
	public final static double MIN_ITEM_BALANCE_DIVIDEND=10.0;

	public String getBankClientName(MOB mob, Clan.Function func, boolean checked);
	public void addDepositInventory(String depositorName, Item thisThang);
	public boolean delDepositInventory(String depositorName, Item thisThang);
	public void delAllDeposits(String depositorName);
	public int numberDeposited(String depositorName);
	public List<String> getAccountNames();
	public List<PlayerData> getRawPDDepositInventory(String depositorName);
	public List<Item> getDepositedItems(String depositorName);
	public Item findDepositInventory(String mob, String likeThis);
	public void setCoinInterest(double interest);
	public void setItemInterest(double interest);
	public void setLoanInterest(double interest);
	public double getLoanInterest();
	public double getCoinInterest();
	public double getItemInterest();
	public String bankChain();
	public void setBankChain(String name);
	public double getBalance(String depositorName);
	public double totalItemsWorth(String depositorName);
	public MoneyLibrary.DebtItem getDebtInfo(String depositorName);
}
