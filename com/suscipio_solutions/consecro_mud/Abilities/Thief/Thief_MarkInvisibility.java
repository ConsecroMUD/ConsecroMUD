package com.suscipio_solutions.consecro_mud.Abilities.Thief;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.PhyStats;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;
import com.suscipio_solutions.consecro_mud.core.interfaces.Tickable;


public class Thief_MarkInvisibility extends ThiefSkill
{
	@Override public String ID() { return "Thief_MarkInvisibility"; }
	private final static String localizedName = CMLib.lang().L("Invisibility to Mark");
	@Override public String name() { return localizedName; }
	@Override public String displayText(){ return "";}
	@Override protected int canAffectCode(){return CAN_MOBS;}
	@Override protected int canTargetCode(){return 0;}
	@Override public int abstractQuality(){return Ability.QUALITY_OK_SELF;}
	@Override public int classificationCode(){return Ability.ACODE_THIEF_SKILL|Ability.DOMAIN_STEALTHY;}
	@Override public boolean isAutoInvoked(){return true;}
	@Override public boolean canBeUninvoked(){return false;}
	public boolean active=false;
	public MOB mark=null;

	public MOB getMark(MOB mob)
	{
		final Thief_Mark A=(Thief_Mark)mob.fetchEffect("Thief_Mark");
		if(A!=null)
			return A.mark;
		return null;
	}
	public int getMarkTicks(MOB mob)
	{
		final Thief_Mark A=(Thief_Mark)mob.fetchEffect("Thief_Mark");
		if((A!=null)&&(A.mark!=null))
			return A.ticks;
		return -1;
	}

	@Override
	public void affectPhyStats(Physical affected, PhyStats affectableStats)
	{
		super.affectPhyStats(affected,affectableStats);
		if(active)
			affectableStats.setDisposition(affectableStats.disposition()|PhyStats.IS_INVISIBLE);
	}

	@Override
	public boolean tick(Tickable ticking, int tickID)
	{
		if(!super.tick(ticking,tickID)) return false;
		if((affected!=null)&&(affected instanceof MOB))
		{
			final MOB mob=(MOB)affected;
			mark=getMark(mob);
			if((mark!=null)
			&&(mob.location()!=null)
			&&(mob.location().isInhabitant(mark))
			&&((mob.fetchAbility(ID())==null)||proficiencyCheck(mob,0,false)))
			{
				if(!active)
				{
					active=true;
					helpProficiency(mob, 0);
					mob.recoverPhyStats();
				}
			}
			else
			if(active)
			{
				active=false;
				mob.recoverPhyStats();
			}
		}
		return true;
	}
}
