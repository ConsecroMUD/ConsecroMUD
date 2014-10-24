package com.suscipio_solutions.consecro_mud.Abilities.Diseases;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.StdAbility;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.DiseaseAffect;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CharStats;
import com.suscipio_solutions.consecro_mud.Common.interfaces.Social;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Food;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Weapon;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_mud.core.interfaces.Drink;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;



@SuppressWarnings("rawtypes")
public class Disease extends StdAbility implements DiseaseAffect
{
	@Override public String ID() { return "Disease"; }
	private final static String localizedName = CMLib.lang().L("Disease");
	@Override public String name() { return localizedName; }
	private final static String localizedStaticDisplay = CMLib.lang().L("(a disease)");
	@Override public String displayText() { return localizedStaticDisplay; }
	@Override protected int canAffectCode(){return CAN_MOBS;}
	@Override protected int canTargetCode(){return CAN_MOBS|CAN_ITEMS;}
	private static final String[] triggerStrings =I(new String[] {"DISEASE"});
	@Override public String[] triggerStrings(){return triggerStrings;}
	@Override public int abstractQuality(){return Ability.QUALITY_MALICIOUS;}
	@Override public boolean putInCommandlist(){return false;}
	@Override public int classificationCode(){return Ability.ACODE_DISEASE;}
	protected boolean DISEASE_MALICIOUS(){return true;}

	@Override
	public String getHealthConditionDesc()
	{
		return "Suffering the effects of "+name();
	}

	protected int DISEASE_TICKS(){return 48;}
	protected int DISEASE_DELAY(){return 5;}
	protected String DISEASE_DONE(){return "Your disease has run its course.";}
	protected String DISEASE_START(){return "^G<S-NAME> come(s) down with a disease.^?";}
	protected String DISEASE_AFFECT(){return "<S-NAME> ache(s) and groan(s).";}
	protected boolean DISEASE_REQSEE(){return false;}

	@Override public int spreadBitmap() { return 0; }
	@Override public int abilityCode() { return spreadBitmap(); }
	@Override public int difficultyLevel(){return 0;}
	protected boolean processing=false;

	protected int diseaseTick=DISEASE_DELAY();

	protected boolean catchIt(MOB mob, Physical target)
	{
		MOB diseased=invoker;
		if(invoker==target) return true;
		if(diseased==null) diseased=mob;
		if((diseased==null)&&(target instanceof MOB)) diseased=(MOB)target;

		if((target!=null)
		&&(diseased!=null)
		&&(target.fetchEffect(ID())==null)
		&&((!DISEASE_REQSEE())||((target instanceof MOB)&&(CMLib.flags().canBeSeenBy(diseased,(MOB)target)))))
		{
			if(target instanceof MOB)
			{
				final MOB targetMOB=(MOB)target;
				if((CMLib.dice().rollPercentage()>targetMOB.charStats().getSave(CharStats.STAT_SAVE_DISEASE))
				&&(targetMOB.location()!=null))
				{
					final MOB following=targetMOB.amFollowing();
					final boolean doMe=invoke(diseased,targetMOB,true,0);
					if(targetMOB.amFollowing()!=following)
						targetMOB.setFollowing(following);
					return doMe;
				}
				spreadImmunity(targetMOB);
			}
			else
			{
				maliciousAffect(diseased,target,0,DISEASE_TICKS(),-1);
				return true;
			}
		}
		return false;
	}
	protected boolean catchIt(MOB mob)
	{
		if(mob==null) return false;
		if(mob.location()==null) return false;
		final MOB target=mob.location().fetchRandomInhabitant();
		return catchIt(mob,target);
	}

	@Override
	public void unInvoke()
	{
		if(affected==null)
			return;
		if(affected instanceof MOB)
		{
			final MOB mob=(MOB)affected;

			super.unInvoke();
			if(canBeUninvoked())
			{
				if(!mob.amDead())
					spreadImmunity(mob);
				mob.tell(mob,null,this,DISEASE_DONE());
			}
		}
		else
			super.unInvoke();
	}

	@Override
	public void executeMsg(final Environmental myHost, final CMMsg msg)
	{
		if(affected==null) return;
		if(affected instanceof MOB)
		{
			final MOB mob=(MOB)affected;

			// when this spell is on a MOBs Affected list,
			// it should consistantly prevent the mob
			// from trying to do ANYTHING except sleep
			if((CMath.bset(spreadBitmap(),DiseaseAffect.SPREAD_DAMAGE))
			&&(msg.amISource(mob))
			&&(msg.targetMinor()==CMMsg.TYP_DAMAGE)
			&&(msg.tool()!=null)
			&&(msg.tool() instanceof Weapon)
			&&(((Weapon)msg.tool()).weaponClassification()==Weapon.CLASS_NATURAL)
			&&(msg.source().fetchWieldedItem()==null)
			&&(msg.target() instanceof MOB)
			&&(msg.target()!=msg.source())
			&&(CMLib.dice().rollPercentage()>(((MOB)msg.target()).charStats().getSave(CharStats.STAT_SAVE_DISEASE)+70)))
				catchIt(mob,(MOB)msg.target());
			else
			if((CMath.bset(spreadBitmap(),DiseaseAffect.SPREAD_CONTACT))
			&&(msg.amISource(mob)||msg.amITarget(mob))
			&&(msg.target() instanceof MOB)
			&&(CMath.bset(msg.targetMajor(),CMMsg.MASK_MOVE)||CMath.bset(msg.targetMajor(),CMMsg.MASK_HANDS))
			&&((msg.tool()==null)
				||((msg.tool() instanceof Weapon)
					&&(((Weapon)msg.tool()).weaponClassification()==Weapon.CLASS_NATURAL))))
				catchIt(mob,msg.amITarget(mob)?msg.source():(MOB)msg.target());
			else
			if((CMath.bset(spreadBitmap(),DiseaseAffect.SPREAD_STD))
			&&((msg.amITarget(mob))||(msg.amISource(mob)))
			&&(msg.tool() instanceof Social)
			&&(msg.target() instanceof MOB)
			&&(msg.tool().Name().equals("MATE <T-NAME>")
				||msg.tool().Name().equals("SEX <T-NAME>")))
				catchIt(mob,msg.amITarget(mob)?msg.source():(MOB)msg.target());
		}
		else
		if(affected instanceof Item)
		{
			try
			{
				if(!processing)
				{
					final Item myItem=(Item)affected;
					if(myItem.owner()==null) return;
					processing=true;
					switch(msg.sourceMinor())
					{
					case CMMsg.TYP_DRINK:
						if((CMath.bset(spreadBitmap(),DiseaseAffect.SPREAD_CONSUMPTION))
						||(CMath.bset(spreadBitmap(),DiseaseAffect.SPREAD_CONTACT)))
						{
							if((myItem instanceof Drink)
							&&(msg.amITarget(myItem)))
								catchIt(msg.source(),msg.source());
						}
						break;
					case CMMsg.TYP_EAT:
						if((CMath.bset(spreadBitmap(),DiseaseAffect.SPREAD_CONSUMPTION))
						||(CMath.bset(spreadBitmap(),DiseaseAffect.SPREAD_CONTACT)))
						{
	
							if((myItem instanceof Food)
							&&(msg.amITarget(myItem)))
								catchIt(msg.source(),msg.source());
						}
						break;
					case CMMsg.TYP_GET:
					case CMMsg.TYP_PUSH:
					case CMMsg.TYP_PULL:
						if(CMath.bset(spreadBitmap(),DiseaseAffect.SPREAD_CONTACT))
						{
							if((!(myItem instanceof Drink))
							  &&(!(myItem instanceof Food))
							  &&(msg.amITarget(myItem)))
								catchIt(msg.source(),msg.source());
						}
						break;
					}
				}
			}
			finally
			{
				processing=false;
			}
		}
		super.executeMsg(myHost,msg);
	}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		final MOB target=this.getTarget(mob,commands,givenTarget);
		if(target==null) return false;

		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;
		boolean success=proficiencyCheck(mob,0,auto);
		if(success)
		{
			final MOB mvictim=mob.getVictim();
			final MOB tvictim=target.getVictim();
			final CMMsg msg=CMClass.getMsg(mob,target,this,CMMsg.MASK_HANDS|(auto?CMMsg.MASK_ALWAYS:0)|CMMsg.MASK_MALICIOUS|CMMsg.TYP_DISEASE,"");
			final Room R=target.location();
			if((R!=null)&&(R.okMessage(target,msg)))
			{
				R.send(target,msg);
				if(msg.value()<=0)
				{
					R.show(target,null,CMMsg.MSG_OK_VISUAL,DISEASE_START());
					success=maliciousAffect(mob,target,asLevel,DISEASE_TICKS(),-1)!=null;
				}
				else
					spreadImmunity(target);
			}
			if(!DISEASE_MALICIOUS())
			{
				if((mvictim==null)&&(mob.getVictim()==target))
					mob.setVictim(null);
				if((tvictim==null)&&(target.getVictim()==mob))
					target.setVictim(null);
			}
			else
			if(auto)
			{
				if(mob.getVictim()!=mvictim) mob.setVictim(mvictim);
				if(target.getVictim()!=tvictim) target.setVictim(tvictim);
			}

		}
		return success;
	}
}
