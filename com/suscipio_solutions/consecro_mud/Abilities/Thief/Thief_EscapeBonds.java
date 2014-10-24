package com.suscipio_solutions.consecro_mud.Abilities.Thief;
import java.util.List;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CharStats;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;
import com.suscipio_solutions.consecro_mud.core.interfaces.Tickable;



@SuppressWarnings("rawtypes")
public class Thief_EscapeBonds extends ThiefSkill
{
	@Override public String ID() { return "Thief_EscapeBonds"; }
	private final static String localizedName = CMLib.lang().L("Escape Bonds");
	@Override public String name() { return localizedName; }
	private final static String localizedStaticDisplay = CMLib.lang().L("(Slipping from your bonds)");
	@Override public String displayText() { return localizedStaticDisplay; }
	@Override protected int canAffectCode(){return CAN_MOBS;}
	@Override protected int canTargetCode(){return 0;}
	@Override public int classificationCode(){return Ability.ACODE_THIEF_SKILL|Ability.DOMAIN_BINDING;}
	@Override public int abstractQuality(){return Ability.QUALITY_INDIFFERENT;}
	private static final String[] triggerStrings =I(new String[] {"ESCAPEBONDS","ESCAPE"});
	@Override public String[] triggerStrings(){return triggerStrings;}


	@Override
	public boolean tick(Tickable ticking, int tickID)
	{
		if((affected!=null)&&(affected instanceof MOB))
		{
			final MOB mob=(MOB)affected;
			if((!CMLib.flags().aliveAwakeMobile(mob,true))
			||(!CMLib.flags().isBound(mob)))
			{ unInvoke(); return false;}
			final List<Ability> V=CMLib.flags().flaggedAffects(mob,Ability.FLAG_BINDING);
			if(V.size()==0)
			{ unInvoke(); return false;}
			final int newStrength=mob.charStats().getStat(CharStats.STAT_STRENGTH)
						   +getXLEVELLevel(mob)
						   +(mob.charStats().getStat(CharStats.STAT_DEXTERITY)*2);
			final CMMsg msg=CMClass.getMsg(mob,null,null,CMMsg.MSG_HANDS,L("<S-NAME> slip(s) and wiggle(s) in <S-HIS-HER> bonds."));
			for(int v=0;v<V.size();v++)
			{
				mob.charStats().setStat(CharStats.STAT_STRENGTH,newStrength);
				final Ability A=V.get(v);
				if(A.okMessage(mob,msg)) A.executeMsg(mob,msg);
			}
			mob.recoverCharStats();
		}
		return true;
	}

	@Override
	public void unInvoke()
	{
		final MOB M=(MOB)affected;
		super.unInvoke();
		if((M!=null)&&(!M.amDead()))
		{
			if(!CMLib.flags().isBound(M))
				M.tell(L("You slip free of your bonds."));
			else
				M.tell(L("You stop trying to slip free of your bonds."));
		}
	}

	@Override
	public int castingQuality(MOB mob, Physical target)
	{
		if(mob!=null)
		{
			if(mob.fetchEffect(this.ID())!=null)
				return Ability.QUALITY_INDIFFERENT;
			if((!CMLib.flags().aliveAwakeMobile(mob,true))||(!CMLib.flags().isBound(mob)))
				return Ability.QUALITY_INDIFFERENT;
			final List<Ability> V=CMLib.flags().flaggedAffects(mob,Ability.FLAG_BINDING);
			if(V.size()==0)
				return Ability.QUALITY_INDIFFERENT;
			return super.castingQuality(mob, target,Ability.QUALITY_BENEFICIAL_SELF);
		}
		return super.castingQuality(mob,target);
	}


	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		MOB target=mob;
		if((auto)&&(givenTarget!=null)&&(givenTarget instanceof MOB))
			target=(MOB)givenTarget;
		if(target.fetchEffect(this.ID())!=null)
		{
			mob.tell(target,null,null,L("<S-NAME> <S-IS-ARE> already trying to slip free of <S-HIS-HER> bonds."));
			return false;
		}
		if((!CMLib.flags().aliveAwakeMobile(mob,true))||(!CMLib.flags().isBound(mob)))
		{
			mob.tell(target,null,null,L("<T-NAME> <T-IS-ARE> not bound!"));
			return false;
		}
		final List<Ability> V=CMLib.flags().flaggedAffects(mob,Ability.FLAG_BINDING);
		if(V.size()==0)
		{
			mob.tell(target,null,null,L("<T-NAME> <T-IS-ARE> not bound by anything which can be slipped free of."));
			return false;
		}

		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		final boolean success=proficiencyCheck(mob,0,auto);

		final CMMsg msg=CMClass.getMsg(mob,target,this,CMMsg.MASK_ALWAYS|CMMsg.MSG_DELICATE_HANDS_ACT,auto?L("<T-NAME> start(s) slipping from <T-HIS-HER> bonds."):L("<S-NAME> attempt(s) to slip free of <S-HIS-HER> bonds."));
		if(!success)
			return beneficialVisualFizzle(mob,null,L("<S-NAME> attempt(s) to slip free of <S-HIS-HER> bonds, but can't seem to concentrate."));
		else
		if(mob.location().okMessage(mob,msg))
		{
			mob.location().send(mob,msg);
			beneficialAffect(mob,target,asLevel,0);
		}
		return success;
	}
}
