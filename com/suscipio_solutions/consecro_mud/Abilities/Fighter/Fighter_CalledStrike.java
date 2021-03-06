package com.suscipio_solutions.consecro_mud.Abilities.Fighter;
import java.util.List;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Amputator;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.PhyStats;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Weapon;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.Races.interfaces.Race;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;



@SuppressWarnings("rawtypes")
public class Fighter_CalledStrike extends FighterSkill
{
	@Override public String ID() { return "Fighter_CalledStrike"; }
	private final static String localizedName = CMLib.lang().L("Called Strike");
	@Override public String name() { return localizedName; }
	private static final String[] triggerStrings =I(new String[] {"CALLEDSTRIKE"});
	@Override public int abstractQuality(){return Ability.QUALITY_MALICIOUS;}
	@Override public String displayText(){return "";}
	@Override public String[] triggerStrings(){return triggerStrings;}
	@Override protected int canAffectCode(){return 0;}
	@Override protected int canTargetCode(){return Ability.CAN_MOBS;}
	@Override public int classificationCode(){ return Ability.ACODE_SKILL|Ability.DOMAIN_MARTIALLORE;}
	@Override public int usageType(){return USAGE_MOVEMENT;}

	protected String gone="";
	protected MOB target=null;
	protected int hpReq=9;

	@Override
	public void affectPhyStats(Physical affected, PhyStats affectableStats)
	{
		affectableStats.setAttackAdjustment(affectableStats.attackAdjustment()-100);
	}

	protected boolean amputate()
	{
		final MOB mob=target;
		if(mob==null) return false;
		Amputator A=(Amputator)mob.fetchEffect("Amputation");
		if(A==null)	A=(Amputator)CMClass.getAbility("Amputation");
		if(A.amputate(mob,A,gone)!=null)
		{
			if(mob.fetchEffect(A.ID())==null)
				mob.addNonUninvokableEffect(A);
			return true;
		}
		return false;
	}

	@Override
	public boolean okMessage(final Environmental myHost, final CMMsg msg)
	{
		if((affected==null)||(!(affected instanceof MOB))||(target==null))
		   return super.okMessage(myHost,msg);
		final MOB mob=(MOB)affected;
		if(msg.amISource(mob)
		&&(msg.amITarget(target))
		&&(msg.targetMinor()==CMMsg.TYP_DAMAGE))
		{
			int hurtAmount=msg.value();
			final int reqDivisor=hpReq+getXLEVELLevel(invoker());
			if(hurtAmount>=(target.baseState().getHitPoints()/reqDivisor))
			{
				hurtAmount=(target.baseState().getHitPoints()/reqDivisor);
				msg.setValue(msg.value()+hurtAmount);
				amputate();
			}
			else
				mob.tell(mob,target,null,L("You failed to cut off <T-YOUPOSS> '@x1'.",gone));
			unInvoke();
		}
		return super.okMessage(myHost,msg);
	}

	protected boolean prereqs(MOB mob, boolean quiet)
	{
		if(mob.isInCombat()&&(mob.rangeToTarget()>0))
		{
			if(!quiet)
			mob.tell(L("You are too far away to perform a called strike!"));
			return false;
		}

		final Item w=mob.fetchWieldedItem();
		if((w==null)||(!(w instanceof Weapon)))
		{
			if(!quiet)
			mob.tell(L("You need a weapon to perform a called strike!"));
			return false;
		}
		final Weapon wp=(Weapon)w;
		if(wp.weaponType()!=Weapon.TYPE_SLASHING)
		{
			if(!quiet)
			mob.tell(L("You cannot amputate with @x1!",wp.name()));
			return false;
		}
		return true;
	}

	@Override
	public int castingQuality(MOB mob, Physical target)
	{
		if(mob!=null)
		{
			if(!prereqs(mob,true))
				return Ability.QUALITY_INDIFFERENT;
		}
		return super.castingQuality(mob,target);
	}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		if(!prereqs(mob,false)) return false;

		gone="";
		hpReq=9;
		target=null;

		if(commands.size()>0)
		{
			final String s=(String)commands.firstElement();
			if(mob.location().fetchInhabitant(s)!=null)
				target=mob.location().fetchInhabitant(s);
			if((target!=null)&&(!CMLib.flags().canBeSeenBy(target,mob)))
			{
				mob.tell(L("You can't see '@x1' here.",s));
				return false;
			}
			if(target!=null)
				commands.removeElementAt(0);
		}
		if(target==null)
			target=mob.getVictim();
		if((target==null)||(target==mob))
		{
			mob.tell(L("Do this to whom?"));
			return false;
		}
		if(target.fetchEffect(ID())!=null)
		{
			mob.tell(L("@x1 already has a call against one of @x2 limbs.",target.name(mob),target.charStats().hisher()));
			return false;
		}

		Amputator A=(Amputator)target.fetchEffect("Amputation");
		if(A==null)	A=(Amputator)CMClass.getAbility("Amputation");

		final List<String> remainingLimbList=A.remainingLimbNameSet(target);
		if(remainingLimbList.size()==0)
		{
			if(!auto)
				mob.tell(L("There is nothing left on @x1 to cut off!",target.name(mob)));
			return false;
		}
		if(mob.isMonster())
			gone=remainingLimbList.get(CMLib.dice().roll(1,remainingLimbList.size(),-1));
		else
		if(commands.size()<=0)
		{
			mob.tell(L("You must specify a body part to cut off."));
			final StringBuffer str=new StringBuffer(L("Parts include: "));
			for(int i=0;i<remainingLimbList.size();i++)
				str.append((remainingLimbList.get(i))+", ");
			mob.tell(str.toString().substring(0,str.length()-2)+".");
			return false;
		}
		else
		{
			final String off=CMParms.combine(commands,0);
			if((off.equalsIgnoreCase("head"))
			&&(target.charStats().getBodyPart(Race.BODY_HEAD)>=0))
			{
				gone=Race.BODYPARTSTR[Race.BODY_HEAD].toLowerCase();
				hpReq=3;
			}
			else
			for(int i=0;i<remainingLimbList.size();i++)
				if(remainingLimbList.get(i).toUpperCase().startsWith(off.toUpperCase()))
				{
					gone=remainingLimbList.get(i);
					break;
				}
			if(gone.length()==0)
			{
				mob.tell(L("'@x1' is not a valid body part.",off));
				final StringBuffer str=new StringBuffer(L("Parts include: "));
				for(int i=0;i<remainingLimbList.size();i++)
					str.append((remainingLimbList.get(i))+", ");
				mob.tell(str.toString().substring(0,str.length()-2)+".");
				return false;
			}
		}

		// the invoke method for spells receives as
		// parameters the invoker, and the REMAINING
		// command line parameters, divided into words,
		// and added as String objects to a vector.
		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		// now see if it worked
		final boolean success=proficiencyCheck(mob,0,auto);
		if((success)&&(gone.length()>0))
		{
			final CMMsg msg=CMClass.getMsg(mob,target,this,(auto?CMMsg.MASK_ALWAYS:0)|CMMsg.MASK_MALICIOUS|CMMsg.MSG_NOISYMOVEMENT,L("^F^<FIGHT^><S-NAME> call(s) '@x1'!^</FIGHT^>^?",gone));
			CMLib.color().fixSourceFightColor(msg);
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				invoker=mob;
				beneficialAffect(mob,mob,asLevel,2);
				final Ability A2=target.fetchEffect("Injury");
				if(A2!=null) A2.setMiscText(mob.Name()+"/"+gone);
				mob.recoverPhyStats();
			}
		}
		else
			return maliciousFizzle(mob,null,L("<S-NAME> call(s) '@x1', but fail(s) <S-HIS-HER> attack.",gone));

		// return whether it worked
		return success;
	}
}
