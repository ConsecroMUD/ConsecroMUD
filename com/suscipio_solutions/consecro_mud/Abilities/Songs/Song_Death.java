package com.suscipio_solutions.consecro_mud.Abilities.Songs;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Weapon;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Tickable;



public class Song_Death extends Song
{
	@Override public String ID() { return "Song_Death"; }
	private final static String localizedName = CMLib.lang().L("Death");
	@Override public String name() { return localizedName; }
	@Override public int abstractQuality(){ return Ability.QUALITY_MALICIOUS;}
	@Override protected int getXMAXRANGELevel(MOB mob){return 0;} // people are complaining about multi-room death

	@Override
	public boolean tick(Tickable ticking, int tickID)
	{
		if(!super.tick(ticking,tickID))
			return false;

		final MOB mob=(MOB)affected;
		if(mob==null) return false;
		if(mob==invoker) return true;
		if(invoker==null) return false;

		final int hpLoss=(int)Math.round(Math.floor(mob.curState().getHitPoints()*(0.07+(0.02*(1+super.getXLEVELLevel(invoker()))))));
		CMLib.combat().postDamage(invoker,mob,this,hpLoss,CMMsg.MASK_ALWAYS|CMMsg.TYP_UNDEAD,Weapon.TYPE_BURSTING,"^SThe painful song <DAMAGE> <T-NAME>!^?");
		return true;
	}

}
