package com.suscipio_solutions.consecro_mud.Items.interfaces;


public interface Coins extends Item
{
	public long getNumberOfCoins();
	public void setNumberOfCoins(long number);
	public boolean putCoinsBack();
	public double getDenomination();
	public void setDenomination(double valuePerCoin);
	public double getTotalValue();
	public String getCurrency();
	public void setCurrency(String named);
}
