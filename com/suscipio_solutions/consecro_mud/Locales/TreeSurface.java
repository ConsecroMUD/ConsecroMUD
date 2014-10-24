package com.suscipio_solutions.consecro_mud.Locales;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;


public class TreeSurface extends ClimbableSurface
{
	@Override public String ID(){return "TreeSurface";}
	public TreeSurface()
	{
		super();
		name="the tree";
		basePhyStats.setWeight(4);
		recoverPhyStats();
	}
	@Override public int domainType(){return Room.DOMAIN_OUTDOORS_WOODS;}
}
