package com.suscipio_solutions.consecro_mud.Abilities.Spells;
import java.util.Enumeration;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.StdAbility;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.CharClasses.interfaces.CharClass;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMSecurity;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;


@SuppressWarnings("rawtypes")
public class Spell extends StdAbility
{
	@Override public String ID() { return "Spell"; }
	private final static String localizedName = CMLib.lang().L("a Spell");
	@Override public String name() { return localizedName; }
	@Override public String displayText(){ return "";}
	@Override protected int canAffectCode(){return 0;}
	@Override protected int canTargetCode(){return CAN_MOBS;}
	@Override public int abstractQuality(){ return Ability.QUALITY_INDIFFERENT;}
	private static final String[] triggerStrings =I(new String[] {"CAST","CA","C"});
	@Override public String[] triggerStrings(){return triggerStrings;}
	@Override public int classificationCode(){return Ability.ACODE_SPELL;}

	protected static final int CHAIN_LENGTH=4;

	@Override
	public Ability maliciousAffect(MOB mob,
								   Physical target,
								   int asLevel,
								   int tickAdjustmentFromStandard,
								   int additionAffectCheckCode)
	{
		final Ability doneA=super.maliciousAffect(mob,target,asLevel,tickAdjustmentFromStandard,additionAffectCheckCode);
		if((doneA!=null)
		&&(target!=null)
		&&(target instanceof MOB)
		&&(mob!=target)
		&&(!((MOB)target).isMonster())
		&&(CMLib.dice().rollPercentage()==1)
		&&(((MOB)target).charStats().getCurrentClass().baseClass().equals("Mage")))
		{
			final MOB tmob=(MOB)target;
			int num=0;
			for(final Enumeration<Ability> a=tmob.effects();a.hasMoreElements();)
			{
				final Ability A=a.nextElement();
				if((A!=null)
				&&(A instanceof Spell)
				&&(A.abstractQuality()==Ability.QUALITY_MALICIOUS))
				{
					num++;
					if((num>5)&&(!CMSecurity.isDisabled(CMSecurity.DisFlag.AUTODISEASE)))
					{
						final Ability A2=CMClass.getAbility("Disease_Magepox");
						if((A2!=null)&&(target.fetchEffect(A2.ID())==null))
							A2.invoke(mob,target,true,asLevel);
						break;
					}
				}
			}
		}
		return doneA;
	}
	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;
		if((!auto)&&(mob.isMine(this))&&(mob.location()!=null))
		{
			if((!mob.isMonster())
			&&(!disregardsArmorCheck(mob))
			&&(!CMLib.utensils().armorCheck(mob,CharClass.ARMOR_CLOTH))
			&&(CMLib.dice().rollPercentage()<50))
			{
				mob.location().show(mob,null,CMMsg.MSG_OK_VISUAL,L("<S-NAME> watch(es) <S-HIS-HER> armor absorb <S-HIS-HER> magical energy!"));
				return false;
			}
			if(!CMLib.flags().canConcentrate(mob))
			{
				mob.location().show(mob,null,CMMsg.MSG_OK_VISUAL,L("<S-NAME> can't seem to concentrate."));
				return false;
			}
		}
		return true;
	}
}
