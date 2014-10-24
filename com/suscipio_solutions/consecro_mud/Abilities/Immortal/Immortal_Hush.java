package com.suscipio_solutions.consecro_mud.Abilities.Immortal;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.Log;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;


@SuppressWarnings("rawtypes")
public class Immortal_Hush extends Immortal_Skill
{
	boolean doneTicking=false;
	@Override public String ID() { return "Immortal_Hush"; }
	private final static String localizedName = CMLib.lang().L("Hush");
	@Override public String name() { return localizedName; }
	private final static String localizedStaticDisplay = CMLib.lang().L("(Hushed)");
	@Override public String displayText() { return localizedStaticDisplay; }
	@Override protected int canAffectCode(){return CAN_MOBS;}
	@Override protected int canTargetCode(){return CAN_MOBS;}
	@Override public int abstractQuality(){return Ability.QUALITY_MALICIOUS;}
	private static final String[] triggerStrings =I(new String[] {"HUSH"});
	@Override public String[] triggerStrings(){return triggerStrings;}
	@Override public int classificationCode(){return Ability.ACODE_SKILL|Ability.DOMAIN_IMMORTAL;}
	@Override public int maxRange(){return adjustedMaxInvokerRange(1);}
	@Override public int usageType(){return USAGE_MOVEMENT;}

	@Override
	public boolean okMessage(final Environmental myHost, final CMMsg msg)
	{
		if(!super.okMessage(myHost,msg))
			return false;


		if(((msg.sourceMinor()==CMMsg.TYP_TELL)
			||(msg.othersMajor(CMMsg.MASK_CHANNEL)))
		&&((msg.source()==affected)
			||((msg.source().location()==CMLib.map().roomLocation(affected))
				&&(msg.source().isMonster())
				&&(msg.source().willFollowOrdersOf((MOB)affected)))))
		{
			msg.source().tell(L("Your message drifts into oblivion."));
			return false;
		}
		return true;
	}

	@Override
	public void unInvoke()
	{
		if(!(affected instanceof MOB))
			return;
		final MOB mob=(MOB)affected;

		super.unInvoke();

		if(canBeUninvoked())
			mob.tell(L("You are no longer hushed!"));
	}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		final MOB target=getTargetAnywhere(mob,commands,givenTarget,false,true,false);
		if(target==null) return false;

		final Ability A=target.fetchEffect(ID());
		if(A!=null)
		{
			A.unInvoke();
			mob.tell(L("@x1 is released from his hushing.",target.Name()));
			return true;
		}

		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		final boolean success=proficiencyCheck(mob,0,auto);

		if(success)
		{
			final CMMsg msg=CMClass.getMsg(mob,target,this,CMMsg.MASK_MOVE|CMMsg.TYP_JUSTICE|(auto?CMMsg.MASK_ALWAYS:0),auto?L("Silence falls upon <T-NAME>!"):L("^F<S-NAME> hush(es) <T-NAMESELF>.^?"));
			CMLib.color().fixSourceFightColor(msg);
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				mob.location().show(target,null,CMMsg.MSG_OK_VISUAL,L("<S-NAME> <S-IS-ARE> hushed!"));
				beneficialAffect(mob,target,asLevel,Ability.TICKS_ALMOST_FOREVER);
				Log.sysOut("Banish",mob.Name()+" hushed "+target.name()+".");
			}
		}
		else
			return beneficialVisualFizzle(mob,target,L("<S-NAME> attempt(s) to hush <T-NAMESELF>, but fail(s)."));
		return success;
	}
}
