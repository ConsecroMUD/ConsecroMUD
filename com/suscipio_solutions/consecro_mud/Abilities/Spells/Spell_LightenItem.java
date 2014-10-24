package com.suscipio_solutions.consecro_mud.Abilities.Spells;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.PhyStats;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Wearable;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;


@SuppressWarnings("rawtypes")
public class Spell_LightenItem extends Spell
{
	@Override public String ID() { return "Spell_LightenItem"; }
	private final static String localizedName = CMLib.lang().L("Lighten Item");
	@Override public String name() { return localizedName; }
	@Override protected int canAffectCode(){return CAN_ITEMS;}
	@Override protected int canTargetCode(){return CAN_ITEMS;}
	@Override public int classificationCode(){return Ability.ACODE_SPELL|Ability.DOMAIN_ENCHANTMENT;}
	@Override public int abstractQuality(){ return Ability.QUALITY_INDIFFERENT;}

	@Override
	public void affectPhyStats(Physical affected, PhyStats affectableStats)
	{
		super.affectPhyStats(affected,affectableStats);
		affectableStats.setWeight(0);
	}

	@Override
	public void unInvoke()
	{
		if(canBeUninvoked())
		if((affected!=null)&&(affected instanceof Item))
		{
			final Item item=(Item)affected;
			if((item.owner()!=null)
			&&(item.owner() instanceof MOB)
			&&(((MOB)item.owner()).isMine(item)))
			{
				final MOB mob=(MOB)item.owner();
				mob.tell(L("@x1 grows heavy again.",item.name()));
				if((mob.phyStats().weight()+item.basePhyStats().weight())>mob.maxCarry())
				{
					if(!item.amWearingAt(Wearable.IN_INVENTORY))
						CMLib.commands().postRemove(mob,item,false);
					if(item.amWearingAt(Wearable.IN_INVENTORY))
						CMLib.commands().postDrop(mob,item,false,false,false);
				}
			}
		}
		super.unInvoke();
	}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		final Item target=getTarget(mob,mob.location(),givenTarget,commands,Wearable.FILTER_ANY);
		if(target==null)
		{
			final String str=CMParms.combine(commands,0).toUpperCase();
			if(str.equals("MONEY")||str.equals("GOLD")||str.equals("COINS"))
				mob.tell(L("You can't cast this spell on your own coins."));
			return false;
		}

		if(target.fetchEffect(this.ID())!=null)
		{
			mob.tell(L("@x1 is already light!",target.name(mob)));
			return false;
		}

		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		final boolean success=proficiencyCheck(mob,0,auto);

		if(success)
		{
			final CMMsg msg=CMClass.getMsg(mob,target,this,verbalCastCode(mob,target,auto),auto?"":L("^S<S-NAME> wave(s) <S-HIS-HER> hands around <T-NAMESELF>, incanting.^?"));
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				mob.location().show(mob,target,CMMsg.MSG_OK_ACTION,L("<T-NAME> grow(s) much lighter."));
				beneficialAffect(mob,target,asLevel,100);
				target.recoverPhyStats();
				mob.recoverPhyStats();
				mob.location().recoverRoomStats();
			}

		}
		else
			beneficialWordsFizzle(mob,target,L("<S-NAME> wave(s) <S-HIS-HER> hands around <T-NAMESELF>, incanting, but nothing happens."));


		// return whether it worked
		return success;
	}
}
