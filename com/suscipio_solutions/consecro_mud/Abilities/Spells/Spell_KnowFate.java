package com.suscipio_solutions.consecro_mud.Abilities.Spells;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.Session;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.Log;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;
import com.suscipio_solutions.consecro_mud.core.interfaces.Tickable;


@SuppressWarnings({"unchecked","rawtypes"})
public class Spell_KnowFate extends Spell
{
	@Override public String ID() { return "Spell_KnowFate"; }
	private final static String localizedName = CMLib.lang().L("Know Fate");
	@Override public String name() { return localizedName; }
	@Override public int abstractQuality(){ return Ability.QUALITY_INDIFFERENT;}
	@Override protected int canAffectCode(){return 0;}
	@Override public int classificationCode(){ return Ability.ACODE_SPELL|Ability.DOMAIN_DIVINATION;}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		final MOB target=this.getTarget(mob,commands,givenTarget);
		if(target==null) return false;

		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		final boolean success=proficiencyCheck(mob,0,auto);

		if(success)
		{
			final CMMsg msg=CMClass.getMsg(mob,target,this,somanticCastCode(mob,target,auto),auto?"":L("^S<S-NAME> concentrate(s) on <T-NAMESELF>!^?"));
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);

				String[] aliasNames=new String[0];
				if(mob.playerStats()!=null)
					aliasNames=mob.playerStats().getAliasNames();
				final List<List<String>> combatV=new LinkedList<List<String>>();
				for (final String aliasName : aliasNames)
				{
					final String alias=mob.playerStats().getAlias(aliasName);
					if(alias.length()>0)
					{
						final List<String> all_stuff=CMParms.parseSquiggleDelimited(alias,true);
						  for(final String stuff : all_stuff)
						  {
							final Vector preCommands=CMParms.parse(stuff);
						  	final List THIS_CMDS=new Vector(preCommands.size());
						  	combatV.add(THIS_CMDS);
							for(int v=preCommands.size()-1;v>=0;v--)
								THIS_CMDS.add(0,preCommands.elementAt(v));
						  }
					}
				 }

				int iwin=0;
				int hewin=0;
				long ihp=0;
				long hehp=0;
				int draws=0;

				final Session fakeS=(Session)CMClass.getCommon("FakeSession");
				fakeS.initializeSession(null,Thread.currentThread().getThreadGroup().getName(),"MEMORY");
				for(int tries=0;tries<20;tries++)
				{
					final MOB newMOB=(MOB)mob.copyOf();
					final MOB newVictiM=(MOB)target.copyOf();
					final Room arenaR=CMClass.getLocale("StdRoom");
					arenaR.setArea(mob.location().getArea());
					newMOB.setSession(fakeS);
					arenaR.bringMobHere(newMOB,false);
					arenaR.bringMobHere(newVictiM,false);
					newMOB.setVictim(newVictiM);
					newVictiM.setVictim(newMOB);
					newMOB.setStartRoom(null);
					newVictiM.setStartRoom(null);

					int motionlessTries=10;
					while((!newMOB.amDead())
					&&(!newVictiM.amDead())
					&&(!newMOB.amDestroyed())
					&&(!newVictiM.amDestroyed()))
					{
						if(newMOB.commandQueSize()==0)
							for(final List<String> cmd : combatV)
								newMOB.enqueCommand(cmd, 0, 0);
						final int nowHp=newMOB.curState().getHitPoints();
						final int hisHp=newVictiM.curState().getHitPoints();
						try
						{
							newMOB.setVictim(newVictiM);
							newVictiM.setVictim(newMOB);
							CMLib.commands().postStand(newMOB,true);
							CMLib.commands().postStand(newVictiM,true);
							newMOB.tick(newMOB,Tickable.TICKID_MOB);
							newVictiM.tick(newVictiM,Tickable.TICKID_MOB);
						}
						catch(final Exception t)
						{
							Log.errOut("Spell_KnowFate",t);
						}
						final int nowHp2=newMOB.curState().getHitPoints();
						final int hisHp2=newVictiM.curState().getHitPoints();
						if((nowHp==nowHp2)&&(hisHp==hisHp2))
						{
							if(--motionlessTries==0)
								break;
						}
						else
							motionlessTries=10;
					}

					if((newMOB.amDead())||(newMOB.amDestroyed()))
					{
						hewin++;
						hehp+=newMOB.curState().getHitPoints();
					}
					else
					if((newVictiM.amDead())||(newVictiM.amDestroyed()))
					{
						iwin++;
						ihp+=newMOB.curState().getHitPoints();
					}
					else
						draws++;
					newMOB.destroy();
					newVictiM.destroy();
					arenaR.setArea(null);
					arenaR.destroy();
					fakeS.onlyPrint("--------------------------------------------\n\r");
				}
				String addendum="";
				if(draws>0)
					addendum=" with "+draws+" draws.";
				if(iwin>hewin)
					mob.tell(L("@x1% of the time, you defeat @x2 with @x3 hit points left@x4.",""+iwin,target.charStats().himher(),""+(ihp/iwin),addendum));
				else
				if(hewin>iwin)
					mob.tell(L("@x1% of the time you die, and @x2 still has @x3 hit points left@x4.",""+hewin,target.charStats().himher(),""+(hehp/hewin),addendum));
				else
				if(iwin>0)
					mob.tell(L("Half of the time, you defeat @x1 with @x2 hit points left@x3.",target.charStats().himher(),""+(ihp/iwin),addendum));
				else
					mob.tell(L("You can't hurt each other .. there were @x1% draws.",""+(draws*5)));
				//Log.debugOut(fakeS.afkMessage());
			}
		}
		else
			return beneficialVisualFizzle(mob,target,L("<S-NAME> concentrate(s) on <T-NAMESELF>, but look(s) frustrated."));

		// return whether it worked
		return success;
	}
}
