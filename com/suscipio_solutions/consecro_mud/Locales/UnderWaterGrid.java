package com.suscipio_solutions.consecro_mud.Locales;
import java.util.List;

import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.PhyStats;
import com.suscipio_solutions.consecro_mud.Exits.interfaces.Exit;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMProps;
import com.suscipio_solutions.consecro_mud.core.Directions;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;
import com.suscipio_solutions.consecro_mud.core.interfaces.Places;


public class UnderWaterGrid extends StdGrid
{
	@Override public String ID(){return "UnderWaterGrid";}
	public UnderWaterGrid()
	{
		super();
		basePhyStats().setDisposition(basePhyStats().disposition()|PhyStats.IS_SWIMMING);
		basePhyStats.setWeight(3);
		recoverPhyStats();
		setDisplayText("Under the water");
		setDescription("");
		xsize=CMProps.getIntVar(CMProps.Int.SKYSIZE);
		ysize=CMProps.getIntVar(CMProps.Int.SKYSIZE);
		if(xsize<0) xsize=xsize*-1;
		if(ysize<0) ysize=ysize*-1;
		if((xsize==0)||(ysize==0))
		{
			xsize=3;
			ysize=3;
		}
		climask=Places.CLIMASK_WET;
		atmosphere=RawMaterial.RESOURCE_FRESHWATER;
	}

	@Override public int domainType(){return Room.DOMAIN_OUTDOORS_UNDERWATER;}
	@Override public String getGridChildLocaleID(){return "UnderWater";}
	@Override protected int baseThirst(){return 0;}

	@Override
	public boolean okMessage(final Environmental myHost, final CMMsg msg)
	{
		switch(UnderWater.isOkUnderWaterAffect(this,msg))
		{
		case -1: return false;
		case 1: return true;
		}
		return super.okMessage(myHost,msg);
	}
	@Override
	public void executeMsg(final Environmental myHost, final CMMsg msg)
	{
		super.executeMsg(myHost,msg);
		UnderWater.sinkAffects(this,msg);
	}
	@Override
	public void affectPhyStats(Physical affected, PhyStats affectableStats)
	{
		super.affectPhyStats(affected,affectableStats);
		affectableStats.setDisposition(affectableStats.disposition()|PhyStats.IS_SWIMMING);
	}
	@Override public List<Integer> resourceChoices(){return UnderWater.roomResources;}

	@Override
	protected Room findCenterRoom(int dirCode)
	{
		if(dirCode==Directions.UP)
			return subMap[0][0];
		else
		if(dirCode!=Directions.DOWN)
			return super.findCenterRoom(dirCode);
		else
			return subMap[subMap.length-1][subMap[0].length-1];
	}

	@Override
	protected void buildFinalLinks()
	{
		final Exit ox=CMClass.getExit("Open");
		for(int d=Directions.NUM_DIRECTIONS()-1;d>=0;d--)
		{
			if(d==Directions.GATE) continue;
			final Room dirRoom=rawDoors()[d];
			Exit dirExit=getRawExit(d);
			if((dirExit==null)||(dirExit.hasADoor()))
				dirExit=ox;
			if(dirRoom!=null)
			{
				Exit altExit=dirRoom.getRawExit(Directions.getOpDirectionCode(d));
				if(altExit==null) altExit=ox;

				switch(d)
				{
					case Directions.NORTH:
					for (final Room[] element : subMap)
						linkRoom(element[0],dirRoom,d,dirExit,altExit);
						break;
					case Directions.SOUTH:
					for (final Room[] element : subMap)
						linkRoom(element[subMap[0].length-1],dirRoom,d,dirExit,altExit);
						break;
					case Directions.EAST:
						for(int y=0;y<subMap[0].length;y++)
							linkRoom(subMap[subMap.length-1][y],dirRoom,d,dirExit,altExit);
						break;
					case Directions.WEST:
						for(int y=0;y<subMap[0].length;y++)
							linkRoom(subMap[0][y],dirRoom,d,dirExit,altExit);
						break;
					case Directions.UP:
						linkRoom(subMap[0][0],dirRoom,d,dirExit,altExit);
						break;
					case Directions.DOWN:
						linkRoom(subMap[subMap.length-1][subMap[0].length-1],dirRoom,d,dirExit,altExit);
						break;
					case Directions.NORTHEAST:
						linkRoom(subMap[subMap.length-1][0],dirRoom,d,dirExit,altExit);
						break;
					case Directions.NORTHWEST:
						linkRoom(subMap[0][0],dirRoom,d,dirExit,altExit);
						break;
					case Directions.SOUTHEAST:
						linkRoom(subMap[subMap.length-1][subMap[0].length-1],dirRoom,d,dirExit,altExit);
						break;
					case Directions.SOUTHWEST:
						linkRoom(subMap[0][subMap[0].length-1],dirRoom,d,dirExit,altExit);
						break;
				}
			}
		}
	}

	@Override
	public void buildGrid()
	{
		clearGrid(null);
		try
		{
			subMap=new Room[xsize][ysize];
			final Exit ox=CMClass.getExit("Open");
			for(int x=0;x<subMap.length;x++)
				for(int y=0;y<subMap[x].length;y++)
				{
					final Room newRoom=getGridRoom(x,y);
					if(newRoom!=null)
					{
						subMap[x][y]=newRoom;
						if((y>0)&&(subMap[x][y-1]!=null))
						{
							linkRoom(newRoom,subMap[x][y-1],Directions.NORTH,ox,ox);
							linkRoom(newRoom,subMap[x][y-1],Directions.UP,ox,ox);
						}

						if((x>0)&&(subMap[x-1][y]!=null))
							linkRoom(newRoom,subMap[x-1][y],Directions.WEST,ox,ox);

						if((y>0)&&(x>0)&&(subMap[x-1][y-1]!=null)&&(Directions.NORTHWEST<Directions.NUM_DIRECTIONS()))
							linkRoom(newRoom,subMap[x-1][y-1],Directions.NORTHWEST,ox,ox);

						if((y>0)&&(x<subMap.length-1)&&(subMap[x+1][y-1]!=null)&&(Directions.NORTHEAST<Directions.NUM_DIRECTIONS()))
							linkRoom(newRoom,subMap[x+1][y-1],Directions.NORTHEAST,ox,ox);
					}
				}
			buildFinalLinks();
			if((subMap[0][0]!=null)
			&&(subMap[0][0].rawDoors()[Directions.UP]==null)
			&&(xsize>1))
				linkRoom(subMap[0][0],subMap[1][0],Directions.UP,ox,ox);
			for(int y=0;y<subMap[0].length;y++)
				linkRoom(subMap[0][y],subMap[subMap.length-1][y],Directions.WEST,ox,ox);
			for (final Room[] element : subMap)
				linkRoom(element[0],element[element.length-1],Directions.NORTH,ox,ox);
			for(int x=1;x<subMap.length;x++)
				linkRoom(subMap[x][0],subMap[x-1][subMap[x].length-1],Directions.UP,ox,ox);
			if(Directions.NORTHWEST<Directions.NUM_DIRECTIONS())
				linkRoom(subMap[0][0],subMap[subMap.length-1][subMap[0].length-1],Directions.NORTHWEST,ox,ox);
			if(Directions.NORTHEAST<Directions.NUM_DIRECTIONS())
				linkRoom(subMap[subMap.length-1][0],subMap[0][subMap[0].length-1],Directions.NORTHEAST,ox,ox);
		}
		catch(final Exception e)
		{
			clearGrid(null);
		}
	}
}
