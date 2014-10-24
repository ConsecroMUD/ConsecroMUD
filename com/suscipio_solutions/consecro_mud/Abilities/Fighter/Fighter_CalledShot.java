package com.suscipio_solutions.consecro_mud.Abilities.Fighter;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Weapon;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;



public class Fighter_CalledShot extends Fighter_CalledStrike
{
	@Override public String ID() { return "Fighter_CalledShot"; }
	private final static String localizedName = CMLib.lang().L("Called Shot");
	@Override public String name() { return localizedName; }
	private static final String[] triggerStrings =I(new String[] {"CALLEDSHOT"});
	@Override public String[] triggerStrings(){return triggerStrings;}

	@Override
	protected boolean prereqs(MOB mob, boolean quiet)
	{
		if(mob.isInCombat()&&(mob.rangeToTarget()==0))
		{
			if(!quiet)
				mob.tell(L("You are too close to perform a called shot!"));
			return false;
		}

		final Item w=mob.fetchWieldedItem();
		if((w==null)||(!(w instanceof Weapon)))
		{
			if(!quiet)
				mob.tell(L("You need a weapon to perform a called shot!"));
			return false;
		}
		final Weapon wp=(Weapon)w;
		if((wp.weaponClassification()!=Weapon.CLASS_RANGED)&&(wp.weaponClassification()!=Weapon.CLASS_THROWN))
		{
			if(!quiet)
				mob.tell(L("You cannot shoot with @x1!",wp.name()));
			return false;
		}
		return true;
	}
}
