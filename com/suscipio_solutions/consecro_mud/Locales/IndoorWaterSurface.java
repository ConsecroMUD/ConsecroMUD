package com.suscipio_solutions.consecro_mud.Locales;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.core.interfaces.Drink;
import com.suscipio_solutions.consecro_mud.core.interfaces.Places;


public class IndoorWaterSurface extends WaterSurface implements Drink
{
	@Override public String ID(){return "IndoorWaterSurface";}
	public IndoorWaterSurface()
	{
		super();
		name="the water";
		recoverPhyStats();
		climask=Places.CLIMASK_WET;
	}
	@Override public int domainType(){return Room.DOMAIN_INDOORS_WATERSURFACE;}

	@Override protected String UnderWaterLocaleID(){return "IndoorUnderWaterGrid";}
	@Override protected int UnderWaterDomainType(){return Room.DOMAIN_INDOORS_UNDERWATER;}
	@Override protected boolean IsUnderWaterFatClass(Room thatSea){return (thatSea instanceof IndoorUnderWaterGrid)||(thatSea instanceof IndoorUnderWaterThinGrid);}
}
