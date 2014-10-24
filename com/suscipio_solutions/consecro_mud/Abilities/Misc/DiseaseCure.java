package com.suscipio_solutions.consecro_mud.Abilities.Misc;
import java.util.List;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.StdAbility;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.DiseaseAffect;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Food;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Wearable;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Drink;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;



@SuppressWarnings({"unchecked","rawtypes"})
public class DiseaseCure extends StdAbility
{
	@Override public String ID() { return "DiseaseCure"; }
	private final static String localizedName = CMLib.lang().L("A Cure");
	@Override public String name() { return localizedName; }
	@Override public int abstractQuality(){ return Ability.QUALITY_BENEFICIAL_OTHERS;}
	@Override public String displayText(){ return "";}
	@Override protected int canAffectCode(){return CAN_ITEMS;}
	@Override protected int canTargetCode(){return CAN_MOBS;}
	@Override public int classificationCode(){return Ability.ACODE_SKILL;}
	protected boolean processing=false;

	public List<Ability> returnOffensiveAffects(Physical fromMe)
	{
		final Vector offenders=new Vector();

		for(int a=0;a<fromMe.numEffects();a++) // personal
		{
			final Ability A=fromMe.fetchEffect(a);
			if((A!=null)
			&&(A instanceof DiseaseAffect)
			&&((text().length()==0)||(A.name().toUpperCase().indexOf(text().toUpperCase())>=0)||(A.ID().toUpperCase().indexOf(text().toUpperCase())>=0)))
				offenders.addElement(A);
		}
		return offenders;
	}

	@Override
	public void executeMsg(final Environmental myHost, final CMMsg msg)
	{
		if(affected==null) return;
		if(affected instanceof Item)
		{
			if(!processing)
			{
				final Item myItem=(Item)affected;
				if(myItem.owner()==null) return;
				processing=true;
				if(msg.amITarget(myItem))
					switch(msg.sourceMinor())
					{
					case CMMsg.TYP_DRINK:
						if(myItem instanceof Drink)
						{
							invoke(msg.source(),null,msg.source(),true,0);
							myItem.destroy();
						}
						break;
					case CMMsg.TYP_EAT:
						if(myItem instanceof Food)
						{
							invoke(msg.source(),null,msg.source(),true,0);
							myItem.destroy();
						}
						break;
					case CMMsg.TYP_WEAR:
						if(myItem.rawProperLocationBitmap()!=Wearable.WORN_HELD)
						{
							invoke(msg.source(),null,msg.source(),true,0);
							myItem.destroy();
						}
						break;
					}
			}
			processing=false;
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

		final boolean success=proficiencyCheck(mob,0,auto);
		final List<Ability> offensiveAffects=returnOffensiveAffects(target);

		if((success)&&(offensiveAffects.size()>0))
		{
			// it worked, so build a copy of this ability,
			// and add it to the affects list of the
			// affected MOB.  Then tell everyone else
			// what happened.
			final CMMsg msg=CMClass.getMsg(mob,target,this,CMMsg.MSG_OK_VISUAL,null);
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				for(int a=offensiveAffects.size()-1;a>=0;a--)
					offensiveAffects.get(a).unInvoke();
				if((!CMLib.flags().stillAffectedBy(target,offensiveAffects,false))&&(target.location()!=null))
					target.location().show(target,null,CMMsg.MSG_OK_VISUAL,L("<S-NAME> feel(s) much better."));
			}
		}

		// return whether it worked
		return success;
	}
}
