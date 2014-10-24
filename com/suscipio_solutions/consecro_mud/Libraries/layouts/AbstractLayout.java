package com.suscipio_solutions.consecro_mud.Libraries.layouts;
import java.util.List;
import java.util.Random;

import com.suscipio_solutions.consecro_mud.Libraries.interfaces.AreaGenerationLibrary.LayoutManager;
import com.suscipio_solutions.consecro_mud.Libraries.interfaces.AreaGenerationLibrary.LayoutNode;
import com.suscipio_solutions.consecro_mud.Libraries.interfaces.AreaGenerationLibrary.LayoutRuns;
import com.suscipio_solutions.consecro_mud.core.Directions;


/**
 * Abstract area layout pattern
 * node tags:
 * nodetype: surround, leaf, offleaf, street, square, interior
 * nodeexits: n,s,e,w, n,s, e,w, n,e,w, etc
 * nodeflags: corner, gate, intersection, tee
 * NODEGATEEXIT: (for gate, offleaf, square): n s e w etc
 * noderun: (for surround, street): n,s e,w
 *
 
 */
public abstract class AbstractLayout implements LayoutManager
{
	Random r = new Random();

	public int diff(int width, int height, int num) {
		final int x = width * height;
		return (x<num) ? (num - x) : (x - num);
	}

	@Override
	public abstract String name();
	@Override
	public abstract List<LayoutNode> generate(int num, int dir);

	public static int getDirection(LayoutNode from, LayoutNode to)
	{
		if(to.coord()[1]<from.coord()[1]) return Directions.NORTH;
		if(to.coord()[1]>from.coord()[1]) return Directions.SOUTH;
		if(to.coord()[0]<from.coord()[0]) return Directions.WEST;
		if(to.coord()[0]>from.coord()[0]) return Directions.EAST;
		return -1;
	}

	public static LayoutRuns getRunDirection(int dirCode)
	{
		switch(dirCode)
		{
		case Directions.NORTH:
		case Directions.SOUTH:
			return LayoutRuns.ns;
		case Directions.EAST:
		case Directions.WEST:
			return LayoutRuns.ew;
		}
		return LayoutRuns.ns;
	}

}
