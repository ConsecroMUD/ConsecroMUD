package com.suscipio_solutions.consecro_mud.Areas;
import java.util.Random;

import com.suscipio_solutions.consecro_mud.Common.interfaces.TimeClock;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.CMProps;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_mud.core.interfaces.BoundedObject;
import com.suscipio_solutions.consecro_mud.core.interfaces.SpaceObject;


public class StdPlanet extends StdTimeZone implements SpaceObject
{
	@Override public String ID(){	return "StdPlanet";}

	protected static double[]	emptyDirection	= new double[2];

	protected long[]	coordinates	= new long[3];
	protected long		radius;

	public StdPlanet()
	{
		super();

		myClock = (TimeClock)CMClass.getCommon("DefaultTimeClock");
		coordinates=new long[]{Math.round(Long.MAX_VALUE*Math.random()),Math.round(Long.MAX_VALUE*Math.random()),Math.round(Long.MAX_VALUE*Math.random())};
		Random random=new Random(System.currentTimeMillis());
		radius=SpaceObject.Distance.PlanetRadius.dm + (random.nextLong() % (SpaceObject.Distance.PlanetRadius.dm / 20));
	}

	@Override public long[] coordinates(){return coordinates;}
	@Override public void setCoords(long[] coords)
	{
		if((coords!=null)&&(coords.length==3))
			CMLib.map().moveSpaceObject(this,coords);
	}
	@Override public double[] direction(){return emptyDirection;}
	@Override public void setDirection(double[] dir){ }
	@Override public long speed(){return 0;}
	@Override public void setSpeed(long v){}
	@Override public long radius() { return radius; }
	@Override public void setRadius(long radius) { this.radius=radius; }
	@Override public void setName(String newName)
	{
		super.setName(newName);
		myClock.setLoadName(newName);
	}

	@Override public SpaceObject knownTarget(){return null;}
	@Override public void setKnownTarget(SpaceObject O){}
	@Override public SpaceObject knownSource(){return null;}
	@Override public void setKnownSource(SpaceObject O){}

	@Override public long getMass() { return radius * MULTIPLIER_PLANET_MASS;}

	@Override
	public BoundedCube getBounds()
	{
		return new BoundedObject.BoundedCube(coordinates(),radius());
	}

	private final static String[] MYCODES={"COORDS","RADIUS"};
	@Override
	public String getStat(String code)
	{
		switch(getLocCodeNum(code))
		{
		case 0: return CMParms.toStringList(this.coordinates());
		case 1: return ""+radius();
		default: return super.getStat(code);
		}
	}
	@Override
	public void setStat(String code, String val)
	{
		switch(getLocCodeNum(code))
		{
		case 0: setCoords(CMParms.toLongArray(CMParms.parseCommas(val,true))); break;
		case 1: setRadius(CMath.s_long(val)); break;
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
