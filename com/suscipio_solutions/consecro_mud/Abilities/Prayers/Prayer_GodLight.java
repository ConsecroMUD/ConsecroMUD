package com.suscipio_solutions.consecro_mud.Abilities.Prayers;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.PhyStats;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Wearable;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.Races.interfaces.Race;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;



@SuppressWarnings("rawtypes")
public class Prayer_GodLight extends Prayer
{
	@Override public String ID() { return "Prayer_GodLight"; }
	private final static String localizedName = CMLib.lang().L("Godlight");
	@Override public String name() { return localizedName; }
	private final static String localizedStaticDisplay = CMLib.lang().L("(Godlight)");
	@Override public String displayText() { return localizedStaticDisplay; }
	@Override public long flags(){return Ability.FLAG_HOLY;}
	@Override public int classificationCode(){return Ability.ACODE_PRAYER|Ability.DOMAIN_CREATION;}
	@Override public int abstractQuality(){return Ability.QUALITY_MALICIOUS;}

	@Override
	public void affectPhyStats(Physical affected, PhyStats affectableStats)
	{
		super.affectPhyStats(affected,affectableStats);
		if(affected==null) return;
		affectableStats.setDisposition(affectableStats.disposition()|PhyStats.IS_LIGHTSOURCE);
		affectableStats.setDisposition(affectableStats.disposition()|PhyStats.IS_GLOWING);
		if(CMath.bset(affectableStats.disposition(),PhyStats.IS_DARK))
			affectableStats.setDisposition(affectableStats.disposition()-PhyStats.IS_DARK);
		if(!(affected instanceof MOB)) return;
		affectableStats.setSensesMask(affectableStats.sensesMask()|PhyStats.CAN_NOT_SEE);
	}

	@Override
	public void unInvoke()
	{
		// undo the affects of this spell
		if(!(affected instanceof MOB))
			return;
		final MOB mob=(MOB)affected;

		super.unInvoke();

		if(canBeUninvoked())
			mob.tell(L("Your vision returns."));
	}


	@Override
	public int castingQuality(MOB mob, Physical target)
	{
		if(mob!=null)
		{
			if(mob.isInCombat())
			{
				if(CMLib.flags().isInDark(mob.location()))
					return Ability.QUALITY_INDIFFERENT;
				if(target instanceof MOB)
				{
					if(((MOB)target).charStats().getBodyPart(Race.BODY_EYE)==0)
						return Ability.QUALITY_INDIFFERENT;
					if(!CMLib.flags().canSee((MOB)target))
						return Ability.QUALITY_INDIFFERENT;
				}
			}
		}
		return super.castingQuality(mob,target);
	}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{

		Physical target=null;
		if((!auto)
		&&((commands.size()==0)||(((String)commands.firstElement()).equalsIgnoreCase("ROOM")))
		&&(!mob.isInCombat()))
			target=mob.location();
		else
		{
			if((commands.size()==0)&&(mob.isInCombat()))
				target=mob.getVictim();
			if(target==null)
				target=getAnyTarget(mob,commands,givenTarget,Wearable.FILTER_UNWORNONLY);
		}
		if(target==null) return false;
		if((target instanceof Room)&&(target.fetchEffect(ID())!=null))
		{
			mob.tell(L("This place already has the god light."));
			return false;
		}

		if((target instanceof MOB)
		&&(((MOB)target).charStats().getBodyPart(Race.BODY_EYE)==0))
		{
			mob.tell(L("@x1 has no eyes, and would not be affected.",target.name(mob)));
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
			if(target instanceof Room) mob.phyStats().setSensesMask(mob.phyStats().sensesMask()|PhyStats.CAN_SEE_DARK);
			final CMMsg msg=CMClass.getMsg(mob,target,this,verbalCastCode(mob,target,auto)|CMMsg.MASK_MALICIOUS,auto?"":((target instanceof MOB)?"^S<S-NAME> point(s) to <T-NAMESELF> and "+prayWord(mob)+". A beam of bright sunlight flashes into <T-HIS-HER> eyes!^?":"^S<S-NAME> point(s) at <T-NAMESELF> and "+prayWord(mob)+".^?"));
			if(mob.location().okMessage(mob,msg))
			{
				mob.recoverPhyStats();
				mob.location().send(mob,msg);
				if(msg.value()<=0)
				{
					if(target instanceof MOB)
						mob.location().show((MOB)target,null,CMMsg.MSG_OK_VISUAL,L("<S-NAME> go(es) blind!"));
					maliciousAffect(mob,target,asLevel,0,-1);
					mob.location().recoverRoomStats();
					mob.location().recoverRoomStats();
				}
			}
		}
		else
			return maliciousFizzle(mob,target,L("<S-NAME> point(s) at <T-NAMESELF> and @x1, but nothing happens.",prayWord(mob)));


		// return whether it worked
		return success;
	}
}
