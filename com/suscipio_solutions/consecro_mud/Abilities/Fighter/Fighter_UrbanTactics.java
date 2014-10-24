package com.suscipio_solutions.consecro_mud.Abilities.Fighter;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.core.CMLib;



public class Fighter_UrbanTactics extends Fighter_FieldTactics
{
	@Override public String ID() { return "Fighter_UrbanTactics"; }
	private final static String localizedName = CMLib.lang().L("Urban Tactics");
	@Override public String name() { return localizedName; }
	private static final Integer[] landClasses =
	{
		Integer.valueOf(Room.DOMAIN_OUTDOORS_CITY),
		Integer.valueOf(Room.DOMAIN_INDOORS_METAL),
		Integer.valueOf(Room.DOMAIN_INDOORS_STONE),
		Integer.valueOf(Room.DOMAIN_INDOORS_WOOD)
	};
	@Override public Integer[] landClasses(){return landClasses;}
}
