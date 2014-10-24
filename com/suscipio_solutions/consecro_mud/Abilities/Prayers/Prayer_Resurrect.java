package com.suscipio_solutions.consecro_mud.Abilities.Prayers;
import java.util.List;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.MendingSkill;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Items.interfaces.DeadBody;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Wearable;
import com.suscipio_solutions.consecro_mud.Libraries.interfaces.DatabaseEngine;
import com.suscipio_solutions.consecro_mud.Libraries.interfaces.DatabaseEngine.PlayerData;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.CMSecurity;
import com.suscipio_solutions.consecro_mud.core.CMStrings;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;
import com.suscipio_solutions.consecro_mud.core.interfaces.PhysicalAgent;


@SuppressWarnings({"unchecked","rawtypes"})
public class Prayer_Resurrect extends Prayer implements MendingSkill
{
	@Override public String ID() { return "Prayer_Resurrect"; }
	private final static String localizedName = CMLib.lang().L("Resurrect");
	@Override public String name() { return localizedName; }
	@Override public int classificationCode(){return Ability.ACODE_PRAYER|Ability.DOMAIN_DEATHLORE;}
	@Override public int abstractQuality(){ return Ability.QUALITY_INDIFFERENT;}
	@Override public long flags(){return Ability.FLAG_HOLY;}
	@Override protected int canTargetCode(){return Ability.CAN_ITEMS;}

	@Override
	public boolean supportsMending(Physical item)
	{
		return (item instanceof DeadBody);
	}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		Physical body=null;
		body=getTarget(mob,mob.location(),givenTarget,commands,Wearable.FILTER_UNWORNONLY);
		DatabaseEngine.PlayerData nonPlayerData=null;
		boolean playerCorpse=false;
		if((body==null)&&(CMSecurity.isASysOp(mob)))
		{
			final List<PlayerData> V=CMLib.database().DBReadData("HEAVEN");
			final Vector<Physical> allObjs=new Vector<Physical>();
			final Vector allDataPs=new Vector();
			if((V!=null)&&(V.size()>0))
			for(int v=0;v<V.size();v++)
			{
				final DatabaseEngine.PlayerData dataP=V.get(v);
				final String data=dataP.xml;
				final PhysicalAgent obj=parseHeavenlyData(data);
				if(obj!=null)
				{
					allDataPs.addElement(dataP);
					allObjs.addElement(obj);
				}
			}
			if(allObjs.size()==0) return false;
			final String name=CMParms.combine(commands,0);
			if(name.equalsIgnoreCase("list"))
			{
				mob.tell("^x"+CMStrings.padRight(L("Guardian"),15)
						+CMStrings.padRight(L("Child name"),45)
						+CMStrings.padRight(L("Birth date"),16)+"^?");
				for(int i=0;i<allObjs.size();i++)
				{
					body=allObjs.elementAt(i);
					final Ability age=body.fetchEffect("Age");
					mob.tell(CMStrings.padRight(((DatabaseEngine.PlayerData)allDataPs.elementAt(i)).who,15)
							+CMStrings.padRight(body.name(),45)
							+CMStrings.padRight(((age==null)?"":CMLib.time().date2String(CMath.s_long(age.text()))),16)+"\n\r"+CMStrings.padRight("",15)+body.description());
				}
				return false;
			}
			Physical P=(Physical)CMLib.english().fetchEnvironmental(allObjs,name,true);
			if(P==null) P=(Physical)CMLib.english().fetchEnvironmental(allObjs,name,false);
			if(P==null) return false;
			for(int i=0;i<allObjs.size();i++)
				if(allObjs.elementAt(i)==P)
				{
					nonPlayerData=(DatabaseEngine.PlayerData)allDataPs.elementAt(i);
					body=P;
					break;
				}
		}
		if(nonPlayerData==null)
		{
			if(body==null) return false;
			if((!(body instanceof DeadBody))
			||(((DeadBody)body).mobName().length()==0))
			{
				mob.tell(L("You can't resurrect that."));
				return false;
			}
			playerCorpse=((DeadBody)body).playerCorpse();
			if(!playerCorpse)
			{
				final Ability AGE=body.fetchEffect("Age");
				if((AGE!=null)&&(CMath.isLong(AGE.text()))&&(CMath.s_long(AGE.text())>Short.MAX_VALUE))
				{
					MOB M=null;
					for(int i=0;i<mob.location().numInhabitants();i++)
					{
						M=mob.location().fetchInhabitant(i);
						if((M!=null)&&(!M.isMonster()))
						{
							final List<PlayerData> V=CMLib.database().DBReadData(M.Name(),"HEAVEN",M.Name()+"/HEAVEN/"+AGE.text());
							if((V!=null)&&(V.size()>0))
							{
								nonPlayerData=V.get(0);
								break;
							}
						}
					}
					if(nonPlayerData==null)
					{
						mob.tell(L("You can't seem to focus on @x1's spirit.  Perhaps if loved ones were here?",body.Name()));
						return false;
					}
				}
				else
				{
					mob.tell(L("You can't resurrect @x1.",((DeadBody)body).charStats().himher()));
					return false;
				}
			}
		}

		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		boolean success=proficiencyCheck(mob,0,auto);
		if(success)
		{
			// it worked, so build a copy of this ability,
			// and add it to the affects list of the
			// affected MOB.  Then tell everyone else
			// what happened.
			final CMMsg msg=CMClass.getMsg(mob,body,this,verbalCastCode(mob,body,auto),auto?L("<T-NAME> is resurrected!"):L("^S<S-NAME> resurrect(s) <T-NAMESELF>!^?"));
			if(mob.location().okMessage(mob,msg))
			{
				invoker=mob;
				mob.location().send(mob,msg);
				if(playerCorpse)
					success = CMLib.utensils().resurrect(mob,mob.location(), (DeadBody)body, super.getXPCOSTLevel(mob));
				else
				if((nonPlayerData!=null) && (body != null))
				{
					final String data=nonPlayerData.xml;
					final Environmental object=parseHeavenlyData(data);
					if(object==null)
						mob.location().show(mob,body,CMMsg.MSG_OK_VISUAL,L("<T-NAME> twitch(es) for a moment, but the spirit is too far gone."));
					else
					if(object instanceof Item)
					{
						body.destroy();
						mob.location().showHappens(CMMsg.MSG_OK_VISUAL,L("@x1 comes back to life!",object.Name()));
						mob.location().addItem((Item)object);
					}
					else
					{
						final MOB rejuvedMOB=(MOB)object;
						rejuvedMOB.recoverCharStats();
						rejuvedMOB.recoverMaxState();
						body.delEffect(body.fetchEffect("Age")); // so misskids doesn't record it
						body.destroy();
						rejuvedMOB.bringToLife(mob.location(),true);
						rejuvedMOB.location().show(rejuvedMOB,null,CMMsg.MSG_NOISYMOVEMENT,L("<S-NAME> get(s) up!"));
					}
					mob.location().recoverRoomStats();
				}
			}
		}
		else
			beneficialWordsFizzle(mob,body,auto?"":L("<S-NAME> attempt(s) to resurrect <T-NAMESELF>, but nothing happens."));


		// return whether it worked
		return success;
	}

	public PhysicalAgent parseHeavenlyData(String data)
	{
		String classID=null;
		int ability=0;
		int x=data.indexOf('/');
		if(x>=0)
		{
			classID=data.substring(0,x);
			data=data.substring(x+1);
		}
		x=data.indexOf('/');
		if(x>=0)
		{
			ability=CMath.s_int(data.substring(0,x));
			data=data.substring(x+1);
		}
		PhysicalAgent object=CMClass.getItem(classID);
		if(object==null) object=CMClass.getMOB(classID);
		if(object==null) return null;
		object.setMiscText(data);
		object.basePhyStats().setAbility(ability);
		object.recoverPhyStats();
		return object;

	}
}
