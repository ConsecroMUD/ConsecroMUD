package com.suscipio_solutions.consecro_mud.Abilities.Common;
import java.util.List;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.Libraries.interfaces.ListingLibrary;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.CMStrings;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_mud.core.interfaces.ItemPossessor;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;
import com.suscipio_solutions.consecro_mud.core.interfaces.Tickable;




@SuppressWarnings("rawtypes")
public class Smelting extends CraftingSkill
{
	@Override public String ID() { return "Smelting"; }
	private final static String localizedName = CMLib.lang().L("Smelting");
	@Override public String name() { return localizedName; }
	private static final String[] triggerStrings =I(new String[] {"SMELT","SMELTING"});
	@Override public String[] triggerStrings(){return triggerStrings;}
	@Override public String supportedResourceString(){return "METAL|MITHRIL";}
	public String parametersFormat(){ return "ITEM_NAME\tITEM_LEVEL\tBUILD_TIME_TICKS\t\t\t\tRESOURCE_NAME\tRESOURCE_NAME";}

	//protected static final int RCP_FINALNAME=0;
	//protected static final int RCP_LEVEL=1;
	//protected static final int RCP_TICKS=2;
	//private static final int RCP_WOOD_ALWAYSONEONE=3;
	//private static final int RCP_VALUE_DONTMATTER=4;
	//private static final int RCP_CLASSTYPE=5;
	protected static final int RCP_METALONE=6;
	protected static final int RCP_METALTWO=7;

	protected int amountMaking=0;

	@Override
	public boolean tick(Tickable ticking, int tickID)
	{
		if((affected!=null)&&(affected instanceof MOB)&&(tickID==Tickable.TICKID_MOB))
		{
			final MOB mob=(MOB)affected;
			if((buildingI==null)
			||(amountMaking<1)
			||(getRequiredFire(mob,0)==null))
			{
				messedUp=true;
				unInvoke();
			}
		}
		return super.tick(ticking,tickID);
	}

	@Override public String parametersFile(){ return "smelting.txt";}
	@Override protected List<List<String>> loadRecipes(){return super.loadRecipes(parametersFile());}

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
					amountMaking=amountMaking*(abilityCode());
					if(messedUp)
						commonEmote(mob,"<S-NAME> ruin(s) "+buildingI.name()+"!");
					else
					for(int i=0;i<amountMaking;i++)
					{
						final Item copy=(Item)buildingI.copyOf();
						copy.setMiscText(buildingI.text());
						copy.recoverPhyStats();
						mob.location().addItem(copy,ItemPossessor.Expire.Player_Drop);
					}
				}
				buildingI=null;
			}
		}
		super.unInvoke();
	}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		if(super.checkStop(mob, commands))
			return true;
		randomRecipeFix(mob,addRecipes(mob,loadRecipes()),commands,0);
		if(commands.size()==0)
		{
			commonTell(mob,L("Make what? Enter \"smelt list\" for a list, or \"smelt stop\" to cancel."));
			return false;
		}
		final List<List<String>> recipes=addRecipes(mob,loadRecipes());
		final String str=(String)commands.elementAt(0);
		String startStr=null;
		int duration=4;
		if(str.equalsIgnoreCase("list"))
		{
			String mask=CMParms.combine(commands,1);
			boolean allFlag=false;
			if(mask.equalsIgnoreCase("all"))
			{
				allFlag=true;
				mask="";
			}
			final int[] cols={
					ListingLibrary.ColFixer.fixColWidth(20,mob.session()),
					ListingLibrary.ColFixer.fixColWidth(3,mob.session()),
					ListingLibrary.ColFixer.fixColWidth(16,mob.session())
				};
			final StringBuffer buf=new StringBuffer(L("@x1 @x2 @x3 Metal #2\n\r",CMStrings.padRight(L("Item"),cols[0]),CMStrings.padRight(L("Lvl"),cols[1]),CMStrings.padRight(L("Metal #1"),cols[2])));
			for(int r=0;r<recipes.size();r++)
			{
				final List<String> V=recipes.get(r);
				if(V.size()>0)
				{
					final String item=replacePercent(V.get(RCP_FINALNAME),"");
					final int level=CMath.s_int(V.get(RCP_LEVEL));
					final String metal1=V.get(RCP_METALONE).toLowerCase();
					final String metal2=V.get(RCP_METALTWO).toLowerCase();
					if(((level<=xlevel(mob))||allFlag)
					&&((mask.length()==0)||mask.equalsIgnoreCase("all")||CMLib.english().containsString(item,mask)))
						buf.append(CMStrings.padRight(item,cols[0])+" "+CMStrings.padRight(""+level,cols[1])+" "+CMStrings.padRight(metal1,cols[2])+" "+metal2+"\n\r");
				}
			}
			commonTell(mob,buf.toString());
			return true;
		}
		final Item fire=getRequiredFire(mob,0);
		if(fire==null) return false;
		activity = CraftingActivity.CRAFTING;
		buildingI=null;
		messedUp=false;
		String recipeName=CMParms.combine(commands,0);
		int maxAmount=0;
		if((commands.size()>1)&&(CMath.isNumber((String)commands.lastElement())))
		{
			maxAmount=CMath.s_int((String)commands.lastElement());
			commands.removeElementAt(commands.size()-1);
			recipeName=CMParms.combine(commands,0);
		}
		List<String> foundRecipe=null;
		final List<List<String>> matches=matchingRecipeNames(recipes,recipeName,true);
		for(int r=0;r<matches.size();r++)
		{
			final List<String> V=matches.get(r);
			if(V.size()>0)
			{
				final int level=CMath.s_int(V.get(RCP_LEVEL));
				if(level<=xlevel(mob))
				{
					foundRecipe=V;
					break;
				}
			}
		}
		if(foundRecipe==null)
		{
			commonTell(mob,L("You don't know how to make '@x1'.  Try \"smelt list\" for a list.",recipeName));
			return false;
		}
		final String doneResourceDesc=foundRecipe.get(RCP_FINALNAME);
		final String resourceDesc1=foundRecipe.get(RCP_METALONE);
		final String resourceDesc2=foundRecipe.get(RCP_METALTWO);
		final int resourceCode1=RawMaterial.CODES.FIND_IgnoreCase(resourceDesc1);
		final int resourceCode2=RawMaterial.CODES.FIND_IgnoreCase(resourceDesc2);
		final int doneResourceCode=RawMaterial.CODES.FIND_IgnoreCase(doneResourceDesc);
		if((resourceCode1<0)||(resourceCode2<0)||(doneResourceCode<0))
		{
			commonTell(mob,L("ConsecroMUD error in this alloy.  Please let your local Immortal know."));
			return false;
		}
		final int amountResource1=CMLib.materials().findNumberOfResource(mob.location(),RawMaterial.CODES.GET(resourceCode1));
		final int amountResource2=CMLib.materials().findNumberOfResource(mob.location(),RawMaterial.CODES.GET(resourceCode2));
		if(amountResource1==0)
		{
			commonTell(mob,L("There is no @x1 here to make @x2 from.  It might need to be put down first.",resourceDesc1,doneResourceDesc));
			return false;
		}
		if(amountResource2==0)
		{
			commonTell(mob,L("There is no @x1 here to make @x2 from.  It might need to be put down first.",resourceDesc2,doneResourceDesc));
			return false;
		}
		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;
		amountMaking=amountResource1;
		if(amountResource2<amountResource1) amountMaking=amountResource2;
		if((maxAmount>0)&&(amountMaking>maxAmount)) amountMaking=maxAmount;
		CMLib.materials().destroyResourcesValue(mob.location(),amountMaking,RawMaterial.CODES.GET(resourceCode1),0,null);
		CMLib.materials().destroyResourcesValue(mob.location(),amountMaking,RawMaterial.CODES.GET(resourceCode2),0,null);
		duration=getDuration(CMath.s_int(foundRecipe.get(RCP_TICKS)),mob,CMath.s_int(foundRecipe.get(RCP_LEVEL)),6);
		amountMaking+=amountMaking;
		buildingI=(Item)CMLib.materials().makeResource(RawMaterial.CODES.GET(doneResourceCode),null,false,null);
		startStr=L("<S-NAME> start(s) smelting @x1.",doneResourceDesc.toLowerCase());
		displayText=L("You are smelting @x1",doneResourceDesc.toLowerCase());
		playSound="sizzling.wav";
		verb=L("smelting @x1",doneResourceDesc.toLowerCase());

		messedUp=!proficiencyCheck(mob,0,auto);

		final CMMsg msg=CMClass.getMsg(mob,buildingI,this,getActivityMessageType(),startStr);
		if(mob.location().okMessage(mob,msg))
		{
			mob.location().send(mob,msg);
			buildingI=(Item)msg.target();
			beneficialAffect(mob,mob,asLevel,duration);
		}
		return true;
	}
}
