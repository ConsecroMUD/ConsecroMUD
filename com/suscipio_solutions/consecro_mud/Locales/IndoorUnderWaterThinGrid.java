package com.suscipio_solutions.consecro_mud.Locales;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;


public class IndoorUnderWaterThinGrid extends UnderWaterThinGrid
{
	@Override public String ID(){return "IndoorUnderWaterThinGrid";}

	@Override public int domainType(){return Room.DOMAIN_INDOORS_UNDERWATER;}
	@Override public String getGridChildLocaleID(){return "IndoorUnderWater";}
}
