package com.suscipio_solutions.consecro_mud.Abilities.Fighter;
import java.util.List;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Amputator;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.PhyStats;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.Races.interfaces.Race;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.collections.XVector;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;



@SuppressWarnings({"unchecked","rawtypes"})
public class Fighter_Gouge extends MonkSkill
{
	boolean doneTicking=false;
	@Override public String ID() { return "Fighter_Gouge"; }
	private final static String localizedName = CMLib.lang().L("Gouge");
	@Override public String name() { return localizedName; }
	private final static String localizedStaticDisplay = CMLib.lang().L("(Gouged Eyes)");
	@Override public String displayText() { return localizedStaticDisplay; }
	@Override protected int canAffectCode(){return CAN_MOBS;}
	@Override protected int canTargetCode(){return CAN_MOBS;}
	@Override public int abstractQuality(){return Ability.QUALITY_MALICIOUS;}
	private static final String[] triggerStrings =I(new String[] {"GOUGE"});
	@Override public String[] triggerStrings(){return triggerStrings;}
	@Override public int classificationCode(){return Ability.ACODE_SKILL|Ability.DOMAIN_DIRTYFIGHTING;}
	@Override protected int overrideMana(){return 100;}
	@Override public int usageType(){return USAGE_MOVEMENT;}

	@Override
	public void affectPhyStats(Physical affected, PhyStats affectableStats)
	{
		super.affectPhyStats(affected,affectableStats);
		if(!doneTicking)
			affectableStats.setSensesMask(affectableStats.sensesMask()|PhyStats.CAN_NOT_SEE);
	}

	@Override
	public boolean okMessage(final Environmental myHost, final CMMsg msg)
	{
		if(!(affected instanceof MOB))
			return true;

		final MOB mob=(MOB)affected;

		if((doneTicking)&&(msg.amISource(mob)))
			unInvoke();
		return true;
	}

	@Override
	public void unInvoke()
	{
		if(!(affected instanceof MOB))
			return;
		final MOB mob=(MOB)affected;

		super.unInvoke();

		if(canBeUninvoked())
			mob.tell(L("Your eyes feel better."));
	}

	@Override
	public int castingQuality(MOB mob, Physical target)
	{
		if((mob!=null)&&(target!=null))
		{
			if(mob.isInCombat()&&(mob.rangeToTarget()>0))
				return Ability.QUALITY_INDIFFERENT;
			if(((mob.charStats().getBodyPart(Race.BODY_HAND)<=0))
			||((mob.charStats().getMyRace().bodyMask()[Race.BODY_HAND]<=0)
			   &&(mob.charStats().getBodyPart(Race.BODY_FOOT)<=0)))
				return Ability.QUALITY_INDIFFERENT;
			if((target instanceof MOB)&&(((MOB)target).charStats().getBodyPart(Race.BODY_EYE)<=0))
				return Ability.QUALITY_INDIFFERENT;
			if((target instanceof MOB)&&(!CMLib.flags().canSee((MOB)target)))
				return Ability.QUALITY_INDIFFERENT;
			if(anyWeapons(mob))
				return Ability.QUALITY_INDIFFERENT;
			if(target.fetchEffect(ID())!=null)
				return Ability.QUALITY_INDIFFERENT;
		}
		return super.castingQuality(mob,target);
	}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		final MOB target=this.getTarget(mob,commands,givenTarget);
		if(target==null) return false;

		if((!auto)
		&&((mob.charStats().getBodyPart(Race.BODY_HAND)<=0))
		||((mob.charStats().getMyRace().bodyMask()[Race.BODY_HAND]<=0)
		   &&(mob.charStats().getBodyPart(Race.BODY_FOOT)<=0)))
		{
			mob.tell(L("You need hands to gouge."));
			return false;
		}

		if((!auto)&&(target.charStats().getBodyPart(Race.BODY_EYE)<=0))
		{
			mob.tell(L("@x1 has no eyes!",target.name(mob)));
			return false;
		}

		if((!auto)&&(anyWeapons(mob)))
		{
			mob.tell(L("Your hands must be free to gouge."));
			return false;
		}

		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		final boolean success=proficiencyCheck(mob,0,auto);
		final boolean hit=(auto)||CMLib.combat().rollToHit(mob,target);
		if((success)&&(hit))
		{
			final CMMsg msg=CMClass.getMsg(mob,target,this,CMMsg.MSK_MALICIOUS_MOVE|CMMsg.TYP_JUSTICE|(auto?CMMsg.MASK_ALWAYS:0),auto?"":L("^F^<FIGHT^><S-NAME> gouge(s) at <T-YOUPOSS> eyes!^</FIGHT^>^?"));
			CMLib.color().fixSourceFightColor(msg);
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				mob.location().show(target,null,CMMsg.MSG_OK_VISUAL,L("<S-NAME> <S-IS-ARE> blinded!"));
				maliciousAffect(mob,target,asLevel,5,-1);
				Amputator A=(Amputator)target.fetchEffect("Amputation");
				if(A==null) A=(Amputator)CMClass.getAbility("Amputation");
				final List<String> remainingLimbList=A.remainingLimbNameSet(target);
				String gone=null;
				for(int i=0;i<remainingLimbList.size();i++)
					if(remainingLimbList.get(i).toUpperCase().endsWith("EYE"))
					{
						gone=remainingLimbList.get(i);
						break;
					}
				if(gone!=null)
				{
					Ability A2=CMClass.getAbility("Injury");
					if(A2!=null)
					{
						A2.setMiscText(mob.Name()+"/"+gone);
						final CMMsg msg2=CMClass.getMsg(mob,target,this,CMMsg.MSG_DAMAGE,L("<DAMAGE> <T-NAME>."));
						msg2.setValue(target.maxState().getHitPoints()/(20-getXLEVELLevel(mob)));
						if(!A2.invoke(mob,new XVector(msg2),target,true,0))
						{
							A2=target.fetchEffect("Injury");
							if( A2 != null )
							{
							  A2.setMiscText(mob.Name()+"/"+gone);
							  A2.okMessage(target,msg2);
							}
						}
					}
				}
			}
		}
		else
			return maliciousFizzle(mob,target,L("<S-NAME> attempt(s) to gouge <T-YOUPOSS> eyes, but fail(s)."));
		return success;
	}
}
