package com.suscipio_solutions.consecro_mud.Abilities.Common;
import java.util.List;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.ItemPossessor;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;
import com.suscipio_solutions.consecro_mud.core.interfaces.Tickable;



@SuppressWarnings({"unchecked","rawtypes"})
public class Shearing extends CommonSkill
{
	@Override public String ID() { return "Shearing"; }
	private final static String localizedName = CMLib.lang().L("Shearing");
	@Override public String name() { return localizedName; }
	private static final String[] triggerStrings =I(new String[] {"SHEAR","SHEARING"});
	@Override public int classificationCode() {   return Ability.ACODE_COMMON_SKILL|Ability.DOMAIN_ANIMALAFFINITY; }
	@Override public String[] triggerStrings(){return triggerStrings;}

	private MOB sheep=null;
	protected boolean failed=false;
	public Shearing()
	{
		super();
		displayText=L("You are shearing something...");
		verb=L("shearing");
	}

	protected int getDuration(MOB mob, int weight)
	{
		int duration=((weight/(10+getXLEVELLevel(mob))));
		duration = super.getDuration(duration, mob, 1, 10);
		if(duration>40) duration=40;
		return duration;
	}
	@Override protected int baseYield() { return 1; }

	@Override
	public boolean tick(Tickable ticking, int tickID)
	{
		if((sheep!=null)
		&&(affected instanceof MOB)
		&&(((MOB)affected).location()!=null)
		&&((!((MOB)affected).location().isInhabitant(sheep))))
			unInvoke();
		return super.tick(ticking,tickID);
	}

	public Vector getMyWool(MOB M)
	{
		final Vector wool=new Vector();
		if((M!=null)
		&&(M.charStats().getMyRace()!=null)
		&&(M.charStats().getMyRace().myResources()!=null)
		&&(M.charStats().getMyRace().myResources().size()>0))
		{
			final List<RawMaterial> V=M.charStats().getMyRace().myResources();
			for(int v=0;v<V.size();v++)
				if((V.get(v) != null)
				&&(V.get(v).material()==RawMaterial.RESOURCE_WOOL))
					wool.addElement(V.get(v));
		}
		return wool;
	}

	@Override
	public void unInvoke()
	{
		if(canBeUninvoked())
		{
			if((affected!=null)&&(affected instanceof MOB))
			{
				final MOB mob=(MOB)affected;
				if((sheep!=null)&&(!aborted))
				{
					if((failed)||(!mob.location().isInhabitant(sheep)))
						commonTell(mob,L("You messed up your shearing completely."));
					else
					{
						mob.location().show(mob,null,sheep,getActivityMessageType(),L("<S-NAME> manage(s) to shear <O-NAME>."));
						spreadImmunity(sheep);
						final int yield=abilityCode()<=0?1:abilityCode();
						for(int i=0;i<yield;i++)
						{
							final Vector V=getMyWool(sheep);
							for(int v=0;v<V.size();v++)
							{
								RawMaterial I=(RawMaterial)V.elementAt(v);
								I=(RawMaterial)I.copyOf();
								mob.location().addItem(I,ItemPossessor.Expire.Monster_EQ);
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
		MOB target=null;
		final Room R=mob.location();
		if(R==null) return false;
		sheep=null;
		if((mob.isMonster()
		&&(!CMLib.flags().isAnimalIntelligence(mob)))
		&&(commands.size()==0))
		{
			for(int i=0;i<R.numInhabitants();i++)
			{
				final MOB M=R.fetchInhabitant(i);
				if((M!=mob)&&(CMLib.flags().canBeSeenBy(M,mob))&&(getMyWool(M).size()>0))
				{
					target=M;
					break;
				}
			}
		}
		else
		if(commands.size()==0)
			mob.tell(L("Shear what?"));
		else
			target=super.getTarget(mob,commands,givenTarget);

		if(target==null) return false;
		if((getMyWool(target).size()<=0)
		||(!target.okMessage(target,CMClass.getMsg(target,target,this,CMMsg.MSG_OK_ACTION,null))))
		{
			commonTell(mob,target,null,L("You can't shear <T-NAME>, there's no wool left on <T-HIM-HER>."));
			return false;
		}
		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;
		failed=!proficiencyCheck(mob,0,auto);
		final CMMsg msg=CMClass.getMsg(mob,target,this,getActivityMessageType(),getActivityMessageType(),getActivityMessageType(),L("<S-NAME> start(s) shearing <T-NAME>."));
		if(mob.location().okMessage(mob,msg))
		{
			mob.location().send(mob,msg);
			sheep=target;
			verb=L("shearing @x1",target.name());
			playSound="scissor.wav";
			final int duration=getDuration(mob,target.phyStats().weight());
			beneficialAffect(mob,mob,asLevel,duration);
		}
		return true;
	}
}
