package com.suscipio_solutions.consecro_mud.Abilities.Spells;
import java.util.Random;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Weapon;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;


@SuppressWarnings("rawtypes")
public class Spell_Siphon extends Spell
{
	private static Random randomizer = null;
	public Spell_Siphon()
	{
		super();
		if(randomizer==null)
		   randomizer = new Random(System.currentTimeMillis());
	}
	@Override public String ID() { return "Spell_Siphon"; }
	private final static String localizedName = CMLib.lang().L("Siphon");
	@Override public String name() { return localizedName; }
	private final static String localizedStaticDisplay = CMLib.lang().L("(Siphon spell)");
	@Override public String displayText() { return localizedStaticDisplay; }
	@Override public int maxRange(){return adjustedMaxInvokerRange(1);}
	@Override public int abstractQuality(){return Ability.QUALITY_BENEFICIAL_OTHERS;}
	@Override public int classificationCode(){ return Ability.ACODE_SPELL|Ability.DOMAIN_ENCHANTMENT;}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		final MOB target=getTarget(mob,commands,givenTarget);
		if(target==null) return false;

		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		final boolean success=proficiencyCheck(mob,0,auto);
		if(success)
		{
			final CMMsg msg=CMClass.getMsg(mob,target,this,verbalCastCode(mob,target,auto),auto?L("<T-NAME> feel(s) a thirst for the energy of others."):L("^S<S-NAME> invoke(s) an area deprived of energy around <T-NAMESELF>.^?"));
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				beneficialAffect(mob,target,asLevel,0);
			}
		}
		else
			beneficialWordsFizzle(mob,target,L("<S-NAME> attempt(s) to invoke an energy thirst, but fail(s)."));

		return success;
	}

   @Override
public void unInvoke()
	{
		// undo the affects of this spell
		if(!(affected instanceof MOB))
			return;
		final MOB mob=(MOB)affected;
		super.unInvoke();

		mob.tell(L("You no longer feel a thirst for the energy of others."));
	}

	@Override
	public boolean okMessage(final Environmental myHost, final CMMsg msg)
	{
		if(!(affected instanceof MOB))
			return true;

		final MOB mob=(MOB)affected;

		if((msg.amITarget(mob))
		&&(!msg.amISource(mob))
		&&(msg.targetMinor()==CMMsg.TYP_DAMAGE)
		&&((msg.value())>0)
		&&(msg.tool()!=null)
		&&(msg.tool() instanceof Weapon)
		&&(CMLib.dice().rollPercentage()>50)
		&&(msg.source().curState().getMana()>0))
		{
			final MOB sourceM = msg.source();
			final CMMsg msg2=CMClass.getMsg(mob,sourceM,null,CMMsg.MSG_QUIETMOVEMENT,L("<S-NAME> siphon(s) mana from <T-NAME>!"));
			if(mob.location().okMessage(mob,msg2))
			{
				final int maxManaRestore = 3;
				final int curSourceMana = sourceM.curState().getMana();
				int manaDrain = 0;
				if(maxManaRestore <= curSourceMana)
				{
				   manaDrain = maxManaRestore;
				}
				else
				{
				   manaDrain = curSourceMana;
				}
				mob.curState().adjMana(manaDrain, mob.maxState());
				sourceM.curState().adjMana(manaDrain * -1, sourceM.maxState());
				mob.location().send(mob,msg2);
			}
		}
		return super.okMessage(myHost, msg);
	}
}
