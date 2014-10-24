package com.suscipio_solutions.consecro_mud.Locales;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;


public class CityStreet extends StdRoom
{
	@Override public String ID(){return "CityStreet";}
	public CityStreet()
	{
		super();
		name="the street";
		basePhyStats.setWeight(1);
		recoverPhyStats();
	}
	@Override public int domainType(){return Room.DOMAIN_OUTDOORS_CITY;}
}
