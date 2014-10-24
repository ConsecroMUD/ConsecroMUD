package com.suscipio_solutions.consecro_mud.Abilities.Common;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Wearable;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.CMStrings;
import com.suscipio_solutions.consecro_mud.core.collections.XVector;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;
import com.suscipio_solutions.consecro_mud.core.interfaces.Tickable;



@SuppressWarnings({"unchecked","rawtypes"})
public class BodyPiercing extends CommonSkill
{
	@Override public String ID() { return "BodyPiercing"; }
	private final static String localizedName = CMLib.lang().L("Body Piercing");
	@Override public String name() { return localizedName; }
	private static final String[] triggerStrings =I(new String[] {"BODYPIERCE","BODYPIERCING"});
	@Override public String[] triggerStrings(){return triggerStrings;}
	@Override public int classificationCode() {   return Ability.ACODE_COMMON_SKILL|Ability.DOMAIN_ARTISTIC; }
	protected String writing="";
	MOB target=null;
	public BodyPiercing()
	{
		super();
		displayText=L("You are piercing...");
		verb=L("piercing");
	}

	@Override
	public void unInvoke()
	{
		if(canBeUninvoked())
		{
			if((affected!=null)&&(affected instanceof MOB)&&(!aborted)&&(!helping)&&(target!=null))
			{
				final MOB mob=(MOB)affected;
				if(writing.length()==0)
					commonEmote(mob,"<S-NAME> mess(es) up the piercing on "+target.name(mob)+".");
				else
				{
					commonEmote(mob,"<S-NAME> complete(s) the piercing on "+target.name(mob)+".");
					target.addTattoo(new MOB.Tattoo(writing));
				}
			}
		}
		super.unInvoke();
	}

	@Override
	public boolean tick(Tickable ticking, int tickID)
	{
		if((affected!=null)&&(affected instanceof MOB)&&(tickID==Tickable.TICKID_MOB))
		{
			final MOB mob=(MOB)affected;
			if((target==null)
			||(mob.location()!=target.location())
			||(!CMLib.flags().canBeSeenBy(target,mob)))
			{aborted=true; unInvoke(); return false;}
		}
		return super.tick(ticking,tickID);
	}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		if(super.checkStop(mob, commands))
			return true;
		if(commands.size()<2)
		{
			commonTell(mob,L("You must specify remove and/or whom you want to pierce, and what body part to pierce."));
			return false;
		}
		String name=(String)commands.firstElement();
		String part=CMParms.combine(commands,1);
		String command="";
		if(commands.size()>2)
		{
			if(((String)commands.firstElement()).equalsIgnoreCase("REMOVE"))
			{
				command=((String)commands.firstElement()).toUpperCase();
				name=(String)commands.elementAt(1);
				part=CMParms.combine(commands,2);
			}
		}

		final MOB target=super.getTarget(mob,new XVector(name),givenTarget);
		if(target==null) return false;
		if((target.isMonster())
		&&(CMLib.flags().aliveAwakeMobile(target,true))
		&&(!mob.getGroupMembers(new HashSet<MOB>()).contains(target)))
		{
			mob.tell(L("@x1 doesn't want any piercings.",target.Name()));
			return false;
		}

		int partNum=-1;
		final StringBuffer allParts=new StringBuffer("");
		
		final String[][] piercables={
										{"lip", "nose"},
										{"left ear","right ear", "ears"},
										{"eyebrows"},
										{"left nipple","right nipple","belly button","nipples"}
									};
		
		final long[] piercable={Wearable.WORN_HEAD,
								Wearable.WORN_EARS,
								Wearable.WORN_EYES,
								Wearable.WORN_TORSO};
		
		String fullPartName=null;
		final Wearable.CODES codes = Wearable.CODES.instance();
		String wearLocName=null;
		for(int i=0;i<codes.total();i++)
		{
			for(int ii=0;ii<piercable.length;ii++)
				if(codes.get(i)==piercable[ii])
				{
					for(int iii=0;iii<piercables[ii].length;iii++)
					{
						if(piercables[ii][iii].startsWith(part.toLowerCase()))
						{
							partNum=i;
							fullPartName=piercables[ii][iii];
							wearLocName=codes.name(partNum).toUpperCase();
						}
						allParts.append(", "+CMStrings.capitalizeAndLower(piercables[ii][iii]));
					}
					break;
				}
		}
		if((partNum<0)||(wearLocName==null))
		{
			commonTell(mob,L("'@x1' is not a valid location.  Valid locations include: @x2",part,allParts.toString().substring(2)));
			return false;
		}
		final long wornCode=codes.get(partNum);
		final String wornName=fullPartName;

		if((target.getWearPositions(wornCode)<=0)
		||(target.freeWearPositions(wornCode,(short)(Short.MIN_VALUE+1),(short)0)<=0))
		{
			commonTell(mob,L("That location is not available for piercing. Make sure no clothing is being worn there."));
			return false;
		}

		int numTattsDone=0;
		for(final Enumeration<MOB.Tattoo> e=target.tattoos();e.hasMoreElements();)
		{
			final MOB.Tattoo T=e.nextElement();
			if(T.tattooName.startsWith(wearLocName+":"))
				numTattsDone++;
		}
		if("REMOVE".equals(command))
		{
			if(numTattsDone<=0)
			{
				commonTell(mob,L("There is no piercing there to heal."));
				return false;
			}
		}
		else
		if(numTattsDone>=target.getWearPositions(codes.get(partNum)))
		{
			commonTell(mob,L("That location is already decorated."));
			return false;
		}

		if((!super.invoke(mob,commands,givenTarget,auto,asLevel))||(wornName==null))
			return false;
		if(wornName.toLowerCase().endsWith("s"))
		{
			writing=wearLocName+":Pierced "+wornName.toLowerCase();
			verb=L("piercing @x1 on  @x2",target.name(),wornName);
		}
		else
		{
			writing=wearLocName+":A pierced "+wornName.toLowerCase();
			verb=L("piercing @x1 on the @x2",target.name(),wornName);
		}
		displayText=L("You are @x1",verb);
		if(!proficiencyCheck(mob,0,auto)) 
			writing="";
		final int duration=getDuration(30,mob,1,6);
		String msgStr=L("<S-NAME> start(s) piercing <T-NAMESELF> on the @x1.",wornName.toLowerCase());
		if("REMOVE".equals(command))
			msgStr=L("<S-NAME> heal(s) the piercing on <T-YOUPOSS> @x1.",wornName.toLowerCase());
		final CMMsg msg=CMClass.getMsg(mob,target,this,getActivityMessageType(),msgStr);
		if(mob.location().okMessage(mob,msg))
		{
			mob.location().send(mob,msg);
			if("REMOVE".equals(command))
				target.delTattoo(target.findTattoo(writing));
			else
			{
				beneficialAffect(mob,mob,asLevel,duration);
				final BodyPiercing A=(BodyPiercing)mob.fetchEffect(ID());
				if(A!=null) 
					A.target=target;
			}
		}
		return true;
	}
}

