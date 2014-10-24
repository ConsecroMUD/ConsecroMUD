package com.suscipio_solutions.consecro_mud.Locales;
import java.util.List;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Common.interfaces.PhyStats;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.core.interfaces.Places;


@SuppressWarnings({"unchecked","rawtypes"})
public class SewerMaze extends StdMaze
{
	@Override public String ID(){return "SewerMaze";}
	public SewerMaze()
	{
		super();
		myID=this.getClass().getName().substring(this.getClass().getName().lastIndexOf('.')+1);
		basePhyStats().setDisposition(basePhyStats().disposition()|PhyStats.IS_DARK);
		basePhyStats.setWeight(2);
		recoverPhyStats();
		climask=Places.CLIMASK_WET;
	}
	@Override public int domainType(){return Room.DOMAIN_INDOORS_CAVE;}

	@Override public String getGridChildLocaleID(){return "SewerRoom";}
	@Override public List<Integer> resourceChoices(){return new Vector();}
}
