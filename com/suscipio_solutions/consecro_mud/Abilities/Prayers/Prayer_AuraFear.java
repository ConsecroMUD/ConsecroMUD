package com.suscipio_solutions.consecro_mud.Abilities.Prayers;
import java.util.HashSet;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Commands.interfaces.Command;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CharStats;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Wearable;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_mud.core.collections.XVector;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;
import com.suscipio_solutions.consecro_mud.core.interfaces.Tickable;



@SuppressWarnings({"unchecked","rawtypes"})
public class Prayer_AuraFear extends Prayer
{
	@Override public String ID() { return "Prayer_AuraFear"; }
	private final static String localizedName = CMLib.lang().L("Aura of Fear");
	@Override public String name() { return localizedName; }
	private final static String localizedStaticDisplay = CMLib.lang().L("(Fear Aura)");
	@Override public String displayText() { return localizedStaticDisplay; }
	@Override public int classificationCode(){return Ability.ACODE_PRAYER|Ability.DOMAIN_COMMUNING;}
	@Override protected int canAffectCode(){return Ability.CAN_MOBS|Ability.CAN_ROOMS|Ability.CAN_ITEMS;}
	@Override protected int canTargetCode(){return Ability.CAN_MOBS|Ability.CAN_ROOMS|Ability.CAN_ITEMS;}
	@Override public int abstractQuality(){ return Ability.QUALITY_MALICIOUS;}
	@Override public long flags(){return Ability.FLAG_UNHOLY;}
	private int ratingTickDown=4;

	public Prayer_AuraFear()
	{
		super();

		ratingTickDown = 4;
	}


	@Override
	public void unInvoke()
	{
		// undo the affects of this spell
		final Room R=CMLib.map().roomLocation(affected);
		final Environmental E=affected;

		super.unInvoke();

		if((canBeUninvoked())&&(R!=null)&&(E!=null))
			R.showHappens(CMMsg.MSG_OK_VISUAL,L("The fearful aura around @x1 fades.",E.name()));
	}

	@Override
	public boolean tick(Tickable ticking, int tickID)
	{
		if(affected==null)
			return super.tick(ticking,tickID);

		if((--ratingTickDown)>=0)
			return super.tick(ticking,tickID);
		ratingTickDown=4;
		final Room R=CMLib.map().roomLocation(affected);
		if(R==null)
			return super.tick(ticking,tickID);

		HashSet H=null;
		if((invoker()!=null)&&(invoker().location()==R))
		{
			H=new HashSet();
			invoker().getGroupMembers(H);
			H.add(invoker());
		}
		if((affected instanceof MOB)&&(affected!=invoker()))
		{
			if(H==null) H=new HashSet();
			((MOB)affected).getGroupMembers(H);
			H.add(affected);
		}
		for(int i=0;i<R.numInhabitants();i++)
		{
			final MOB M=R.fetchInhabitant(i);
			final MOB blame=((invoker!=null)&&(invoker!=M))?invoker:M;
			if((M!=null)&&((H==null)||(!H.contains(M))))
			{
				if(CMLib.dice().rollPercentage()<M.charStats().getSave(CharStats.STAT_SAVE_MIND))
					R.show(M,null,affected,CMMsg.MASK_EYES|CMMsg.MSG_NOISYMOVEMENT,L("<S-NAME> shudder(s) at the sight of <O-NAME>."));
				else
				{
					// do that fear thing
					// sit and cringe, or flee if mobile
					if(M.isMonster())
					{
						if((!CMLib.flags().isMobile(M))||(!M.isInCombat()))
						{
							final Command C=CMClass.getCommand("Sit");
							try{if(C!=null) C.execute(M,new XVector("Sit"),Command.METAFLAG_FORCED);}catch(final Exception e){}
							if(CMLib.flags().isSitting(M))
							{
								R.show(M,null,affected,CMMsg.MASK_EYES|CMMsg.MSG_HANDS|CMMsg.MASK_SOUND,L("<S-NAME> cringe(s) in fear at the sight of <O-NAME>."));
								final Ability A=CMClass.getAbility("Spell_Fear");
								if(A!=null) A.startTickDown(blame,M,Ability.TICKS_ALMOST_FOREVER);
							}
						}
						else
						if(M.isInCombat())
						{
							R.show(M,null,affected,CMMsg.MASK_EYES|CMMsg.MSG_NOISE,L("<S-NAME> scream(s) in fear at the sight of <O-NAME>."));
							final Command C=CMClass.getCommand("Flee");
							try{if(C!=null) C.execute(M,new XVector("Flee"),Command.METAFLAG_FORCED);}catch(final Exception e){}
						}
						else
						{
							R.show(M,null,affected,CMMsg.MASK_EYES|CMMsg.MSG_NOISE,L("<S-NAME> scream(s) in fear at the sight of <O-NAME>."));
							CMLib.tracking().beMobile(M,false,true,false,false,null,null);
						}
					}
					else
					{
						if(M.isInCombat())
						{
							R.show(M,null,affected,CMMsg.MASK_EYES|CMMsg.MSG_NOISE,L("<S-NAME> scream(s) in fear at the sight of <O-NAME>."));
							final Command C=CMClass.getCommand("Flee");
							try{if(C!=null) C.execute(M,new XVector("Flee"),Command.METAFLAG_FORCED);}catch(final Exception e){}
						}
						else
						{
							R.show(M,null,affected,CMMsg.MASK_EYES|CMMsg.MSG_NOISE,L("<S-NAME> scream(s) in fear at the sight of <O-NAME>."));
							CMLib.tracking().beMobile(M,false,true,false,false,null,null);
							if(M.location()==R)
							{
								R.show(M,null,affected,CMMsg.MASK_EYES|CMMsg.MSG_HANDS|CMMsg.MASK_SOUND,L("<S-NAME> cringe(s) in fear at the sight of <O-NAME>."));
								final Ability A=CMClass.getAbility("Spell_Fear");
								if(A!=null) A.startTickDown(blame,M,Ability.TICKS_ALMOST_FOREVER);
							}
						}
					}
				}
			}
		}
		return super.tick(ticking,tickID);
	}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		final Physical target=getAnyTarget(mob,commands,givenTarget,Wearable.FILTER_UNWORNONLY);
		if(target==null) return false;
		if(target.fetchEffect(ID())!=null)
		{
			mob.tell(L("The aura of fear is already surrounding @x1.",target.name(mob)));
			return false;
		}
		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		final boolean success=proficiencyCheck(mob,0,auto);

		if(success)
		{
			// it worked, so build a copy of this ability,
			// and add it to the affects list of the
			// affected MOB.  Then tell everyone else
			// what happened.
			int affectType=verbalCastCode(mob,target,auto);
			if((mob==target)&&(CMath.bset(affectType,CMMsg.MASK_MALICIOUS)))
				affectType=CMath.unsetb(affectType,CMMsg.MASK_MALICIOUS);
			final CMMsg msg=CMClass.getMsg(mob,target,this,affectType,auto?"":L("^S<S-NAME> @x1 for an aura of fear to surround <T-NAMESELF>.^?",prayWord(mob)));
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				mob.location().show(mob,target,CMMsg.MSG_OK_VISUAL,L("An aura descends over <T-NAME>!"));
				maliciousAffect(mob,target,asLevel,0,-1);
			}
		}
		else
			return maliciousFizzle(mob,target,L("<S-NAME> @x1 for an aura of fear, but <S-HIS-HER> plea is not answered.",prayWord(mob)));


		// return whether it worked
		return success;
	}
}


