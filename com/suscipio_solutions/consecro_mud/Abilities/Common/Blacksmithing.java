package com.suscipio_solutions.consecro_mud.Abilities.Common;
import java.util.List;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.ItemCraftor;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.Session;
import com.suscipio_solutions.consecro_mud.Common.interfaces.Session.InputCallback;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Armor;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Container;
import com.suscipio_solutions.consecro_mud.Items.interfaces.DoorKey;
import com.suscipio_solutions.consecro_mud.Items.interfaces.FalseLimb;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Light;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Potion;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Shield;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Weapon;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Wearable;
import com.suscipio_solutions.consecro_mud.Libraries.interfaces.ListingLibrary;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.CMStrings;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_mud.core.collections.PairVector;
import com.suscipio_solutions.consecro_mud.core.interfaces.Drink;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;
import com.suscipio_solutions.consecro_mud.core.interfaces.Rideable;
import com.suscipio_solutions.consecro_mud.core.interfaces.Tickable;



@SuppressWarnings({"unchecked","rawtypes"})
public class Blacksmithing extends EnhancedCraftingSkill implements ItemCraftor
{
	@Override public String ID() { return "Blacksmithing"; }
	private final static String localizedName = CMLib.lang().L("Blacksmithing");
	@Override public String name() { return localizedName; }
	private static final String[] triggerStrings =I(new String[] {"BLACKSMITH","BLACKSMITHING"});
	@Override public String[] triggerStrings(){return triggerStrings;}
	@Override public String supportedResourceString(){return "METAL|MITHRIL";}
	@Override
	public String parametersFormat(){ return
		"ITEM_NAME\tITEM_LEVEL\tBUILD_TIME_TICKS\tMATERIALS_REQUIRED\t"
	   +"ITEM_BASE_VALUE\tITEM_CLASS_ID\tSTATUE||RIDE_BASIS||CONTAINER_TYPE_OR_LIDLOCK\t"
	   +"CONTAINER_CAPACITY||LIQUID_CAPACITY\tCODED_SPELL_LIST";}

	//protected static final int RCP_FINALNAME=0;
	//protected static final int RCP_LEVEL=1;
	//protected static final int RCP_TICKS=2;
	protected static final int RCP_WOOD=3;
	protected static final int RCP_VALUE=4;
	protected static final int RCP_CLASSTYPE=5;
	protected static final int RCP_MISCTYPE=6;
	protected static final int RCP_CAPACITY=7;
	protected static final int RCP_SPELL=8;

	@Override
	public boolean tick(Tickable ticking, int tickID)
	{
		if((affected!=null)&&(affected instanceof MOB)&&(tickID==Tickable.TICKID_MOB))
		{
			final MOB mob=(MOB)affected;
			if(fireRequired)
			{
				if((buildingI==null)
				||(getRequiredFire(mob,0)==null))
				{
					messedUp=true;
					unInvoke();
				}
			}
		}
		return super.tick(ticking,tickID);
	}

	@Override public String parametersFile(){ return "blacksmith.txt";}
	@Override protected List<List<String>> loadRecipes(){return super.loadRecipes(parametersFile());}

	@Override public boolean supportsDeconstruction() { return true; }

	@Override
	protected boolean doLearnRecipe(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		fireRequired=false;
		return super.doLearnRecipe( mob, commands, givenTarget, auto, asLevel );
	}

	@Override
	public boolean mayICraft(final Item I)
	{
		if(I==null) return false;
		if(!super.mayBeCrafted(I))
			return false;
		if(((I.material()&RawMaterial.MATERIAL_MASK)!=RawMaterial.MATERIAL_METAL)
		&&((I.material()&RawMaterial.MATERIAL_MASK)!=RawMaterial.MATERIAL_MITHRIL))
			return false;
		if(CMLib.flags().isDeadlyOrMaliciousEffect(I))
			return false;
		if(isANativeItem(I.Name()) && (!(I instanceof Armor)) && (!(I instanceof Weapon)))
			return true;
		if(I instanceof Rideable)
		{
			final Rideable R=(Rideable)I;
			final int rideType=R.rideBasis();
			switch(rideType)
			{
			case Rideable.RIDEABLE_LADDER:
			case Rideable.RIDEABLE_SLEEP:
			case Rideable.RIDEABLE_SIT:
			case Rideable.RIDEABLE_TABLE:
				return true;
			default:
				return false;
			}
		}
		if(I instanceof DoorKey)
			return true;
		if(I instanceof Shield)
			return false;
		if(I instanceof Weapon)
			return false;
		if(I instanceof Light)
			return true;
		if(I instanceof Armor)
			return false;
		if(I instanceof Container)
			return true;
		if((I instanceof Drink)&&(!(I instanceof Potion)))
			return true;
		if(I instanceof FalseLimb)
			return true;
		if(I.rawProperLocationBitmap()==Wearable.WORN_HELD)
			return true;
		return (isANativeItem(I.Name()));
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
					{
						if(activity == CraftingActivity.LEARNING)
							commonEmote(mob,"<S-NAME> fail(s) to learn how to make "+buildingI.name()+".");
						else
							commonTell(mob,L("You've ruined @x1!",buildingI.name(mob)));
						buildingI.destroy();
					}
					else
					if(activity==CraftingActivity.LEARNING)
					{
						deconstructRecipeInto( buildingI, recipeHolder );
						buildingI.destroy();
					}
					else
						dropAWinner(mob,buildingI);
				}
				buildingI=null;
			}
		}
		super.unInvoke();
	}

	@Override
	public String getDecodedComponentsDescription(final MOB mob, final List<String> recipe)
	{
		return super.getComponentDescription( mob, recipe, RCP_WOOD );
	}

	@Override
	public boolean invoke(final MOB mob, Vector commands, Physical givenTarget, final boolean auto, final int asLevel)
	{
		final Vector originalCommands=(Vector)commands.clone();
		if(super.checkStop(mob, commands))
			return true;

		fireRequired=true;

		final CraftParms parsedVars=super.parseAutoGenerate(auto,givenTarget,commands);
		givenTarget=parsedVars.givenTarget;

		final PairVector<Integer,Integer> enhancedTypes=enhancedTypes(mob,commands);
		randomRecipeFix(mob,addRecipes(mob,loadRecipes()),commands,parsedVars.autoGenerate);
		if(commands.size()==0)
		{
			commonTell(mob,L("Make what? Enter \"@x1 list\" for a list, \"@x2 learn <item>\" to gain recipes, or \"@x3 stop\" to cancel.",triggerStrings()[0].toLowerCase(),triggerStrings()[0].toLowerCase(),triggerStrings()[0].toLowerCase()));
			return false;
		}
		if((!auto)
		&&(commands.size()>0)
		&&(((String)commands.firstElement()).equalsIgnoreCase("bundle")))
		{
			bundling=true;
			if(super.invoke(mob,commands,givenTarget,auto,asLevel))
				return super.bundle(mob,commands);
			return false;
		}
		final List<List<String>> recipes=addRecipes(mob,loadRecipes());
		final String str=(String)commands.elementAt(0);
		String startStr=null;
		bundling=false;
		int duration=4;
		final int[] cols={
				ListingLibrary.ColFixer.fixColWidth(16,mob.session()),
				ListingLibrary.ColFixer.fixColWidth(3,mob.session())
			};
		if(str.equalsIgnoreCase("list"))
		{
			String mask=CMParms.combine(commands,1);
			boolean allFlag=false;
			if(mask.equalsIgnoreCase("all"))
			{
				allFlag=true;
				mask="";
			}
			final StringBuffer buf=new StringBuffer(L("@x1 @x2 Metals required\n\r",CMStrings.padRight(L("Item"),cols[0]),CMStrings.padRight(L("Lvl"),cols[1])));
			for(int r=0;r<recipes.size();r++)
			{
				final List<String> V=recipes.get(r);
				if(V.size()>0)
				{
					final String item=replacePercent(V.get(RCP_FINALNAME),"");
					final int level=CMath.s_int(V.get(RCP_LEVEL));
					final String wood=getComponentDescription(mob,V,RCP_WOOD);
					if(((level<=xlevel(mob))||allFlag)
					&&((mask.length()==0)||mask.equalsIgnoreCase("all")||CMLib.english().containsString(item,mask)))
						buf.append(CMStrings.padRight(item,cols[0])+" "+CMStrings.padRight(""+level,cols[1])+" "+wood+"\n\r");
				}
			}
			commonTell(mob,buf.toString());
			enhanceList(mob);
			return true;
		}
		else
		if((commands.firstElement() instanceof String)&&(((String)commands.firstElement())).equalsIgnoreCase("learn"))
		{
			return doLearnRecipe(mob, commands, givenTarget, auto, asLevel);
		}

		activity = CraftingActivity.CRAFTING;
		buildingI=null;
		messedUp=false;
		String statue=null;
		if((commands.size()>1)&&((String)commands.lastElement()).startsWith("STATUE="))
		{
			statue=(((String)commands.lastElement()).substring(7)).trim();
			if(statue.length()==0)
				statue=null;
			else
				commands.removeElementAt(commands.size()-1);
		}
		int amount=-1;
		if((commands.size()>1)&&(CMath.isNumber((String)commands.lastElement())))
		{
			amount=CMath.s_int((String)commands.lastElement());
			commands.removeElementAt(commands.size()-1);
		}
		final String recipeName=CMParms.combine(commands,0);
		List<String> foundRecipe=null;
		final List<List<String>> matches=matchingRecipeNames(recipes,recipeName,true);
		for(int r=0;r<matches.size();r++)
		{
			final List<String> V=matches.get(r);
			if(V.size()>0)
			{
				final int level=CMath.s_int(V.get(RCP_LEVEL));
				if((parsedVars.autoGenerate>0)||(level<=xlevel(mob)))
				{
					foundRecipe=V;
					break;
				}
			}
		}
		if(foundRecipe==null)
		{
			commonTell(mob,L("You don't know how to make a '@x1'.  Try \"@x2 list\" for a list.",recipeName,triggerStrings[0].toLowerCase()));
			return false;
		}

		final String woodRequiredStr = foundRecipe.get(RCP_WOOD);
		final List<Object> componentsFoundList=getAbilityComponents(mob, woodRequiredStr, "make "+CMLib.english().startWithAorAn(recipeName),parsedVars.autoGenerate);
		if(componentsFoundList==null) return false;
		int woodRequired=CMath.s_int(woodRequiredStr);
		woodRequired=adjustWoodRequired(woodRequired,mob);

		if(amount>woodRequired) woodRequired=amount;
		final String misctype=foundRecipe.get(RCP_MISCTYPE);
		final int[] pm={RawMaterial.MATERIAL_METAL,RawMaterial.MATERIAL_MITHRIL};
		bundling=misctype.equalsIgnoreCase("BUNDLE");
		final int[][] data=fetchFoundResourceData(mob,
											woodRequired,"metal",pm,
											0,null,null,
											bundling,
											parsedVars.autoGenerate,
											enhancedTypes);
		if(data==null) return false;
		fixDataForComponents(data,componentsFoundList);
		woodRequired=data[0][FOUND_AMT];
		if(!bundling)
		{
			fireRequired=true;
			final Item fire=getRequiredFire(mob,parsedVars.autoGenerate);
			if(fire==null) return false;
		}
		else
			fireRequired=false;

		final Session session=mob.session();
		if((misctype.equalsIgnoreCase("statue"))
		&&(session!=null)
		&&((statue==null)||(statue.trim().length()==0)))
		{
			final Ability me=this;
			final Physical target=givenTarget;
			session.prompt(new InputCallback(InputCallback.Type.PROMPT,"",0)
			{
				@Override public void showPrompt() {session.promptPrint(L("What is a statue this of?\n\r: "));}
				@Override public void timedOut() {}
				@Override public void callBack()
				{
					final String of=this.input;
					if((of.trim().length()==0)||(of.indexOf('<')>=0))
						return;
					final Vector newCommands=(Vector)originalCommands.clone();
					newCommands.add("STATUE="+of);
					me.invoke(mob, newCommands, target, auto, asLevel);
				}
			});
			return false;
		}

		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;
		final int lostValue=parsedVars.autoGenerate>0?0:
			CMLib.materials().destroyResourcesValue(mob.location(),data[0][FOUND_AMT],data[0][FOUND_CODE],0,null)
			+CMLib.ableMapper().destroyAbilityComponents(componentsFoundList);
		buildingI=CMClass.getItem(foundRecipe.get(RCP_CLASSTYPE));
		if(buildingI==null)
		{
			commonTell(mob,L("There's no such thing as a @x1!!!",foundRecipe.get(RCP_CLASSTYPE)));
			return false;
		}
		duration=getDuration(CMath.s_int(foundRecipe.get(RCP_TICKS)),mob,CMath.s_int(foundRecipe.get(RCP_LEVEL)),4);
		String itemName=replacePercent(foundRecipe.get(RCP_FINALNAME),RawMaterial.CODES.NAME(data[0][FOUND_CODE])).toLowerCase();
		if(bundling)
			itemName="a "+woodRequired+"# "+itemName;
		else
			itemName=CMLib.english().startWithAorAn(itemName);
		buildingI.setName(itemName);
		startStr=L("<S-NAME> start(s) smithing @x1.",buildingI.name());
		displayText=L("You are smithing @x1",buildingI.name());
		verb=L("smithing @x1",buildingI.name());
		playSound="tinktinktink2.wav";
		buildingI.setDisplayText(L("@x1 lies here",itemName));
		buildingI.setDescription(itemName+". ");
		buildingI.basePhyStats().setWeight(getStandardWeight(woodRequired,bundling));
		buildingI.setBaseValue(CMath.s_int(foundRecipe.get(RCP_VALUE))+(woodRequired*(RawMaterial.CODES.VALUE(data[0][FOUND_CODE]))));
		buildingI.setMaterial(data[0][FOUND_CODE]);
		buildingI.basePhyStats().setLevel(CMath.s_int(foundRecipe.get(RCP_LEVEL)));
		buildingI.setSecretIdentity(getBrand(mob));
		final int capacity=CMath.s_int(foundRecipe.get(RCP_CAPACITY));
		final String spell=(foundRecipe.size()>RCP_SPELL)?foundRecipe.get(RCP_SPELL).trim():"";
		addSpells(buildingI,spell);

		if((misctype.equalsIgnoreCase("statue"))
		&&(statue!=null)
		&&(statue.trim().length()>0))
		{
			buildingI.setName(L("@x1 of @x2",itemName,statue.trim()));
			buildingI.setDisplayText(L("@x1 of @x2 is here",itemName,statue.trim()));
			buildingI.setDescription(L("@x1 of @x2. ",itemName,statue.trim()));
		}
		else
		if(buildingI instanceof Rideable)
		{
			setRideBasis((Rideable)buildingI,misctype);
			if(capacity==0)
				((Rideable)buildingI).setRiderCapacity(1);
			else
			if(capacity<5)
				((Rideable)buildingI).setRiderCapacity(capacity);
		}
		else
		if(buildingI instanceof Container)
		{
			((Container)buildingI).setCapacity(capacity+woodRequired);
			if(misctype.equalsIgnoreCase("LID"))
				((Container)buildingI).setDoorsNLocks(true,false,true,false,false,false);
			else
			if(misctype.equalsIgnoreCase("LOCK"))
			{
				((Container)buildingI).setDoorsNLocks(true,false,true,true,false,true);
				((Container)buildingI).setKeyName(Double.toString(Math.random()));
			}
			else
				((Container)buildingI).setContainTypes(getContainerType(misctype));
		}
		if(buildingI instanceof Drink)
		{
			if(CMLib.flags().isGettable(buildingI))
			{
				((Drink)buildingI).setLiquidHeld(capacity*50);
				((Drink)buildingI).setThirstQuenched(250);
				if((capacity*50)<250)
					((Drink)buildingI).setThirstQuenched(capacity*50);
				((Drink)buildingI).setLiquidRemaining(0);
			}
		}
		if(bundling) buildingI.setBaseValue(lostValue);
		buildingI.recoverPhyStats();
		buildingI.text();
		buildingI.recoverPhyStats();


		messedUp=!proficiencyCheck(mob,0,auto);

		if(bundling)
		{
			messedUp=false;
			duration=1;
			verb=L("bundling @x1",RawMaterial.CODES.NAME(buildingI.material()).toLowerCase());
			startStr=L("<S-NAME> start(s) @x1.",verb);
			displayText=L("You are @x1",verb);
		}

		if(parsedVars.autoGenerate>0)
		{
			commands.addElement(buildingI);
			return true;
		}

		final CMMsg msg=CMClass.getMsg(mob,buildingI,this,getActivityMessageType(),startStr);
		if(mob.location().okMessage(mob,msg))
		{
			mob.location().send(mob,msg);
			buildingI=(Item)msg.target();
			beneficialAffect(mob,mob,asLevel,duration);
			enhanceItem(mob,buildingI,enhancedTypes);
		}
		else
		if(bundling)
		{
			messedUp=false;
			aborted=false;
			unInvoke();
		}
		return true;
	}
}
