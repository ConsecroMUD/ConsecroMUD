package com.suscipio_solutions.consecro_mud.Abilities.Thief;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Tickable;


public class Thief_ImprovedHiding extends ThiefSkill
{
	@Override public String ID() { return "Thief_ImprovedHiding"; }
	private final static String localizedName = CMLib.lang().L("Improved Hiding");
	@Override public String name() { return localizedName; }
	@Override public String displayText(){ return "";}
	@Override protected int canAffectCode(){return CAN_MOBS;}
	@Override protected int canTargetCode(){return 0;}
	@Override public int classificationCode(){return Ability.ACODE_THIEF_SKILL|Ability.DOMAIN_STEALTHY;}
	@Override public int abstractQuality(){return Ability.QUALITY_OK_SELF;}
	@Override public boolean isAutoInvoked(){return true;}
	@Override public boolean canBeUninvoked(){return false;}
	public boolean active=false;

	public void improve(MOB mob, boolean yesorno)
	{
		final Ability A=mob.fetchEffect("Thief_Hide");
		if(A!=null)
		{
			if(yesorno)
				A.setAbilityCode(1);
			else
				A.setAbilityCode(0);
		}
	}

	@Override
	public boolean okMessage(final Environmental myHost, final CMMsg msg)
	{
		if(!(affected instanceof MOB))
			return super.okMessage(myHost,msg);

		final MOB mob=(MOB)affected;
		if((!CMLib.flags().isHidden(mob))&&(active))
		{
			active=false;
			improve(mob,false);
			mob.recoverPhyStats();
		}
		return super.okMessage(myHost,msg);
	}

	@Override
	public boolean tick(Tickable ticking, int tickID)
	{
		if(!super.tick(ticking,tickID)) return false;
		if((affected!=null)&&(affected instanceof MOB))
		{
			if(CMLib.flags().isHidden(affected))
			{
				if(!active)
				{
					active=true;
					helpProficiency((MOB)affected, 0);
					improve((MOB)affected,true);
				}
			}
			else
			if(active)
			{
				active=false;
				improve((MOB)affected,false);
			}
		}
		return true;
	}
}
