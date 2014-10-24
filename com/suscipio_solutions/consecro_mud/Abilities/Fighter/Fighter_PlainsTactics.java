package com.suscipio_solutions.consecro_mud.Abilities.Fighter;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.core.CMLib;



public class Fighter_PlainsTactics extends Fighter_FieldTactics
{
	@Override public String ID() { return "Fighter_PlainsTactics"; }
	private final static String localizedName = CMLib.lang().L("Plains Tactics");
	@Override public String name() { return localizedName; }
	private static final Integer[] landClasses = {Integer.valueOf(Room.DOMAIN_OUTDOORS_PLAINS)};
	@Override public Integer[] landClasses(){return landClasses;}
}
