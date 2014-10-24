package com.suscipio_solutions.consecro_mud.Abilities.Songs;
import java.util.Set;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Exits.interfaces.Exit;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.Directions;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;
import com.suscipio_solutions.consecro_mud.core.interfaces.Tickable;



@SuppressWarnings("rawtypes")
public class Song_Flight extends Song
{
	@Override public String ID() { return "Song_Flight"; }
	private final static String localizedName = CMLib.lang().L("Flight");
	@Override public String name() { return localizedName; }
	@Override public int abstractQuality(){ return Ability.QUALITY_OK_OTHERS;}
	@Override protected boolean skipStandardSongInvoke(){return true;}
	@Override protected boolean HAS_QUANTITATIVE_ASPECT(){return false;}

	@Override
	public boolean tick(Tickable ticking, int tickID)
	{
		if(!super.tick(ticking,tickID))
			return false;

		final MOB mob=(MOB)affected;
		if(mob==null) return false;
		if(mob==invoker) return true;
		if(mob.amFollowing()!=invoker)
			return false;
		return true;
	}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		timeOut=0;
		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		if((!auto)&&(!CMLib.flags().canSpeak(mob)))
		{
			mob.tell(L("You can't sing!"));
			return false;
		}

		final boolean success=proficiencyCheck(mob,0,auto);
		unsingAllByThis(mob,mob);
		if(success)
		{
			invoker=mob;
			originRoom=mob.location();
			commonRoomSet=getInvokerScopeRoomSet(null);
			String str=auto?L("^SThe @x1 begins to play!^?",songOf()):L("^S<S-NAME> begin(s) to sing the @x1.^?",songOf());
			if((!auto)&&(mob.fetchEffect(this.ID())!=null))
				str=L("^S<S-NAME> start(s) the @x1 over again.^?",songOf());

			for(int v=0;v<commonRoomSet.size();v++)
			{
				final Room R=(Room)commonRoomSet.elementAt(v);
				final String msgStr=getCorrectMsgString(R,str,v);
				final CMMsg msg=CMClass.getMsg(mob,null,this,verbalCastCode(mob,null,auto),msgStr);
				if(R.okMessage(mob,msg))
				{
					final Set<MOB> h=sendMsgAndGetTargets(mob, R, msg, givenTarget, auto);
					if(h==null) continue;

					for (final Object element : h)
					{
						final MOB follower=(MOB)element;
						// malicious songs must not affect the invoker!
						int affectType=CMMsg.MSG_CAST_VERBAL_SPELL;
						if((castingQuality(mob,follower)==Ability.QUALITY_MALICIOUS)&&(follower!=mob))
							affectType=CMMsg.MSG_CAST_ATTACK_VERBAL_SPELL;
						if(auto) affectType=affectType|CMMsg.MASK_ALWAYS;

						if((CMLib.flags().canBeHeardSpeakingBy(invoker,follower)&&(follower.fetchEffect(this.ID())==null)))
						{
							final CMMsg msg2=CMClass.getMsg(mob,follower,this,affectType,null);
							if(R.okMessage(mob,msg2))
							{
								follower.location().send(mob,msg2);
								if(msg2.value()<=0)
								{
									int directionCode=-1;
									String direction="";
									for(int d=0;d<7;d++)
									{
										final Exit thisExit=follower.location().getExitInDir(d);
										if(thisExit!=null)
										{
											if(thisExit.isOpen())
											{
												direction=Directions.getDirectionName(d);
												break;
											}
										}
									}
									directionCode=Directions.getDirectionCode(direction);
									if(directionCode<0)
									{
										mob.tell(L("Flee where?!"));
										return false;
									}
									CMLib.tracking().walk(follower,directionCode,true,false);
								}
							}
						}
					}
				}
			}
		}
		else
			mob.location().show(mob,null,CMMsg.MSG_NOISE,L("<S-NAME> hit(s) a foul note."));

		return success;
	}
}
