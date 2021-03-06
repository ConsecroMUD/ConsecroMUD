package com.suscipio_solutions.consecro_mud.Abilities.Prayers;
import java.util.List;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.PhyStats;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Wearable;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;



@SuppressWarnings({"unchecked","rawtypes"})
public class Prayer_Curse extends Prayer
{
	@Override public String ID() { return "Prayer_Curse"; }
	private final static String localizedName = CMLib.lang().L("Curse");
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
			affectableStats.setArmor(affectableStats.armor()+5+(2*xlvl));
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

	public static Item getSomething(MOB mob, boolean blessedOnly)
	{
		final Vector good=new Vector();
		final Vector great=new Vector();
		Item target=null;
		for(int i=0;i<mob.numItems();i++)
		{
			final Item I=mob.getItem(i);
			if((!blessedOnly)||(isBlessed(I)))
				if(I.amWearingAt(Wearable.IN_INVENTORY))
					good.addElement(I);
				else
					great.addElement(I);
		}
		if(great.size()>0)
			target=(Item)great.elementAt(CMLib.dice().roll(1,great.size(),-1));
		else
		if(good.size()>0)
			target=(Item)good.elementAt(CMLib.dice().roll(1,good.size(),-1));
		return target;
	}

	public static void endLowerBlessings(Physical target, int level)
	{
		final List<Ability> V=CMLib.flags().domainAffects(target,Ability.DOMAIN_BLESSING);
		for(int v=0;v<V.size();v++)
		{
			final Ability A=V.get(v);
			if(CMLib.ableMapper().lowestQualifyingLevel(A.ID())<=level)
				A.unInvoke();
		}
	}
	public static boolean isBlessed(Item item)
	{
		return CMLib.flags().domainAffects(item,Ability.DOMAIN_BLESSING).size()>0;
	}

	public static void endLowerCurses(Physical target, int level)
	{
		final List<Ability> V=CMLib.flags().domainAffects(target,Ability.DOMAIN_CURSING);
		for(int v=0;v<V.size();v++)
		{
			final Ability A=V.get(v);
			if(CMLib.ableMapper().lowestQualifyingLevel(A.ID())<level)
				A.unInvoke();
		}
	}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		final MOB target=this.getTarget(mob,commands,givenTarget);
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
			final CMMsg msg=CMClass.getMsg(mob,target,this,verbalCastCode(mob,target,auto)|CMMsg.MASK_MALICIOUS,auto?L("<T-NAME> <T-IS-ARE> cursed!"):L("^S<S-NAME> curse(s) <T-NAMESELF>.^?"));
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				if(msg.value()<=0)
				{
					final Item I=getSomething(mob,true);
					if(I!=null)
					{
						endLowerBlessings(I,CMLib.ableMapper().lowestQualifyingLevel(ID()));
						I.recoverPhyStats();
					}
					endLowerBlessings(target,CMLib.ableMapper().lowestQualifyingLevel(ID()));
					success=maliciousAffect(mob,target,asLevel,0,-1)!=null;
					target.recoverPhyStats();
				}
			}
		}
		else
			return maliciousFizzle(mob,target,L("<S-NAME> attempt(s) to curse <T-NAMESELF>, but nothing happens."));


		// return whether it worked
		return success;
	}
}
