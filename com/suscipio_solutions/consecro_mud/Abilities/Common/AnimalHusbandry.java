package com.suscipio_solutions.consecro_mud.Abilities.Common;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CharStats;
import com.suscipio_solutions.consecro_mud.Common.interfaces.Social;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.collections.XVector;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;
import com.suscipio_solutions.consecro_mud.core.interfaces.Tickable;


@SuppressWarnings("rawtypes")
public class AnimalHusbandry extends CommonSkill
{
	@Override public String ID() { return "AnimalHusbandry"; }
	private final static String localizedName = CMLib.lang().L("Animal Husbandry");
	@Override public String name() { return localizedName; }
	private static final String[] triggerStrings =I(new String[] {"HUSBAND","ANIMALHUSBANDRY"});
	@Override public String[] triggerStrings(){return triggerStrings;}
	@Override public int classificationCode() {   return Ability.ACODE_COMMON_SKILL|Ability.DOMAIN_ANIMALAFFINITY; }

	protected MOB[] husbanding=null;
	protected boolean messedUp=false;

	public AnimalHusbandry()
	{
		super();
		displayText=L("You are, well, husbanding ...");
		verb=L("husbanding");
	}

	@Override
	public boolean tick(Tickable ticking, int tickID)
	{
		if((affected!=null)
		&&(affected instanceof MOB)
		&&(tickID==Tickable.TICKID_MOB))
		{
			final MOB mob=(MOB)affected;
			if((husbanding==null)||(mob.location()==null))
			{
				messedUp=true;
				unInvoke();
			}
			for(final MOB husbandM : husbanding)
			{
				if((husbandM==null)||(mob.location()==null))
				{
					messedUp=true;
					unInvoke();
				}
				if(!mob.location().isInhabitant(husbandM))
				{
					messedUp=true;
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
				if((husbanding!=null)&&(!aborted))
				{
					final MOB husbandM=husbanding[0];
					final MOB wifeM=husbanding[1];
					if((husbandM==null)||(wifeM==null))
						commonTell(mob,L("You've failed to husband properly..."));
					else
					if(wifeM.fetchEffect("Pregnancy")!=null)
						commonTell(mob,L("@x1 is already pregnant.",wifeM.name()));
					else
					{
						final Social S=CMLib.socials().fetchSocial("MATE", wifeM, true);
						if(S!=null)
						{
							if(husbanding[0].charStats().getMyRace().canBreedWith(husbanding[1].charStats().getMyRace()))
							{
								Ability A=CMClass.getAbility("Chant_Fertility");
								if(A!=null) A.startTickDown(husbandM, wifeM, 2);
								A=CMClass.getAbility("Chant_Fertility");
								if(A!=null) A.startTickDown(wifeM, husbandM, 2);
							}
							S.invoke(husbandM, new XVector<String>("MATE",wifeM.name()), wifeM, false);
						}
						else
						{
							if(proficiencyCheck(mob, 0, false))
							{
								final Ability A=CMClass.getAbility("Pregnancy");
								if((A!=null)
								&&(wifeM.fetchAbility(A.ID())==null)
								&&(wifeM.fetchEffect(A.ID())==null))
								{
									A.invoke(husbandM,wifeM,true,0);
								}
							}
						}
						mob.location().show(mob,husbandM,wifeM,getActivityMessageType(),L("<S-NAME> manage(s) to coax <T-NAME> into doing <T-HIS-HER> duty towards <O-NAME>."));
						final Ability A=wifeM.fetchEffect("Pregnancy");
						if(A!=null)
						{
							A.makeNonUninvokable();
							A.setSavable(true);
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

		verb=L("husbanding");
		verb=L("taming");
		husbanding=new MOB[2];
		if(!CMLib.law().doesHavePriviledgesHere(mob, mob.location()))
		{
			commonTell(mob,L("You need to be in your own pasture to do this."));
			return false;
		}
		if(commands.size()<2)
		{
			commonTell(mob,L("Which animals should I husband here?"));
			return false;
		}
		final String[] names=new String[]{(String)commands.get(0),CMParms.combine(commands,1)};
		int highestLevel=0;
		for(final String name : names)
		{
			final MOB M=mob.location().fetchInhabitant(name);
			if(M==null)
			{
				commonTell(mob,L("You don't see anyone called '@x1' here.",name));
				return false;
			}
			else
			{
				if(!CMLib.flags().canBeSeenBy(M,mob))
				{
					commonTell(mob,L("You don't see anyone called '@x1' here.",name));
					return false;
				}
				if((!M.isMonster())||(!CMLib.flags().isAnimalIntelligence(M)))
				{
					commonTell(mob,L("You can't use @x1.",M.name(mob)));
					return false;
				}
				if(M.fetchEffect("Pregnancy")!=null)
				{
					commonTell(mob,L("@x1 is already pregnant.",M.name(mob)));
					return false;
				}
				if(!M.charStats().getMyRace().canBreedWith(M.charStats().getMyRace()))
				{
					commonTell(mob,L("You can't use @x1.",M.name(mob)));
					return false;
				}
				if((!CMLib.flags().canMove(M))||(CMLib.flags().isBoundOrHeld(M)))
				{
					commonTell(mob,L("@x1 doesn't seem willing to cooperate.",M.name(mob)));
					return false;
				}
				if(M.charStats().getStat(CharStats.STAT_GENDER)=='M')
				{
					if(husbanding[0]!=null)
					{
						commonTell(mob,L("You can't use two males!"));
						return false;
					}
					husbanding[0]=M;
				}
				else
				if(M.charStats().getStat(CharStats.STAT_GENDER)=='F')
				{
					if(!M.isGeneric())
					{
						commonTell(mob,L("I'm sorry, @x1 just won't work out as a mother.",M.name(mob)));
						return false;
					}
					if(husbanding[1]!=null)
					{
						commonTell(mob,L("You can't use two females!"));
						return false;
					}
					husbanding[1]=M;
				}
				else
				{
					commonTell(mob,L("You can't use @x1 -- it's neuter!",M.name(mob)));
					return false;
				}
				if(M.phyStats().level()>highestLevel)
					highestLevel=M.phyStats().level();
			}
		}

		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;
		messedUp=!proficiencyCheck(mob,-highestLevel+(2*getXLEVELLevel(mob)),auto);
		final int duration=getDuration(55,mob,highestLevel,20);
		verb=L("husbanding @x1 to @x2",husbanding[0].name(),husbanding[1].name());
		final CMMsg msg=CMClass.getMsg(mob,null,this,getActivityMessageType(),L("<S-NAME> start(s) @x1.",verb));
		if(mob.location().okMessage(mob,msg))
		{
			mob.location().send(mob,msg);
			beneficialAffect(mob,mob,asLevel,duration);
		}
		return true;
	}
}
