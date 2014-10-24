package com.suscipio_solutions.consecro_mud.Abilities.Prayers;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CharState;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CharStats;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;



@SuppressWarnings("rawtypes")
public class Prayer_DivineConstitution extends Prayer
{
	@Override public String ID() { return "Prayer_DivineConstitution"; }
	private final static String localizedName = CMLib.lang().L("Divine Constitution");
	@Override public String name() { return localizedName; }
	private final static String localizedStaticDisplay = CMLib.lang().L("(Divine Constitution)");
	@Override public String displayText() { return localizedStaticDisplay; }
	@Override public int classificationCode(){return Ability.ACODE_PRAYER|Ability.DOMAIN_HEALING;}
	@Override protected int canAffectCode(){return Ability.CAN_MOBS;}
	@Override protected int canTargetCode(){return Ability.CAN_MOBS;}
	@Override public int abstractQuality(){ return Ability.QUALITY_BENEFICIAL_OTHERS;}
	@Override public long flags(){return Ability.FLAG_HOLY;}
	protected int conPts=1;
	protected int xtraHPs=0;
	protected int maxPoints=6;

	@Override
	public void affectCharStats(MOB affected, CharStats affectableStats)
	{
		super.affectCharStats(affected,affectableStats);
		if(affected==null) return;
		affectableStats.setStat(CharStats.STAT_CONSTITUTION, affectableStats.getStat(CharStats.STAT_CONSTITUTION)+conPts);
		affectableStats.setStat(CharStats.STAT_MAX_CONSTITUTION_ADJ, affectableStats.getStat(CharStats.STAT_MAX_CONSTITUTION_ADJ)+conPts);
	}

	@Override
	public void affectCharState(MOB affected, CharState affectableMaxState)
	{
		super.affectCharState(affected, affectableMaxState);
		if(affected==null) return;
		affectableMaxState.setHitPoints(affectableMaxState.getHitPoints()+xtraHPs);
	}

	@Override
	public boolean okMessage(Environmental host, CMMsg msg)
	{

		if((msg.target()==affected)
		&&(affected instanceof MOB)
		&&(msg.targetMinor()==CMMsg.TYP_HEALING)
		&&(msg.source().location()!=null)
		&&(msg.source()==invoker())
		&&(conPts<maxPoints)
		&&(CMLib.dice().rollPercentage()<(50+(5*getX1Level(msg.source())))))
		{
			final MOB M=(MOB)affected;
			if(M!=null)
			{
				final Room R=M.location();
				final int diff = (M.curState().getHitPoints() - M.maxState().getHitPoints()) + msg.value();
				if((diff>0)
				&&(msg.value()>diff))
				{
					R.show(M,null,CMMsg.MSG_OK_VISUAL,L("<S-NAME> gain(s) divine health!"));
					conPts++;
					xtraHPs+=1+diff;
					msg.source().recoverCharStats();
					msg.source().recoverMaxState();
				}
			}
		}
		return super.okMessage(host,msg);
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
			if((mob.location()!=null)&&(!mob.amDead()))
				mob.location().show(mob,null,CMMsg.MSG_OK_VISUAL,L("<S-YOUPOSS> divine constitution fades."));
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
			final CMMsg msg=CMClass.getMsg(mob,target,this,verbalCastCode(mob,target,auto),L(auto?"<T-NAME> become(s) covered by divine constitution.":"^S<S-NAME> "+prayWord(mob)+" for <T-NAMESELF> to be covered by divine constitution.^?"));
			if(mob.location().okMessage(mob,msg))
			{
				conPts=1+(super.getXLEVELLevel(mob)/2);
				xtraHPs=0;
				maxPoints=6+(super.getXLEVELLevel(mob)/2);
				mob.location().send(mob,msg);
				beneficialAffect(mob,target,asLevel,0);
				target.recoverPhyStats();
				target.recoverCharStats();
				target.recoverMaxState();
			}
		}
		else
			return beneficialWordsFizzle(mob,target,L("<S-NAME> @x1 for <T-NAMESELF> to have divine constitution, but nothing happens.",prayWord(mob)));


		// return whether it worked
		return success;
	}
}
