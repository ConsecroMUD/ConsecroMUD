package com.suscipio_solutions.consecro_mud.Abilities.Properties;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.TriggeredAffect;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.PhyStats;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Weapon;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Wearable;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;


public class Prop_FightSpellCast extends Prop_SpellAdder
{
	@Override public String ID() { return "Prop_FightSpellCast"; }
	@Override public String name(){ return "Casting spells when properly used during combat";}
	@Override protected int canAffectCode(){return Ability.CAN_ITEMS;}

	@Override
	public String accountForYourself()
	{ return spellAccountingsWithMask("Casts "," during combat.");}

	@Override public long flags(){return Ability.FLAG_CASTER;}

	@Override public int triggerMask() { return TriggeredAffect.TRIGGER_HITTING_WITH; }

	@Override
	public void affectPhyStats(Physical affected, PhyStats affectableStats)
	{}

	@Override
	public void executeMsg(final Environmental myHost, final CMMsg msg)
	{
		if(processing) return;

		if(!(affected instanceof Item)) return;
		processing=true;

		final Item myItem=(Item)affected;

		if((myItem!=null)
		&&(msg.targetMinor()==CMMsg.TYP_DAMAGE)
		&&((msg.value())>0)
		&&(!myItem.amWearingAt(Wearable.IN_INVENTORY))
		&&(myItem.owner() instanceof MOB)
		&&(msg.target() instanceof MOB))
		{
			final MOB mob=(MOB)myItem.owner();
			if((mob.isInCombat())
			&&(mob.location()!=null)
			&&(!mob.amDead()))
			{
				if((myItem instanceof Weapon)
				&&(msg.tool()==myItem)
				&&(myItem.amWearingAt(Wearable.WORN_WIELD))
				&&(msg.amISource(mob)))
					addMeIfNeccessary(msg.source(),(MOB)msg.target(),true,0,maxTicks);
				else
				if((msg.amITarget(mob))
				&&(!myItem.amWearingAt(Wearable.WORN_WIELD))
				&&(!(myItem instanceof Weapon)))
					addMeIfNeccessary(mob,mob,true,0,maxTicks);
			}
		}
		processing=false;
	}
}
