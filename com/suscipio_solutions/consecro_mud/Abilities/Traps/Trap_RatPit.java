package com.suscipio_solutions.consecro_mud.Abilities.Traps;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Items.interfaces.CagedAnimal;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;


public class Trap_RatPit extends Trap_SnakePit
{
	@Override public String ID() { return "Trap_RatPit"; }
	private final static String localizedName = CMLib.lang().L("rat pit");
	@Override public String name() { return localizedName; }
	@Override protected int canAffectCode(){return Ability.CAN_ROOMS;}
	@Override protected int canTargetCode(){return 0;}
	@Override protected int trapLevel(){return 12;}
	@Override public String requiresToSet(){return "some caged rats";}


	@Override
	protected Item getCagedAnimal(MOB mob)
	{
		if(mob==null) return null;
		if(mob.location()==null) return null;
		for(int i=0;i<mob.location().numItems();i++)
		{
			final Item I=mob.location().getItem(i);
			if(I instanceof CagedAnimal)
			{
				final MOB M=((CagedAnimal)I).unCageMe();
				if((M!=null)&&(M.baseCharStats().getMyRace().racialCategory().equalsIgnoreCase("Rodent")))
					return I;
			}
		}
		return null;
	}

}
