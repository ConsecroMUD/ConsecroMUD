package com.suscipio_solutions.consecro_mud.Races;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Areas.interfaces.Area;
import com.suscipio_solutions.consecro_mud.Common.interfaces.PhyStats;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;


public class Shadow extends Spirit
{
	@Override public String ID(){	return "Shadow"; }
	@Override public String name(){ return "Shadow"; }
	@Override public long forbiddenWornBits(){return 0;}
	@Override protected boolean destroyBodyAfterUse(){return true;}

	protected static Vector<RawMaterial> resources=new Vector<RawMaterial>();
	@Override public int availabilityCode(){return Area.THEME_FANTASY|Area.THEME_SKILLONLYMASK;}

	@Override
	public void affectPhyStats(Physical affected, PhyStats affectableStats)
	{
		if((CMLib.flags().isInDark(affected))
		||((affected instanceof MOB)&&(((MOB)affected).location()!=null)&&(CMLib.flags().isInDark((((MOB)affected).location())))))
			affectableStats.setDisposition(affectableStats.disposition()|PhyStats.IS_INVISIBLE);
		affectableStats.setDisposition(affectableStats.disposition()|PhyStats.IS_GOLEM);
	}
}

