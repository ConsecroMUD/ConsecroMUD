package com.suscipio_solutions.consecro_mud.Locales;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.core.CMSecurity;
import com.suscipio_solutions.consecro_mud.core.interfaces.CMObject;
import com.suscipio_solutions.consecro_mud.core.interfaces.Places;


public class IndoorWaterThinSurface extends IndoorWaterSurface
{
	@Override public String ID(){return "IndoorWaterThinSurface";}
	public IndoorWaterThinSurface()
	{
		super();
		name="the water";
		basePhyStats.setWeight(2);
		recoverPhyStats();
		climask=Places.CLIMASK_WET;
	}
	@Override public int domainType(){return Room.DOMAIN_INDOORS_WATERSURFACE;}
	@Override protected String UnderWaterLocaleID(){return "IndoorWaterThinGrid";}
	@Override protected int UnderWaterDomainType(){return Room.DOMAIN_INDOORS_UNDERWATER;}
	@Override protected boolean IsUnderWaterFatClass(Room thatSea){return (thatSea instanceof IndoorUnderWaterGrid)||(thatSea instanceof IndoorUnderWaterThinGrid);}
	@Override
	public CMObject newInstance()
	{
		if(!CMSecurity.isDisabled(CMSecurity.DisFlag.THINGRIDS))
			return super.newInstance();
		return new IndoorWaterSurface().newInstance();
	}
}
