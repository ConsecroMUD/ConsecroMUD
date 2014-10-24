package com.suscipio_solutions.consecro_mud.Abilities.Thief;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.PhyStats;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;
import com.suscipio_solutions.consecro_mud.core.interfaces.Tickable;


public class Thief_SneakAttack extends ThiefSkill
{
	@Override public String ID() { return "Thief_SneakAttack"; }
	private final static String localizedName = CMLib.lang().L("Sneak Attack");
	@Override public String name() { return localizedName; }
	@Override public String displayText(){return "";}
	@Override public int abstractQuality(){return Ability.QUALITY_MALICIOUS;}
	@Override public int classificationCode(){return Ability.ACODE_THIEF_SKILL|Ability.DOMAIN_DIRTYFIGHTING;}
	@Override protected int canAffectCode(){return Ability.CAN_MOBS;}
	@Override protected int canTargetCode(){return 0;}
	@Override public boolean isAutoInvoked(){return true;}
	@Override public boolean canBeUninvoked(){return false;}
	protected boolean activated=false;
	protected boolean oncePerRound=false;

	@Override
	public void affectPhyStats(Physical affected, PhyStats affectableStats)
	{
		super.affectPhyStats(affected,affectableStats);
		if(activated)
		{
			final double prof=(proficiency())/100.0;
			final double xlvl=super.getXLEVELLevel(invoker());
			affectableStats.setDamage(affectableStats.damage()+(int)Math.round((((affectableStats.damage())/4.0)+xlvl)*prof));
			affectableStats.setAttackAdjustment(affectableStats.attackAdjustment()+(int)Math.round((50.0+(10.0*xlvl))*prof));
		}
	}

	@Override
	public boolean okMessage(final Environmental myHost, final CMMsg msg)
	{
		if(!super.okMessage(myHost,msg)) return false;
		if((affected==null)||((!(affected instanceof MOB)))) return true;
		if(activated
		   &&(!oncePerRound)
		   &&msg.amISource((MOB)affected)
		   &&(msg.targetMinor()==CMMsg.TYP_DAMAGE))
		{
			oncePerRound=true;
			helpProficiency((MOB)affected, 0);
		}
		return true;
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
		if(oncePerRound) oncePerRound=false;
		return super.tick(ticking,tickID);
	}

}
