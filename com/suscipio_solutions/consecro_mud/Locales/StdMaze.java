package com.suscipio_solutions.consecro_mud.Locales;
import java.util.Hashtable;

import com.suscipio_solutions.consecro_mud.Common.interfaces.PhyStats;
import com.suscipio_solutions.consecro_mud.Exits.interfaces.Exit;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_mud.core.Directions;

@SuppressWarnings({"unchecked","rawtypes"})
public class StdMaze extends StdGrid
{
	@Override public String ID(){return "StdMaze";}
	public StdMaze()
	{
		super();
	}

	@Override
	protected Room getGridRoom(int x, int y)
	{
		final Room R=super.getGridRoom(x,y);
		if((R!=null)&&(!CMath.bset(R.phyStats().sensesMask(),PhyStats.SENSE_ROOMUNEXPLORABLE)))
		{
			R.basePhyStats().setSensesMask(R.basePhyStats().sensesMask()|PhyStats.SENSE_ROOMUNEXPLORABLE);
			R.phyStats().setSensesMask(R.phyStats().sensesMask()|PhyStats.SENSE_ROOMUNEXPLORABLE);
		}
		return R;
	}
	@Override
	protected Room findCenterRoom(int dirCode)
	{
		final Room dirRoom=rawDoors()[dirCode];
		if(dirRoom!=null)
		{
			final Room altR=super.findCenterRoom(dirCode);
			if(altR!=null)
			{
				final Exit ox=CMClass.getExit("Open");
				linkRoom(altR,dirRoom,dirCode,ox,ox);
				return altR;
			}
		}
		return null;
	}

	protected boolean goodDir(int x, int y, int dirCode)
	{
		if(dirCode==Directions.UP) return false;
		if(dirCode==Directions.DOWN) return false;
		if(dirCode>=Directions.GATE) return false;
		if((x==0)&&(dirCode==Directions.WEST)) return false;
		if((y==0)&&(dirCode==Directions.NORTH)) return false;
		if((x>=(subMap.length-1))&&(dirCode==Directions.EAST)) return false;
		if((y>=(subMap[0].length-1))&&(dirCode==Directions.SOUTH)) return false;
		return true;
	}

	protected Room roomDir(int x, int y, int dirCode)
	{
		if(!goodDir(x,y,dirCode)) return null;
		return subMap[getX(x,dirCode)][getY(y,dirCode)];
	}

	protected int getY(int y, int dirCode)
	{
		switch(dirCode)
		{
		case Directions.NORTH:
			return y-1;
		case Directions.SOUTH:
			return y+1;
		}
		return y;
	}
	protected int getX(int x, int dirCode)
	{
		switch(dirCode)
		{
		case Directions.EAST:
			return x+1;
		case Directions.WEST:
			return x-1;
		}
		return x;
	}

	protected void mazify(Hashtable visited, int x, int y)
	{

		if(visited.get(subMap[x][y])!=null) return;
		final Room room=subMap[x][y];
		visited.put(room,room);
		final Exit ox=CMClass.getExit("Open");

		boolean okRoom=true;
		while(okRoom)
		{
			okRoom=false;
			for(int d=Directions.NUM_DIRECTIONS()-1;d>=0;d--)
			{
				if(d==Directions.GATE) continue;
				final Room possRoom=roomDir(x,y,d);
				if(possRoom!=null)
					if(visited.get(possRoom)==null)
					{
						okRoom=true;
						break;
					}
			}
			if(okRoom)
			{
				Room goRoom=null;
				int dirCode=-1;
				while(goRoom==null)
				{
					final int d=CMLib.dice().roll(1,Directions.NUM_DIRECTIONS(),0)-1;
					final Room possRoom=roomDir(x,y,d);
					if(possRoom!=null)
						if(visited.get(possRoom)==null)
						{
							goRoom=possRoom;
							dirCode=d;
						}
				}
				linkRoom(room,goRoom,dirCode,ox,ox);
				mazify(visited,getX(x,dirCode),getY(y,dirCode));
			}
		}
	}

	protected void buildMaze()
	{
		final Hashtable visited=new Hashtable();
		final int x=xsize/2;
		final int y=ysize/2;
		mazify(visited,x,y);
	}

	@Override
	public void buildGrid()
	{
		clearGrid(null);
		try
		{
			subMap=new Room[xsize][ysize];
			for(int x=0;x<subMap.length;x++)
				for(int y=0;y<subMap[x].length;y++)
				{
					final Room newRoom=getGridRoom(x,y);
					if(newRoom!=null)
						subMap[x][y]=newRoom;
				}
			buildMaze();
			buildFinalLinks();
		}
		catch(final Exception e)
		{
			clearGrid(null);
		}
	}
}
