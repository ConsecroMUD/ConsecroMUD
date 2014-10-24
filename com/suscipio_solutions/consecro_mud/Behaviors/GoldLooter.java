package com.suscipio_solutions.consecro_mud.Behaviors;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.interfaces.Tickable;



public class GoldLooter extends StdBehavior
{
	@Override public String ID(){return "GoldLooter";}

	int tickTocker=1;
	int tickTock=0;

	@Override
	public String accountForYourself()
	{
		return "gold looting";
	}

	@Override
	public boolean tick(Tickable ticking, int tickID)
	{
		super.tick(ticking,tickID);

		if(tickID!=Tickable.TICKID_MOB) return true;
		if(--tickTock>0) return true;
		((MOB)ticking).setAttribute(MOB.Attrib.AUTOGOLD,true);
		if((++tickTocker)==100) tickTocker=99;
		tickTock=tickTocker;
		return true;
	}
}
