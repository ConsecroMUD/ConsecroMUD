package com.suscipio_solutions.consecro_mud.Abilities.Fighter;
import java.util.Set;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CharState;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;
import com.suscipio_solutions.consecro_mud.core.interfaces.Tickable;



@SuppressWarnings("rawtypes")
public class Fighter_Rallycry extends FighterSkill
{
	@Override public String ID() { return "Fighter_Rallycry"; }
	private final static String localizedName = CMLib.lang().L("Rally Cry");
	@Override public String name() { return localizedName; }
	private final static String localizedStaticDisplay = CMLib.lang().L("(Rally Cry)");
	@Override public String displayText() { return localizedStaticDisplay; }
	@Override public int abstractQuality(){ return Ability.QUALITY_BENEFICIAL_OTHERS;}
	private static final String[] triggerStrings =I(new String[] {"RALLYCRY"});
	@Override public String[] triggerStrings(){return triggerStrings;}
	@Override protected int canAffectCode(){return 0;}
	@Override protected int canTargetCode(){return Ability.CAN_MOBS;}
	@Override public int classificationCode(){return Ability.ACODE_SKILL|Ability.DOMAIN_SINGING;}
	protected int timesTicking=0;
	protected int hpUp=0;

	@Override
	public void affectCharState(MOB affected, CharState affectableStats)
	{
		super.affectCharState(affected,affectableStats);
		if(invoker==null) return;
		affectableStats.setHitPoints(affectableStats.getHitPoints()+hpUp);
	}

	@Override
	public boolean tick(Tickable ticking, int tickID)
	{
		if(!super.tick(ticking,tickID))
			return false;
		if((affected==null)||(invoker==null)||(!(affected instanceof MOB)))
			return false;
		if((!((MOB)affected).isInCombat())&&(++timesTicking>5))
			unInvoke();
		return true;
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
		{
			mob.tell(L("You feel less rallied."));
			mob.recoverMaxState();
			if(mob.curState().getHitPoints()>mob.maxState().getHitPoints())
				mob.curState().setHitPoints(mob.maxState().getHitPoints());
		}
	}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		final boolean success=proficiencyCheck(mob,0,auto);
		if(success)
		{
			final CMMsg msg=CMClass.getMsg(mob,null,this,CMMsg.MSG_SPEAK,auto?"":L("^S<S-NAME> scream(s) a mighty RALLYING CRY!!^?"));
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				final Set<MOB> h=properTargets(mob,givenTarget,auto);
				if(h==null) return false;
				for (final Object element : h)
				{
					final MOB target=(MOB)element;
					target.location().show(target,null,CMMsg.MSG_OK_VISUAL,L("<S-NAME> seem(s) rallied!"));
					timesTicking=0;
					hpUp=mob.phyStats().level()+(2*getXLEVELLevel(mob));
					beneficialAffect(mob,target,asLevel,0);
					target.recoverMaxState();
					if(target.fetchEffect(ID())!=null)
						mob.curState().adjHitPoints(hpUp,mob.maxState());
				}
			}
		}
		else
			beneficialWordsFizzle(mob,null,auto?"":L("<S-NAME> mumble(s) a weak rally cry."));

		// return whether it worked
		return success;
	}
}
