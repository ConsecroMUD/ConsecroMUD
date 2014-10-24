package com.suscipio_solutions.consecro_mud.Abilities.Prayers;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.PhyStats;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Wearable;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;



@SuppressWarnings("rawtypes")
public class Prayer_CurseItem extends Prayer
{
	@Override public String ID() { return "Prayer_CurseItem"; }
	private final static String localizedName = CMLib.lang().L("Curse Item");
	@Override public String name() { return localizedName; }
	private final static String localizedStaticDisplay = CMLib.lang().L("(Cursed)");
	@Override public String displayText() { return localizedStaticDisplay; }
	@Override public int abstractQuality(){ return Ability.QUALITY_MALICIOUS;}
	@Override public int classificationCode(){return Ability.ACODE_PRAYER|Ability.DOMAIN_CURSING;}
	@Override public long flags(){return Ability.FLAG_UNHOLY;}
	@Override protected int canAffectCode(){return Ability.CAN_MOBS|Ability.CAN_ITEMS;}
	@Override protected int canTargetCode(){return Ability.CAN_MOBS|Ability.CAN_ITEMS;}

	@Override
	public void affectPhyStats(Physical affected, PhyStats affectableStats)
	{
		super.affectPhyStats(affected,affectableStats);
		if(affected==null) return;
		affectableStats.setDisposition(affectableStats.disposition()|PhyStats.IS_EVIL);
		final int xlvl=super.getXLEVELLevel(invoker());
		if(affected instanceof MOB)
			affectableStats.setArmor(affectableStats.armor()+(10+(2*xlvl)));
		else
		if(affected instanceof Item)
			affectableStats.setAbility(affectableStats.ability()-1);
	}

	@Override
	public void unInvoke()
	{


		// undo the affects of this spell
		if(!(affected instanceof MOB))
		{
			if(canBeUninvoked())
			if((affected instanceof Item)&&(((Item)affected).owner()!=null)&&(((Item)affected).owner() instanceof MOB))
				((MOB)((Item)affected).owner()).tell(L("The curse on @x1 is lifted.",((Item)affected).name()));
			super.unInvoke();
			return;
		}
		final MOB mob=(MOB)affected;
		if(canBeUninvoked())
			mob.tell(L("The curse is lifted."));
		super.unInvoke();
	}

	@Override
	public boolean okMessage(final Environmental myHost, final CMMsg msg)
	{
		if(!super.okMessage(myHost,msg))
			return false;

		if(affected==null) return true;
		if(!(affected instanceof Item)) return true;

		final Item item=(Item)affected;

		final MOB mob=msg.source();
		if((msg.tool()==item)&&(msg.sourceMinor()==CMMsg.TYP_THROW))
		{
			mob.tell(L("You can't seem to get rid of @x1.",item.name()));
			return false;
		}
		else
		if(!msg.amITarget(item))
			return true;
		else
		switch(msg.targetMinor())
		{
		case CMMsg.TYP_REMOVE:
			if(!item.amWearingAt(Wearable.IN_INVENTORY))
			{
				if(item.amWearingAt(Wearable.WORN_WIELD)||item.amWearingAt(Wearable.WORN_HELD))
				{
					mob.tell(L("You can't seem to let go of @x1.",item.name()));
					return false;
				}
				mob.tell(L("You can't seem to remove @x1.",item.name()));
				return false;
			}
			break;
		case CMMsg.TYP_DROP:
			mob.tell(L("You can't seem to get rid of @x1.",item.name()));
			return false;
		}
		return true;
	}

	@Override
	public int castingQuality(MOB mob, Physical target)
	{
		if(mob!=null)
		{
			if((target instanceof MOB)&&(mob!=target))
			{
				Item I=Prayer_Curse.getSomething((MOB)target,true);
				if(I==null)
					I=Prayer_Curse.getSomething((MOB)target,false);
				if(I==null) return Ability.QUALITY_INDIFFERENT;
			}
		}
		return super.castingQuality(mob,target);
	}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		final MOB mobTarget=getTarget(mob,commands,givenTarget,true,false);
		Item target=null;
		if(mobTarget!=null)
			target=Prayer_Curse.getSomething(mobTarget,true);
		if((target==null)&&(mobTarget!=null))
			target=Prayer_Curse.getSomething(mobTarget,false);
		if(target==null)
			target=getTarget(mob,mob.location(),givenTarget,commands,Wearable.FILTER_ANY);

		if(target==null) return false;

		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		boolean success=proficiencyCheck(mob,0,auto);

		if(success)
		{
			// it worked, so build a copy of this ability,
			// and add it to the affects list of the
			// affected MOB.  Then tell everyone else
			// what happened.
			final CMMsg msg=CMClass.getMsg(mob,target,this,verbalCastCode(mob,target,auto),auto?L("<T-NAME> <T-IS-ARE> cursed!"):L("^S<S-NAME> curse(s) <T-NAMESELF>.^?"));
			final CMMsg msg2=CMClass.getMsg(mob,mobTarget,this,verbalCastCode(mob,mobTarget,auto),null);
			if((mob.location().okMessage(mob,msg))&&((mobTarget==null)||(mob.location().okMessage(mob,msg2))))
			{
				mob.location().send(mob,msg);
				if(mobTarget!=null)
					mob.location().send(mob,msg2);
				if(msg.value()<=0)
				{
					Prayer_Curse.endLowerBlessings(target,CMLib.ableMapper().lowestQualifyingLevel(ID()));
					success=maliciousAffect(mob,target,asLevel,0,-1)!=null;
					target.recoverPhyStats();
					mob.recoverPhyStats();
				}
			}
		}
		else
			return maliciousFizzle(mob,target,L("<S-NAME> attempt(s) to curse <T-NAMESELF>, but nothing happens."));


		// return whether it worked
		return success;
	}
}
