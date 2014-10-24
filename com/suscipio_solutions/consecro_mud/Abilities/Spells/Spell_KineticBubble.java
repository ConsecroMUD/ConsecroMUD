package com.suscipio_solutions.consecro_mud.Abilities.Spells;
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
public class Spell_KineticBubble extends Spell
{
	@Override public String ID() { return "Spell_KineticBubble"; }
	private final static String localizedName = CMLib.lang().L("Kinetic Bubble");
	@Override public String name() { return localizedName; }
	private final static String localizedStaticDisplay = CMLib.lang().L("(Kinetic Bubble)");
	@Override public String displayText() { return localizedStaticDisplay; }
	@Override public int abstractQuality(){ return Ability.QUALITY_BENEFICIAL_SELF;}
	@Override protected int canAffectCode(){return CAN_MOBS;}
	@Override public int classificationCode(){return Ability.ACODE_SPELL|Ability.DOMAIN_ABJURATION;}
	protected int kickBack=0;

	@Override
	public void unInvoke()
	{
		// undo the affects of this spell
		if(!(affected instanceof MOB))
			return;
		final MOB mob=(MOB)affected;
		if(canBeUninvoked())
			if((mob.location()!=null)&&(!mob.amDead()))
				mob.location().show(mob,null,CMMsg.MSG_OK_VISUAL,L("<S-YOUPOSS> Kinetic Bubble pops."));

		super.unInvoke();
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
			if((msg.targetMinor()==CMMsg.TYP_DAMAGE)
			&&(mob.rangeToTarget()==0)
			&&(msg.source()!=mob)
			&&(msg.tool()!=null)
			&&(msg.tool() instanceof Weapon))
			{
				final CMMsg msg2=CMClass.getMsg(mob,source,this,verbalCastCode(mob,source,true),null);
				if(source.location().okMessage(mob,msg2))
				{
					source.location().send(mob,msg2);
					if(invoker==null) invoker=source;
					if((msg2.value()<=0)&&(msg.value()>3))
					{
						final int damage = CMLib.dice().roll( 1, (getXLEVELLevel(mob) + msg.value()) / 3 , 0 );
						CMLib.combat().postDamage(mob,source,this,damage,CMMsg.MASK_ALWAYS|CMMsg.TYP_CAST_SPELL,Weapon.TYPE_BURSTING,"The bubble around <S-NAME> <DAMAGES> <T-NAME>!");
					}
				}
			}

		}
		return;
	}

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
			final CMMsg msg=CMClass.getMsg(mob,target,this,somanticCastCode(mob,target,auto),auto?L("<T-NAME> <T-IS-ARE> surrounded by a Kinetic Bubble!"):L("^S<S-NAME> invoke(s) a Kinetic Bubble around <T-NAMESELF>!^?"));
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				kickBack=0;
				beneficialAffect(mob,target,asLevel,0);
			}
		}
		else
			beneficialWordsFizzle(mob,target,L("<S-NAME> attempt(s) to invoke a Kinetic Bubble, but fail(s)."));

		return success;
	}
}
