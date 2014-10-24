package com.suscipio_solutions.consecro_mud.Abilities.Thief;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.PhyStats;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.Directions;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;


@SuppressWarnings("rawtypes")
public class Thief_Sneak extends ThiefSkill
{
	@Override public String ID() { return "Thief_Sneak"; }
	private final static String localizedName = CMLib.lang().L("Sneak");
	@Override public String name() { return localizedName; }
	@Override protected int canAffectCode(){return 0;}
	@Override protected int canTargetCode(){return 0;}
	@Override public int abstractQuality(){return Ability.QUALITY_INDIFFERENT;}
	@Override public int classificationCode(){return Ability.ACODE_THIEF_SKILL|Ability.DOMAIN_STEALTHY;}
	private static final String[] triggerStrings =I(new String[] {"SNEAK"});
	@Override public String[] triggerStrings(){return triggerStrings;}
	@Override public int usageType(){return USAGE_MOVEMENT;}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		String dir=CMParms.combine(commands,0);
		if(commands.size()>0) dir=(String)commands.lastElement();
		final int dirCode=Directions.getGoodDirectionCode(dir);
		if(dirCode<0)
		{
			mob.tell(L("Sneak where?"));
			return false;
		}

		if((mob.location().getRoomInDir(dirCode)==null)||(mob.location().getExitInDir(dirCode)==null))
		{
			mob.tell(L("Sneak where?"));
			return false;
		}

		final MOB highestMOB=getHighestLevelMOB(mob,null);
		int levelDiff=(mob.phyStats().level()+(super.getXLEVELLevel(mob)*2))-getMOBLevel(highestMOB);

		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		boolean success=false;
		final CMMsg msg=CMClass.getMsg(mob,null,this,auto?CMMsg.MSG_OK_VISUAL:CMMsg.MSG_DELICATE_HANDS_ACT,L("You quietly sneak @x1.",Directions.getDirectionName(dirCode)),CMMsg.NO_EFFECT,null,CMMsg.NO_EFFECT,null);
		if(mob.location().okMessage(mob,msg))
		{
			mob.location().send(mob,msg);
			if(levelDiff<0)
				levelDiff=levelDiff*8;
			else
				levelDiff=levelDiff*10;
			success=proficiencyCheck(mob,levelDiff,auto);
			if(success)
			{
				mob.basePhyStats().setDisposition(mob.basePhyStats().disposition()|PhyStats.IS_SNEAKING);
				mob.recoverPhyStats();
			}
			CMLib.tracking().walk(mob,dirCode,false,false);
			if(success)
			{

				final int disposition=mob.basePhyStats().disposition();
				if((disposition&PhyStats.IS_SNEAKING)>0)
				{
					mob.basePhyStats().setDisposition(disposition-PhyStats.IS_SNEAKING);
					mob.recoverPhyStats();
				}
				Ability toHide=mob.fetchAbility("Thief_Hide");
				if(toHide==null) toHide=mob.fetchAbility("Ranger_Hide");
				if(toHide!=null)
					toHide.invoke(mob,new Vector(),null,false,asLevel);
			}
			if(CMLib.flags().isSneaking(mob))
				mob.phyStats().setDisposition(mob.phyStats().disposition()-PhyStats.IS_SNEAKING);
		}
		return success;
	}

}
