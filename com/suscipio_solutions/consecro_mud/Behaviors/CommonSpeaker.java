package com.suscipio_solutions.consecro_mud.Behaviors;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.interfaces.Tickable;



public class CommonSpeaker extends StdBehavior
{
	@Override public String ID(){return "CommonSpeaker";}

	@Override
	public String accountForYourself()
	{
		return "common speaking";
	}

	int tickTocker=1;
	int tickTock=0;
	@Override
	public boolean tick(Tickable ticking, int tickID)
	{
		super.tick(ticking,tickID);

		if(tickID!=Tickable.TICKID_MOB) return true;
		if(--tickTock>0) return true;

		final Ability L=CMClass.getAbility("Common");
		if(L!=null) L.invoke((MOB)ticking,null,true,0);
		if((++tickTocker)==100) tickTocker=99;
		tickTock=tickTocker;
		return true;
	}
}
