package com.suscipio_solutions.consecro_mud.Abilities.Common;
import java.util.List;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.ItemCraftor;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Scroll;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Wearable;
import com.suscipio_solutions.consecro_mud.Libraries.interfaces.ExpertiseLibrary;
import com.suscipio_solutions.consecro_mud.Libraries.interfaces.ListingLibrary;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.CMProps;
import com.suscipio_solutions.consecro_mud.core.CMStrings;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;
import com.suscipio_solutions.consecro_mud.core.interfaces.Tickable;



@SuppressWarnings({"unchecked","rawtypes"})
public class ScrollScribing extends SpellCraftingSkill implements ItemCraftor
{
	@Override public String ID() { return "ScrollScribing"; }
	private final static String localizedName = CMLib.lang().L("Scroll Scribing");
	@Override public String name() { return localizedName; }
	private static final String[] triggerStrings =I(new String[] {"ENSCRIBE","SCROLLSCRIBE","SCROLLSCRIBING"});
	@Override public String[] triggerStrings(){return triggerStrings;}
	@Override protected ExpertiseLibrary.SkillCostDefinition getRawTrainingCost() { return CMProps.getSkillTrainCostFormula(ID()); }
	@Override public String supportedResourceString(){return "MISC";}
	@Override public String parametersFormat(){ return "SPELL_ID\tRESOURCE_NAME";}

	String oldName="";
	protected Ability theSpell=null;
	@Override
	public boolean tick(Tickable ticking, int tickID)
	{
		if((affected!=null)&&(affected instanceof MOB)&&(tickID==Tickable.TICKID_MOB))
		{
			final MOB mob=(MOB)affected;
			if((buildingI==null)
			||(theSpell==null))
			{
				aborted=true;
				unInvoke();
			}
			else
			if(tickUp==0)
			{
				commonEmote(mob,"<S-NAME> start(s) scribing "+buildingI.name()+".");
				displayText=L("You are scribing @x1",buildingI.name());
				verb=L("scribing @x1",buildingI.name());
			}
		}
		return super.tick(ticking,tickID);
	}

	@Override public String parametersFile(){ return "scribing.txt";}
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
					if(messedUp)
					{
						if(oldName.length()>0)
							commonTell(mob,L("Something went wrong! @x1 explodes!",(Character.toUpperCase(oldName.charAt(0))+oldName.substring(1))));
					}
					else
						mob.addItem(buildingI);
				}
				buildingI=null;
			}
		}
		super.unInvoke();
	}

	protected int spellLevel(MOB mob, Ability A)
	{
		int lvl=CMLib.ableMapper().qualifyingLevel(mob,A);
		if(lvl<0) lvl=CMLib.ableMapper().lowestQualifyingLevel(A.ID());
		switch(lvl)
		{
		case 0: return lvl;
		case 1: return lvl;
		case 2: return lvl+1;
		case 3: return lvl+1;
		case 4: return lvl+2;
		case 5: return lvl+2;
		case 6: return lvl+3;
		case 7: return lvl+3;
		case 8: return lvl+4;
		case 9: return lvl+4;
		default: return lvl+5;
		}
	}

	@Override public boolean supportsDeconstruction() { return false; }

	@Override public ItemKeyPair craftItem(String recipe) { return craftItem(recipe,0,false); }

	protected Item buildItem(Ability theSpell, int level)
	{
		buildingI=CMClass.getItem("GenScroll");
		((Scroll)buildingI).setSpellList(theSpell.ID());
		buildingI.setName(L("a scroll of @x1",theSpell.name().toLowerCase()));
		buildingI.setDisplayText(L("a scroll of @x1 sits here.",theSpell.name().toLowerCase()));
		buildingI.setDescription("");
		buildingI.basePhyStats().setLevel(level);
		buildingI.phyStats().setLevel(level);
		buildingI.recoverPhyStats();
		buildingI.setUsesRemaining(1);
		buildingI.text();
		return buildingI;
	}

	@Override
	public String getDecodedComponentsDescription(final MOB mob, final List<String> recipe)
	{
		return "Not implemented";
	}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		if(super.checkStop(mob, commands))
			return true;

		final CraftParms parsedVars=super.parseAutoGenerate(auto,givenTarget,commands);
		givenTarget=parsedVars.givenTarget;
		if(parsedVars.autoGenerate>0)
		{
			final Ability theSpell=super.getCraftableSpellRecipeSpell(commands);
			if(theSpell==null) return false;
			final int level=spellLevel(mob,theSpell);
			buildingI=buildItem(theSpell, level);
			commands.addElement(buildingI);
			return true;
		}
		randomRecipeFix(mob,addRecipes(mob,loadRecipes()),commands,0);
		if(commands.size()<1)
		{
			commonTell(mob,L("Enscribe what? Enter \"enscribe list\" for a list, or \"enscribe stop\" to cancel."));
			return false;
		}
		final List<List<String>> recipes=addRecipes(mob,loadRecipes());
		final String pos=(String)commands.lastElement();
		if((commands.firstElement() instanceof String)&&(((String)commands.firstElement())).equalsIgnoreCase("list"))
		{
			String mask=CMParms.combine(commands,1);
			boolean allFlag=false;
			if(mask.equalsIgnoreCase("all"))
			{
				allFlag=true;
				mask="";
			}
			final StringBuffer buf=new StringBuffer(L("Scrolls you know how to enscribe:\n\r"));
			final int colWidth=ListingLibrary.ColFixer.fixColWidth(25,mob.session());
			buf.append(CMStrings.padRight(L("Spell"),colWidth)+" "+CMStrings.padRight(L("Spell"),colWidth)+" "+CMStrings.padRight(L("Spell"),colWidth));
			int toggler=1;
			final int toggleTop=3;
			for(int r=0;r<recipes.size();r++)
			{
				final List<String> V=recipes.get(r);
				if(V.size()>0)
				{
					final String spell=V.get(0);
					final Ability A=mob.fetchAbility(spell);
					if((A!=null)
					&&((spellLevel(mob,A)>=0)||(allFlag))
					&&((xlevel(mob)>=spellLevel(mob,A))||(allFlag))
					&&((mask.length()==0)||mask.equalsIgnoreCase("all")||CMLib.english().containsString(spell,mask)))
					{
						buf.append(CMStrings.padRight(A.name(),colWidth)+((toggler!=toggleTop)?" ":"\n\r"));
						if(++toggler>toggleTop) toggler=1;
					}
				}
			}
			if(toggler!=1) buf.append("\n\r");
			commonTell(mob,buf.toString());
			return true;
		}
		else
		if((!auto)&&(commands.size()<2))
		{
			commonEmote(mob,"You must specify what magic you wish to enscribe, and the paper to enscribe it in.");
			return false;
		}
		else
		{
			buildingI=getTarget(mob,null,givenTarget,CMParms.parse(pos),Wearable.FILTER_UNWORNONLY);
			commands.remove(pos);
			if(buildingI==null) return false;
			if(!mob.isMine(buildingI))
			{
				commonTell(mob,L("You'll need to pick that up first."));
				return false;
			}
			if(!CMath.bset(buildingI.material(),RawMaterial.MATERIAL_PAPER))
			{
				commonTell(mob,L("@x1 isn't even made of paper!",buildingI.name(mob)));
				return false;
			}
			if(!(buildingI instanceof Scroll))
			{
				commonTell(mob,L("There's can't enscribe magic on @x1!",buildingI.name(mob)));
				return false;
			}
			if(((Scroll)buildingI).getSpells().size()>0)
			{
				commonTell(mob,L("You can only scribe on blank scrolls."));
				return false;
			}
			final String recipeName=CMParms.combine(commands,0);
			theSpell=null;
			int theSpellLevel=1;
			String ingredient="";
			for(int r=0;r<recipes.size();r++)
			{
				final List<String> V=recipes.get(r);
				if(V.size()>0)
				{
					final String spell=V.get(0);
					final Ability A=mob.fetchAbility(spell);
					if((A!=null)
					&&(xlevel(mob)>=spellLevel(mob,A))
					&&(A.name().equalsIgnoreCase(recipeName)))
					{
						theSpell=A;
						theSpellLevel=spellLevel(mob,A);
						ingredient=V.get(1);
					}
				}
			}
			if(theSpell==null)
			{
				commonTell(mob,L("You don't know how to enscribe '@x1'.  Try \"enscribe list\" for a list.",recipeName));
				return false;
			}
			int experienceToLose=10;
			experienceToLose+=CMLib.ableMapper().qualifyingLevel(mob,theSpell)*10;
			experienceToLose-=CMLib.ableMapper().qualifyingClassLevel(mob,theSpell)*5;
			final int resourceType=RawMaterial.CODES.FIND_IgnoreCase(ingredient);

			int[][] data = null;
			if(resourceType>0)
			{
				final int[] pm={resourceType};
				data=fetchFoundResourceData(mob,
											1,ingredient,pm,
											0,null,null,
											bundling,
											-1,
											null);
				if(data==null) return false;
			}
			if(experienceToLose<10) experienceToLose=10;

			if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
				return false;

			if((resourceType>0)&&(data != null))
				CMLib.materials().destroyResourcesValue(mob.location(),data[0][FOUND_AMT],data[0][FOUND_CODE],0,null);

			playSound=null;
			experienceToLose=getXPCOSTAdjustment(mob,experienceToLose);
			CMLib.leveler().postExperience(mob,null,null,-experienceToLose,false);
			commonTell(mob,L("You lose @x1 experience points for the effort.",""+experienceToLose));
			oldName=buildingI.name();
			buildingI.destroy();
			buildingI=buildItem(theSpell, theSpellLevel);
			buildingI.setSecretIdentity(getBrand(mob));

			int duration=CMLib.ableMapper().qualifyingLevel(mob,theSpell)*5;
			if(duration<10) duration=10;
			messedUp=!proficiencyCheck(mob,0,auto);

			final CMMsg msg=CMClass.getMsg(mob,buildingI,this,getActivityMessageType(),null);
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				buildingI=(Item)msg.target();
				beneficialAffect(mob,mob,asLevel,duration);
			}
		}
		return true;
	}
}
