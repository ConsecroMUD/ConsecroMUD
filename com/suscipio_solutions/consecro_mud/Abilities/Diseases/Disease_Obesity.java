package com.suscipio_solutions.consecro_mud.Abilities.Diseases;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CharState;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CharStats;
import com.suscipio_solutions.consecro_mud.Common.interfaces.PhyStats;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMProps;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;
import com.suscipio_solutions.consecro_mud.core.interfaces.Tickable;





@SuppressWarnings("rawtypes")
public class Disease_Obesity extends Disease
{
	@Override public String ID() { return "Disease_Obesity"; }
	private final static String localizedName = CMLib.lang().L("Obesity");
	@Override public String name() { return localizedName; }
	@Override
	public String displayText()
	{
		final int amount=amountOfFat();
		if(amount<20)
			return "(Chubby)";
		else
		if(amount<60)
			return "(Fat)";
		else
		if(amount<120)
			return "(Obese)";
		else
			return "(Morbid obesity)";
	}
	@Override protected int canAffectCode(){return CAN_MOBS;}
	@Override protected int canTargetCode(){return CAN_MOBS;}
	@Override public int abstractQuality(){return Ability.QUALITY_MALICIOUS;}
	@Override public boolean putInCommandlist(){return false;}
	@Override public int difficultyLevel(){return 10;}

	@Override protected int DISEASE_TICKS(){return 999999;}
	@Override protected int DISEASE_DELAY(){return 50;}
	@Override protected String DISEASE_DONE(){return "You've become fit and trim!";}
	@Override protected String DISEASE_START(){return "^G<S-NAME> look(s) like <S-HE-SHE> <S-HAS-HAVE> been gaining some weight.^?";}
	@Override protected String DISEASE_AFFECT(){return "";}
	@Override public int abilityCode(){return 0;}
	@Override public boolean canBeUninvoked(){canBeUninvoked=!(amountOfFat()>0);return super.canBeUninvoked();}
	protected long lastLoss=-1;
	protected int fatAmount=-1;

	protected int amountOfFat()
	{
		if((fatAmount<0)&&(CMath.isNumber(text())))
			fatAmount=CMath.s_int(text());
		if(fatAmount<0) fatAmount=0;
		if(fatAmount>=0) return fatAmount;
		return 1;
	}

	@Override
	public void setMiscText(String newText)
	{
		super.setMiscText(newText);
		fatAmount=-1;
	}

	private void setFatAmountChange(int change)
	{
		setMiscText(""+(amountOfFat()+change));
	}

	@Override
	public void affectCharStats(MOB affectedMob, CharStats affectableStats)
	{
		super.affectCharStats(affectedMob, affectableStats);
		affectableStats.setStat(CharStats.STAT_WEIGHTADJ,
			affectableStats.getStat(CharStats.STAT_WEIGHTADJ)
			+(int)Math.round(CMath.mul(affectedMob.basePhyStats().weight(),CMath.div(CMath.s_int(text()),100.0))));
	}
	@Override
	public void affectPhyStats(Physical affected, PhyStats affectableStats)
	{
		super.affectPhyStats(affected,affectableStats);
	}

	@Override
	public void affectCharState(MOB affected, CharState affectableState)
	{
		super.affectCharState(affected,affectableState);
		final int oldMovement=affectableState.getMovement();
		affectableState.setMovement(affectableState.getMovement()-(int)Math.round(CMath.mul(affectableState.getMovement(),CMath.div(CMath.s_int(text()),100.0))));
		if((affectableState.getMovement()<20)&&(oldMovement>20)) affectableState.setMovement(20);
	}

	@Override
	public boolean tick(Tickable ticking, int tickID)
	{
		if((ticking==affected)&&(tickID==Tickable.TICKID_MOB)&&(affected instanceof MOB))
		{
			final MOB mob=(MOB)affected;
			if((mob.curState().getMovement()<mob.maxState().getMovement()/10)
			&&((lastLoss<0)||((System.currentTimeMillis()-lastLoss)>10000)))
			{
				lastLoss=System.currentTimeMillis();
				final int change=CMLib.dice().roll(1,10,0);
				final int fat=amountOfFat();
				if(fat>=0)
				{
					if(fat<change)
						setFatAmountChange(-fat);
					else
						setFatAmountChange(-change);
				}
			}
			if((mob.curState().adjTicksHungry(false) >= CMProps.getTicksPerMudHour())
			&&((lastLoss<0)||((System.currentTimeMillis()-lastLoss)>10000)))
			{
				lastLoss=System.currentTimeMillis();
				final int change=CMLib.dice().roll(1,3,0);
				final int fat=amountOfFat();
				if(fat>=0)
				{
					if(fat<change)
						setFatAmountChange(-fat);
					else
						setFatAmountChange(-change);
				}
			}
			if(amountOfFat()<=0)
				unInvoke();
		}
		return super.tick(ticking,tickID);
	}

	@Override
	public void executeMsg(Environmental host, CMMsg msg)
	{
		if((msg.source()==affected)
		&&(msg.targetMinor()==CMMsg.TYP_EAT)
		&&(msg.target()!=null)
		&&(msg.source().curState().getHunger()>=msg.source().maxState().maxHunger(msg.source().baseWeight())))
		{
			setFatAmountChange(CMLib.dice().roll(1,5,0));
			msg.source().recoverPhyStats();
			msg.source().recoverCharStats();
			msg.source().recoverMaxState();
		}
		else
		if((msg.target()==affected)
		&&((msg.targetMinor()==CMMsg.TYP_LOOK)||(msg.targetMinor()==CMMsg.TYP_EXAMINE))
		&&(CMLib.flags().canBeSeenBy(affected,msg.source()))
		&&(affected instanceof MOB))
		{
			final int amount=amountOfFat();
			String str="";
			if(amount<20)
				str=L("a bit chubby");
			else
			if(amount<60)
				str=L("fat");
			else
			if(amount<120)
				str=L("obese");
			else
				str=L("morbidly obese");
			msg.addTrailerMsg(CMClass.getMsg(msg.source(),null,null,
										  CMMsg.MSG_OK_VISUAL,L("\n\r@x1 is @x2.\n\r",affected.name(),str),
										  CMMsg.NO_EFFECT,null,
										  CMMsg.NO_EFFECT,null));
		}
		super.executeMsg(host,msg);
	}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		final MOB target=this.getTarget(mob,commands,givenTarget);
		if(target==null) return false;

		if(super.invoke(mob,commands,givenTarget,auto,asLevel))
		{
			final Ability A=target.fetchEffect(ID());
			if(A!=null) A.setMiscText(""+CMLib.dice().roll(1,5,0));
			return true;
		}
		return false;
	}
}
