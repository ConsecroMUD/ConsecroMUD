package com.suscipio_solutions.consecro_mud.Libraries.interfaces;
import java.util.List;

import com.suscipio_solutions.consecro_mud.Items.interfaces.Container;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.PhysicalAgent;

public interface MaterialLibrary extends CMLibrary
{
	public Environmental unbundle(Item I, int number, Container C);
	public Environmental splitBundle(Item I, int size, Container C);
	public int getMaterialRelativeInt(String s);
	public int getMaterialCode(String s, boolean exact);
	public int getResourceCode(String s, boolean exact);
	public String getResourceDesc(int MASK);
	public String getMaterialDesc(int MASK);
	public Item makeItemResource(int type);
	public PhysicalAgent makeResource(int myResource, String localeCode, boolean noAnimals, String fullName);
	public void addEffectsToResource(Item I);
	public int getRandomResourceOfMaterial(int material);
	public boolean rebundle(Item I);
	public boolean quickDestroy(Item I);
	public int destroyResourcesValue(MOB E, int howMuch, int finalMaterial, int otherMaterial, Item never);
	public int destroyResourcesValue(Room E, int howMuch, int finalMaterial, int otherMaterial, Item never);
	public int destroyResourcesValue(List<Item> V, int howMuch, int finalMaterial, int otherMaterial, Item never, Container C);
	public int destroyResourcesAmt(MOB E, int howMuch, int finalMaterial);
	public int destroyResourcesAmt(Room E, int howMuch, int finalMaterial);
	public int destroyResourcesAmt(List<Item> V, int howMuch, int finalMaterial);
	public Item fetchFoundOtherEncoded(Room E, String otherRequired);
	public Item fetchFoundOtherEncoded(MOB E, String otherRequired);
	public Item findMostOfMaterial(Room E, int material);
	public Item findMostOfMaterial(MOB E, int material);
	public int findNumberOfResource(Room E, int resource);
	public int findNumberOfResource(MOB E, int resource);
	public Item findMostOfMaterial(Room E, String other);
	public Item findMostOfMaterial(MOB E, String other);
	public Item findFirstResource(Room E, int resource);
	public Item findFirstResource(MOB E, int resource);
	public Item findFirstResource(Room E, String other);
	public Item findFirstResource(MOB E, String other);
	public void adjustResourceName(Item I);
	public String genericType(Item I);
}
