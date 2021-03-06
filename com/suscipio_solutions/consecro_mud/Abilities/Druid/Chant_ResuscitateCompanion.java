package com.suscipio_solutions.consecro_mud.Abilities.Druid;
import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.MendingSkill;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Items.interfaces.DeadBody;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Wearable;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;


@SuppressWarnings({"rawtypes"})
public class Chant_ResuscitateCompanion extends Chant implements MendingSkill
{
	@Override public String ID() { return "Chant_ResuscitateCompanion"; }
	private final static String localizedName = CMLib.lang().L("Resuscitate Companion");
	@Override public String name() { return localizedName; }
	@Override public String displayText() { return ""; }
	@Override public int classificationCode(){return Ability.ACODE_CHANT|Ability.DOMAIN_ANIMALAFFINITY;}
	@Override public int abstractQuality(){ return Ability.QUALITY_INDIFFERENT;}
	@Override public boolean isAutoInvoked() { return true; }
	@Override protected int canAffectCode(){return Ability.CAN_MOBS;}
	@Override protected int canTargetCode(){return Ability.CAN_ITEMS;}
	private final List<WeakReference<DeadBody>> companionMobs=new LinkedList<WeakReference<DeadBody>>();

	private boolean isCompanionBody(final DeadBody body)
	{
		for(final Iterator<WeakReference<DeadBody>> m=companionMobs.iterator();m.hasNext();)
		{
			final WeakReference<DeadBody> wM=m.next();
			if(wM.get()==body)
				return true;
		}
		return false;
	}
	
	@Override
	public boolean supportsMending(Physical item)
	{
		return (item instanceof DeadBody)
				&&(((DeadBody)item).savedMOB()!=null)
				&&(!((DeadBody)item).playerCorpse())
				&&(CMLib.flags().isAnimalIntelligence(((DeadBody)item).savedMOB()));
	}

	@Override
	public boolean okMessage(Environmental myHost, CMMsg msg)
	{
		if(!super.okMessage(myHost, msg))
			return false;
		if(affected instanceof MOB)
		{
			final MOB myChar=(MOB)affected;
			if((msg.sourceMinor()==CMMsg.TYP_DEATH)
			&&(msg.source().isMonster())
			&&(CMLib.flags().isAnimalIntelligence(msg.source()))
			&&(msg.source().amFollowing()==myChar))
			{
				final Chant_ResuscitateCompanion A=(Chant_ResuscitateCompanion)myChar.fetchAbility(ID());
				final MOB aniM=msg.source();
				final Room room=myChar.location();
				if(A!=null)
				{
					msg.addTrailerRunnable(new Runnable()
					{
						@Override
						public void run()
						{
							if((room!=null)
							&&(!aniM.amDestroyed())
							&&(aniM.amDead()))
							{
								for(final Iterator<WeakReference<DeadBody>> m=companionMobs.iterator();m.hasNext();)
								{
									final WeakReference<DeadBody> wM=m.next();
									if(wM.get()==null)
										m.remove();
								}
								for(int i=room.numItems()-1;i>=0;i--)
								{
									final Item I=room.getItem(i);
									if((I instanceof DeadBody)
									&&(((DeadBody)I).mobName().equals(aniM.Name()))
									&&(!isCompanionBody((DeadBody)I)))
									{
										final List<WeakReference<DeadBody>> companionMobs=A.companionMobs;
										while(companionMobs.size()>10)
											companionMobs.remove(companionMobs.iterator().next());
										companionMobs.add(new WeakReference<DeadBody>((DeadBody)I));
									}
								}
							}
						}
					});
				}
			}
		}
		return true;
	}
	
	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		Physical body=null;
		body=getTarget(mob,mob.location(),givenTarget,commands,Wearable.FILTER_UNWORNONLY);
		if(body==null) 
			return false;
		if((!(body instanceof DeadBody))
		||(((DeadBody)body).mobName().length()==0)
		||(((DeadBody)body).savedMOB()==null))
		{
			mob.tell(L("@x1 can not be resuscitated.",body.Name()));
			return false;
		}
		boolean playerCorpse=((DeadBody)body).playerCorpse();
		if(playerCorpse)
		{
			mob.tell(L("You can't resuscitate @x1.",((DeadBody)body).charStats().himher()));
			return false;
		}
		if(!isCompanionBody((DeadBody)body))
		{
			mob.tell(L("@x1 was either not your companion, or you were not present at the time of death.",((DeadBody)body).mobName()));
			return false;
		}

		if(mob.isInCombat())
		{
			mob.tell(L("You can't do that while in combat!"));
			return false;
		}

		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		boolean success=proficiencyCheck(mob,0,auto);
		if(success)
		{
			final CMMsg msg=CMClass.getMsg(mob,body,this,verbalCastCode(mob,body,auto),auto?L("<T-NAME> is resuscitated!"):L("^S<S-NAME> resuscitate(s) <T-NAMESELF>!^?"));
			if(mob.location().okMessage(mob,msg))
			{
				invoker=mob;
				mob.location().send(mob,msg);
				if(playerCorpse)
					success = CMLib.utensils().resurrect(mob,mob.location(), (DeadBody)body, super.getXPCOSTLevel(mob));
				else
				{
					final MOB rejuvedMOB=((DeadBody)body).savedMOB();
					for(Iterator<WeakReference<DeadBody>> m=companionMobs.iterator();m.hasNext();)
					{
						WeakReference<DeadBody> wM=m.next();
						if(wM.get()==body)
							m.remove();
					}
					rejuvedMOB.recoverCharStats();
					rejuvedMOB.recoverMaxState();
					body.delEffect(body.fetchEffect("Age")); // so misskids doesn't record it
					body.destroy();
					rejuvedMOB.bringToLife(mob.location(),true);
					rejuvedMOB.location().show(rejuvedMOB,null,CMMsg.MSG_NOISYMOVEMENT,L("<S-NAME> get(s) up!"));
				}
			}
		}
		else
			beneficialWordsFizzle(mob,body,auto?"":L("<S-NAME> attempt(s) to resuscitate <T-NAMESELF>, but nothing happens."));
		// return whether it worked
		return success;
	}
}
