package com.suscipio_solutions.consecro_mud.Abilities.Fighter;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.PhyStats;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Weapon;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;



public class Fighter_TrueShot extends FighterSkill
{
	@Override public String ID() { return "Fighter_TrueShot"; }
	private final static String localizedName = CMLib.lang().L("True Shot");
	@Override public String name() { return localizedName; }
	@Override public String displayText(){ return "";}
	@Override protected int canAffectCode(){return CAN_MOBS;}
	@Override protected int canTargetCode(){return 0;}
	@Override public int abstractQuality(){return Ability.QUALITY_BENEFICIAL_SELF;}
	@Override public int classificationCode(){return Ability.ACODE_SKILL|Ability.DOMAIN_MARTIALLORE;}
	@Override public boolean isAutoInvoked(){return true;}
	@Override public boolean canBeUninvoked(){return false;}
	protected boolean gettingBonus=false;

	@Override
	public void affectPhyStats(Physical affected, PhyStats affectableStats)
	{
		if(!(affected instanceof MOB)) return;
		final Item w=((MOB)affected).fetchWieldedItem();
		if((w==null)||(!(w instanceof Weapon))) return;
		if((((Weapon)w).weaponClassification()==Weapon.CLASS_RANGED)
		||(((Weapon)w).weaponClassification()==Weapon.CLASS_THROWN))
		{
			gettingBonus=true;
			final int bonus=(int)Math.round(CMath.mul(affectableStats.attackAdjustment(),(CMath.div(proficiency(),200.0-(10*getXLEVELLevel(invoker()))))));
			affectableStats.setAttackAdjustment(affectableStats.attackAdjustment()+bonus);
		}
		else
			gettingBonus=false;
	}
	@Override
	public void executeMsg(final Environmental myHost, final CMMsg msg)
	{
		super.executeMsg(myHost,msg);

		if(!(affected instanceof MOB))
			return;

		final MOB mob=(MOB)affected;

		if((msg.amISource(mob))
		&&(gettingBonus)
		&&(msg.targetMinor()==CMMsg.TYP_WEAPONATTACK)
		&&(CMLib.dice().rollPercentage()>95)
		&&(mob.isInCombat())
		&&(!mob.amDead())
		&&(msg.target() instanceof MOB))
			helpProficiency(mob, 0);
	}
}
