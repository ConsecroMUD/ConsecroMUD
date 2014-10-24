package com.suscipio_solutions.consecro_mud.Abilities.Paladin;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.Common.EnhancedCraftingSkill;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Weapon;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.collections.PairVector;
import com.suscipio_solutions.consecro_mud.core.interfaces.ItemPossessor;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;
import com.suscipio_solutions.consecro_mud.core.interfaces.Tickable;




@SuppressWarnings("rawtypes")
public class Paladin_CraftHolyAvenger extends EnhancedCraftingSkill
{
	@Override public String ID() { return "Paladin_CraftHolyAvenger"; }
	private final static String localizedName = CMLib.lang().L("Craft Holy Avenger");
	@Override public String name() { return localizedName; }
	private static final String[] triggerStrings =I(new String[] {"CRAFTHOLY","CRAFTHOLYAVENGER","CRAFTAVENGER"});
	@Override public String[] triggerStrings(){return triggerStrings;}

	@Override
	public boolean tick(Tickable ticking, int tickID)
	{
		if((affected!=null)&&(affected instanceof MOB)&&(tickID==Tickable.TICKID_MOB))
		{
			final MOB mob=(MOB)affected;
			if((buildingI==null)
			||(getRequiredFire(mob,0)==null))
			{
				messedUp=true;
				unInvoke();
			}
		}
		return super.tick(ticking,tickID);
	}

	@Override
	public void unInvoke()
	{
		if(canBeUninvoked())
		{
			if((affected!=null)&&(affected instanceof MOB))
			{
				final MOB mob=(MOB)affected;
				if((buildingI!=null)&&(!aborted))
				{
					if(messedUp)
						commonEmote(mob,"<S-NAME> mess(es) up crafting the Holy Avenger.");
					else
						mob.location().addItem(buildingI,ItemPossessor.Expire.Player_Drop);
				}
				buildingI=null;
			}
		}
		super.unInvoke();
	}


	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		int completion=16;
		final Item fire=getRequiredFire(mob,0);
		if(fire==null) return false;
		final PairVector<Integer,Integer> enhancedTypes=enhancedTypes(mob,commands);
		buildingI=null;
		messedUp=false;
		int woodRequired=50;
		final int[] pm={RawMaterial.MATERIAL_METAL,RawMaterial.MATERIAL_MITHRIL};
		final int[][] data=fetchFoundResourceData(mob,
											woodRequired,"metal",pm,
											0,null,null,
											false,
											auto?RawMaterial.RESOURCE_MITHRIL:0,
											enhancedTypes);
		if(data==null) return false;
		woodRequired=data[0][FOUND_AMT];

		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;
		if(!auto)
			CMLib.materials().destroyResourcesValue(mob.location(),woodRequired,data[0][FOUND_CODE],0,null);
		buildingI=CMClass.getWeapon("GenWeapon");
		completion=50-CMLib.ableMapper().qualifyingClassLevel(mob,this);
		final String itemName="the Holy Avenger";
		buildingI.setName(itemName);
		final String startStr=L("<S-NAME> start(s) crafting @x1.",buildingI.name());
		displayText=L("You are crafting @x1",buildingI.name());
		verb=L("crafting @x1",buildingI.name());
		buildingI.setDisplayText(L("@x1 lies here",itemName));
		buildingI.setDescription(itemName+". ");
		buildingI.basePhyStats().setWeight(woodRequired);
		buildingI.setBaseValue(0);
		buildingI.setMaterial(data[0][FOUND_CODE]);
		buildingI.basePhyStats().setLevel(mob.phyStats().level());
		buildingI.basePhyStats().setAbility(5);
		final Weapon w=(Weapon)buildingI;
		w.setWeaponClassification(Weapon.CLASS_SWORD);
		w.setWeaponType(Weapon.TYPE_SLASHING);
		w.setRanges(w.minRange(),1);
		buildingI.setRawLogicalAnd(true);
		Ability A=CMClass.getAbility("Prop_HaveZapper");
		A.setMiscText("-CLASS +Paladin -ALIGNMENT +Good");
		buildingI.addNonUninvokableEffect(A);
		A=CMClass.getAbility("Prop_Doppleganger");
		A.setMiscText("120%");
		buildingI.addNonUninvokableEffect(A);

		buildingI.recoverPhyStats();
		buildingI.text();
		buildingI.recoverPhyStats();

		messedUp=!proficiencyCheck(mob,0,auto);
		if(completion<6) completion=6;
		final CMMsg msg=CMClass.getMsg(mob,null,CMMsg.MSG_NOISYMOVEMENT,startStr);
		if(mob.location().okMessage(mob,msg))
		{
			mob.location().send(mob,msg);
			beneficialAffect(mob,mob,asLevel,completion);
			enhanceItem(mob,buildingI,enhancedTypes);
		}
		return true;
	}
}
