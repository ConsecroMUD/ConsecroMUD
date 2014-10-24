package com.suscipio_solutions.consecro_mud.Abilities.Songs;
import java.util.HashSet;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Weapon;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Tickable;



public class Dance_Flamenco extends Dance
{
	@Override public String ID() { return "Dance_Flamenco"; }
	private final static String localizedName = CMLib.lang().L("Flamenco");
	@Override public String name() { return localizedName; }
	@Override public int abstractQuality(){ return Ability.QUALITY_MALICIOUS;}

	@Override
	public boolean tick(Tickable ticking, int tickID)
	{
		if(!super.tick(ticking,tickID))
			return false;

		final MOB mob=(MOB)affected;
		if(mob==null) return false;
		if(mob==invoker) return true;
		if(invoker==null) return false;

		final int hpLoss=CMLib.dice().roll(adjustedLevel(invoker(),0),8,0)
				  +CMLib.dice().roll(invoker().getGroupMembers(new HashSet<MOB>()).size()-1,8,0);
		CMLib.combat().postDamage(invoker,mob,this,hpLoss,CMMsg.MASK_ALWAYS|CMMsg.TYP_CAST_SPELL,Weapon.TYPE_BURSTING,"^SThe flamenco dance <DAMAGE> <T-NAME>!^?");
		return true;
	}

}
