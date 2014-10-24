package com.suscipio_solutions.consecro_mud.Abilities.Paladin;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CharStats;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMath;



public class Paladin_ImprovedResists extends PaladinSkill
{
	@Override public String ID() { return "Paladin_ImprovedResists"; }
	private final static String localizedName = CMLib.lang().L("Paladin`s Resistance");
	@Override public String name() { return localizedName; }
	@Override public int classificationCode(){return Ability.ACODE_SKILL|Ability.DOMAIN_HOLYPROTECTION;}

	@Override
	public void affectCharStats(MOB affected, CharStats affectableStats)
	{
		super.affectCharStats(affected,affectableStats);
		if((affected!=null)&&(CMLib.flags().isGood(affected)))
		{
			final int amount=(int)Math.round(CMath.mul(CMath.div(proficiency(),100.0),affected.phyStats().level()+(2*getXLEVELLevel(invoker))));
			for(final int i : CharStats.CODES.SAVING_THROWS())
				affectableStats.setStat(i,affectableStats.getStat(i)+amount);
		}
	}
}
