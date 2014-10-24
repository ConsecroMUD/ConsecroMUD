package com.suscipio_solutions.consecro_mud.Abilities.Songs;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.PhyStats;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;
import com.suscipio_solutions.consecro_mud.core.interfaces.Tickable;


public class Skill_SlowFall extends BardSkill
{
	@Override public String ID() { return "Skill_SlowFall"; }
	private final static String localizedName = CMLib.lang().L("Slow Fall");
	@Override public String name() { return localizedName; }
	@Override public String displayText(){return activated?"(Slow Fall)":"";}
	@Override public int abstractQuality(){ return Ability.QUALITY_INDIFFERENT;}
	@Override protected int canAffectCode(){return CAN_MOBS;}
	@Override public boolean isAutoInvoked(){return true;}
	@Override public boolean canBeUninvoked(){return false;}
	@Override public int classificationCode() {   return Ability.ACODE_SKILL|Ability.DOMAIN_FITNESS; }
	public boolean activated=false;

	@Override
	public void affectPhyStats(Physical affected, PhyStats affectableStats)
	{
		super.affectPhyStats(affected,affectableStats);
		if(activated) affectableStats.setWeight(0);
	}

	@Override
	public boolean tick(Tickable ticking, int tickID)
	{
		if(affected!=null)
		{
			if((affected.fetchEffect("Falling")!=null)
			   &&((!(affected instanceof MOB))
				  ||(((MOB)affected).fetchAbility(ID())==null)
				  ||proficiencyCheck((MOB)affected,0,false)))
			{
				activated=true;
				affected.recoverPhyStats();
				if(affected instanceof MOB)
					helpProficiency((MOB)affected, 0);
			}
			else
			if(activated)
			{
				activated=false;
				affected.recoverPhyStats();
			}
		}
		return super.tick(ticking,tickID);
	}
}
