
package com.suscipio_solutions.consecro_mud.Abilities.Songs;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.PhyStats;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;



public class Song_Rage extends Song
{
	@Override public String ID() { return "Song_Rage"; }
	private final static String localizedName = CMLib.lang().L("Rage");
	@Override public String name() { return localizedName; }
	@Override public int abstractQuality(){ return Ability.QUALITY_MALICIOUS;}
	@Override
	public void affectPhyStats(Physical affected, PhyStats affectableStats)
	{
		super.affectPhyStats(affected,affectableStats);
		if(invoker==null) return;
		if(affected==invoker) return;
		affectableStats.setDamage(affectableStats.damage()+(int)Math.round(CMath.div(affectableStats.damage(),2.0+CMath.mul(0.25,super.getXLEVELLevel(invoker())))));
		affectableStats.setAttackAdjustment(affectableStats.attackAdjustment()-(int)Math.round(CMath.div(affectableStats.attackAdjustment(),6.0+CMath.mul(0.5,super.getXLEVELLevel(invoker())))));
		affectableStats.setArmor(affectableStats.armor()+super.adjustedLevel(invoker(),0));
	}


	@Override
	public boolean okMessage(final Environmental myHost, final CMMsg msg)
	{
		if(!super.okMessage(myHost,msg))
			return false;

		if(msg.amISource(invoker)) return true;
		if(msg.sourceMinor()!=CMMsg.TYP_FLEE) return true;
		if(msg.source().fetchEffect(this.ID())==null) return true;

		msg.source().tell(L("You are too enraged to flee."));
		return false;
	}

}
