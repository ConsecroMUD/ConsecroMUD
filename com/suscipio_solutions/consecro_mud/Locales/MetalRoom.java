package com.suscipio_solutions.consecro_mud.Locales;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.core.interfaces.Places;


public class MetalRoom extends StdRoom
{
	@Override public String ID(){return "MetalRoom";}
	public MetalRoom()
	{
		super();
		name="the room";
		basePhyStats.setWeight(1);
		recoverPhyStats();
		climask=Places.CLIMASK_NORMAL;
	}
	@Override public int domainType(){return Room.DOMAIN_INDOORS_METAL;}
}
