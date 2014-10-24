package com.suscipio_solutions.consecro_mud.Abilities.Thief;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Food;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Weapon;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Wearable;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.interfaces.Drink;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;


@SuppressWarnings({"unchecked","rawtypes"})
public class Thief_UsePoison extends ThiefSkill
{
	@Override public String ID() { return "Thief_UsePoison"; }
	private final static String localizedName = CMLib.lang().L("Use Poison");
	@Override public String name() { return localizedName; }
	@Override protected int canAffectCode(){return 0;}
	@Override protected int canTargetCode(){return Ability.CAN_ITEMS;}
	@Override public int abstractQuality(){return Ability.QUALITY_INDIFFERENT;}
	private static final String[] triggerStrings =I(new String[] {"POISON","USEPOISON"});
	@Override public int classificationCode(){return Ability.ACODE_THIEF_SKILL|Ability.DOMAIN_POISONING;}
	@Override public String[] triggerStrings(){return triggerStrings;}
	@Override public int usageType(){return USAGE_MOVEMENT|USAGE_MANA;}

	public List<Ability> returnOffensiveAffects(Physical fromMe)
	{
		final Vector offenders=new Vector();

		for(final Enumeration<Ability> a=fromMe.effects();a.hasMoreElements();)
		{
			final Ability A=a.nextElement();
			if((A!=null)&&((A.classificationCode()&Ability.ALL_ACODES)==Ability.ACODE_POISON))
				offenders.addElement(A);
		}
		return offenders;
	}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		if(commands.size()<2)
		{
			mob.tell(L("What would you like to poison, and which poison would you use?"));
			return false;
		}
		final Item target=mob.fetchItem(null,Wearable.FILTER_UNWORNONLY,(String)commands.elementAt(0));
		if((target==null)||(!CMLib.flags().canBeSeenBy(target,mob)))
		{
			mob.tell(L("You don't see '@x1' here.",((String)commands.elementAt(0))));
			return false;
		}
		if((!(target instanceof Food))
		&&(!(target instanceof Drink))
		&&(!(target instanceof Weapon)))
		{
			mob.tell(L("You don't know how to poison @x1.",target.name(mob)));
			return false;
		}
		final Item poison=mob.fetchItem(null,Wearable.FILTER_UNWORNONLY,CMParms.combine(commands,1));
		if((poison==null)||(!CMLib.flags().canBeSeenBy(poison,mob)))
		{
			mob.tell(L("You don't see '@x1' here.",CMParms.combine(commands,1)));
			return false;
		}
		final List<Ability> V=returnOffensiveAffects(poison);
		if((V.size()==0)||(!(poison instanceof Drink)))
		{
			mob.tell(L("@x1 is not a poison!",poison.name()));
			return false;
		}
		final Drink dPoison=(Drink)poison;

		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		final boolean success=proficiencyCheck(mob,0,auto);

		final CMMsg msg=CMClass.getMsg(mob,target,this,CMMsg.MSG_THIEF_ACT,L("<S-NAME> attempt(s) to poison <T-NAMESELF>."));
		if(mob.location().okMessage(mob,msg))
		{
			mob.location().send(mob,msg);
			if(success)
			{
				final Ability A=V.get(0);
				if(A!=null)
				{
					if(target instanceof Weapon)
						A.invoke(mob,target,true,adjustedLevel(mob,asLevel));
					else
						target.addNonUninvokableEffect(A);

					int amountToTake=dPoison.thirstQuenched()/5;
					if(amountToTake<1) amountToTake=1;
					dPoison.setLiquidRemaining(dPoison.liquidRemaining()-amountToTake);
					if(dPoison.disappearsAfterDrinking()
					||((dPoison instanceof RawMaterial)&&(dPoison.liquidRemaining()<=0)))
						dPoison.destroy();
				}

			}
		}
		return success;
	}

}
