package com.suscipio_solutions.consecro_mud.Abilities.Prayers;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.Deity;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;



@SuppressWarnings("rawtypes")
public class Prayer_Faithless extends Prayer
{
	@Override public String ID() { return "Prayer_Faithless"; }
	private final static String localizedName = CMLib.lang().L("Faithless");
	@Override public String name() { return localizedName; }
	@Override public int classificationCode(){return Ability.ACODE_PRAYER|Ability.DOMAIN_EVANGELISM;}
	@Override public int abstractQuality(){ return Ability.QUALITY_MALICIOUS;}
	@Override public long flags(){return Ability.FLAG_UNHOLY;}
	@Override protected int overrideMana(){return 100;}

	@Override
	public int castingQuality(MOB mob, Physical target)
	{
		if(mob!=null)
		{
			if(mob.isInCombat())
				return Ability.QUALITY_INDIFFERENT;
			if(target instanceof MOB)
			{
				if(((MOB)target).charStats().getCurrentClass().baseClass().equals("Cleric"))
					return Ability.QUALITY_INDIFFERENT;
				if(CMLib.flags().isAnimalIntelligence(((MOB)target))||CMLib.flags().isGolem(target))
					return Ability.QUALITY_INDIFFERENT;
				if(((MOB)target).getWorshipCharID().length()==0)
					return Ability.QUALITY_INDIFFERENT;
			}
		}
		return super.castingQuality(mob,target);
	}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		final MOB target=this.getTarget(mob,commands,givenTarget);
		if(target==null) return false;
		if((!auto)&&(target.charStats().getCurrentClass().baseClass().equals("Cleric")))
		{
			mob.tell(L("@x1 can not be affected by this prayer.",target.name(mob)));
			return false;
		}
		if(CMLib.flags().isAnimalIntelligence(target)||CMLib.flags().isGolem(target))
		{
			if(!auto)mob.tell(L("@x1 can not be affected by this prayer.",target.name(mob)));
			return false;
		}

		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		int levelDiff=target.phyStats().level()-(mob.phyStats().level()+(2*super.getXLEVELLevel(mob)));
		if(levelDiff<0) levelDiff=0;
		final boolean success=proficiencyCheck(mob,-(levelDiff*25),auto);
		Deity D=null;
		if(target.getWorshipCharID().length()>0)
			D=CMLib.map().getDeity(target.getWorshipCharID());
		int type=verbalCastCode(mob,target,auto);
		int mal=CMMsg.MASK_MALICIOUS;
		if(auto){ type=CMath.unsetb(type,CMMsg.MASK_MALICIOUS); mal=0;}
		if((success)&&(D!=null))
		{
			// it worked, so build a copy of this ability,
			// and add it to the affects list of the
			// affected MOB.  Then tell everyone else
			// what happened.
			final CMMsg msg=CMClass.getMsg(mob,target,this,type,auto?"":L("^S<S-NAME> @x1 for <T-NAMESELF> to lose faith!^?",prayWord(mob)));
			final CMMsg msg2=CMClass.getMsg(target,D,this,CMMsg.MSG_REBUKE,L("<S-NAME> LOSE(S) FAITH!!!"));
			final CMMsg msg3=CMClass.getMsg(mob,target,this,CMMsg.MSK_CAST_VERBAL|mal|CMMsg.TYP_MIND|(auto?CMMsg.MASK_ALWAYS:0),null);
			if((mob.location().okMessage(mob,msg))
			&&(mob.location().okMessage(mob,msg3))
			&&(mob.location().okMessage(mob,msg2)))
			{
				mob.location().send(mob,msg);
				mob.location().send(mob,msg3);
				if((msg.value()<=0)&&(msg3.value()<=0))
					mob.location().send(mob,msg2);
			}
		}
		else
			maliciousFizzle(mob,target,auto?"":L("<S-NAME> @x1 for <T-NAMESELF>, but nothing happens.",prayWord(mob)));


		// return whether it worked
		return success;
	}
}
