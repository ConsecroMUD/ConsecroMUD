package com.suscipio_solutions.consecro_mud.Abilities.Fighter;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Weapon;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.Races.interfaces.Race;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Tickable;



public class Fighter_MonkeyPunch extends MonkSkill
{
	@Override public String ID() { return "Fighter_MonkeyPunch"; }
	private final static String localizedName = CMLib.lang().L("Monkey Punch");
	@Override public String name() { return localizedName; }
	@Override public String displayText(){ return "";}
	@Override protected int canAffectCode(){return CAN_MOBS;}
	@Override protected int canTargetCode(){return 0;}
	@Override public int abstractQuality(){return Ability.QUALITY_MALICIOUS;}
	@Override public int classificationCode(){return Ability.ACODE_SKILL|Ability.DOMAIN_PUNCHING;}
	@Override public boolean isAutoInvoked(){return true;}
	@Override public boolean canBeUninvoked(){return false;}
	protected Weapon naturalWeapon=null;

	@Override
	public boolean tick(Tickable ticking, int tickID)
	{
		if(!super.tick(ticking,tickID))
			return false;
		if((tickID==Tickable.TICKID_MOB)
		   &&(affected!=null)
		   &&(affected instanceof MOB))
		{
			final MOB mob=(MOB)affected;
			if((mob.isInCombat())
			&&(CMLib.flags().aliveAwakeMobileUnbound(mob,true))
			&&(mob.rangeToTarget()==0)
			&&(mob.charStats().getBodyPart(Race.BODY_HAND)>1)
			&&(!anyWeapons(mob)))
			{
				if(CMLib.dice().rollPercentage()>95)
					helpProficiency(mob, 0);
				if((naturalWeapon==null)
				||(naturalWeapon.amDestroyed()))
				{
					naturalWeapon=CMClass.getWeapon("GenWeapon");
					naturalWeapon.setName(L("a monkey punch"));
					naturalWeapon.setMaterial(RawMaterial.RESOURCE_BONE);
					naturalWeapon.setUsesRemaining(1000);
					naturalWeapon.setWeaponType(Weapon.TYPE_BASHING);
					naturalWeapon.basePhyStats().setDamage(5);
					naturalWeapon.recoverPhyStats();
				}
				naturalWeapon.setUsesRemaining(100);
				CMLib.combat().postAttack(mob,mob.getVictim(),naturalWeapon);
			}
		}
		return true;
	}

	@Override
	public boolean okMessage(final Environmental myHost, final CMMsg msg)
	{
		if(!super.okMessage(myHost,msg))
			return false;

		if(!(affected instanceof MOB))
			return true;

		final MOB mob=(MOB)affected;
		if(msg.amISource(mob)
		&&(msg.targetMinor()==CMMsg.TYP_DAMAGE)
		&&(msg.tool() instanceof Weapon)
		&&(msg.tool()==naturalWeapon))
			msg.setValue(msg.value()+naturalWeapon.basePhyStats().damage()+super.getXLEVELLevel(mob));
		return true;
	}
}
