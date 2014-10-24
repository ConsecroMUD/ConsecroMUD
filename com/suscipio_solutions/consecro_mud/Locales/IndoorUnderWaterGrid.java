package com.suscipio_solutions.consecro_mud.Locales;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;


public class IndoorUnderWaterGrid extends UnderWaterGrid
{
	@Override public String ID(){return "IndoorUnderWaterGrid";}

	@Override public int domainType(){return Room.DOMAIN_INDOORS_UNDERWATER;}
	@Override public String getGridChildLocaleID(){return "IndoorUnderWater";}
}
