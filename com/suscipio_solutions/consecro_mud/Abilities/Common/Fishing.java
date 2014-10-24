package com.suscipio_solutions.consecro_mud.Abilities.Common;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Container;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.ItemPossessor;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;
import com.suscipio_solutions.consecro_mud.core.interfaces.Tickable;



@SuppressWarnings("rawtypes")
public class Fishing extends GatheringSkill
{
	@Override public String ID() { return "Fishing"; }
	private final static String localizedName = CMLib.lang().L("Fishing");
	@Override public String name() { return localizedName; }
	private static final String[] triggerStrings =I(new String[] {"FISH"});
	@Override public String[] triggerStrings(){return triggerStrings;}
	@Override public int classificationCode(){return Ability.ACODE_COMMON_SKILL|Ability.DOMAIN_GATHERINGSKILL;}
	@Override public String supportedResourceString(){return "FLESH";}

	protected Item found=null;
	protected String foundShortName="";
	public Fishing()
	{
		super();
		displayText=L("You are fishing...");
		verb=L("fishing");
	}

	protected int getDuration(MOB mob, int level)
	{
		return getDuration(45,mob,level,15);
	}
	@Override protected int baseYield() { return 1; }

	@Override
	public boolean tick(Tickable ticking, int tickID)
	{
		if((affected!=null)&&(affected instanceof MOB)&&(tickID==Tickable.TICKID_MOB))
		{
			final MOB mob=(MOB)affected;
			if(tickUp==6)
			{
				if(found!=null)
					commonTell(mob,L("You got a tug on the line!"));
				else
				{
					final StringBuffer str=new StringBuffer(L("Nothing is biting around here.\n\r"));
					commonTell(mob,str.toString());
					unInvoke();
				}

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
				if((found!=null)&&(!aborted)&&(!helping))
				{
					final int amount=CMLib.dice().roll(1,3,0)*(abilityCode());
					String s="s";
					if(amount==1) s="";
					mob.location().show(mob,null,getActivityMessageType(),L("<S-NAME> manage(s) to catch @x1 pound@x2 of @x3.",""+amount,s,foundShortName));
					for(int i=0;i<amount;i++)
					{
						final Item newFound=(Item)found.copyOf();
						mob.location().addItem(newFound,ItemPossessor.Expire.Player_Drop);
						if((mob.riding()!=null)&&(mob.riding() instanceof Container))
							newFound.setContainer((Container)mob.riding());
						CMLib.commands().postGet(mob,null,newFound,true);
					}
				}
			}
		}
		super.unInvoke();
	}


	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		if(super.checkStop(mob, commands))
			return true;
		bundling=false;
		if((!auto)
		&&(commands.size()>0)
		&&(((String)commands.firstElement()).equalsIgnoreCase("bundle")))
		{
			bundling=true;
			if(super.invoke(mob,commands,givenTarget,auto,asLevel))
				return super.bundle(mob,commands);
			return false;
		}

		int foundFish=-1;
		boolean maybeFish=false;
		if(mob.location()!=null)
		{
			for(final int fishCode : RawMaterial.CODES.FISHES())
				if(mob.location().myResource()==fishCode)
				{
					foundFish=fishCode;
					maybeFish=true;
				}
				else
				if((mob.location().resourceChoices()!=null)
				&&(mob.location().resourceChoices().contains(Integer.valueOf(fishCode))))
					maybeFish=true;
		}
		if(!maybeFish)
		{
			commonTell(mob,L("The fishing doesn't look too good around here."));
			return false;
		}
		verb=L("fishing");
		found=null;
		playSound="fishreel.wav";
		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;
		if((proficiencyCheck(mob,0,auto))
		   &&(foundFish>0))
		{
			found=(Item)CMLib.materials().makeResource(foundFish,Integer.toString(mob.location().domainType()),false,null);
			foundShortName="nothing";
			if(found!=null)
				foundShortName=RawMaterial.CODES.NAME(found.material()).toLowerCase();
		}
		final int duration=getDuration(mob,1);
		final CMMsg msg=CMClass.getMsg(mob,found,this,getActivityMessageType(),L("<S-NAME> start(s) fishing."));
		if(mob.location().okMessage(mob,msg))
		{
			mob.location().send(mob,msg);
			found=(Item)msg.target();
			beneficialAffect(mob,mob,asLevel,duration);
		}
		return true;
	}
}
