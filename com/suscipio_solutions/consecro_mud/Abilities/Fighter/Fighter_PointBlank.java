package com.suscipio_solutions.consecro_mud.Abilities.Fighter;
import java.util.List;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.PhyStats;
import com.suscipio_solutions.consecro_mud.Items.interfaces.AmmunitionWeapon;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Weapon;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Wearable;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.collections.XVector;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;
import com.suscipio_solutions.consecro_mud.core.interfaces.Tickable;



@SuppressWarnings({"unchecked","rawtypes"})
public class Fighter_PointBlank extends FighterSkill
{
	@Override public String ID() { return "Fighter_PointBlank"; }
	private final static String localizedName = CMLib.lang().L("Point Blank Shot");
	@Override public String name() { return localizedName; }
	@Override public String displayText(){ return "";}
	@Override public int abstractQuality(){return Ability.QUALITY_INDIFFERENT;}
	@Override protected int canAffectCode(){return Ability.CAN_MOBS;}
	@Override protected int canTargetCode(){return 0;}
	@Override public boolean isAutoInvoked(){return true;}
	@Override public boolean canBeUninvoked(){return false;}
	@Override public int classificationCode(){return Ability.ACODE_SKILL|Ability.DOMAIN_MARTIALLORE;}
	public int checkDown=4;

	protected List<Weapon> qualifiedWeapons=new Vector<Weapon>();

	@Override
	protected void cloneFix(Ability E)
	{
		super.cloneFix(E);
		qualifiedWeapons=new XVector<Weapon>(((Fighter_PointBlank)E).qualifiedWeapons);
	}

	@Override
	public void setMiscText(String newText)
	{
		super.setMiscText(newText);
		qualifiedWeapons=new Vector();
	}

	@Override
	public void executeMsg(Environmental host, CMMsg msg)
	{
		super.executeMsg(host,msg);
		if((affected instanceof Weapon)
		&&((Weapon)affected).amWearingAt(Wearable.IN_INVENTORY))
		{
			final Weapon targetW=(Weapon)affected;
			qualifiedWeapons.remove(targetW);
			targetW.delEffect(targetW.fetchEffect(ID()));
			targetW.recoverPhyStats();
		}
		else
		if((msg.source()==affected)
		&&(msg.target() instanceof AmmunitionWeapon))
		{
			final AmmunitionWeapon W=(AmmunitionWeapon)msg.target();
			if((W.weaponClassification()==Weapon.CLASS_RANGED)
			&&(W.ammunitionType().length()>0))
			{
				if(((msg.targetMinor()==CMMsg.TYP_WEAR)
				   ||(msg.targetMinor()==CMMsg.TYP_WIELD)
				   ||(msg.targetMinor()==CMMsg.TYP_HOLD))
				&&(!qualifiedWeapons.contains(W))
				&&((msg.source().fetchAbility(ID())==null)||proficiencyCheck(null,0,false)))
				{
					qualifiedWeapons.add(W);
					final Ability A=(Ability)this.copyOf();
					A.setInvoker(invoker());
					A.setSavable(false);
					W.addEffect(A);
					W.recoverPhyStats();
				}
				else
				if(((msg.targetMinor()==CMMsg.TYP_REMOVE)
					||(msg.targetMinor()==CMMsg.TYP_DROP))
				&&(qualifiedWeapons.contains(msg.target())))
				{
					qualifiedWeapons.remove(msg.target());
					W.delEffect(W.fetchEffect(ID()));
					W.recoverPhyStats();
				}
			}
		}
	}

	@Override
	public void affectPhyStats(Physical affected, PhyStats affectableStats)
	{
		super.affectPhyStats(affected,affectableStats);
		if(affected instanceof Item)
			affectableStats.setSensesMask(affectableStats.sensesMask()|PhyStats.SENSE_ITEMNOMINRANGE);
	}

	@Override
	public boolean tick(Tickable ticking, int tickID)
	{
		if(!super.tick(ticking,tickID)) return false;
		if(!(affected instanceof MOB))
			return true;

		final MOB mob=(MOB)affected;
		if(--checkDown<=0)
		{
			checkDown=5;
			final Item w=mob.fetchWieldedItem();
			if((w!=null)
			&&(w instanceof AmmunitionWeapon)
			&&(((Weapon)w).weaponClassification()==Weapon.CLASS_RANGED)
			&&(((AmmunitionWeapon)w).ammunitionType().length()>0)
			&&((mob.fetchAbility(ID())==null)||proficiencyCheck(null,0,false)))
			{
				if((CMLib.dice().rollPercentage()<5)&&(mob.isInCombat())&&(mob.rangeToTarget() == 0))
					helpProficiency(mob, 0);
				if(w.fetchEffect(ID())==null)
				{
					if(!qualifiedWeapons.contains(w))
						qualifiedWeapons.add((Weapon)w);
					final Ability A=(Ability)this.copyOf();
					A.setSavable(false);
					A.setInvoker(invoker());
					w.addEffect(A);
					w.recoverPhyStats();
				}
			}
			for(int i=qualifiedWeapons.size()-1;i>=0;i--)
			{
				final Item I=qualifiedWeapons.get(i);
				if((I.amWearingAt(Wearable.IN_INVENTORY))
				||(I.owner()!=affected))
				{
					qualifiedWeapons.remove(I);
					I.delEffect(I.fetchEffect(ID()));
					I.recoverPhyStats();
				}
			}
		}
		return true;
	}
}
