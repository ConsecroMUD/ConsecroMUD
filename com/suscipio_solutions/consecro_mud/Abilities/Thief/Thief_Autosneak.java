package com.suscipio_solutions.consecro_mud.Abilities.Thief;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Exits.interfaces.Exit;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_mud.core.Directions;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;


@SuppressWarnings("rawtypes")
public class Thief_Autosneak extends ThiefSkill
{
	@Override public String ID() { return "Thief_Autosneak"; }
	@Override public String displayText() {return "(AutoSneak)";}
	private final static String localizedName = CMLib.lang().L("AutoSneak");
	@Override public String name() { return localizedName; }
	@Override protected int canAffectCode(){return CAN_MOBS;}
	@Override protected int canTargetCode(){return 0;}
	@Override public int abstractQuality(){return Ability.QUALITY_OK_SELF;}
	private static final String[] triggerStrings =I(new String[] {"AUTOSNEAK"});
	@Override public String[] triggerStrings(){return triggerStrings;}
	@Override public int classificationCode() {   return Ability.ACODE_SKILL|Ability.DOMAIN_STEALTHY; }
	protected boolean noRepeat=false;

	@Override
	public boolean okMessage(final Environmental myHost, final CMMsg msg)
	{
		if(!super.okMessage(myHost,msg))
			return false;

		if((affected instanceof MOB)
		&&(!noRepeat)
		&&(msg.targetMinor()==CMMsg.TYP_LEAVE)
		&&(msg.source()==affected)
		&&(msg.target() instanceof Room)
		&&(msg.tool() instanceof Exit)
		&&(((MOB)affected).location()!=null))
		{
			int dir=-1;
			final MOB mob=(MOB)affected;
			for(int d=Directions.NUM_DIRECTIONS()-1;d>=0;d--)
				if((mob.location().getRoomInDir(d)==msg.target())
				||(mob.location().getReverseExit(d)==msg.tool())
				||(mob.location().getExitInDir(d)==msg.tool()))
				{ dir=d; break;}
			if(dir>=0)
			{
				Ability A=mob.fetchAbility("Thief_Sneak");
				if(A==null) A=mob.fetchAbility("Ranger_Sneak");
				if(A!=null)
				{
					noRepeat=true;
					if(A.invoke(mob,CMParms.parse(Directions.getDirectionName(dir)),null,false,0))
					{
						final int[] usage=A.usageCost(mob,false);
						if(CMath.bset(A.usageType(),Ability.USAGE_HITPOINTS)&&(usage[USAGEINDEX_HITPOINTS]>0))
							mob.curState().adjHitPoints(usage[USAGEINDEX_HITPOINTS]/2,mob.maxState());
						if(CMath.bset(A.usageType(),Ability.USAGE_MANA)&&(usage[USAGEINDEX_MANA]>0))
							mob.curState().adjMana(usage[USAGEINDEX_MANA]/2,mob.maxState());
						if(CMath.bset(A.usageType(),Ability.USAGE_MOVEMENT)&&(usage[USAGEINDEX_MOVEMENT]>0))
							mob.curState().adjMovement(usage[USAGEINDEX_MOVEMENT]/2,mob.maxState());
					}
					if(CMLib.dice().rollPercentage()<10)
						helpProficiency(mob, 0);
					noRepeat=false;
				}
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		if((mob.fetchEffect(ID())!=null))
		{
			mob.tell(L("You are no longer automatically sneaking around."));
			mob.delEffect(mob.fetchEffect(ID()));
			return false;
		}
		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		final boolean success=proficiencyCheck(mob,0,auto);

		if(success)
		{
			mob.tell(L("You will now automatically sneak around while you move."));
			beneficialAffect(mob,mob,asLevel,adjustedLevel(mob,asLevel));
			final Ability A=mob.fetchEffect(ID());
			if(A!=null) A.makeLongLasting();
		}
		else
			beneficialVisualFizzle(mob,null,L("<S-NAME> attempt(s) to get into <S-HIS-HER> sneaking stance, but fail(s)."));
		return success;
	}

}
