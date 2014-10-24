package com.suscipio_solutions.consecro_mud.Locales;
import java.util.List;

import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;

public class SaltWaterSurface extends WaterSurface
{
	@Override public String ID(){return "SaltWaterSurface";}
	public SaltWaterSurface()
	{
		super();
	}
	@Override protected String UnderWaterLocaleID(){return "UnderSaltWaterGrid";}

	@Override public int liquidType(){return RawMaterial.RESOURCE_SALTWATER;}
	@Override protected int UnderWaterDomainType(){return Room.DOMAIN_OUTDOORS_UNDERWATER;}
	@Override protected boolean IsUnderWaterFatClass(Room thatSea){return (thatSea instanceof UnderSaltWaterGrid)||(thatSea instanceof UnderSaltWaterThinGrid);}
	@Override public List<Integer> resourceChoices(){return UnderSaltWater.roomResources;}

}
