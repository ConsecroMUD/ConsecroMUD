package com.suscipio_solutions.consecro_mud.Abilities.Common;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;



@SuppressWarnings({"unchecked","rawtypes"})
public class SlaveTrading extends CommonSkill
{
	@Override public String ID() { return "SlaveTrading"; }
	private final static String localizedName = CMLib.lang().L("Slave Trading");
	@Override public String name() { return localizedName; }
	private static final String[] triggerStrings =I(new String[] {"SLAVETRADING","SLAVETRADE","SLAVESELL","SSELL"});
	@Override public String[] triggerStrings(){return triggerStrings;}
	@Override protected int canAffectCode(){return 0;}
	@Override protected int canTargetCode(){return Ability.CAN_MOBS;}
	@Override public int classificationCode() {   return Ability.ACODE_COMMON_SKILL|Ability.DOMAIN_CRIMINAL; }

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		commands.insertElementAt("SELL",0);
		final Environmental shopkeeper=CMLib.english().parseShopkeeper(mob,commands,"Sell whom to whom?");
		if(shopkeeper==null) return false;
		if(commands.size()==0)
		{
			commonTell(mob,L("Sell whom?"));
			return false;
		}

		final String str=CMParms.combine(commands,0);
		final MOB M=mob.location().fetchInhabitant(str);
		if(M!=null)
		{
			if(!CMLib.flags().canBeSeenBy(M,mob))
			{
				commonTell(mob,L("You don't see anyone called '@x1' here.",str));
				return false;
			}
			if(!M.isMonster())
			{
				commonTell(mob,M,null,L("You can't sell <T-NAME> as a slave."));
				return false;
			}
			if(CMLib.flags().isAnimalIntelligence(M))
			{
				commonTell(mob,M,null,L("You can't sell <T-NAME> as a slave.  Animals are not slaves."));
				return false;
			}
			if((M.fetchEffect("Skill_Enslave")==null)||(!M.fetchEffect("Skill_Enslave").text().equals(mob.Name())))
			{
				commonTell(mob,M,null,L("<T-NAME> do(es)n't seem to be your slave."));
				return false;
			}
		}

		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;
		if(proficiencyCheck(mob,0,auto))
		{
			final CMMsg msg=CMClass.getMsg(mob,shopkeeper,M,CMMsg.MSG_SELL,L("<S-NAME> sell(s) <O-NAME> to <T-NAME>."));
			if(mob.location().okMessage(mob,msg))
				mob.location().send(mob,msg);
		}
		else
			beneficialWordsFizzle(mob,shopkeeper,L("<S-NAME> <S-IS-ARE>n't able to strike a deal with <T-NAME>."));
		return true;
	}
}
