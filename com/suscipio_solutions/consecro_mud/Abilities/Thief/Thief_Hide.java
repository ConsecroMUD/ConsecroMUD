package com.suscipio_solutions.consecro_mud.Abilities.Thief;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CharStats;
import com.suscipio_solutions.consecro_mud.Common.interfaces.PhyStats;
import com.suscipio_solutions.consecro_mud.Exits.interfaces.Exit;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;


@SuppressWarnings("rawtypes")
public class Thief_Hide extends ThiefSkill
{
	@Override public String ID() { return "Thief_Hide"; }
	private final static String localizedName = CMLib.lang().L("Hide");
	@Override public String name() { return localizedName; }
	@Override public String displayText(){ return "";}
	@Override protected int canAffectCode(){return CAN_MOBS;}
	@Override protected int canTargetCode(){return 0;}
	@Override public int abstractQuality(){return Ability.QUALITY_OK_SELF;}
	@Override public int classificationCode(){return Ability.ACODE_THIEF_SKILL|Ability.DOMAIN_STEALTHY;}
	private static final String[] triggerStrings =I(new String[] {"HIDE"});
	@Override public String[] triggerStrings(){return triggerStrings;}
	@Override public int usageType(){return USAGE_MOVEMENT|USAGE_MANA;}
	public int code=0;
	private int bonus=0;

	@Override public int abilityCode(){return code;}
	@Override public void setAbilityCode(int newCode){code=newCode;}


	@Override
	public void executeMsg(final Environmental myHost, final CMMsg msg)
	{
		if(!(affected instanceof MOB))
			return;

		final MOB mob=(MOB)affected;

		if(msg.amISource(mob))
		{
			if(((msg.sourceMinor()==CMMsg.TYP_ENTER)
				||(msg.sourceMinor()==CMMsg.TYP_LEAVE)
				||(msg.sourceMinor()==CMMsg.TYP_FLEE)
				||(msg.sourceMinor()==CMMsg.TYP_RECALL))
			&&(!msg.sourceMajor(CMMsg.MASK_ALWAYS))
			&&(msg.sourceMajor()>0))
			{
				unInvoke();
				mob.recoverPhyStats();
			}
			else
			if((abilityCode()==0)
			&&(!msg.sourceMajor(CMMsg.MASK_ALWAYS))
			&&(msg.othersMinor()!=CMMsg.TYP_LOOK)
			&&(msg.othersMinor()!=CMMsg.TYP_EXAMINE)
			&&(msg.othersMajor()>0))
			{
				if(msg.othersMajor(CMMsg.MASK_SOUND))
				{
					unInvoke();
					mob.recoverPhyStats();
				}
				else
				switch(msg.othersMinor())
				{
				case CMMsg.TYP_SPEAK:
				case CMMsg.TYP_CAST_SPELL:
					{
						unInvoke();
						mob.recoverPhyStats();
					}
					break;
				case CMMsg.TYP_OPEN:
				case CMMsg.TYP_CLOSE:
				case CMMsg.TYP_LOCK:
				case CMMsg.TYP_UNLOCK:
				case CMMsg.TYP_PUSH:
				case CMMsg.TYP_PULL:
					if((msg.target()!=null)
					&&((msg.target() instanceof Exit)
						||((msg.target() instanceof Item)
						   &&(!msg.source().isMine(msg.target())))))
					{
						unInvoke();
						mob.recoverPhyStats();
					}
					break;
				}
			}
		}
		return;
	}

	@Override
	public void affectCharStats(MOB affected, CharStats affectableStats)
	{
		super.affectCharStats(affected,affectableStats);
		affectableStats.setStat(CharStats.STAT_SAVE_DETECTION,proficiency()+bonus+affectableStats.getStat(CharStats.STAT_SAVE_DETECTION));
	}
	@Override
	public void affectPhyStats(Physical affected, PhyStats affectableStats)
	{
		super.affectPhyStats(affected,affectableStats);
		affectableStats.setDisposition(affectableStats.disposition()|PhyStats.IS_HIDDEN);
		if(CMLib.flags().isSneaking(affected))
			affectableStats.setDisposition(affectableStats.disposition()-PhyStats.IS_SNEAKING);
	}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		final Room room=mob.location();
		if((mob.fetchEffect(this.ID())!=null)||(room==null))
		{
			mob.tell(L("You are already hiding."));
			return false;
		}

		if(mob.isInCombat())
		{
			mob.tell(L("Not while in combat!"));
			return false;
		}

		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		final MOB highestMOB=getHighestLevelMOB(mob,null);
		final int levelDiff=(mob.phyStats().level()+(2*super.getXLEVELLevel(mob)))-getMOBLevel(highestMOB);

		final String str=L("You creep into a shadow and remain completely still.");

		boolean success=(highestMOB==null)||proficiencyCheck(mob,levelDiff*10,auto);

		if(!success)
		{
			if(highestMOB!=null)
				beneficialVisualFizzle(mob,highestMOB,L("<S-NAME> attempt(s) to hide from <T-NAMESELF> and fail(s)."));
			else
				beneficialVisualFizzle(mob,null,L("<S-NAME> attempt(s) to hide and fail(s)."));
		}
		else
		{
			final CMMsg msg=CMClass.getMsg(mob,null,this,auto?CMMsg.MSG_OK_ACTION:(CMMsg.MSG_DELICATE_HANDS_ACT|CMMsg.MASK_MOVE),str,CMMsg.NO_EFFECT,null,CMMsg.NO_EFFECT,null);
			if(room.okMessage(mob,msg))
			{
				room.send(mob,msg);
				invoker=mob;
				final Ability newOne=(Ability)this.copyOf();
				((Thief_Hide)newOne).bonus=getXLEVELLevel(mob)*2;
				if(mob.fetchEffect(newOne.ID())==null)
					mob.addEffect(newOne);
				mob.recoverPhyStats();
			}
			else
				success=false;
		}
		return success;
	}
}
