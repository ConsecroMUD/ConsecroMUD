package com.suscipio_solutions.consecro_mud.Items.Basic;
import java.util.Random;

import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.BoundedObject;
import com.suscipio_solutions.consecro_mud.core.interfaces.SpaceObject;


public class StdSpaceBody extends StdItem implements SpaceObject
{
	@Override public String ID(){	return "StdSpaceBody";}

	protected long[]		coordinates	= new long[3];
	protected long			radius;
	protected double[]		direction	= new double[2];
	protected long 			speed		= 0;
	protected SpaceObject	spaceSource = null;
	protected SpaceObject	spaceTarget = null;

	public StdSpaceBody()
	{
		super();
		setName("a thing in space");
		setDisplayText("a thing is floating in space");
		Random random=new Random(System.currentTimeMillis());
		radius=SpaceObject.Distance.Kilometer.dm + (random.nextLong() % (SpaceObject.Distance.Kilometer.dm / 2));
		basePhyStats().setWeight(100);
		basePhyStats().setLevel(1);
		recoverPhyStats();
		setMaterial(RawMaterial.RESOURCE_STONE);
	}

	public void destroy()
	{
		CMLib.map().delObjectInSpace(this);
		super.destroy();
	}
	
	@Override
	public BoundedCube getBounds()
	{
		return new BoundedObject.BoundedCube(coordinates(),radius());
	}

	@Override
	public long[] coordinates()
	{
		return coordinates;
	}

	@Override
	public void setCoords(long[] coords)
	{
		if((coords!=null)&&(coords.length==3))
			CMLib.map().moveSpaceObject(this,coords);
	}

	@Override
	public long radius()
	{
		return radius;
	}

	@Override
	public void setRadius(long radius)
	{
		this.radius=radius;
	}

	@Override
	public double[] direction()
	{
		return direction;
	}

	@Override
	public void setDirection(double[] dir)
	{
		if((dir!=null)&&(dir.length==2))
			direction=dir;
	}

	@Override
	public long speed()
	{
		return speed;
	}

	@Override
	public void setSpeed(long v)
	{
		speed=v;
	}

	@Override
	public SpaceObject knownTarget()
	{
		return spaceTarget;
	}

	@Override
	public void setKnownTarget(SpaceObject O)
	{
		spaceTarget=O;
	}

	@Override
	public SpaceObject knownSource()
	{
		return spaceSource;
	}

	@Override
	public void setKnownSource(SpaceObject O)
	{
		spaceSource=O;
	}

	@Override
	public long getMass()
	{
		return basePhyStats().weight() * radius();
	}
}
