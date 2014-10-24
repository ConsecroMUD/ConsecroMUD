package com.suscipio_solutions.consecro_mud.Libraries.layouts;

import java.util.List;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Libraries.interfaces.AreaGenerationLibrary.LayoutFlags;
import com.suscipio_solutions.consecro_mud.Libraries.interfaces.AreaGenerationLibrary.LayoutNode;
import com.suscipio_solutions.consecro_mud.Libraries.interfaces.AreaGenerationLibrary.LayoutTypes;
import com.suscipio_solutions.consecro_mud.core.Directions;



public class MazeLayout extends AbstractLayout
{
	@Override public String name() { return "MAZE";}

	public void fillMaze(LayoutSet lSet, LayoutNode p, int width, int height)
	{
		final Vector<Integer> dirs = new Vector<Integer>();
		for(int i=0;i<4;i++)
			dirs.add(Integer.valueOf(i));
		final Vector<Integer> rdirs = new Vector<Integer>();
		while(dirs.size()>0)
		{
			final int x = r.nextInt(dirs.size());
			final Integer dir = dirs.elementAt(x);
			dirs.removeElementAt(x);
			rdirs.addElement(dir);
		}
		for(int r=0;r<rdirs.size();r++)
		{
			final Integer dir = rdirs.elementAt(r);
			final LayoutNode p2 = lSet.makeNextNode(p, dir.intValue());
			if((!lSet.isUsed(p2.coord()))
			&&(p2.coord()[0]>=0)
			&&(p2.coord()[1]<=0)
			&&(p2.coord()[0]<width)
			&&(p2.coord()[1]>-height))
			{
				lSet.use(p2,LayoutTypes.street);
				p.crossLink(p2);
				fillMaze(lSet,p2,width,height);
			}
		}

	}

	@Override
	public List<LayoutNode> generate(int num, int dir)
	{
		final Vector<LayoutNode> set = new Vector<LayoutNode>();
		final int diameter = (int)Math.round(Math.sqrt(num));
		final int plusX = (diff(diameter,diameter,num) > diff(diameter+1,diameter,num)) ? 1 : 0;

		final LayoutSet lSet = new LayoutSet(set,num);
		LayoutNode n = null;
		switch(dir)
		{
		case Directions.NORTH: n=new DefaultLayoutNode(new long[]{(diameter+plusX)/2,0}); break;
		case Directions.SOUTH: n=new DefaultLayoutNode(new long[]{(diameter+plusX)/2,-diameter+1}); break;
		case Directions.EAST: n=new DefaultLayoutNode(new long[]{0,(-diameter+1)/2}); break;
		case Directions.WEST: n=new DefaultLayoutNode(new long[]{diameter+plusX-1,(-diameter+1)/2}); break;
		case Directions.NORTHEAST: n=new DefaultLayoutNode(new long[]{0,(-diameter+1)/2}); break;
		case Directions.NORTHWEST: n=new DefaultLayoutNode(new long[]{diameter+plusX-1,(-diameter+1)/2}); break;
		case Directions.SOUTHEAST: n=new DefaultLayoutNode(new long[]{0,(-diameter+1)/2}); break;
		case Directions.SOUTHWEST: n=new DefaultLayoutNode(new long[]{diameter+plusX-1,(-diameter+1)/2}); break;
		}
		if(n!=null)
		{
			//n.flagGateExit(dir);
			lSet.use(n,LayoutTypes.street);
			n.flag(LayoutFlags.gate);
			fillMaze(lSet,n,diameter+plusX,diameter);
			lSet.fillInFlags();
		}
		return set;
	}

}
