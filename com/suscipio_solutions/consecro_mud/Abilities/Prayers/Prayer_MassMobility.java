package com.suscipio_solutions.consecro_mud.Abilities.Prayers;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CharStats;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;


@SuppressWarnings("rawtypes")
public class Prayer_MassMobility extends Prayer
{
	@Override public String ID() { return "Prayer_MassMobility"; }
	private final static String localizedName = CMLib.lang().L("Mass Mobility");
	@Override public String name() { return localizedName; }
	@Override public int classificationCode(){return Ability.ACODE_PRAYER|Ability.DOMAIN_RESTORATION;}
	@Override public int abstractQuality(){ return Ability.QUALITY_BENEFICIAL_OTHERS;}
	@Override public long flags(){return Ability.FLAG_HOLY|Ability.FLAG_UNHOLY;}
	private final static String localizedStaticDisplay = CMLib.lang().L("(Mass Mobility)");
	@Override public String displayText() { return localizedStaticDisplay; }



	@Override
	public boolean okMessage(final Environmental myHost, final CMMsg msg)
	{
		if(!(affected instanceof MOB))
			return true;

		final MOB mob=(MOB)affected;
		if((msg.amITarget(mob))
		&&(CMath.bset(msg.targetMajor(),CMMsg.MASK_MALICIOUS))
		&&(msg.targetMinor()==CMMsg.TYP_CAST_SPELL)
		&&(msg.tool()!=null)
		&&(msg.tool() instanceof Ability)
		&&(!mob.amDead()))
		{
			final Ability A=(Ability)msg.tool();
			final MOB newMOB=CMClass.getFactoryMOB();
			final CMMsg msg2=CMClass.getMsg(newMOB,null,null,CMMsg.MSG_SIT,null);
			newMOB.recoverPhyStats();
			try
			{
				A.affectPhyStats(newMOB,newMOB.phyStats());
				if((!CMLib.flags().aliveAwakeMobileUnbound(newMOB,true))
				   ||(CMath.bset(A.flags(),Ability.FLAG_PARALYZING))
				   ||(!A.okMessage(newMOB,msg2)))
				{
					mob.location().show(mob,msg.source(),null,CMMsg.MSG_OK_VISUAL,L("The aura around <S-NAME> repels the @x1 from <T-NAME>.",A.name()));
					newMOB.destroy();
					return false;
				}
			}
			catch(final Exception e)
			{}
			newMOB.destroy();
		}
		return true;
	}


	@Override
	public void affectCharStats(MOB affectedMOB, CharStats affectedStats)
	{
		super.affectCharStats(affectedMOB,affectedStats);
		if(affectedStats.getStat(CharStats.STAT_SAVE_PARALYSIS)<(Short.MAX_VALUE/2))
			affectedStats.setStat(CharStats.STAT_SAVE_PARALYSIS,affectedStats.getStat(CharStats.STAT_SAVE_PARALYSIS)+100);
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
			mob.tell(L("The aura of mobility around you fades."));
	}


	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		final boolean success=proficiencyCheck(mob,0,auto);

		final Room room=mob.location();
		int affectType=CMMsg.MSG_CAST_VERBAL_SPELL;
		if(auto) affectType=affectType|CMMsg.MASK_ALWAYS;
		if((success)&&(room!=null))
		{
			CMMsg msg=CMClass.getMsg(mob,null,this,affectType,auto?"":L("^S<S-NAME> @x1 for an aura of mobility!^?",prayWord(mob)));
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				for(int i=0;i<room.numInhabitants();i++)
				{
					final MOB target=room.fetchInhabitant(i);
					if(target==null) break;

					// it worked, so build a copy of this ability,
					// and add it to the affects list of the
					// affected MOB.  Then tell everyone else
					// what happened.
					msg=CMClass.getMsg(mob,target,this,affectType,L("Mobility is invoked upon <T-NAME>."));
					if(mob.location().okMessage(mob,msg))
					{
						mob.location().send(mob,msg);
						beneficialAffect(mob,target,asLevel,0);
					}
				}
			}
		}
		else
		{
			beneficialWordsFizzle(mob,null,L("<S-NAME> @x1, but nothing happens.",prayWord(mob)));
			return false;
		}
		return success;
	}
}
