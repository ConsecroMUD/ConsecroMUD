package com.suscipio_solutions.consecro_mud.Items.interfaces;
import com.suscipio_solutions.consecro_mud.core.collections.ReadOnlyList;
import com.suscipio_solutions.consecro_mud.core.interfaces.CloseableLockable;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;

public interface Container extends Item, CloseableLockable
{
	public ReadOnlyList<Item> getContents();
	public int capacity();
	public void setCapacity(int newValue);
	public boolean canContain(Environmental E);
	public boolean isInside(Item I);
	public long containTypes();
	public void setContainTypes(long containTypes);
	public void emptyPlease(boolean flatten);
	
	public static final int CONTAIN_ANYTHING=0;
	public static final int CONTAIN_LIQUID=1;
	public static final int CONTAIN_COINS=2;
	public static final int CONTAIN_SWORDS=4;
	public static final int CONTAIN_DAGGERS=8;
	public static final int CONTAIN_OTHERWEAPONS=16;
	public static final int CONTAIN_ONEHANDWEAPONS=32;
	public static final int CONTAIN_BODIES=64;
	public static final int CONTAIN_READABLES=128;
	public static final int CONTAIN_SCROLLS=256;
	public static final int CONTAIN_CAGED=512;
	public static final int CONTAIN_KEYS=1024;
	public static final int CONTAIN_DRINKABLES=2048;
	public static final int CONTAIN_CLOTHES=4096;
	public static final int CONTAIN_SMOKEABLES=8192;
	public static final int CONTAIN_SSCOMPONENTS=16384;
	public static final int CONTAIN_FOOTWEAR=32768;
	public static final int CONTAIN_RAWMATERIALS=65536;
	public static final String[] CONTAIN_DESCS={"ANYTHING",
												"LIQUID",
												"COINS",
												"SWORDS",
												"DAGGERS",
												"OTHER WEAPONS",
												"ONE-HANDED WEAPONS",
												"BODIES",
												"READABLES",
												"SCROLLS",
												"CAGED ANIMALS",
												"KEYS",
												"DRINKABLES",
												"CLOTHES",
												"SMOKEABLES",
												"SS COMPONENTS",
												"FOOTWEAR",
												"RAWMATERIALS"};
}
