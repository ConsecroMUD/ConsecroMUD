package com.suscipio_solutions.consecro_mud.Abilities.Thief;
import java.util.List;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Trap;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CharStats;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.Directions;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;


@SuppressWarnings({"unchecked","rawtypes"})
public class Thief_Lure extends ThiefSkill implements Trap
{
	@Override public String ID() { return "Thief_Lure"; }
	private final static String localizedName = CMLib.lang().L("Lure");
	@Override public String name() { return localizedName; }
	@Override protected int canAffectCode(){return 0;}
	@Override protected int canTargetCode(){return Ability.CAN_MOBS;}
	@Override public int classificationCode(){return Ability.ACODE_THIEF_SKILL|Ability.DOMAIN_DECEPTIVE;}
	@Override public int abstractQuality(){return Ability.QUALITY_INDIFFERENT;}
	private static final String[] triggerStrings =I(new String[] {"LURE"});
	@Override public String[] triggerStrings(){return triggerStrings;}
	@Override public int usageType(){return USAGE_MOVEMENT|USAGE_MANA;}
	public int code=0;

	@Override public int abilityCode(){return code;}
	@Override public void setAbilityCode(int newCode){code=newCode;}

	@Override public boolean isABomb(){return false;}
	@Override public void activateBomb(){}
	@Override public boolean disabled(){return false;}
	@Override public boolean sprung(){return false;}
	@Override public void disable(){ unInvoke();}
	@Override public void setReset(int Reset){}
	@Override public int getReset(){return 0;}
	@Override public void spring(MOB M){}
	@Override public boolean maySetTrap(MOB mob, int asLevel){return false;}
	@Override public boolean canSetTrapOn(MOB mob, Physical P){return false;}
	@Override public List<Item> getTrapComponents() { return new Vector(); }
	@Override public String requiresToSet(){return "";}
	@Override
	public Trap setTrap(MOB mob, Physical P, int trapBonus, int qualifyingClassLevel, boolean perm)
	{return null;}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		if(commands.size()<2)
		{
			mob.tell(L("Lure whom which direction?"));
			return false;
		}
		if(mob.isInCombat())
		{
			mob.tell(L("Not while you are fighting!"));
			return false;
		}
		String str=(String)commands.lastElement();
		commands.removeElementAt(commands.size()-1);
		final int dirCode=Directions.getGoodDirectionCode(str);
		if((dirCode<0)||(mob.location()==null)||(mob.location().getRoomInDir(dirCode)==null)||(mob.location().getExitInDir(dirCode)==null))
		{
			mob.tell(L("'@x1' is not a valid direction.",str));
			return false;
		}
		final String direction=Directions.getInDirectionName(dirCode);

		final MOB target=this.getTarget(mob,commands,givenTarget);
		if(target==null) return false;

		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		final int levelDiff=target.phyStats().level()-(mob.phyStats().level()+abilityCode()+(getXLEVELLevel(mob)*2));

		boolean success=proficiencyCheck(mob,-(levelDiff*(!CMLib.flags().canBeSeenBy(mob,target)?5:10)),auto);
		success=success&&(CMLib.dice().rollPercentage()+(getXLEVELLevel(mob)*3)>target.charStats().getSave(CharStats.STAT_SAVE_TRAPS));
		success=success&&(CMLib.dice().rollPercentage()+(getXLEVELLevel(mob)*3)>target.charStats().getSave(CharStats.STAT_SAVE_MIND));

		str=L("<S-NAME> attempt(s) to lure <T-NAME> @x1.",direction);
		final CMMsg msg=CMClass.getMsg(mob,target,this,(auto?CMMsg.MASK_ALWAYS:0)|CMMsg.MSG_SPEAK,str);
		if(mob.location().okMessage(mob,msg))
		{
			mob.location().send(mob,msg);
			if((success)&&(CMLib.tracking().walk(mob,dirCode,false,false))&&(CMLib.flags().canBeHeardSpeakingBy(target,mob)))
				CMLib.tracking().walk(target,dirCode,false,false);
		}
		return success;
	}

}
