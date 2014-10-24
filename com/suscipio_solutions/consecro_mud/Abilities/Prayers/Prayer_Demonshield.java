package com.suscipio_solutions.consecro_mud.Abilities.Prayers;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CharStats;
import com.suscipio_solutions.consecro_mud.Common.interfaces.PhyStats;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Weapon;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;



@SuppressWarnings("rawtypes")
public class Prayer_Demonshield extends Prayer
{
	@Override public String ID() { return "Prayer_Demonshield"; }
	private final static String localizedName = CMLib.lang().L("Demonshield");
	@Override public String name() { return localizedName; }
	private final static String localizedStaticDisplay = CMLib.lang().L("(Demonshield)");
	@Override public String displayText() { return localizedStaticDisplay; }
	@Override public int classificationCode(){return Ability.ACODE_PRAYER|Ability.DOMAIN_HOLYPROTECTION;}
	@Override public int abstractQuality(){ return Ability.QUALITY_BENEFICIAL_OTHERS;}
	@Override protected int canAffectCode(){return CAN_MOBS;}
	@Override public long flags(){return Ability.FLAG_UNHOLY|Ability.FLAG_HEATING|Ability.FLAG_FIREBASED;}
	final static String msgStr=CMLib.lang().L("The unholy flames around <S-NAME> flare and <DAMAGE> <T-NAME>!");
	protected long oncePerTickTime=0;

	@Override
	public void unInvoke()
	{
		// undo the affects of this spell
		if(!(affected instanceof MOB))
			return;
		final MOB mob=(MOB)affected;

		super.unInvoke();

		if(canBeUninvoked())
			if((mob.location()!=null)&&(!mob.amDead()))
				mob.location().show(mob,null,CMMsg.MSG_OK_VISUAL,L("<S-YOUPOSS> demonic flame shield vanishes."));
	}

	@Override
	public void executeMsg(final Environmental myHost, final CMMsg msg)
	{
		super.executeMsg(myHost,msg);
		if(affected==null) return;
		if(!(affected instanceof MOB)) return;
		final MOB mob=(MOB)affected;
		if(msg.target()==null) return;
		if(msg.source()==null) return;
		final MOB source=msg.source();
		if(source.location()==null) return;


		if(msg.amITarget(mob))
		{
			if((CMath.bset(msg.targetMajor(),CMMsg.MASK_HANDS)||(msg.targetMajor(CMMsg.MASK_MOVE)))
			   &&(msg.source().rangeToTarget()==0)
			   &&(oncePerTickTime!=mob.lastTickedDateTime()))
			{
				if((CMLib.dice().rollPercentage()>(source.charStats().getStat(CharStats.STAT_DEXTERITY)*3))
				   &&(!CMLib.flags().isEvil(source)))
				{
					final CMMsg msg2=CMClass.getMsg(mob,source,this,verbalCastCode(mob,source,true),null);
					if(source.location().okMessage(mob,msg2))
					{
						source.location().send(mob,msg2);
						if(invoker==null) invoker=source;
						if(msg2.value()<=0)
						{
							final int damage = CMLib.dice().roll( 1,
															(int)Math.round( ( adjustedLevel( invoker(), 0 ) + ( 2.0 * (super.getX1Level( invoker() )) ) ) / 5.0 ),
															1 );
							CMLib.combat().postDamage(mob,source,this,damage,CMMsg.MASK_MALICIOUS|CMMsg.MASK_ALWAYS|CMMsg.TYP_FIRE,Weapon.TYPE_BURNING,msgStr);
							if((!mob.isInCombat())&&(mob.isMonster())&&(mob!=invoker)&&(invoker!=null)&&(mob.location()==invoker.location())&&(mob.location().isInhabitant(invoker))&&(CMLib.flags().canBeSeenBy(invoker,mob)))
								CMLib.combat().postAttack(mob,invoker,mob.fetchWieldedItem());
						}
					}
					oncePerTickTime=mob.lastTickedDateTime();
				}
			}
		}
		return;
	}

	@Override
	public void affectPhyStats(Physical affected, PhyStats affectableStats)
	{
		super.affectPhyStats(affected,affectableStats);
		if(affected==null) return;
		if(!(affected instanceof MOB)) return;
		affectableStats.setArmor(affectableStats.armor()-(1+(2*getXLEVELLevel(invoker()))));
	}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		final MOB target=this.getTarget(mob,commands,givenTarget);
		if(target==null) return false;

		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		final boolean success=proficiencyCheck(mob,0,auto);

		if(success)
		{
			// it worked, so build a copy of this ability,
			// and add it to the affects list of the
			// affected MOB.  Then tell everyone else
			// what happened.
			final CMMsg msg=CMClass.getMsg(mob,target,this,verbalCastCode(mob,target,auto),((auto?"":"^S<S-NAME> "+prayWord(mob)+".  ")+L("A field of unholy flames erupt(s) around <T-NAME>!^?"))+CMLib.protocol().msp("fireball.wav",10));
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				beneficialAffect(mob,target,asLevel,0);
			}
		}
		else
			return beneficialWordsFizzle(mob,target,L("<S-NAME> @x1, but only sparks emerge.",prayWord(mob)));


		// return whether it worked
		return success;
	}
}
