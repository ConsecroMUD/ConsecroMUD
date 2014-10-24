package com.suscipio_solutions.consecro_mud.Abilities.Songs;
import java.util.Set;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;


@SuppressWarnings("rawtypes")
public class Skill_Conduct extends BardSkill
{
	@Override public String ID() { return "Skill_Conduct"; }
	private final static String localizedName = CMLib.lang().L("Conduct Symphony");
	@Override public String name() { return localizedName; }
	private final static String localizedStaticDisplay = CMLib.lang().L("(Conduct Symphony)");
	@Override public String displayText() { return localizedStaticDisplay; }
	@Override protected int canAffectCode(){return CAN_MOBS;}
	@Override protected int canTargetCode(){return CAN_MOBS;}
	private static final String[] triggerStrings =I(new String[] {"CONDUCT"});
	@Override public String[] triggerStrings(){return triggerStrings;}
	@Override public int classificationCode(){ return Ability.ACODE_SKILL|Ability.DOMAIN_PLAYING;}
	@Override public int maxRange(){return adjustedMaxInvokerRange(2);}


	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		Ability SYMPHONY=mob.fetchAbility("Play_Symphony");
		if((!auto)&&(SYMPHONY==null))
		{
			mob.tell(L("But you don't know how to play a symphony."));
			return false;
		}
		if(SYMPHONY==null)
		{
			SYMPHONY=CMClass.getAbility("Play_Symphony");
			SYMPHONY.setProficiency(100);
		}
		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		if((!auto)&&(!CMLib.flags().aliveAwakeMobileUnbound(mob,false)))
			return false;

		final boolean success=proficiencyCheck(mob,0,auto);
		new Play().unplayAll(mob,mob);
		if(success)
		{
			String str=auto?L("^SSymphonic Conduction Begins!^?"):L("^S<S-NAME> begin(s) to wave <S-HIS-HER> arms in a mystical way!^?");
			if((!auto)&&(mob.fetchEffect(this.ID())!=null))
				str=L("^S<S-NAME> start(s) conducting the symphony over again.^?");

			final CMMsg msg=CMClass.getMsg(mob,null,this,(auto?CMMsg.MASK_ALWAYS:0)|CMMsg.MSG_CAST_SOMANTIC_SPELL,str);
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				invoker=mob;

				final Set<MOB> h=properTargets(mob,givenTarget,auto);
				if(h==null) return false;
				if(!h.contains(mob)) h.add(mob);

				for (final Object element : h)
				{
					final MOB follower=(MOB)element;

					// malicious songs must not affect the invoker!
					int affectType=CMMsg.MSG_CAST_SOMANTIC_SPELL;
					if(auto) affectType=affectType|CMMsg.MASK_ALWAYS;
					if(CMLib.flags().canBeSeenBy(invoker,follower))
					{
						final CMMsg msg2=CMClass.getMsg(mob,follower,this,affectType,null);
						if(mob.location().okMessage(mob,msg2))
						{
							follower.location().send(follower,msg2);
							if(msg2.value()<=0)
								SYMPHONY.invoke(follower,new Vector(),null,false,asLevel+(3*getXLEVELLevel(mob)));
						}
					}
				}
				mob.location().recoverRoomStats();
			}
		}
		else
			mob.location().show(mob,null,CMMsg.MSG_NOISE,L("<S-NAME> wave(s) <S-HIS-HER> arms around, looking silly."));

		return success;
	}
}
