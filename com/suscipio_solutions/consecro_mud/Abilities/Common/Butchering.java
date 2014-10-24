package com.suscipio_solutions.consecro_mud.Abilities.Common;
import java.util.List;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.DiseaseAffect;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Items.interfaces.DeadBody;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Food;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Wearable;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_mud.core.interfaces.Drink;
import com.suscipio_solutions.consecro_mud.core.interfaces.ItemPossessor;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;



@SuppressWarnings({"unchecked","rawtypes"})
public class Butchering extends GatheringSkill
{
	@Override public String ID() { return "Butchering"; }
	private final static String localizedName = CMLib.lang().L("Butchering");
	@Override public String name() { return localizedName; }
	private static final String[] triggerStrings =I(new String[] {"BUTCHER","BUTCHERING","SKIN"});
	@Override public String[] triggerStrings(){return triggerStrings;}

	@Override public String supportedResourceString(){return "FLESH|LEATHER|BLOOD|BONE|MILK|EGGS|WOOL";}
	protected DeadBody body=null;
	protected boolean failed=false;
	public Butchering()
	{
		super();
		displayText=L("You are skinning and butchering something...");
		verb=L("skinning and butchering");
	}

	protected int getDuration(MOB mob, int weight)
	{
		int duration=((weight/(10+getXLEVELLevel(mob))));
		duration = super.getDuration(duration, mob, 1, 3);
		if(duration>40) duration=40;
		return duration;
	}
	@Override protected int baseYield() { return 1; }

	@Override
	public void unInvoke()
	{
		if(canBeUninvoked())
		{
			if((affected!=null)&&(affected instanceof MOB))
			{
				final MOB mob=(MOB)affected;
				if((body!=null)&&(!aborted))
				{
					if(failed)
						commonTell(mob,L("You messed up your butchering completely."));
					else
					{
						mob.location().show(mob,null,body,getActivityMessageType(),L("<S-NAME> manage(s) to skin and chop up <O-NAME>."));
						final List<RawMaterial> resources=body.charStats().getMyRace().myResources();
						final Vector diseases=new Vector();
						for(int i=0;i<body.numEffects();i++)
						{
							final Ability A=body.fetchEffect(i);
							if((A!=null)&&(A instanceof DiseaseAffect))
							{
								if((CMath.bset(((DiseaseAffect)A).spreadBitmap(),DiseaseAffect.SPREAD_CONSUMPTION))
								||(CMath.bset(((DiseaseAffect)A).spreadBitmap(),DiseaseAffect.SPREAD_CONTACT)))
									diseases.addElement(A);
							}
						}
						for(int y=0;y<abilityCode();y++)
						{
							for(int i=0;i<resources.size();i++)
							{
								final Item newFound=(Item)((Item)resources.get(i)).copyOf();
								if((newFound instanceof Food)||(newFound instanceof Drink))
									for(int d=0;d<diseases.size();d++)
										newFound.addNonUninvokableEffect((Ability)((Ability)diseases.elementAt(d)).copyOf());
								newFound.recoverPhyStats();
								mob.location().addItem(newFound,ItemPossessor.Expire.Resource);
								mob.location().recoverRoomStats();
							}
						}
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

		body=null;
		Item I=null;

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


		if((mob.isMonster()
		&&(!CMLib.flags().isAnimalIntelligence(mob)))
		&&(commands.size()==0))
		{
			for(int i=0;i<mob.location().numItems();i++)
			{
				final Item I2=mob.location().getItem(i);
				if((I2 instanceof DeadBody)
				&&(CMLib.flags().canBeSeenBy(I2,mob))
				&&(I2.container()==null))
				{
					I=I2;
					break;
				}
			}
		}
		else
			I=getTarget(mob,mob.location(),givenTarget,commands,Wearable.FILTER_UNWORNONLY);

		if(I==null) 
			return false;
		if((!(I instanceof DeadBody))
		   ||(((DeadBody)I).charStats()==null)
		   ||((DeadBody)I).playerCorpse()
		   ||(((DeadBody)I).charStats().getMyRace()==null))
		{
			commonTell(mob,L("You can't butcher @x1.",I.name(mob)));
			return false;
		}
		final List<RawMaterial> resources=((DeadBody)I).charStats().getMyRace().myResources();
		if((resources==null)||(resources.size()==0))
		{
			commonTell(mob,L("There doesn't appear to be any good parts on @x1.",I.name(mob)));
			return false;
		}
		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;
		failed=!proficiencyCheck(mob,0,auto);
		final CMMsg msg=CMClass.getMsg(mob,I,this,getActivityMessageType(),getActivityMessageType(),getActivityMessageType(),L("<S-NAME> start(s) butchering <T-NAME>."));
		if(mob.location().okMessage(mob,msg))
		{
			mob.location().send(mob,msg);
			body=(DeadBody)I;
			verb=L("skinning and butchering @x1",I.name());
			playSound="ripping.wav";
			final int duration=getDuration(mob,I.phyStats().weight());
			beneficialAffect(mob,mob,asLevel,duration);
			body.emptyPlease(false);
			body.destroy();
		}
		return true;
	}
}
