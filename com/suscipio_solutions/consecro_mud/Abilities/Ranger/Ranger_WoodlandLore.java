package com.suscipio_solutions.consecro_mud.Abilities.Ranger;
import com.suscipio_solutions.consecro_mud.Abilities.StdAbility;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.PhyStats;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;


public class Ranger_WoodlandLore extends StdAbility
{
	@Override public String ID() { return "Ranger_WoodlandLore"; }
	private final static String localizedName = CMLib.lang().L("Woodland Lore");
	@Override public String name() { return localizedName; }
	@Override public String displayText(){ return "";}
	@Override public int abstractQuality(){return Ability.QUALITY_BENEFICIAL_SELF;}
	@Override protected int canAffectCode(){return Ability.CAN_MOBS;}
	@Override protected int canTargetCode(){return 0;}
	@Override public boolean isAutoInvoked(){return true;}
	@Override public boolean canBeUninvoked(){return false;}
	@Override public int classificationCode(){ return Ability.ACODE_SKILL;}

	@Override
	public void affectPhyStats(Physical affected, PhyStats affectableStats)
	{
		super.affectPhyStats(affected,affectableStats);
		if((invoker==null)&&(affected instanceof MOB))
		   invoker=(MOB)affected;
		if((invoker!=null)
		   &&(invoker.location()!=null)
		   &&(((invoker.location().domainType()&Room.INDOORS)==0)
		   &&(invoker.location().domainType()!=Room.DOMAIN_OUTDOORS_SPACEPORT)
		   &&(invoker.location().domainType()!=Room.DOMAIN_OUTDOORS_CITY)))
		{
			final int xlvl=super.getXLEVELLevel(invoker());
			affectableStats.setDamage(affectableStats.damage()+5+xlvl);
			affectableStats.setAttackAdjustment(affectableStats.attackAdjustment()+10+(2*xlvl));
			affectableStats.setArmor(affectableStats.armor()-20-(2*xlvl));
		}
	}

}
