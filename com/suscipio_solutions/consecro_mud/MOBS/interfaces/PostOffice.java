package com.suscipio_solutions.consecro_mud.MOBS.interfaces;
import java.util.Map;

import com.suscipio_solutions.consecro_mud.Common.interfaces.Clan;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.core.interfaces.ShopKeeper;



public interface PostOffice extends ShopKeeper
{
	public String getSenderName(MOB mob, Clan.Function func, boolean checked);
	public void addToBox(String boxName, Item thisThang, String from, String to, long holdTime, double COD);
	public boolean delFromBox(String boxName, Item thisThang);
	public void emptyBox(String boxName);
	public Map<String, String> getOurOpenBoxes(String boxName);
	public void createBoxHere(String boxName, String forward);
	public void deleteBoxHere(String boxName);
	public MailPiece parsePostalItemData(String data);
	public Item findBoxContents(String boxName, String likeThis);
	public String postalChain();
	public void setPostalChain(String name);
	public String postalBranch(); // based on individual shopkeeper
	public String findProperBranch(String name);

	public double minimumPostage();
	public void setMinimumPostage(double d);
	public double postagePerPound();
	public void setPostagePerPound(double d);
	public double holdFeePerPound();
	public void setHoldFeePerPound(double d);
	public double feeForNewBox();
	public void setFeeForNewBox(double d);
	public int maxMudMonthsHeld();
	public void setMaxMudMonthsHeld(int months);

	public static class MailPiece
	{
		public String from="";
		public String to="";
		public String time="";
		public String cod="";
		public String classID="";
		public String xml="";
	}

}
