package com.suscipio_solutions.consecro_mud.Abilities.Songs;
import java.util.Set;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Items.interfaces.DeadBody;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Wearable;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMProps;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;



@SuppressWarnings("rawtypes")
public class Play_Dirge extends Play
{
	@Override public String ID() { return "Play_Dirge"; }
	private final static String localizedName = CMLib.lang().L("Dirge");
	@Override public String name() { return localizedName; }
	@Override protected int canAffectCode(){return 0;}
	@Override public int abstractQuality(){ return Ability.QUALITY_BENEFICIAL_OTHERS;}
	@Override protected boolean persistantSong(){return false;}
	@Override protected boolean skipStandardSongTick(){return true;}
	@Override protected String songOf(){return CMLib.english().startWithAorAn(name());}
	@Override protected boolean HAS_QUANTITATIVE_ASPECT(){return false;}
	@Override protected boolean skipStandardSongInvoke(){return true;}

	@Override
	public int castingQuality(MOB mob, Physical target)
	{
		if(mob!=null)
		{
			if(mob.isInCombat())
				return Ability.QUALITY_INDIFFERENT;
		}
		return super.castingQuality(mob,target);
	}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		timeOut=0;
		final Item target=getTarget(mob,mob.location(),givenTarget,commands,Wearable.FILTER_UNWORNONLY);
		if(target==null) return false;

		if((!(target instanceof DeadBody))||(target.rawSecretIdentity().toUpperCase().indexOf("FAKE")>=0))
		{
			mob.tell(L("You may only play this for the dead."));
			return false;
		}
		if((((DeadBody)target).playerCorpse())&&(((DeadBody)target).getContents().size()>0))
		{
			mob.tell(L("You may not play for that body"));
			return false;
		}

		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		final boolean success=proficiencyCheck(mob,0,auto);
		unplayAll(mob,mob);
		if(success)
		{
			invoker=mob;
			originRoom=mob.location();
			commonRoomSet=getInvokerScopeRoomSet(null);
			String str=auto?L("^S@x1 begins to play!^?",songOf()):L("^S<S-NAME> begin(s) to play @x1 on @x2.^?",songOf(),instrumentName());
			if((!auto)&&(mob.fetchEffect(this.ID())!=null))
				str=L("^S<S-NAME> start(s) playing @x1 on @x2 again.^?",songOf(),instrumentName());

			for(int v=0;v<commonRoomSet.size();v++)
			{
				final Room R=(Room)commonRoomSet.elementAt(v);
				final String msgStr=getCorrectMsgString(R,str,v);
				final CMMsg msg=CMClass.getMsg(mob,null,this,somanticCastCode(mob,null,auto),msgStr);
				if(R.okMessage(mob,msg))
				{
					final Set<MOB> h=super.sendMsgAndGetTargets(mob, R, msg, givenTarget, auto);
					if(h==null) continue;

					for (final Object element : h)
					{
						final MOB follower=(MOB)element;

						double exp=10.0;
						final int levelLimit=CMProps.getIntVar(CMProps.Int.EXPRATE);
						final int levelDiff=follower.phyStats().level()-target.phyStats().level();
						if(levelDiff>levelLimit) exp=0.0;
						final int expGained=(int)Math.round(exp);

						// malicious songs must not affect the invoker!
						if(CMLib.flags().canBeHeardSpeakingBy(invoker,follower)&&(expGained>0))
							CMLib.leveler().postExperience(follower,null,null,expGained,false);
					}
					R.recoverRoomStats();
					R.showHappens(CMMsg.MSG_OK_VISUAL,L("@x1 fades away.",target.name()));
					target.destroy();
				}
			}
		}
		else
			mob.location().show(mob,null,CMMsg.MSG_NOISE,L("<S-NAME> hit(s) a foul note."));

		return success;
	}
}
