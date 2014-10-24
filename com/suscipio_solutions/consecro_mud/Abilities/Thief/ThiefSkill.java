package com.suscipio_solutions.consecro_mud.Abilities.Thief;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.StdAbility;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.CharClasses.interfaces.CharClass;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Exits.interfaces.Exit;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Container;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Wearable;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.CMSecurity;
import com.suscipio_solutions.consecro_mud.core.Directions;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;


@SuppressWarnings({"unchecked","rawtypes"})
public class ThiefSkill extends StdAbility
{
	@Override public String ID() { return "ThiefSkill"; }
	private final static String localizedName = CMLib.lang().L("a Thief Skill");
	@Override public String name() { return localizedName; }
	@Override public int abstractQuality(){return Ability.QUALITY_INDIFFERENT;}
	@Override public int enchantQuality(){return Ability.QUALITY_INDIFFERENT;}
	@Override public int classificationCode(){	return Ability.ACODE_THIEF_SKILL;}
	@Override protected int canAffectCode(){return 0;}
	@Override protected int canTargetCode(){return CAN_MOBS;}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		if((!auto)
		&&(!mob.isMonster())
		&&(!disregardsArmorCheck(mob))
		&&(!CMLib.utensils().armorCheck(mob,CharClass.ARMOR_LEATHER))
		&&(mob.isMine(this))
		&&(mob.location()!=null)
		&&(CMLib.dice().rollPercentage()<50))
		{
			mob.location().show(mob,null,CMMsg.MSG_OK_VISUAL,L("<S-NAME> fumble(s) @x1 due to <S-HIS-HER> clumsy armor!",name()));
			return false;
		}
		return true;
	}

	public int getMOBLevel(MOB meMOB)
	{
		if(meMOB==null) return 0;
		return meMOB.phyStats().level();
	}
	public MOB getHighestLevelMOB(MOB meMOB, Vector not)
	{
		if(meMOB==null) return null;
		final Room R=meMOB.location();
		if(R==null) return null;
		int highestLevel=0;
		MOB highestMOB=null;
		final Set<MOB> H=meMOB.getGroupMembers(new HashSet<MOB>());
		if(not!=null) H.addAll(not);
		for(int i=0;i<R.numInhabitants();i++)
		{
			final MOB M=R.fetchInhabitant(i);
			if((M!=null)
			&&(M!=meMOB)
			&&(!CMLib.flags().isSleeping(M))
			&&(!H.contains(M))
			&&(highestLevel<M.phyStats().level())
			&&(!CMSecurity.isASysOp(M)))
			{
				highestLevel=M.phyStats().level();
				highestMOB=M;
			}
		}
		return highestMOB;
	}

	public Physical getOpenable(MOB mob, Room room, Physical givenTarget, Vector commands, int[] dirCode, boolean failOnOpen)
	{
		if((room==null)||(mob==null)) return null;
		final String whatToOpen=CMParms.combine(commands,0);
		Physical unlockThis=null;
		dirCode[0]=Directions.getGoodDirectionCode(whatToOpen);
		if(dirCode[0]>=0)
			unlockThis=room.getExitInDir(dirCode[0]);
		if(unlockThis==null)
			unlockThis=getTarget(mob,room,givenTarget,commands,Wearable.FILTER_ANY);
		else
		if(givenTarget != null)
			unlockThis = givenTarget;

		if(unlockThis instanceof Exit)
		{
			if(((Exit)unlockThis).isOpen()==failOnOpen)
			{
				if(failOnOpen)
					mob.tell(mob,unlockThis,null,L("<T-NAME> is open!"));
				else
					mob.tell(mob,unlockThis,null,L("<T-NAME> is closed!"));
				return null;
			}

		}
		else
		if(unlockThis instanceof Container)
		{
			if(((Container)unlockThis).isOpen()==failOnOpen)
			{
				if(failOnOpen)
					mob.tell(mob,unlockThis,null,L("<T-NAME> is open!"));
				else
					mob.tell(mob,unlockThis,null,L("<T-NAME> is closed!"));
				return null;
			}
		}
		else
		{
			mob.tell(mob,unlockThis,null,L("You can't do that to <T-NAME>."));
			return null;
		}
		return unlockThis;
	}
}
