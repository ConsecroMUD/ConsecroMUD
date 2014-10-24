package com.suscipio_solutions.consecro_mud.Libraries.interfaces;
import java.io.IOException;
import java.util.List;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Language;
import com.suscipio_solutions.consecro_mud.Areas.interfaces.Area;
import com.suscipio_solutions.consecro_mud.CharClasses.interfaces.CharClass;
import com.suscipio_solutions.consecro_mud.Common.interfaces.Clan;
import com.suscipio_solutions.consecro_mud.Common.interfaces.ClanGovernment;
import com.suscipio_solutions.consecro_mud.Common.interfaces.Manufacturer;
import com.suscipio_solutions.consecro_mud.Common.interfaces.PlayerAccount;
import com.suscipio_solutions.consecro_mud.Exits.interfaces.Exit;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.Races.interfaces.Race;
import com.suscipio_solutions.consecro_mud.core.collections.DVector;
import com.suscipio_solutions.consecro_mud.core.exceptions.CMException;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Modifiable;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;
import com.suscipio_solutions.consecro_mud.core.interfaces.PhysicalAgent;

public interface GenericEditor extends CMLibrary
{
	public void modifyArea(MOB mob, Area myArea) throws IOException;
	public Room modifyRoom(MOB mob, Room R) throws IOException;
	public void modifyAccount(MOB mob, PlayerAccount A) throws IOException;
	public void modifyStdItem(MOB mob, Item thang) throws IOException;
	public void modifyStdMob(MOB mob, MOB thang) throws IOException;
	public void modifyComponents(MOB mob, String componentID) throws IOException;
	public void modifyClan(MOB mob, Clan C) throws IOException;
	public void modifyGenAbility(MOB mob, Ability me) throws IOException;
	public void modifyGenLanguage(MOB mob, Language me) throws IOException;
	public void modifyManufacturer(MOB mob, Manufacturer me) throws IOException;
	public void modifyGenCraftSkill(MOB mob, Ability me) throws IOException;
	public void modifyGenClass(MOB mob, CharClass me) throws IOException;
	public void modifyGenExit(MOB mob, Exit me) throws IOException;
	public void modifyGenRace(MOB mob, Race me) throws IOException;
	public void modifyPlayer(MOB mob, MOB me) throws IOException;
	public void modifyGovernment(MOB mob, ClanGovernment me) throws IOException;
	public AbilityMapper.AbilityMapping modifyAllQualifyEntry(MOB mob, String eachOrAll, Ability me) throws IOException;

	public Room changeRoomType(Room R, Room newRoom);
	public void spells(MOB mob, List<Ability> V, int showNumber, int showFlag, boolean inParms) throws IOException;
	public void wornLocation(MOB mob, long[] oldWornLocation, boolean[] logicalAnd, int showNumber, int showFlag) throws IOException;
	public void wornLayer(MOB mob, short[] layerAtt, short[] clothingLayer, int showNumber, int showFlag) throws IOException;
	public void genAbility(MOB mob, Physical P, int showNumber, int showFlag) throws IOException;
	public void genAffects(MOB mob, Physical P, int showNumber, int showFlag) throws IOException;
	public void genBehaviors(MOB mob, PhysicalAgent P, int showNumber, int showFlag) throws IOException;
	public void genDescription(MOB mob, Environmental E, int showNumber, int showFlag) throws IOException;
	public void genDisplayText(MOB mob, Environmental E, int showNumber, int showFlag) throws IOException;
	public void genMiscSet(MOB mob, Environmental E) throws IOException;
	public void genName(MOB mob, Environmental E, int showNumber, int showFlag) throws IOException;
	public void genMiscText(MOB mob, Environmental E, int showNumber, int showFlag) throws IOException;

	public String prompt(MOB mob, String oldVal, int showNumber, int showFlag, String FieldDisp, DVector choices) throws IOException;
	public int promptMulti(MOB mob, int oldVal, int showNumber, int showFlag, String FieldDisp, DVector choices) throws IOException;
	public String promptMultiOrExtra(MOB mob, String oldVal, int showNumber, int showFlag, String FieldDisp, DVector choices) throws IOException;
	public String prompt(MOB mob, String oldVal, int showNumber, int showFlag, String FieldDisp) throws IOException;
	public String prompt(MOB mob, String oldVal, int showNumber, int showFlag, String FieldDisp, boolean emptyOK) throws IOException;
	public String prompt(MOB mob, String oldVal, int showNumber, int showFlag, String FieldDisp, boolean emptyOK, boolean rawPrint) throws IOException;
	public String prompt(MOB mob, String oldVal, int showNumber, int showFlag, String FieldDisp, boolean emptyOK, String help) throws IOException;
	public String prompt(MOB mob, String oldVal, int showNumber, int showFlag, String FieldDisp, String help) throws IOException;
	public String prompt(MOB mob, String oldVal, int showNumber, int showFlag, String FieldDisp, boolean emptyOK, boolean rawPrint, String help) throws IOException;
	public boolean promptToggle(MOB mob, int showNumber, int showFlag, String FieldDisp) throws IOException;
	public boolean prompt(MOB mob, boolean oldVal, int showNumber, int showFlag, String FieldDisp) throws IOException;
	public boolean prompt(MOB mob, boolean oldVal, int showNumber, int showFlag, String FieldDisp, String help) throws IOException;
	public double prompt(MOB mob, double oldVal, int showNumber, int showFlag, String FieldDisp) throws IOException;
	public double prompt(MOB mob, double oldVal, int showNumber, int showFlag, String FieldDisp, String help) throws IOException;
	public int prompt(MOB mob, int oldVal, int showNumber, int showFlag, String FieldDisp) throws IOException;
	public int prompt(MOB mob, int oldVal, int showNumber, int showFlag, String FieldDisp, String help) throws IOException;
	public long prompt(MOB mob, long oldVal, int showNumber, int showFlag, String FieldDisp) throws IOException;
	public long prompt(MOB mob, long oldVal, int showNumber, int showFlag, String FieldDisp, String help) throws IOException;
	public String prompt(MOB mob,
						String oldVal,
						int showNumber,
						int showFlag,
						String FieldDisp,
						boolean emptyOK,
						boolean rawPrint,
						String help,
						CMEval eval,
						Object[] choices) throws IOException;
	public String prompt(MOB mob,
						String oldVal,
						int showNumber,
						int showFlag,
						String FieldDisp,
						boolean emptyOK,
						boolean rawPrint,
						int maxChars,
						String help,
						CMEval eval,
						Object[] choices) throws IOException;
	public void promptStatStr(MOB mob, Modifiable E, int showNumber, int showFlag, String FieldDisp, String Field) throws IOException;
	public void promptStatStr(MOB mob, Modifiable E, String help, int showNumber, int showFlag, String FieldDisp, String Field, boolean emptyOK) throws IOException;
	public void promptStatInt(MOB mob, Modifiable E, int showNumber, int showFlag, String FieldDisp, String Field) throws IOException;
	public void promptStatInt(MOB mob, Modifiable E, String help, int showNumber, int showFlag, String FieldDisp, String Field) throws IOException;
	public void promptStatBool(MOB mob, Modifiable E, int showNumber, int showFlag, String FieldDisp, String Field) throws IOException;
	public void promptStatBool(MOB mob, Modifiable E, String help, int showNumber, int showFlag, String FieldDisp, String Field) throws IOException;
	public void promptStatChoices(MOB mob, Modifiable E, String help, int showNumber, int showFlag, String FieldDisp, String Field, Object[] choices) throws IOException;
	public void promptStatCommaChoices(MOB mob, Modifiable E, String help, int showNumber, int showFlag, String FieldDisp, String Field, Object[] choices) throws IOException;

	public static interface CMEval 
	{ 
		public Object eval(Object val, Object[] choices, boolean emptyOK) 
				throws CMException;
	}
	
	public static class CMEvalStrChoice implements CMEval
	{
		public static CMEvalStrChoice INSTANCE = new CMEvalStrChoice();
		@Override
		public Object eval(Object val, Object[] choices, boolean emptyOK) throws CMException
		{
			if(choices.length==0) return "";
			final String str=val.toString().trim();
			for(final Object o : choices)
				if(str.equalsIgnoreCase(o.toString()))
					return o.toString();
			throw new CMException("That was not one of your choices.");
		}
	}

}
