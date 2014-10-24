package com.suscipio_solutions.consecro_mud.Libraries.interfaces;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Areas.interfaces.Area;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CharState;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CharStats;
import com.suscipio_solutions.consecro_mud.Common.interfaces.PhyStats;
import com.suscipio_solutions.consecro_mud.Common.interfaces.PlayerAccount;
import com.suscipio_solutions.consecro_mud.Common.interfaces.Session;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Ammunition;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Libraries.interfaces.XMLLibrary.XMLpiece;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.exceptions.CMException;
import com.suscipio_solutions.consecro_mud.core.interfaces.CMObject;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;
import com.suscipio_solutions.consecro_mud.core.interfaces.PhysicalAgent;

public interface GenericBuilder extends CMLibrary
{
	public final static String[] GENITEMCODES = {
			"CLASS","USES","LEVEL","ABILITY","NAME",
			"DISPLAY","DESCRIPTION","SECRET","PROPERWORN",
			"WORNAND","BASEGOLD","ISREADABLE","ISDROPPABLE",
			"ISREMOVABLE","MATERIAL","AFFBEHAV",
			"DISPOSITION","WEIGHT","ARMOR",
			"DAMAGE","ATTACK","READABLETEXT","IMG"
	};
	public final static String[] GENMOBCODES = {
			"CLASS","RACE","LEVEL","ABILITY","NAME",
			"DISPLAY","DESCRIPTION","MONEY","ALIGNMENT",
			"DISPOSITION","SENSES","ARMOR",
			"DAMAGE","ATTACK","SPEED","AFFBEHAV",
			"ABLES","INVENTORY","TATTS","EXPS","IMG",
			"FACTIONS","VARMONEY"
	};

	public String getGenMOBTextUnpacked(MOB mob, String newText);
	public void resetGenMOB(MOB mob, String newText);
	public int envFlags(Environmental E);
	public void setEnvFlags(Environmental E, int f);
	public String getGenAbilityXML(Ability A);
	public String getPropertiesStr(Environmental E, boolean fromTop);
	public String getGenScripts(PhysicalAgent E, boolean includeVars);
	public String getGenMobInventory(MOB M);
	public void doGenPropertiesCopy(Environmental fromE, Environmental toE);
	public String unpackRoomFromXML(String buf, boolean andContent);
	public String unpackRoomFromXML(List<XMLpiece> xml, boolean andContent);
	public String fillAreaAndCustomVectorFromXML(String buf,  List<XMLpiece> area, List<CMObject> custom, Map<String,String> externalFiles);
	public String fillCustomVectorFromXML(String xml, List<CMObject> custom, Map<String,String> externalFiles);
	public String fillCustomVectorFromXML(List<XMLpiece> xml,  List<CMObject> custom, Map<String,String> externalFiles);
	public String fillAreasVectorFromXML(String buf,  List<List<XMLpiece>> areas, List<CMObject> custom, Map<String,String> externalFiles);
	public void addAutoPropsToAreaIfNecessary(Area newArea);
	public Area unpackAreaObjectFromXML(String xml) throws CMException;
	public String unpackAreaFromXML(List<XMLpiece> aV, Session S, String overrideAreaType, boolean andRooms);
	public String unpackAreaFromXML(String buf, Session S, String overrideAreaType, boolean andRooms);
	public StringBuffer getAreaXML(Area area,  Session S, Set<CMObject> custom, Set<String> files, boolean andRooms);
	public StringBuffer getAreaObjectXML(Area area, Session S, Set<CMObject> custom, Set<String> files, boolean andRooms);
	public StringBuffer logTextDiff(String e1, String e2);
	public void logDiff(Environmental E1, Environmental E2);
	public Room makeNewRoomContent(Room room, boolean makeLive);
	public StringBuffer getRoomMobs(Room room, Set<CMObject> custom, Set<String> files, Map<String,List<MOB>> found);
	public StringBuffer getMobXML(MOB mob);
	public StringBuffer getMobsXML(List<MOB> mobs, Set<CMObject> custom, Set<String> files, Map<String,List<MOB>> found);
	public StringBuffer getUniqueItemXML(Item item, int type, Map<String,List<Item>> found, Set<String> files);
	public String addItemsFromXML(String xmlBuffer, List<Item> addHere, Session S);
	public String addMOBsFromXML(String xmlBuffer, List<MOB> addHere, Session S);
	public String addItemsFromXML(List<XMLpiece> xml, List<Item> addHere, Session S);
	public String addMOBsFromXML(List<XMLpiece> xml, List<MOB> addHere, Session S);
	public MOB getMobFromXML(String xmlBuffer);
	public Item getItemFromXML(String xmlBuffer);
	// TYPE= 0=item, 1=weapon, 2=armor
	public StringBuffer getRoomItems(Room room, Map<String,List<Item>> found, Set<String> files, int type);
	public StringBuffer getItemsXML(List<Item> items, Map<String,List<Item>> found, Set<String> files, int type);
	public StringBuffer getItemXML(Item item);
	public StringBuffer getRoomXML(Room room,  Set<CMObject> custom, Set<String> files, boolean andContent);
	public Ammunition makeAmmunition(String ammunitionType, int number);
	public void setPropertiesStr(Environmental E, String buf, boolean fromTop);
	public void setPropertiesStr(Environmental E, List<XMLpiece> V, boolean fromTop);
	public void setGenScripts(PhysicalAgent E, List<XMLpiece> buf, boolean restoreVars);
	public void populateShops(Environmental E, List<XMLpiece> buf);
	public String getPlayerXML(MOB mob, Set<CMObject> custom, Set<String> files);
	public String getAccountXML(PlayerAccount account, Set<CMObject> custom, Set<String> files);
	public String addPlayersAndAccountsFromXML(String xmlBuffer, List<PlayerAccount> addAccounts, List<MOB> addMobs, Session S);
	public String getExtraEnvPropertiesStr(Environmental E);
	public void fillFileSet(List<String> V, Set<String> H);
	public void fillFileSet(Environmental E, Set<String> H);
	public String getPhyStatsStr(PhyStats E);
	public String getCharStateStr(CharState E);
	public String getCharStatsStr(CharStats E);
	public String getEnvPropertiesStr(Environmental E);
	public void setCharStats(CharStats E, String props);
	public void setCharState(CharState E, String props);
	public void setPhyStats(PhyStats E, String props);
	public void setEnvProperties(Environmental E, List<XMLpiece> buf);
	public void setExtraEnvProperties(Environmental E, List<XMLpiece> buf);
	public void setAnyGenStat(Physical P, String stat, String value, boolean supportPlusMinusPrefix);
	public void setAnyGenStat(Physical P, String stat, String value);
	public String getAnyGenStat(Physical P, String stat);
	public List<String> getAllGenStats(Physical P);
	public boolean isAnyGenStat(Physical P, String stat);
	public int getGenItemCodeNum(String code);
	public String getGenItemStat(Item I, String code);
	public void setGenItemStat(Item I, String code, String val);
	public int getGenMobCodeNum(String code);
	public String getGenMobStat(MOB M, String code);
	public void setGenMobStat(MOB M, String code, String val);
	public Area copyArea(Area A, String newName, boolean setSavable);
	public String getFactionXML(MOB mob);
	public void setFactionFromXML(MOB mob, List<XMLpiece> xml);
}
