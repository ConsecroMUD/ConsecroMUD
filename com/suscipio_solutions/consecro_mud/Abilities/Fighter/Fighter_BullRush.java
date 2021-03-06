package com.suscipio_solutions.consecro_mud.Abilities.Fighter;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.Directions;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;



@SuppressWarnings("rawtypes")
public class Fighter_BullRush extends FighterSkill
{
	@Override public String ID() { return "Fighter_BullRush"; }
	private final static String localizedName = CMLib.lang().L("Bullrush");
	@Override public String name() { return localizedName; }
	@Override public int minRange(){return 0;}
	@Override public int maxRange(){return adjustedMaxInvokerRange(1);}
	@Override protected int canAffectCode(){return 0;}
	@Override protected int canTargetCode(){return Ability.CAN_MOBS;}
	@Override public int abstractQuality(){return Ability.QUALITY_INDIFFERENT;}
	private static final String[] triggerStrings =I(new String[] {"BULLRUSH"});
	@Override public String[] triggerStrings(){return triggerStrings;}
	@Override public long flags(){return Ability.FLAG_MOVING;}
	@Override public int usageType(){return USAGE_MOVEMENT;}
	@Override public int classificationCode(){ return Ability.ACODE_SKILL|Ability.DOMAIN_ACROBATIC;}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		if(commands.size()<2)
		{
			mob.tell(L("Bullrush whom which direction?"));
			return false;
		}
		if(!mob.isInCombat())
		{
			mob.tell(L("You can only do this in the rage of combat!"));
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

		final int levelDiff=target.phyStats().level()-(((2*getXLEVELLevel(mob))+mob.phyStats().level()));

		final boolean success=proficiencyCheck(mob,-(levelDiff*5),auto);

		str=L("^F^<FIGHT^><S-NAME> bullrush(es) <T-NAME> @x1.^</FIGHT^>^?",direction);
		final CMMsg msg=CMClass.getMsg(mob,target,this,(auto?CMMsg.MASK_ALWAYS:0)|CMMsg.MASK_MOVE|CMMsg.MASK_SOUND|CMMsg.MASK_HANDS|CMMsg.TYP_JUSTICE,str);
		CMLib.color().fixSourceFightColor(msg);
		if(mob.location().okMessage(mob,msg))
		{
			mob.location().send(mob,msg);
			final MOB M1=mob.getVictim();
			final MOB M2=target.getVictim();
			mob.makePeace();
			target.makePeace();
			if((success)&&(CMLib.tracking().walk(mob,dirCode,false,false))&&(CMLib.flags().canBeHeardMovingBy(target,mob)))
			{
				CMLib.tracking().walk(target,dirCode,false,false);
				mob.setVictim(M1);
				target.setVictim(M2);
			}
		}
		return success;
	}

}
