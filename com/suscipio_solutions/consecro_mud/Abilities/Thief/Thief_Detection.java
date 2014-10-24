package com.suscipio_solutions.consecro_mud.Abilities.Thief;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CharStats;
import com.suscipio_solutions.consecro_mud.Common.interfaces.PhyStats;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;
import com.suscipio_solutions.consecro_mud.core.interfaces.Tickable;


@SuppressWarnings("rawtypes")
public class Thief_Detection extends ThiefSkill
{
	@Override public String ID() { return "Thief_Detection"; }
	private final static String localizedName = CMLib.lang().L("Detection");
	@Override public String name() { return localizedName; }
	private final static String localizedStaticDisplay = CMLib.lang().L("(Detecting hidden...)");
	@Override public String displayText() { return localizedStaticDisplay; }
	@Override protected int canAffectCode(){return CAN_MOBS;}
	@Override protected int canTargetCode(){return 0;}
	@Override public int abstractQuality(){return Ability.QUALITY_INDIFFERENT;}
	private static final String[] triggerStrings =I(new String[] {"DETECT","DETECTION"});
	@Override public int classificationCode(){	return Ability.ACODE_THIEF_SKILL|Ability.DOMAIN_ALERT;}
	@Override public String[] triggerStrings(){return triggerStrings;}
	protected Room lastRoom=null;
	private int bonusThisRoom=0;

	@Override
	public void affectCharStats(MOB affected, CharStats affectableStats)
	{
		super.affectCharStats(affected,affectableStats);
		affectableStats.setStat(CharStats.STAT_SAVE_OVERLOOKING,bonusThisRoom+proficiency()+affectableStats.getStat(CharStats.STAT_SAVE_OVERLOOKING));
	}

	@Override
	public void affectPhyStats(Physical affected, PhyStats affectableStats)
	{
		super.affectPhyStats(affected,affectableStats);
		affectableStats.setSensesMask(affectableStats.sensesMask()|PhyStats.CAN_SEE_HIDDEN);
	}

	@Override
	public void unInvoke()
	{
		final MOB M=(MOB)affected;
		super.unInvoke();
		if((M!=null)&&(!M.amDead()))
			M.tell(L("You stop detecting."));
	}

	@Override
	public boolean tick(Tickable ticking, int tickID)
	{
		if((affected!=null)&&(affected instanceof MOB))
		{
			if(!CMLib.flags().aliveAwakeMobile((MOB)affected,true))
			{ unInvoke(); return false;}
			if(((MOB)affected).location()!=lastRoom)
			{
				lastRoom=((MOB)affected).location();
				bonusThisRoom=getXLEVELLevel((MOB)affected)*2;
				((MOB)affected).recoverCharStats();
			}
			else
			if(bonusThisRoom<affected.phyStats().level())
			{
				bonusThisRoom+=5;
				((MOB)affected).recoverCharStats();
			}
		}
		return true;
	}

	@Override
	public int castingQuality(MOB mob, Physical target)
	{
		if(mob!=null)
		{
			if(mob.fetchEffect(this.ID())!=null)
				return Ability.QUALITY_INDIFFERENT;

			final Room R=mob.location();
			if(R!=null)
				for(int r=0;r<R.numInhabitants();r++)
				{
					final MOB M=R.fetchInhabitant(r);
					if((M!=null)&&(M!=mob)&&(CMLib.flags().isHidden(M)))
						return super.castingQuality(mob, target,Ability.QUALITY_BENEFICIAL_SELF);
				}
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
			mob.tell(target,null,null,L("<S-NAME> <S-IS-ARE> already detecting hidden things."));
			return false;
		}

		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		final boolean success=proficiencyCheck(mob,0,auto);

		final CMMsg msg=CMClass.getMsg(mob,target,this,auto?CMMsg.MASK_ALWAYS:CMMsg.MSG_DELICATE_SMALL_HANDS_ACT,CMMsg.MSG_OK_VISUAL,CMMsg.MSG_OK_VISUAL,auto?L("<T-NAME> become(s) very observant."):L("<S-NAME> start(s) examining <S-HIS-HER> surroundings carefully."));
		if(!success)
			return beneficialVisualFizzle(mob,null,auto?"":L("<S-NAME> look(s) around carefully, but become(s) distracted."));
		else
		if(mob.location().okMessage(mob,msg))
		{
			mob.location().send(mob,msg);
			beneficialAffect(mob,target,asLevel,0);
			target.recoverCharStats();
			target.recoverPhyStats();
			target.phyStats().setSensesMask(mob.phyStats().sensesMask()|PhyStats.CAN_SEE_HIDDEN);
			target.phyStats().setSensesMask(mob.phyStats().sensesMask()|PhyStats.CAN_SEE_SNEAKERS);
			CMLib.commands().postLook(target,false);
			target.recoverPhyStats();
		}
		return success;
	}
}
