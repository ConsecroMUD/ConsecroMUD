package com.suscipio_solutions.consecro_mud.Libraries.interfaces;
import java.util.List;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Language;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Trap;
import com.suscipio_solutions.consecro_mud.CharClasses.interfaces.CharClass;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Items.interfaces.DeadBody;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.Races.interfaces.Race;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;

public interface CMMiscUtils extends CMLibrary
{
	public static final int LOOTFLAG_RUIN=1;
	public static final int LOOTFLAG_LOSS=2;
	public static final int LOOTFLAG_WORN=4;
	public static final int LOOTFLAG_UNWORN=8;

	public String builtPrompt(MOB mob);

	public String getFormattedDate(Environmental E);
	public double memoryUse ( Environmental E, int number );
	public String niceCommaList(List<?> V, boolean andTOrF);
	public long[][] compileConditionalRange(List<String> condV, int numDigits, final int startOfRange, final int endOfRange);

	public void outfit(MOB mob, List<Item> items);
	public Language getLanguageSpoken(Physical P);
	public boolean reachableItem(MOB mob, Environmental E);
	public void extinguish(MOB source, Physical target, boolean mundane);
	public boolean armorCheck(MOB mob, int allowedArmorLevel);
	public boolean armorCheck(MOB mob, Item I, int allowedArmorLevel);
	public void recursiveDropMOB(MOB mob, Room room, Item thisContainer, boolean bodyFlag);
	public void confirmWearability(MOB mob);
	public int processVariableEquipment(MOB mob);

	public Trap makeADeprecatedTrap(Physical unlockThis);
	public void setTrapped(Physical myThang, boolean isTrapped);
	public void setTrapped(Physical myThang, Trap theTrap, boolean isTrapped);
	public Trap fetchMyTrap(Physical myThang);

	public MOB getMobPossessingAnother(MOB mob);
	public void roomAffectFully(CMMsg msg, Room room, int dirCode);
	public List<DeadBody> getDeadBodies(Environmental container);
	public boolean resurrect(MOB tellMob, Room corpseRoom, DeadBody body, int XPLevel);

	public Item isRuinedLoot(MOB mob, Item I);

	public void swapRaces(Race newR, Race oldR);
	public void reloadCharClasses(CharClass oldC);

	public boolean disInvokeEffects(Environmental E);
	public int disenchantItem(Item target);
}
