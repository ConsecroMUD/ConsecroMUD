package com.suscipio_solutions.consecro_mud.Abilities.Songs;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CharStats;
import com.suscipio_solutions.consecro_mud.Common.interfaces.PhyStats;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;



public class Dance_Capoeira extends Dance
{
	@Override public String ID() { return "Dance_Capoeira"; }
	private final static String localizedName = CMLib.lang().L("Capoeira");
	@Override public String name() { return localizedName; }
	@Override public int abstractQuality(){ return Ability.QUALITY_BENEFICIAL_OTHERS;}
	@Override protected String danceOf(){return name()+" Dance";}

	@Override
	public int castingQuality(MOB mob, Physical target)
	{
		if(mob!=null)
		{
			if(mob.isInCombat())
				return Ability.QUALITY_INDIFFERENT;
		}
		return super.castingQuality(mob,target);
	}

	@Override
	public void affectPhyStats(Physical affected, PhyStats affectableStats)
	{
		super.affectPhyStats(affected,affectableStats);
		if(affected==null) return;
		if((affected instanceof MOB)&&(((MOB)affected).fetchWieldedItem()==null))
		{
			affectableStats.setAttackAdjustment(affectableStats.attackAdjustment()
												+invoker().charStats().getStat(CharStats.STAT_CHARISMA)
												+(2*adjustedLevel(invoker(),0)));
			affectableStats.setDamage(affectableStats.damage()+(adjustedLevel(invoker(),0)/3));
		}
	}
}
