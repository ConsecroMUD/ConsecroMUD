package com.suscipio_solutions.consecro_mud.Abilities.Fighter;
import java.util.Set;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.PhyStats;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;
import com.suscipio_solutions.consecro_mud.core.interfaces.Tickable;



@SuppressWarnings("rawtypes")
public class Fighter_Battlecry extends FighterSkill
{
	@Override public String ID() { return "Fighter_Battlecry"; }
	private final static String localizedName = CMLib.lang().L("Battle Cry");
	@Override public String name() { return localizedName; }
	private final static String localizedStaticDisplay = CMLib.lang().L("(Battle Cry)");
	@Override public String displayText() { return localizedStaticDisplay; }
	@Override public int abstractQuality(){ return Ability.QUALITY_BENEFICIAL_OTHERS;}
	private static final String[] triggerStrings =I(new String[] {"BATTLECRY"});
	@Override public String[] triggerStrings(){return triggerStrings;}
	@Override protected int canAffectCode(){return 0;}
	@Override protected int canTargetCode(){return Ability.CAN_MOBS;}
	@Override public int classificationCode(){return Ability.ACODE_SKILL|Ability.DOMAIN_SINGING;}

	protected int timesTicking=0;

	@Override
	public void affectPhyStats(Physical affected, PhyStats affectableStats)
	{
		super.affectPhyStats(affected,affectableStats);
		if(invoker==null) return;
		affectableStats.setAttackAdjustment(affectableStats.attackAdjustment()+1+(int)Math.round(CMath.div(affectableStats.attackAdjustment(),6.0-(getXLEVELLevel(invoker())/3))));
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
			mob.tell(L("You calm down a bit."));
	}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		final boolean success=proficiencyCheck(mob,0,auto);
		if(success)
		{
			final CMMsg msg=CMClass.getMsg(mob,null,this,CMMsg.MSG_SPEAK,auto?"":L("^S<S-NAME> scream(s) a mighty BATTLE CRY!!^?"));
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				final Set<MOB> h=properTargets(mob,givenTarget,auto);
				if(h==null) return false;
				for (final Object element : h)
				{
					final MOB target=(MOB)element;
					target.location().show(target,null,CMMsg.MSG_OK_VISUAL,L("<S-NAME> get(s) excited!"));
					timesTicking=0;
					beneficialAffect(mob,target,asLevel,0);
				}
			}
		}
		else
			beneficialWordsFizzle(mob,null,auto?"":L("<S-NAME> mumble(s) a weak battle cry."));

		// return whether it worked
		return success;
	}
}
