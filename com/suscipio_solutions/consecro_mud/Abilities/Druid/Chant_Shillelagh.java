package com.suscipio_solutions.consecro_mud.Abilities.Druid;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.PhyStats;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Weapon;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Wearable;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;



@SuppressWarnings("rawtypes")
public class Chant_Shillelagh extends Chant
{
	@Override public String ID() { return "Chant_Shillelagh"; }
	private final static String localizedName = CMLib.lang().L("Shillelagh");
	@Override public String name() { return localizedName; }
	private final static String localizedStaticDisplay = CMLib.lang().L("(Shillelagh)");
	@Override public String displayText() { return localizedStaticDisplay; }
	@Override public int classificationCode(){return Ability.ACODE_CHANT|Ability.DOMAIN_PLANTCONTROL;}
	@Override public int abstractQuality(){return Ability.QUALITY_INDIFFERENT;}
	@Override protected int canAffectCode(){return CAN_ITEMS;}
	@Override protected int canTargetCode(){return CAN_ITEMS;}

	@Override
	public void affectPhyStats(Physical affected, PhyStats affectableStats)
	{
		super.affectPhyStats(affected,affectableStats);
		if(affected==null) return;
		affectableStats.setDisposition(affectableStats.disposition()|PhyStats.IS_BONUS);
		if(affected instanceof Item)
			affectableStats.setAbility(affectableStats.ability()+4);
	}



	@Override
	public void unInvoke()
	{
		// undo the affects of this spell
		if(canBeUninvoked())
		{
			if(((affected!=null)&&(affected instanceof Item))
			&&((((Item)affected).owner()!=null)
			&&(((Item)affected).owner() instanceof MOB)))
				((MOB)((Item)affected).owner()).tell(L("The enchantment on @x1 fades.",((Item)affected).name()));
		}
		super.unInvoke();
	}

	@Override
	public int castingQuality(MOB mob, Physical target)
	{
		if(mob!=null)
		{
			if((mob.fetchWieldedItem() instanceof Weapon)
			&&((((Weapon)mob.fetchWieldedItem()).material()&RawMaterial.MATERIAL_MASK)!=RawMaterial.MATERIAL_WOODEN)
			&&((((Weapon)mob.fetchWieldedItem()).material()&RawMaterial.MATERIAL_MASK)!=RawMaterial.MATERIAL_VEGETATION)
			&&(mob.fetchWieldedItem().fetchEffect(ID())==null))
				return super.castingQuality(mob, target,Ability.QUALITY_BENEFICIAL_SELF);
		}
		return super.castingQuality(mob,target);
	}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		Item target=getTarget(mob,mob.location(),givenTarget,commands,Wearable.FILTER_ANY);
		if(target==null)
		{
			if((mob.isMonster())
			&&(mob.fetchWieldedItem() instanceof Weapon)
			&&((((Weapon)mob.fetchWieldedItem()).material()&RawMaterial.MATERIAL_MASK)!=RawMaterial.MATERIAL_WOODEN)
			&&((((Weapon)mob.fetchWieldedItem()).material()&RawMaterial.MATERIAL_MASK)!=RawMaterial.MATERIAL_VEGETATION))
				target=mob.fetchWieldedItem();
			else
				return false;
		}

		if(!(target instanceof Weapon))
		{
			mob.tell(L("You can only enchant weapons."));
			return false;
		}
		if(((((Weapon)target).material()&RawMaterial.MATERIAL_MASK)!=RawMaterial.MATERIAL_WOODEN)
		&&((((Weapon)target).material()&RawMaterial.MATERIAL_MASK)!=RawMaterial.MATERIAL_VEGETATION))
		{
			mob.tell(L("You cannot enchant this foreign material."));
			return false;
		}
		if(((Weapon)target).fetchEffect(this.ID())!=null)
		{
			mob.tell(L("@x1 is already enchanted.",target.name(mob)));
			return false;
		}
		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		final boolean success=proficiencyCheck(mob,0,auto);

		if(success)
		{
			// it worked, so build a copy of this ability,
			// and add it to the affects list of the
			// affected MOB.  Then tell everyone else
			// what happened.
			final CMMsg msg=CMClass.getMsg(mob,target,this,verbalCastCode(mob,target,auto),auto?L("<T-NAME> appear(s) enchanted!"):L("^S<S-NAME> chant(s) to <T-NAMESELF>.^?"));
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				beneficialAffect(mob,target,asLevel,0);
				mob.location().show(mob,target,CMMsg.MSG_OK_VISUAL,L("<T-NAME> glow(s)!"));
				target.recoverPhyStats();
				mob.recoverPhyStats();
			}
		}
		else
			return beneficialWordsFizzle(mob,target,L("<S-NAME> chant(s) to <T-NAMESELF>, but nothing happens."));
		// return whether it worked
		return success;
	}
}
