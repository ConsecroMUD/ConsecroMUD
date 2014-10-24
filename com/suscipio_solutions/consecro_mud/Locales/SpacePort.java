package com.suscipio_solutions.consecro_mud.Locales;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.LocationRoom;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.CMProps;
import com.suscipio_solutions.consecro_mud.core.interfaces.SpaceObject;


public class SpacePort extends StdRoom implements LocationRoom
{
	@Override public String ID(){return "SpacePort";}
	protected double[] dirFromCore = new double[2];

	public SpacePort()
	{
		super();
		name="the space port";
		basePhyStats.setWeight(1);
		recoverPhyStats();
	}
	@Override public int domainType(){return Room.DOMAIN_OUTDOORS_SPACEPORT;}

	@Override
	public long[] coordinates()
	{
		final SpaceObject planet=CMLib.map().getSpaceObject(this,true);
		if(planet!=null)
			return CMLib.map().getLocation(planet.coordinates(),dirFromCore,planet.radius());
		return new long[]{0,0,0};
	}
	@Override
	public double[] getDirectionFromCore()
	{
		return dirFromCore;
	}
	@Override
	public void setDirectionFromCore(double[] dir)
	{
		if((dir!=null)&&(dir.length==2))
			dirFromCore=dir;
	}

	private final static String[] MYCODES={"COREDIR"};
	@Override
	public String getStat(String code)
	{
		switch(getLocCodeNum(code))
		{
		case 0: return CMParms.toStringList(this.getDirectionFromCore());
		default: return super.getStat(code);
		}
	}
	@Override
	public void setStat(String code, String val)
	{
		switch(getLocCodeNum(code))
		{
		case 0: this.setDirectionFromCore(CMParms.toDoubleArray(CMParms.parseCommas(val,true))); break;
		default: super.setStat(code, val); break;
		}
	}
	protected int getLocCodeNum(String code)
	{
		for(int i=0;i<MYCODES.length;i++)
			if(code.equalsIgnoreCase(MYCODES[i])) return i;
		return -1;
	}
	private static String[] codes=null;
	@Override
	public String[] getStatCodes()
	{
		return (codes != null) ? codes : (codes =  CMProps.getStatCodesList(CMParms.appendToArray(super.getStatCodes(), MYCODES),this));
	}
}
