package com.suscipio_solutions.consecro_mud.Abilities.Prayers;
import java.util.HashSet;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Weapon;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;
import com.suscipio_solutions.consecro_mud.core.interfaces.Tickable;



@SuppressWarnings({"unchecked","rawtypes"})
public class Prayer_AuraHarm extends Prayer
{
	@Override public String ID() { return "Prayer_AuraHarm"; }
	private final static String localizedName = CMLib.lang().L("Aura of Harm");
	@Override public String name() { return localizedName; }
	private final static String localizedStaticDisplay = CMLib.lang().L("(Harm Aura)");
	@Override public String displayText() { return localizedStaticDisplay; }
	@Override protected int canAffectCode(){return Ability.CAN_ROOMS;}
	@Override protected int canTargetCode(){return 0;}
	@Override public int classificationCode(){return Ability.ACODE_PRAYER|Ability.DOMAIN_VEXING;}
	@Override public int abstractQuality(){ return Ability.QUALITY_MALICIOUS;}
	@Override public long flags(){return Ability.FLAG_UNHOLY;}
	private int damageTickDown=4;

	public Prayer_AuraHarm()
	{
		super();

		damageTickDown = 4;
	}


	@Override
	public void unInvoke()
	{
		// undo the affects of this spell
		if((affected==null)||(!(affected instanceof Room)))
			return;
		final Room R=(Room)affected;

		super.unInvoke();

		if(canBeUninvoked())
			R.showHappens(CMMsg.MSG_OK_VISUAL,L("The harmful aura around you fades."));
	}

	@Override
	public boolean tick(Tickable ticking, int tickID)
	{
		if((affected==null)||(!(affected instanceof Room)))
			return super.tick(ticking,tickID);

		if((--damageTickDown)>=0) return super.tick(ticking,tickID);
		damageTickDown=4;

		HashSet H=null;
		if((invoker()!=null)&&(invoker().location()==affected))
		{
			H=new HashSet();
			invoker().getGroupMembers(H);
		}
		final Room R=(Room)affected;
		for(int i=0;i<R.numInhabitants();i++)
		{
			final MOB M=R.fetchInhabitant(i);
			if((M!=null)&&((H==null)||(!H.contains(M))))
			{
				if(invoker()!=null)
				{
					final int harming=CMLib.dice().roll(1,adjustedLevel(invoker(),0)/3,1);
					CMLib.combat().postDamage(invoker(),M,this,harming,CMMsg.MASK_MALICIOUS|CMMsg.TYP_UNDEAD,Weapon.TYPE_BURSTING,"The unholy aura <DAMAGE> <T-NAME>!");
				}
				else
				{
					final int harming=CMLib.dice().roll(1,CMLib.ableMapper().lowestQualifyingLevel(ID())/3,1);
					CMLib.combat().postDamage(M,M,this,harming,CMMsg.MASK_MALICIOUS|CMMsg.TYP_UNDEAD,Weapon.TYPE_BURSTING,"The unholy aura <DAMAGE> <T-NAME>!");
				}
				if((!M.isInCombat())&&(M.isMonster())&&(M!=invoker)&&(invoker!=null)&&(M.location()==invoker.location())&&(M.location().isInhabitant(invoker))&&(CMLib.flags().canBeSeenBy(invoker,M)))
					CMLib.combat().postAttack(M,invoker,M.fetchWieldedItem());
			}
		}
		return super.tick(ticking,tickID);
	}

	@Override
	public int castingQuality(MOB mob, Physical target)
	{
		if(mob!=null)
		{
			if(target instanceof Room)
			{
				if(!mob.isInCombat())
					return super.castingQuality(mob, target,Ability.QUALITY_INDIFFERENT);
				if(mob.charStats().getMyRace().racialCategory().equals("Undead"))
					return super.castingQuality(mob, target,Ability.QUALITY_BENEFICIAL_SELF);
				return super.castingQuality(mob, target,Ability.QUALITY_MALICIOUS);
			}
		}
		return super.castingQuality(mob,target);
	}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		final Room target=mob.location();
		if(target==null) return false;
		if(target.fetchEffect(ID())!=null)
		{
			mob.tell(L("The aura of harm is already here."));
			return false;
		}
		if(target.fetchEffect("Prayer_AuraHeal")!=null)
		{
			target.fetchEffect("Prayer_AuraHeal").unInvoke();
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
			final CMMsg msg=CMClass.getMsg(mob,target,this,verbalCastCode(mob,target,auto),auto?"":L("^S<S-NAME> @x1 for all to feel pain.^?",prayWord(mob)));
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				mob.location().show(mob,null,CMMsg.MSG_OK_VISUAL,L("A harmful aura descends over the area!"));
				maliciousAffect(mob,target,asLevel,0,-1);
			}
		}
		else
			return maliciousFizzle(mob,target,L("<S-NAME> @x1 for an aura of harm, but <S-HIS-HER> plea is not answered.",prayWord(mob)));


		// return whether it worked
		return success;
	}
}
