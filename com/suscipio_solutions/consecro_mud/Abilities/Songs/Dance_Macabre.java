package com.suscipio_solutions.consecro_mud.Abilities.Songs;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.PhyStats;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;
import com.suscipio_solutions.consecro_mud.core.interfaces.Tickable;



public class Dance_Macabre extends Dance
{
	@Override public String ID() { return "Dance_Macabre"; }
	private final static String localizedName = CMLib.lang().L("Macabre");
	@Override public String name() { return localizedName; }
	@Override public int abstractQuality(){ return Ability.QUALITY_BENEFICIAL_OTHERS;}
	@Override protected String danceOf(){return name()+" Dance";}

	protected boolean activated=false;

	@Override
	public void affectPhyStats(Physical affected, PhyStats affectableStats)
	{
		super.affectPhyStats(affected,affectableStats);
		if(activated)
		{
			affectableStats.setDamage(affectableStats.damage()+(adjustedLevel(invoker(),0)/2));
			affectableStats.setAttackAdjustment(affectableStats.attackAdjustment()+(adjustedLevel(invoker(),0)*3));
		}
		else
		if((affected instanceof MOB)
		&&(((MOB)affected).isInCombat())
		&&(((MOB)affected).getVictim().isInCombat())
		&&(((MOB)affected).getVictim()!=affected))
		{
			affectableStats.setDamage(affectableStats.damage()+(adjustedLevel(invoker(),0)/4));
			affectableStats.setAttackAdjustment(affectableStats.attackAdjustment()+adjustedLevel(invoker(),0));
		}
	}

	@Override
	public boolean tick(Tickable ticking, int tickID)
	{
		if(CMLib.flags().isHidden(affected))
		{
			if(!activated)
			{
				activated=true;
				affected.recoverPhyStats();
			}
		}
		else
		if(activated)
		{
			activated=false;
			affected.recoverPhyStats();
		}
		return super.tick(ticking,tickID);
	}


}
