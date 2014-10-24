package com.suscipio_solutions.consecro_mud.Items.interfaces;
import java.util.List;

public interface PackagedItems extends Item
{
	public boolean packageMe(Item I, int number);
	public boolean isPackagable(List<Item> V);
	public List<Item> unPackage(int number);
	public int numberOfItemsInPackage();
	public Item getItem();
	public void setNumberOfItemsInPackage(int number);
	public String packageText();
	public void setPackageText(String text);
}
