package com.suscipio_solutions.consecro_mud.Abilities.Spells;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CharStats;
import com.suscipio_solutions.consecro_mud.Exits.interfaces.Exit;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.Directions;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;
import com.suscipio_solutions.consecro_mud.core.interfaces.Tickable;


@SuppressWarnings("rawtypes")
public class Spell_Delirium extends Spell
{
	@Override public String ID() { return "Spell_Delirium"; }
	private final static String localizedName = CMLib.lang().L("Delirium");
	@Override public String name() { return localizedName; }
	private final static String localizedStaticDisplay = CMLib.lang().L("(Delirium)");
	@Override public String displayText() { return localizedStaticDisplay; }
	@Override public int maxRange(){return adjustedMaxInvokerRange(1);}
	@Override public int abstractQuality(){return Ability.QUALITY_MALICIOUS;}
	@Override protected int canAffectCode(){return CAN_MOBS;}
	int amountRemaining=0;
	@Override public int classificationCode(){ return Ability.ACODE_SPELL|Ability.DOMAIN_ILLUSION;}

	protected Environmental getRandomOtherName(Environmental likeThisOne)
	{
		if((invoker==null)||(invoker.location()==null))
			return likeThisOne;

		if(likeThisOne instanceof Room)
		{
			int tries=0;
			while((++tries)<1000)
			{
				final Room R=invoker.location().getArea().getRandomProperRoom();
				if(!R.displayText().equals(likeThisOne.displayText()))
					return R;
			}
		}
		else
		if(likeThisOne instanceof Exit)
		{
			int tries=0;
			while((++tries)<1000)
			{
				final Room R=invoker.location().getArea().getRandomProperRoom();
				for(int d=Directions.NUM_DIRECTIONS()-1;d>=0;d--)
				{
					final Exit x=R.getExitInDir(d);
					if((x!=null)&&(!x.name().equals(likeThisOne.name())))
						return x;
				}
			}
		}
		else
		if(likeThisOne instanceof MOB)
		{
			int tries=0;
			while((++tries)<1000)
			{
				final Room R=invoker.location().getArea().getRandomProperRoom();
				if((R!=null)&&(R.numInhabitants()>0))
				{
					final MOB possible=R.fetchRandomInhabitant();
					if((possible!=null)&&(!possible.name().equalsIgnoreCase(likeThisOne.name())))
						return possible;
				}
			}
		}
		else
		if(likeThisOne instanceof Item)
		{
			int tries=0;
			while((++tries)<1000)
			{
				final Room R=invoker.location().getArea().getRandomProperRoom();
				if(R.numItems()>0)
				{
					final Item possible=R.getRandomItem();
					if((possible!=null)&&(!possible.name().equalsIgnoreCase(likeThisOne.name())))
						return possible;
				}
				if(R.numInhabitants()>0)
				{
					final MOB owner=R.fetchRandomInhabitant();
					if((owner!=null)&&(owner.numItems()>0))
					{
						final Item possible=owner.getRandomItem();
						if((possible!=null)&&(!possible.name().equalsIgnoreCase(likeThisOne.name())))
							return possible;
					}
				}
			}
		}
		return null;
	}

	protected String getRand(Environmental likeThis)
	{
		final Environmental E=this.getRandomOtherName(likeThis);
		if(E==null)
		{
			if(likeThis instanceof MOB)
				return "someone";
			return "something";
		}
		return E.name();
	}

	protected String process(MOB mob, String str, Environmental obj)
	{
		if(obj==null) return str;

		int x=str.indexOf("<S-NAME>");
		if(x>=0)
			str=str.substring(0,x)+getRand(obj)+str.substring(x+("<S-NAME>").length());
		x=str.indexOf("<S-HIS-HER>");
		if(x>=0)
			str=str.substring(0,x)+getRand(obj)+str.substring(x+("<S-HIS-HER>").length());
		x=str.indexOf("<T-NAME>");
		if(x>=0)
			str=str.substring(0,x)+getRand(obj)+str.substring(x+("<T-NAME>").length());
		x=str.indexOf("<T-HIS-HER>");
		if(x>=0)
			str=str.substring(0,x)+getRand(obj)+str.substring(x+("<T-HIS-HER>").length());
		x=str.indexOf("<T-NAMESELF>");
		if(x>=0)
			str=str.substring(0,x)+getRand(obj)+str.substring(x+("<T-NAMESELF>").length());
		str=" "+str+" ";
		x=str.toUpperCase().indexOf(" "+obj.name().toUpperCase()+" ");
		if(x>=0)
			str=str.substring(0,x)+" "+getRand(obj)+" "+str.substring(x+(" "+obj.name()+" ").length());
		x=str.toUpperCase().indexOf(" YOU ");
		if(x>=0)
			str=str.substring(0,x)+" "+getRand(mob)+" "+str.substring(x+(" YOU ").length());
		x=str.toUpperCase().indexOf(" "+mob.name().toUpperCase()+" ");
		if(x>=0)
			str=str.substring(0,x)+" "+getRand(mob)+" "+str.substring(x+(" "+mob.name().toUpperCase()+" ").length());
		final MOB victim=mob.getVictim();
		if(victim!=null)
		{
			x=str.toUpperCase().indexOf(" "+victim.name().toUpperCase()+" ");
			if(x>=0)
				str=str.substring(0,x)+" "+getRand(victim)+" "+str.substring(x+(" "+victim.name().toUpperCase()+" ").length());
		}
		return str.trim();
	}

	@Override
	public boolean tick(Tickable ticking, int tickID)
	{
		if((tickID==Tickable.TICKID_MOB)
		&&(affected!=null)
		&&(affected instanceof MOB))
		{
			final MOB mob=(MOB)affected;
			amountRemaining-=mob.charStats().getStat(CharStats.STAT_INTELLIGENCE);
			if(amountRemaining<0)
				unInvoke();
		}
		return super.tick(ticking,tickID);
	}


	@Override
	public boolean okMessage(final Environmental myHost, final CMMsg msg)
	{
		if(!(affected instanceof MOB))
			return true;

		final MOB mob=(MOB)affected;

		final String othersMessage=msg.othersMessage();
		String sourceMessage=msg.sourceMessage();
		String targetMessage=msg.targetMessage();
		if((msg.amITarget(mob))&&(targetMessage!=null))
		{
			targetMessage=process(mob,process(mob,targetMessage,msg.target()),msg.target());
			if(!targetMessage.equals(msg.targetMessage()))
				msg.modify(msg.source(),msg.target(),msg.tool(),msg.sourceCode(),sourceMessage,msg.targetCode(),targetMessage,msg.othersCode(),othersMessage);
		}
		if((msg.amISource(mob))&&(sourceMessage!=null))
		{
			sourceMessage=process(mob,process(mob,sourceMessage,msg.source()),msg.source());
			if(!sourceMessage.equals(msg.sourceMessage()))
				msg.modify(msg.source(),msg.target(),msg.tool(),msg.sourceCode(),sourceMessage,msg.targetCode(),targetMessage,msg.othersCode(),othersMessage);
		}
		return true;
	}

	@Override
	public void unInvoke()
	{
		// undo the affects of this spell
		if(!(affected instanceof MOB))
			return;
		final MOB mob=(MOB)affected;
		super.unInvoke();

			if((mob.location()!=null)&&(!mob.amDead()))
				mob.location().show(mob,null,CMMsg.MSG_OK_VISUAL,L("<S-NAME> begin(s) to feel a bit less delirious."));
	}

	@Override
	public int castingQuality(MOB mob, Physical target)
	{
		if(mob!=null)
		{
			if((mob.isMonster())&&(mob.isInCombat()))
				return Ability.QUALITY_INDIFFERENT;
			if(target instanceof MOB)
			{
			}
		}
		return super.castingQuality(mob,target);
	}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		final MOB target=this.getTarget(mob,commands,givenTarget);
		if(target==null) return false;

		// the invoke method for spells receives as
		// parameters the invoker, and the REMAINING
		// command line parameters, divided into words,
		// and added as String objects to a vector.
		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;


		final boolean success=proficiencyCheck(mob,0,auto);

		if(success)
		{
			// it worked, so build a copy of this ability,
			// and add it to the affects list of the
			// affected MOB.  Then tell everyone else
			// what happened.
			invoker=mob;
			final CMMsg msg=CMClass.getMsg(mob,target,this,verbalCastCode(mob,target,auto),auto?"":L("^S<S-NAME> whisper(s) to <T-NAMESELF>.^?"));
			final CMMsg msg2=CMClass.getMsg(mob,target,this,CMMsg.MSK_CAST_MALICIOUS_VERBAL|CMMsg.TYP_MIND|(auto?CMMsg.MASK_ALWAYS:0),null);
			if((mob.location().okMessage(mob,msg))||(mob.location().okMessage(mob,msg2)))
			{
				mob.location().send(mob,msg);
				mob.location().send(mob,msg2);
				if(msg.value()<=0)
				{
					amountRemaining=300;
					maliciousAffect(mob,target,asLevel,0,-1);
					target.location().show(target,null,CMMsg.MSG_OK_ACTION,L("<S-NAME> go(es) under the grip of delirium!!"));
				}
			}
		}
		else
			return maliciousFizzle(mob,target,L("<S-NAME> whisper(s) to <T-NAMESELF>, but the spell fades."));

		// return whether it worked
		return success;
	}
}
