package com.suscipio_solutions.consecro_mud.Locales;
import java.util.List;

import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Exits.interfaces.Exit;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.Directions;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Places;


public class EndlessOcean extends StdGrid
{
	@Override public String ID(){return "EndlessOcean";}
	public EndlessOcean()
	{
		super();
		name="the ocean";
		basePhyStats.setWeight(2);
		recoverPhyStats();
		climask=Places.CLIMASK_HOT|CLIMASK_DRY;
	}
	@Override public int domainType(){return Room.DOMAIN_OUTDOORS_WATERSURFACE;}

	@Override public String getGridChildLocaleID(){return "SaltWaterSurface";}
	@Override public List<Integer> resourceChoices(){return UnderSaltWater.roomResources;}
	@Override
	public void executeMsg(final Environmental myHost, final CMMsg msg)
	{
		super.executeMsg(myHost,msg);
		UnderWater.sinkAffects(this,msg);
	}
	@Override
	public boolean okMessage(final Environmental myHost, final CMMsg msg)
	{
		switch(WaterSurface.isOkWaterSurfaceAffect(this,msg))
		{
		case -1: return false;
		case 1: return true;
		}
		return super.okMessage(myHost,msg);
	}

	@Override
	public void buildGrid()
	{
		super.buildGrid();
		if(subMap!=null)
		{
			final Exit ox=CMClass.getExit("Open");
			if(rawDoors()[Directions.NORTH]==null)
				for (final Room[] element : subMap)
					if(element[0]!=null)
						linkRoom(element[0],element[yGridSize()/2],Directions.NORTH,ox,ox);
			if(rawDoors()[Directions.SOUTH]==null)
				for (final Room[] element : subMap)
					if(element[yGridSize()-1]!=null)
						linkRoom(element[yGridSize()-1],element[yGridSize()/2],Directions.SOUTH,ox,ox);
			if(rawDoors()[Directions.EAST]==null)
				for(int i=0;i<subMap[0].length;i++)
					if(subMap[xGridSize()-1][i]!=null)
						linkRoom(subMap[xGridSize()-1][i],subMap[xGridSize()/2][i],Directions.EAST,ox,ox);
			if(rawDoors()[Directions.WEST]==null)
				for(int i=0;i<subMap[0].length;i++)
					if(subMap[0][i]!=null)
						linkRoom(subMap[0][i],subMap[xGridSize()/2][i],Directions.WEST,ox,ox);
			if(Directions.NORTHEAST<Directions.NUM_DIRECTIONS())
			{
				if(rawDoors()[Directions.NORTHEAST]==null)
				{
					for (final Room[] element : subMap)
						if(element[0]!=null)
							linkRoom(element[0],subMap[xGridSize()/2][yGridSize()/2],Directions.NORTHEAST,ox,ox);
					for(int i=0;i<subMap[0].length;i++)
						if(subMap[subMap.length-1][i]!=null)
							linkRoom(subMap[subMap.length-1][i],subMap[xGridSize()/2][yGridSize()/2],Directions.NORTHEAST,ox,ox);
				}
				if(rawDoors()[Directions.NORTHWEST]==null)
				{
					for (final Room[] element : subMap)
						if(element[0]!=null)
							linkRoom(element[0],subMap[xGridSize()/2][yGridSize()/2],Directions.NORTHWEST,ox,ox);
					for(int i=0;i<subMap[0].length;i++)
						if(subMap[0][i]!=null)
							linkRoom(subMap[0][i],subMap[xGridSize()/2][yGridSize()/2],Directions.NORTHWEST,ox,ox);
				}
				if(rawDoors()[Directions.SOUTHWEST]==null)
				{
					for (final Room[] element : subMap)
						if(element[yGridSize()-1]!=null)
							linkRoom(element[yGridSize()-1],subMap[xGridSize()/2][yGridSize()/2],Directions.SOUTHWEST,ox,ox);
					for(int i=0;i<subMap[0].length;i++)
						if(subMap[0][i]!=null)
							linkRoom(subMap[0][i],subMap[xGridSize()/2][yGridSize()/2],Directions.SOUTHWEST,ox,ox);
				}
				if(rawDoors()[Directions.SOUTHEAST]==null)
				{
					for (final Room[] element : subMap)
						if(element[yGridSize()-1]!=null)
							linkRoom(element[yGridSize()-1],subMap[xGridSize()/2][yGridSize()/2],Directions.SOUTHEAST,ox,ox);
					for(int i=0;i<subMap[0].length;i++)
						if(subMap[subMap.length-1][i]!=null)
							linkRoom(subMap[subMap.length-1][i],subMap[xGridSize()/2][yGridSize()/2],Directions.NORTHEAST,ox,ox);
				}
			}
		}
	}
}
