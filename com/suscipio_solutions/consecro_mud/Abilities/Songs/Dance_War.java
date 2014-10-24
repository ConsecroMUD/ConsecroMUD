package com.suscipio_solutions.consecro_mud.Abilities.Songs;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CharStats;
import com.suscipio_solutions.consecro_mud.Common.interfaces.PhyStats;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;
import com.suscipio_solutions.consecro_mud.core.interfaces.Tickable;



public class Dance_War extends Dance
{
	@Override public String ID() { return "Dance_War"; }
	private final static String localizedName = CMLib.lang().L("War");
	@Override public String name() { return localizedName; }
	@Override public int abstractQuality(){ return Ability.QUALITY_BENEFICIAL_OTHERS;}
	@Override protected String danceOf(){return name()+" Dance";}

	@Override
	public boolean tick(Tickable ticking, int tickID)
	{
		if(!super.tick(ticking,tickID))
			return false;

		final MOB mob=(MOB)affected;
		if(mob==null)
			return false;
		mob.curState().setMana(0);
		return true;
	}
	@Override
	public void affectPhyStats(Physical affected, PhyStats affectableStats)
	{
		super.affectPhyStats(affected,affectableStats);
		if(affected==null) return;
		final int bonus=adjustedLevel(invoker(),0);
		affectableStats.setAttackAdjustment(affectableStats.attackAdjustment()+(bonus*2));
		affectableStats.setArmor(affectableStats.armor()-(bonus*2));
		affectableStats.setDamage(affectableStats.damage()+(bonus/3));
	}
	@Override
	public void affectCharStats(MOB affected, CharStats affectableStats)
	{
		super.affectCharStats(affected,affectableStats);
		if(invoker==null) return;
		for(final int i : CharStats.CODES.BASECODES())
			affectableStats.setStat(i,affectableStats.getStat(i)+2);
	}
}
