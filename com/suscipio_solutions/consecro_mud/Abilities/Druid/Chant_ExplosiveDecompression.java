package com.suscipio_solutions.consecro_mud.Abilities.Druid;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.PhyStats;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Weapon;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.Races.interfaces.Race;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;



@SuppressWarnings("rawtypes")
public class Chant_ExplosiveDecompression extends Chant
{
	@Override public String ID() { return "Chant_ExplosiveDecompression"; }
	private final static String localizedName = CMLib.lang().L("Explosive Decompression");
	@Override public String name() { return localizedName; }
	@Override public int classificationCode(){return Ability.ACODE_CHANT|Ability.DOMAIN_DEEPMAGIC;}
	@Override public int abstractQuality(){return Ability.QUALITY_MALICIOUS;}
	@Override protected int canAffectCode(){return Ability.CAN_ROOMS;}
	@Override protected int canTargetCode(){return Ability.CAN_ROOMS;}
	@Override public boolean bubbleAffect(){return true;}

	@Override
	public void affectPhyStats(Physical affecting, PhyStats stats)
	{
		super.affectPhyStats(affected,stats);
		if((affected instanceof MOB)&&(((MOB)affected).charStats().getBreathables().length>0))
			stats.setSensesMask(stats.sensesMask()|PhyStats.CAN_NOT_BREATHE);
	}

	@Override
	public int castingQuality(MOB mob, Physical target)
	{
		if(mob!=null)
		{
			if((mob.location().domainType()&Room.INDOORS)==0)
				return Ability.QUALITY_INDIFFERENT;
		}
		return super.castingQuality(mob,target);
	}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		final Room target=mob.location();
		if(target==null) return false;
		if((!auto)&&((target.domainType()&Room.INDOORS)==0))
		{
			mob.tell(L("This chant only works indoors."));
			return false;
		}

		// the invoke method for spells receives as
		// parameters the invoker, and the REMAINING
		// command line parameters, divided into words,
		// and added as String objects to a vector.
		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;
		final boolean success=proficiencyCheck(mob,0,auto);

		if(success)
		{
			// it worked, so build a copy of this ability,
			// and add it to the affects list of the
			// affected MOB.  Then tell everyone else
			// what happened.
			invoker=mob;
			final CMMsg msg=CMClass.getMsg(mob,null,this,verbalCastCode(mob,null,auto),auto?"":L("^S<S-NAME> chant(s) loudly.  A ball of fire forms around <S-NAME>.^?"));
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				if(msg.value()<=0)
				{
					mob.location().showHappens(CMMsg.MSG_OK_VISUAL,L("The ball of fire **EXPLODES**!"));
					for(int i=0;i<target.numInhabitants();i++)
					{
						final MOB M=target.fetchInhabitant(i);
						if((M!=null)&&(M!=mob))
						{
							final CMMsg msg2=CMClass.getMsg(mob,M,this,verbalCastMask(mob,target,auto)|CMMsg.TYP_FIRE,null);
							if(mob.location().okMessage(mob,msg2))
							{
								mob.location().send(mob,msg2);
								invoker=mob;
								final int numDice = adjustedLevel(mob,asLevel)+(2*super.getX1Level(mob));
								int damage = CMLib.dice().roll(numDice, 5, 25);
								if(msg2.value()>0)
									damage = (int)Math.round(CMath.div(damage,2.0));
								CMLib.combat().postDamage(mob,M,this,damage,CMMsg.MASK_ALWAYS|CMMsg.TYP_FIRE,Weapon.TYPE_BURNING,"The flaming blast <DAMAGE> <T-NAME>!");
							}
							if((M.charStats().getBodyPart(Race.BODY_FOOT)>0)
							&&(!CMLib.flags().isFlying(M))&&(CMLib.flags().isStanding(M)))
								mob.location().show(M,null,CMMsg.MASK_ALWAYS|CMMsg.TYP_SIT,L("<S-NAME> <S-IS-ARE> blown off <S-HIS-HER> feet!"));
						}
					}
					maliciousAffect(mob,target,asLevel,20,-1);
					mob.location().showHappens(CMMsg.MSG_OK_VISUAL,L("The fire burns off all the air here!"));
				}
			}
		}
		else
			return maliciousFizzle(mob,target,L("<S-NAME> chant(s) loudly, but nothing happens."));
		// return whether it worked
		return success;
	}
}
