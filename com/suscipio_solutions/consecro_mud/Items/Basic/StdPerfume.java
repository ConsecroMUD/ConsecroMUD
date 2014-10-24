package com.suscipio_solutions.consecro_mud.Items.Basic;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Behaviors.interfaces.Behavior;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Perfume;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Wearable;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;


@SuppressWarnings({"unchecked","rawtypes"})
public class StdPerfume extends StdDrink implements Perfume
{
	@Override public String ID(){	return "StdPerfume";}

	List<String> smellList=new Vector();

	public StdPerfume()
	{
		super();
		setName("a bottle of perfume");
		setDisplayText("a bottle of perfume sits here.");

		material=RawMaterial.RESOURCE_GLASS;
		amountOfThirstQuenched=1;
		amountOfLiquidHeld=10;
		amountOfLiquidRemaining=10;
		disappearsAfterDrinking=true;
		liquidType=RawMaterial.RESOURCE_PERFUME;
		capacity=0;
		baseGoldValue=100;
		setRawProperLocationBitmap(Wearable.WORN_WIELD|Wearable.WORN_ABOUT_BODY|Wearable.WORN_FLOATING_NEARBY|Wearable.WORN_HELD|Wearable.WORN_ARMS|Wearable.WORN_BACK|Wearable.WORN_EARS|Wearable.WORN_EYES|Wearable.WORN_FEET|Wearable.WORN_HANDS|Wearable.WORN_HEAD|Wearable.WORN_LEFT_FINGER|Wearable.WORN_RIGHT_FINGER|Wearable.WORN_LEGS|Wearable.WORN_LEFT_WRIST|Wearable.WORN_MOUTH|Wearable.WORN_NECK|Wearable.WORN_RIGHT_WRIST|Wearable.WORN_TORSO|Wearable.WORN_WAIST);
		recoverPhyStats();
	}

	@Override
	public List<String> getSmellEmotes(Perfume me)
	{	return smellList;}
	@Override
	public String getSmellList()
	{
		final StringBuffer list=new StringBuffer("");
		for(int i=0;i<smellList.size();i++)
			list.append((smellList.get(i))+";");
		return list.toString();
	}
	@Override
	public void setSmellList(String list)
	{smellList=CMParms.parseSemicolons(list,true);}

	@Override
	public void wearIfAble(MOB mob, Perfume me)
	{
		Ability E=mob.fetchEffect("Prop_MOBEmoter");
		if(E!=null)
			mob.tell(L("You can't put any perfume on right now."));
		else
		{
			E=CMClass.getAbility("Prop_MOBEmoter");
			String s=getSmellList();
			if(s.toUpperCase().indexOf("EXPIRES")<0)
				s="expires=50 "+s;
			if(s.toUpperCase().trim().startsWith("SMELL "))
				E.setMiscText(s);
			else
				E.setMiscText("SMELL "+s);
			mob.addNonUninvokableEffect(E);
			E.setSavable(false);
		}
	}

	@Override
	public boolean okMessage(final Environmental myHost, final CMMsg msg)
	{
		if(msg.target()==this)
		{
			if(msg.targetMinor()==CMMsg.TYP_WEAR)
				return true;
			if(!super.okMessage(myHost,msg))
				return false;
			if(msg.targetMinor()==CMMsg.TYP_DRINK)
			{
				msg.source().tell(L("You don't want to be drinking that."));
				return false;
			}
			return true;
		}
		return super.okMessage(myHost,msg);
	}

	@Override
	public void executeMsg(final Environmental myHost, final CMMsg msg)
	{
		if(msg.target()==this)
		{
			if(msg.targetMinor()==CMMsg.TYP_WEAR)
			{
				// the order that these things are checked in should
				// be holy, and etched in stone.
				if(behaviors != null)
					for(final Behavior B : behaviors)
						if(B!=null)
							B.executeMsg(this,msg);

				for(final Enumeration<Ability> a=effects();a.hasMoreElements();)
				{
					final Ability A=a.nextElement();
					if(A!=null)
						A.executeMsg(this,msg);
				}
				amountOfLiquidRemaining-=amountOfThirstQuenched;
				wearIfAble(msg.source(),this);
				if(disappearsAfterDrinking)
					destroy();
				return;
			}
		}
		super.executeMsg(myHost,msg);
	}
}
