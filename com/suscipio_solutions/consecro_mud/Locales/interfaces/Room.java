package com.suscipio_solutions.consecro_mud.Locales.interfaces;
import java.util.Enumeration;
import java.util.List;

import com.suscipio_solutions.consecro_mud.Areas.interfaces.Area;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.Climate;
import com.suscipio_solutions.consecro_mud.Common.interfaces.PhyStats;
import com.suscipio_solutions.consecro_mud.Common.interfaces.TimeClock;
import com.suscipio_solutions.consecro_mud.Exits.interfaces.Exit;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.collections.Filterer;
import com.suscipio_solutions.consecro_mud.core.interfaces.EachApplicable;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.ItemPossessor;
import com.suscipio_solutions.consecro_mud.core.interfaces.PhysicalAgent;
import com.suscipio_solutions.consecro_mud.core.interfaces.Places;


public interface Room extends PhysicalAgent, ItemPossessor, Places
{
	public String roomID();
	public void setRoomID(String newRoomID);

	public final static int INDOORS=128;

	public final static int DOMAIN_OUTDOORS_CITY=0;
	public final static int DOMAIN_OUTDOORS_WOODS=1;
	public final static int DOMAIN_OUTDOORS_ROCKS=2;
	public final static int DOMAIN_OUTDOORS_PLAINS=3;
	public final static int DOMAIN_OUTDOORS_UNDERWATER=4;
	public final static int DOMAIN_OUTDOORS_AIR=5;
	public final static int DOMAIN_OUTDOORS_WATERSURFACE=6;
	public final static int DOMAIN_OUTDOORS_JUNGLE=7;
	public final static int DOMAIN_OUTDOORS_SWAMP=8;
	public final static int DOMAIN_OUTDOORS_DESERT=9;
	public final static int DOMAIN_OUTDOORS_HILLS=10;
	public final static int DOMAIN_OUTDOORS_MOUNTAINS=11;
	public final static int DOMAIN_OUTDOORS_SPACEPORT=12;
	public final static String[] outdoorDomainDescs={
		"CITY",
		"WOODS",
		"ROCKY",
		"PLAINS",
		"UNDERWATER",
		"AIR",
		"WATERSURFACE",
		"JUNGLE",
		"SWAMP",
		"DESERT",
		"HILLS",
		"MOUNTAINS",
		"SPACEPORT"};

	public final static int DOMAIN_INDOORS_STONE=INDOORS+0;
	public final static int DOMAIN_INDOORS_WOOD=INDOORS+1;
	public final static int DOMAIN_INDOORS_CAVE=INDOORS+2;
	public final static int DOMAIN_INDOORS_MAGIC=INDOORS+3;
	public final static int DOMAIN_INDOORS_UNDERWATER=INDOORS+4;
	public final static int DOMAIN_INDOORS_AIR=INDOORS+5;
	public final static int DOMAIN_INDOORS_WATERSURFACE=INDOORS+6;
	public final static int DOMAIN_INDOORS_METAL=INDOORS+7;
	public final static String[] indoorDomainDescs={
		"STONE",
		"WOODEN",
		"CAVE",
		"MAGIC",
		"UNDERWATER",
		"AIR",
		"WATERSURFACE",
		"METAL"};

	public int domainType();
	public int myResource();
	public void setResource(int resourceCode);
	public List<Integer> resourceChoices();
	public void toggleMobility(boolean onoff);
	public boolean getMobility();

	public enum VariationCode
	{
		SUMMER('S',TimeClock.Season.SUMMER.ordinal()),
		SPRING('S',TimeClock.Season.SPRING.ordinal()),
		WINTER('S',TimeClock.Season.WINTER.ordinal()),
		FALL('S',TimeClock.Season.FALL.ordinal()),
		DAY('C',TimeClock.TimeOfDay.DAY.ordinal()),
		DAYTIME('C',TimeClock.TimeOfDay.DAY.ordinal()),
		NIGHT('C',TimeClock.TimeOfDay.NIGHT.ordinal()),
		NIGHTTIME('C',TimeClock.TimeOfDay.NIGHT.ordinal()),
		DAWN('C',TimeClock.TimeOfDay.DAWN.ordinal()),
		DUSK('C',TimeClock.TimeOfDay.DUSK.ordinal()),
		RAIN('W',Climate.WEATHER_RAIN),
		SLEET('W',Climate.WEATHER_SLEET),
		SNOW('W',Climate.WEATHER_SNOW),
		CLEAR('W',Climate.WEATHER_CLEAR),
		HEATWAVE('W',Climate.WEATHER_HEAT_WAVE),
		THUNDERSTORM('W',Climate.WEATHER_THUNDERSTORM),
		BLIZZARD('W',Climate.WEATHER_BLIZZARD),
		WINDY('W',Climate.WEATHER_WINDY),
		DROUGHT('W',Climate.WEATHER_DROUGHT),
		DUSTSTORM('W',Climate.WEATHER_DUSTSTORM),
		COLD('W',Climate.WEATHER_WINTER_COLD),
		HAIL('W',Climate.WEATHER_HAIL),
		CLOUDY('W',Climate.WEATHER_CLOUDY),
		SWIMMING('M',PhyStats.IS_SWIMMING),
		FLYING('M',PhyStats.IS_FLYING),
		CRAWLING('M',PhyStats.IS_SITTING),
		SITTING('M',PhyStats.IS_SITTING),
		FALLING('M',PhyStats.IS_FALLING),
		CLIMBING('M',PhyStats.IS_CLIMBING),
		INVISIBLE('M',PhyStats.IS_INVISIBLE),
		HIDDEN('M',PhyStats.IS_HIDDEN),
		VISITED('V',-1),
		ELSE('\n',-1),
		VARIES('\r',-1)
		;
		public final char c;
		public final int num;
		public final String openTag;
		public final String closeTag;
		VariationCode(char c, int num){this.c=c;this.num=num;openTag="<"+toString()+">";closeTag="</"+toString()+">";}
	}
	public void startItemRejuv();
	public void recoverRoomStats();

	public void clearSky();
	public void giveASky(int zero);
	public boolean isSameRoom(Object O);

	public Area getArea();
	public void setArea(Area newArea);
	public void setGridParent(GridLocale room);
	public GridLocale getGridParent();

	public Room[] rawDoors();
	public void setRawExit(int direction, Environmental E);
	public Exit getRawExit(int direction);
	public Exit getReverseExit(int direction);
	public Exit getPairedExit(int direction);
	public Room getRoomInDir(int direction);
	public Exit getExitInDir(int direction);
	public Room prepareRoomInDir(Room fromRoom, int direction);

	public int getCombatTurnMobIndex();
	public void setCombatTurnMobIndex(final int index);

	public int pointsPerMove(MOB mob);
	public int thirstPerRound(MOB mob);

	public void send(MOB source, CMMsg msg);
	public void sendOthers(MOB source, CMMsg msg);
	public void showHappens(int allCode, String allMessage);
	public void showHappens(int allCode, Environmental like, String allMessage);
	public boolean show(MOB source, Environmental target, int allCode, String allMessage);
	public boolean show(MOB source, Environmental target, Environmental tool, int allCode, String allMessage);
	public boolean show(MOB source, Environmental target, Environmental tool, int srcCode, int tarCode, int othCode, String allMessage);
	public boolean show(MOB source, Environmental target, Environmental tool, int srcCode, String srcMessage, int tarCode, String tarMessage, int othCode, String othMessage);
	public boolean show(MOB source, Environmental target, Environmental tool, int allCode, String srcMessage, String tarMessage, String othMessage);
	public boolean showOthers(MOB source, Environmental target, int allCode, String allMessage);
	public boolean showSource(MOB source, Environmental target, int allCode, String allMessage);
	public boolean showOthers(MOB source, Environmental target, Environmental tool, int allCode, String allMessage);
	public boolean showSource(MOB source, Environmental target, Environmental tool, int allCode, String allMessage);
	public boolean isHere(Environmental E);

	public MOB fetchInhabitant(String inhabitantID);
	public List<MOB> fetchInhabitants(String inhabitantID);
	public Enumeration<MOB> inhabitants();
	public void addInhabitant(MOB mob);
	public void delInhabitant(MOB mob);
	public void delAllInhabitants(boolean destroy);
	public int numInhabitants();
	public boolean isInhabitant(MOB mob);
	public MOB fetchRandomInhabitant();
	public MOB fetchInhabitant(int i);
	public int numPCInhabitants();
	public void bringMobHere(MOB mob, boolean andFollowers);
	/**
	 * Applies the given code to each mob in this room
	 * @param applier code to execute against each object
	 */
	public void eachInhabitant(final EachApplicable<MOB> applier);

	public String getContextName(Environmental E);

	public PhysicalAgent fetchFromRoomFavorExits(String thingName);
	public PhysicalAgent fetchFromRoomFavorItems(Item goodLocation, String thingName);
	public PhysicalAgent fetchFromMOBRoomItemExit(MOB mob, Item goodLocation, String thingName, Filterer<Environmental> filter);
	public PhysicalAgent fetchFromRoomFavorMOBs(Item goodLocation, String thingName);
	public PhysicalAgent fetchFromMOBRoomFavorsItems(MOB mob, Item goodLocation, String thingName, Filterer<Environmental> filter);
	public PhysicalAgent fetchFromMOBRoomFavorsMOBs(MOB mob, Item goodLocation, String thingName, Filterer<Environmental> filter);
}
