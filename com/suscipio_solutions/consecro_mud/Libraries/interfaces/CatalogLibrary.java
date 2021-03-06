package com.suscipio_solutions.consecro_mud.Libraries.interfaces;
import java.util.Enumeration;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMFile;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;

public interface CatalogLibrary extends CMLibrary
{
	public static final String ITEMCAT="ITEMS".intern();
	public static final String MOBSCAT="MOBS".intern();

	public String[] getCatalogItemNames();
	public String[] getCatalogItemNames(String catagory);
	public String[] getCatalogMobNames();
	public String[] getCatalogMobNames(String catagory);
	public String[] getMobCatalogCatagories();
	public String[] getItemCatalogCatagories();
	public void setCatagory(Physical P, String catagory);
	public Item[] getCatalogItems();
	public MOB[] getCatalogMobs();
	public boolean isCatalogObj(Environmental E);
	public boolean isCatalogObj(String name);
	public Item getCatalogItem(String name);
	public MOB getCatalogMob(String name);
	public Physical getCatalogObj(Physical P);
	public CataData getCatalogItemData(String name);
	public CataData getCatalogMobData(String name);
	public CataData getCatalogData(Physical P);
	public void addCatalog(String catagory, Physical PA);
	public void updateCatalogCatagory(Physical modelP, String newCat);
	public void delCatalog(Physical P);
	public void addCatalog(Physical PA);
	public void submitToCatalog(Physical P);
	public void updateCatalog(Physical modelP);
	public StringBuffer checkCatalogIntegrity(Physical P);
	public void updateCatalogIntegrity(Physical P);
	public void changeCatalogUsage(Physical P, boolean add);
	public Item getDropItem(MOB M, boolean live);
	public CataData sampleCataData(String xml);
	public Vector<RoomContent> roomContent(Room R);
	public void updateRoomContent(String roomID, Vector<RoomContent> content);
	public void newInstance(Physical P);
	public void bumpDeathPickup(Physical P);
	public CMFile.CMVFSDir getCatalogRoot(CMFile.CMVFSDir resourcesRoot);

	public static interface RoomContent
	{
		public Physical P();
		public Environmental holder();
		public boolean isDirty();
		public void flagDirty();
		public boolean deleted();
	}

	public static interface CataData
	{
		public MaskingLibrary.CompiledZapperMask getMaskV();
		public String getMaskStr();
		public boolean getWhenLive();
		public double getRate();
		public void setMaskStr(String s);
		public void setWhenLive(boolean l);
		public void setRate(double r);
		public Enumeration<Physical> enumeration();
		public void addReference(Physical P);
		public boolean isReference(Physical P);
		public void delReference(Physical P);
		public int numReferences();
		public String mostPopularArea();
		public String randomRoom();
		public void cleanHouse();
		public Physical getLiveReference();
		public int getDeathsPicksups();
		public void bumpDeathPickup();
		public String category();
		public void setCatagory(String cat);

		public String data();

		public void build(String catadata);
	}
}
