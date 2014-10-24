package com.suscipio_solutions.consecro_mud.Abilities.Fighter;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;



public class MonkSkill extends FighterSkill
{
	@Override public String ID() { return "MonkSkill"; }
	private final static String localizedName = CMLib.lang().L("MonkSkill");
	@Override public String name() { return localizedName; }
	public boolean anyWeapons(final MOB mob)
	{
	   return (mob.fetchWieldedItem()!=null)||(mob.fetchHeldItem()!=null);
	}

}
