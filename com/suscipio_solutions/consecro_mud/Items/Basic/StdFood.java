package com.suscipio_solutions.consecro_mud.Items.Basic;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Food;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMSecurity;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;


public class StdFood extends StdItem implements Food
{
	@Override public String ID(){	return "StdFood";}
	protected int amountOfNourishment=500;
	protected int nourishmentPerBite=0;
	protected long decayTime=0;

	public StdFood()
	{
		super();
		setName("a bit of food");
		basePhyStats.setWeight(2);
		setDisplayText("a bit of food is here.");
		setDescription("Looks like some mystery meat");
		baseGoldValue=5;
		material=RawMaterial.RESOURCE_MEAT;
		recoverPhyStats();
	}



	@Override
	public int nourishment()
	{
		return amountOfNourishment;
	}
	@Override
	public void setNourishment(int amount)
	{
		amountOfNourishment=amount;
	}

	@Override
	public int bite()
	{
		return nourishmentPerBite;
	}
	@Override
	public void setBite(int amount)
	{
		nourishmentPerBite=amount;
	}

	@Override public long decayTime(){return decayTime;}
	@Override public void setDecayTime(long time){decayTime=time;}

	@Override
	public boolean okMessage(final Environmental myHost, final CMMsg msg)
	{
		if(!super.okMessage(myHost,msg))
			return false;
		if(msg.amITarget(this))
		{
			final MOB mob=msg.source();
			switch(msg.targetMinor())
			{
			case CMMsg.TYP_EAT:
				if((!msg.targetMajor(CMMsg.MASK_HANDS))
				||(mob.isMine(this))
				||(!CMLib.flags().isGettable(this)))
				{
					int amountEaten=nourishmentPerBite;
					if((amountEaten<1)||(amountEaten>amountOfNourishment))
						amountEaten=amountOfNourishment;
					msg.setValue((amountEaten<amountOfNourishment)?amountEaten:0);
					return true;
				}
				mob.tell(L("You don't have that."));
				return false;
			}
		}
		return true;
	}

	@Override
	public void executeMsg(final Environmental myHost, final CMMsg msg)
	{
		super.executeMsg(myHost,msg);
		if(msg.amITarget(this))
		{
			final MOB mob=msg.source();
			switch(msg.targetMinor())
			{
			case CMMsg.TYP_EAT:
				final boolean hungry=mob.curState().getHunger()<=0;
				if((!hungry)
				&&(mob.curState().getHunger()>=mob.maxState().maxHunger(mob.baseWeight()))
				&&(CMLib.dice().roll(1,100,0)==1)
				&&(!CMLib.flags().isGolem(msg.source()))
				&&(msg.source().fetchEffect("Disease_Obesity")==null)
				&&(!CMSecurity.isDisabled(CMSecurity.DisFlag.AUTODISEASE)))
				{
					final Ability A=CMClass.getAbility("Disease_Obesity");
					if(A!=null){A.invoke(mob,mob,true,0);}
				}
				int amountEaten=nourishmentPerBite;
				if((amountEaten<1)||(amountEaten>amountOfNourishment))
					amountEaten=amountOfNourishment;
				amountOfNourishment-=amountEaten;
				final boolean full=!mob.curState().adjHunger(amountEaten,mob.maxState().maxHunger(mob.baseWeight()));
				if((hungry)&&(mob.curState().getHunger()>0))
					mob.tell(L("You are no longer hungry."));
				else
				if(full)
					mob.tell(L("You are full."));
				if(amountOfNourishment<=0)
					this.destroy();
				if(!CMath.bset(msg.targetMajor(),CMMsg.MASK_OPTIMIZE))
					mob.location().recoverRoomStats();
				break;
			default:
				break;
			}
		}
	}
}
