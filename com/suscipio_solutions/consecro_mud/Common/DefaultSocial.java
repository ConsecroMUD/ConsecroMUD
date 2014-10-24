package com.suscipio_solutions.consecro_mud.Common;
import java.util.List;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CharState;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CharStats;
import com.suscipio_solutions.consecro_mud.Common.interfaces.PhyStats;
import com.suscipio_solutions.consecro_mud.Common.interfaces.Social;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Wearable;
import com.suscipio_solutions.consecro_mud.Libraries.interfaces.CatalogLibrary;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.CMProps;
import com.suscipio_solutions.consecro_mud.core.CMSecurity;
import com.suscipio_solutions.consecro_mud.core.interfaces.CMObject;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;
import com.suscipio_solutions.consecro_mud.core.interfaces.Tickable;


// requires nothing to load

@SuppressWarnings("rawtypes")
public class DefaultSocial implements Social
{
	protected String Social_name;
	protected String You_see;
	protected String Third_party_sees;
	protected String Target_sees;
	protected String See_when_no_target;
	private String MSPfile="";
	protected int sourceCode=CMMsg.MSG_OK_ACTION;
	protected int othersCode=CMMsg.MSG_OK_ACTION;
	protected int targetCode=CMMsg.MSG_OK_ACTION;

	@Override public String ID() { return "DefaultSocial"; }
	@Override public String name(){ return Social_name;}
	@Override public String Name(){return name();}
	@Override
	public String baseName()
	{
		final int x=name().indexOf(' ');
		if(x<0) return name();
		return name().substring(0,x);
	}
	@Override public String L(final String str, final String ... xs) { return CMLib.lang().fullSessionTranslation(str, xs); }
	@Override public void setName(String newName){Social_name=newName;}
	@Override public String You_see(){return You_see;}
	@Override public String Third_party_sees(){return Third_party_sees;}
	@Override public String Target_sees(){return Target_sees;}
	@Override public String See_when_no_target(){return See_when_no_target;}
	@Override public int sourceCode(){return sourceCode;}
	@Override public int othersCode(){return othersCode;}
	@Override public int targetCode(){return targetCode;}
	@Override public void setYou_see(String str){You_see=str;}
	@Override public void setThird_party_sees(String str){Third_party_sees=str;}
	@Override public void setTarget_sees(String str){Target_sees=str;}
	@Override public void setSee_when_no_target(String str){See_when_no_target=str;}
	@Override public void setSourceCode(int code){sourceCode=code;}
	@Override public void setOthersCode(int code){othersCode=code;}
	@Override public void setTargetCode(int code){targetCode=code;}
	@Override public int getTickStatus(){return Tickable.STATUS_NOT;}
	@Override public String MSPfile(){return MSPfile;}
	@Override public void setMSPfile(String newFile){MSPfile=newFile;}
	@Override public long expirationDate(){return 0;}
	@Override public void setExpirationDate(long time){}
	@Override
	public boolean targetable(Environmental E)
	{
		if(E==null)
			return name().endsWith("-NAME>");
		if(E instanceof MOB)
			return name().endsWith(" <T-NAME>");
		if((E instanceof Item)&&(((Item)E).container()==null))
		{
			final Item I=(Item)E;
			if(I.owner() instanceof Room)
				return name().endsWith(" <I-NAME>");
			if(I.owner() instanceof MOB)
			{
				if(I.amWearingAt(Wearable.IN_INVENTORY))
					return name().endsWith(" <V-NAME>");
				else
					return name().endsWith(" <E-NAME>");
			}
		}
		return false;
	}

	@Override
	public boolean invoke(MOB mob,
						  Vector commands,
						  Physical target,
						  boolean auto)
	{
		String targetStr="";
		if((commands.size()>1)
		&&(!((String)commands.elementAt(1)).equalsIgnoreCase("SELF"))
		&&(!((String)commands.elementAt(1)).equalsIgnoreCase("ALL")))
			targetStr=(String)commands.elementAt(1);

		Physical targetE=target;
		if(targetE==null)
		{
			targetE=mob.location().fetchFromMOBRoomFavorsMOBs(mob,null,targetStr,Wearable.FILTER_ANY);
			if((targetE!=null)&&(!CMLib.flags().canBeSeenBy(targetE,mob)))
			   targetE=null;
			else
			if((targetE!=null)&&(!targetable(targetE)))
			{
				final Social S=CMLib.socials().fetchSocial(baseName(),targetE, true);
				if(S!=null) return S.invoke(mob, commands, targetE, auto);
			}
		}

		final String mspFile=((MSPfile!=null)&&(MSPfile.length()>0))?CMLib.protocol().msp(MSPfile,10):"";

		String You_see=You_see();
		if((You_see!=null)&&(You_see.trim().length()==0))
			You_see=null;

		String Third_party_sees=Third_party_sees();
		if((Third_party_sees!=null)&&(Third_party_sees.trim().length()==0))
			Third_party_sees=null;

		String Target_sees=Target_sees();
		if((Target_sees!=null)&&(Target_sees.trim().length()==0)) Target_sees=null;

		String See_when_no_target=See_when_no_target();
		if((See_when_no_target!=null)&&(See_when_no_target.trim().length()==0))
			See_when_no_target=null;

		if(((targetE==null)&&(targetable(null)))||((targetE!=null)&&(!targetable(targetE))))
		{
			final CMMsg msg=CMClass.getMsg(mob,null,this,(auto?CMMsg.MASK_ALWAYS:0)|sourceCode(),See_when_no_target,CMMsg.NO_EFFECT,null,CMMsg.NO_EFFECT,null);
			if(mob.location().okMessage(mob,msg))
				mob.location().send(mob,msg);
		}
		else
		if(targetE==null)
		{
			final CMMsg msg=CMClass.getMsg(mob,null,this,(auto?CMMsg.MASK_ALWAYS:0)|sourceCode(),(You_see==null)?null:You_see+mspFile,CMMsg.NO_EFFECT,null,othersCode(),(Third_party_sees==null)?null:Third_party_sees+mspFile);
			if(mob.location().okMessage(mob,msg))
				mob.location().send(mob,msg);
		}
		else
		{
			final CMMsg msg=CMClass.getMsg(mob,targetE,this,(auto?CMMsg.MASK_ALWAYS:0)|sourceCode(),(You_see==null)?null:You_see+mspFile,targetCode(),(Target_sees==null)?null:Target_sees+mspFile,othersCode(),(Third_party_sees==null)?null:Third_party_sees+mspFile);
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				if(target instanceof MOB)
				{
					final MOB tmob=(MOB)target;
					if((name().toUpperCase().startsWith("SMILE"))
					&&(mob.charStats().getStat(CharStats.STAT_CHARISMA)>=16)
					&&(mob.charStats().getMyRace().ID().equals(tmob.charStats().getMyRace().ID()))
					&&(CMLib.dice().rollPercentage()==1)
					&&(mob.charStats().getStat(CharStats.STAT_GENDER)!=('N'))
					&&(tmob.charStats().getStat(CharStats.STAT_GENDER)!=('N'))
					&&(mob.charStats().getStat(CharStats.STAT_GENDER)!=tmob.charStats().getStat(CharStats.STAT_GENDER))
					&&(!CMSecurity.isDisabled(CMSecurity.DisFlag.AUTODISEASE)))
					{
						final Ability A=CMClass.getAbility("Disease_Smiles");
						if((A!=null)&&(target.fetchEffect(A.ID())==null))
							A.invoke(tmob,tmob,true,0);
					}
				}
			}
		}
		return true;
	}

	@Override
	public CMMsg makeChannelMsg(MOB mob,
								int channelInt,
								String channelName,
								Vector commands,
								boolean makeTarget)
	{
		String channelColor=CMLib.channels().getChannel(channelInt).colorOverride;
		if(channelColor.length()==0)
			channelColor="^Q";
		final String str=makeTarget?"":(channelColor+"^<CHANNEL \""+channelName+"\"^>["+channelName+"] ");
		final String end=makeTarget?"":"^</CHANNEL^>^N^.";
		return makeMessage(mob,str,end,CMMsg.MASK_CHANNEL,CMMsg.MASK_CHANNEL|(CMMsg.TYP_CHANNEL+channelInt),commands,channelName,makeTarget);
	}
	@Override
	public CMMsg makeMessage(MOB mob,
							 String str,
							 String end,
							 int srcMask,
							 int fullCode,
							 Vector commands,
							 String I3channelName,
							 boolean makeTarget)
	{
		String targetStr="";
		if((commands.size()>1)
		&&(!((String)commands.elementAt(1)).equalsIgnoreCase("SELF"))
		&&(!((String)commands.elementAt(1)).equalsIgnoreCase("ALL")))
			targetStr=(String)commands.elementAt(1);
		Environmental target=null;
		if(targetStr.length()>0)
		{
			String targetMud="";
			if(targetStr.indexOf('@')>0)
				targetMud=targetStr.substring(targetStr.indexOf('@')+1);
			else
			{
				target=CMLib.players().getPlayer(targetStr);
				if((target==null)&&(!makeTarget))
				{
					final MOB possTarget=CMLib.catalog().getCatalogMob(targetStr);
					if(possTarget!=null)
					{
						final CatalogLibrary.CataData data=CMLib.catalog().getCatalogData(possTarget);
						if(data!=null)
							target=data.getLiveReference();
					}
				}
			}
			if(((target==null)&&(makeTarget))
			||((targetMud.length()>0)
				&&(I3channelName!=null)
				&&((CMLib.intermud().i3online())&&(CMLib.intermud().isI3channel(I3channelName)))))
			{
				target=CMClass.getFactoryMOB();
				target.setName(targetStr);
				((MOB)target).setLocation(CMLib.map().getRandomRoom());
			}
			else
			if((target!=null)&&(!CMLib.flags().isInTheGame(target, true)))
				target=null;
			if((target!=null)&&(target instanceof Physical)&&(!CMLib.flags().isSeen((Physical)target)))
				target=null;
		}

		String mspFile=((MSPfile!=null)&&(MSPfile.length()>0))?CMLib.protocol().msp(MSPfile,10):"";
		if(end.length()==0) mspFile="";

		int targetCode=fullCode;
		int otherCode=fullCode;
		int srcCode=srcMask|sourceCode();

		String You_see=You_see();
		if((You_see!=null)&&(You_see.trim().length()==0))
		{
			You_see=null;
			srcCode=CMMsg.NO_EFFECT;
		}
		else
			You_see=str+You_see+end+mspFile;


		String Third_party_sees=Third_party_sees();
		if((Third_party_sees!=null)&&(Third_party_sees.trim().length()==0))
		{
			Third_party_sees=null;
			otherCode=CMMsg.NO_EFFECT;
		}
		else
			Third_party_sees=str+Third_party_sees+end+mspFile;

		String Target_sees=Target_sees();
		if((Target_sees!=null)&&(Target_sees.trim().length()==0))
		{
			Target_sees=null;
			targetCode=CMMsg.NO_EFFECT;
		}
		else
			Target_sees=str+Target_sees+end+mspFile;

		String See_when_no_target=See_when_no_target();
		if((See_when_no_target!=null)&&(See_when_no_target.trim().length()==0))
			See_when_no_target=null;
		else
			See_when_no_target=str+See_when_no_target+end;

		CMMsg msg=null;
		if(((target==null)&&(targetable(null)))||((target!=null)&&(!targetable(target))))
			msg=CMClass.getMsg(mob,null,this,srcCode,See_when_no_target,CMMsg.NO_EFFECT,null,CMMsg.NO_EFFECT,null);
		else
		if(target==null)
			msg=CMClass.getMsg(mob,null,this,srcCode,You_see,CMMsg.NO_EFFECT,null,otherCode,Third_party_sees);
		else
			msg=CMClass.getMsg(mob,target,this,srcCode,You_see,targetCode,Target_sees,otherCode,Third_party_sees);
		return msg;
	}

	@Override public String description(){return "";}
	@Override public void setDescription(String str){}
	@Override public String displayText(){return "";}
	@Override public void setDisplayText(String str){}

	@Override public CMObject newInstance() { return new DefaultSocial();}
	@Override public void initializeClass(){}
	@Override public int compareTo(CMObject o){ return CMClass.classID(this).compareToIgnoreCase(CMClass.classID(o));}
	protected boolean amDestroyed=false;
	@Override public void destroy(){amDestroyed=true;}
	@Override public boolean amDestroyed(){return amDestroyed;}
	@Override public boolean isSavable(){return true;}
	@Override public void setSavable(boolean truefalse){}

	@Override public int getSaveStatIndex(){return getStatCodes().length;}
	private static final String[] CODES={"CLASS","NAME"};
	@Override public String[] getStatCodes(){return CODES;}
	@Override public boolean isStat(String code){ return CMParms.indexOf(getStatCodes(),code.toUpperCase().trim())>=0;}
	protected int getCodeNum(String code)
	{
		for(int i=0;i<CODES.length;i++)
			if(code.equalsIgnoreCase(CODES[i])) return i;
		return -1;
	}
	@Override
	public String getStat(String code)
	{
		switch(getCodeNum(code))
		{
		case 0: return ID();
		case 1: return name();
		}
		return "";
	}
	@Override
	public void setStat(String code, String val)
	{
		switch(getCodeNum(code))
		{
		case 0: return;
		case 1: setName(val); break;
		}
	}
	@Override
	public boolean sameAs(Environmental E)
	{
		if(!(E instanceof Social)) return false;
		final String name=Social_name.toUpperCase().trim();
		if(!(((Social)E).name().toUpperCase().equals(name.trim())))
		   return false;
		if(((You_see == null)!=(((Social)E).You_see() == null))
		||((You_see != null)&&(!You_see.equals(((Social)E).You_see()))))
			return false;
		if(this.sourceCode != ((Social)E).sourceCode())
			return false;
		if(this.targetCode != ((Social)E).targetCode())
			return false;
		if(this.othersCode != ((Social)E).othersCode())
			return false;
		if(((Third_party_sees == null)!=(((Social)E).Third_party_sees() == null))
		||((Third_party_sees != null)&&(!Third_party_sees.equals(((Social)E).Third_party_sees()))))
			return false;
		if(((Target_sees == null)!=(((Social)E).Target_sees() == null))
		||((Target_sees != null)&&(!Target_sees.equals(((Social)E).Target_sees()))))
			return false;
		if(((See_when_no_target == null)!=(((Social)E).See_when_no_target() == null))
		||((See_when_no_target != null)&&(!See_when_no_target.equals(((Social)E).See_when_no_target()))))
			return false;
		if(((MSPfile == null)!=(((Social)E).MSPfile() == null))
		||((MSPfile != null)&&(!MSPfile.equals(((Social)E).MSPfile()))))
			return false;
		return true;
	}
	protected void cloneFix(Social E){}

	@Override
	public CMObject copyOf()
	{
		try
		{
			final DefaultSocial E=(DefaultSocial)this.clone();
			E.cloneFix(this);
			return E;

		}
		catch(final CloneNotSupportedException e)
		{
			return this.newInstance();
		}
	}
	@Override public void setMiscText(String newMiscText){}
	@Override public String text(){return "";}
	@Override public String miscTextFormat(){return CMParms.FORMAT_UNDEFINED;}

	@Override public void affectPhyStats(Physical affected, PhyStats affectableStats)	{}
	@Override public void affectCharStats(MOB affectedMob, CharStats affectableStats)	{}
	@Override public void affectCharState(MOB affectedMob, CharState affectableMaxState)	{}
	@Override public void executeMsg(final Environmental myHost, final CMMsg msg){}
	@Override public boolean okMessage(final Environmental myHost, final CMMsg msg){	return true;}
	@Override public boolean tick(Tickable ticking, int tickID)	{ return true;	}
	@Override public int maxRange(){return Integer.MAX_VALUE;}
	@Override public int minRange(){return 0;}

	@Override public String image(){return "";}
	@Override public String rawImage(){return "";}
	@Override public void setImage(String newImage){}
	@Override public boolean isGeneric(){return false;}
	
	@Override
	public double actionsCost(final MOB mob, final List<String> cmds)
	{
		return CMProps.getSocialActionCost(baseName());
	}
	@Override
	public double combatActionsCost(MOB mob, List<String> cmds)
	{
		return CMProps.getSocialCombatActionCost(baseName());
	}
	@Override
	public double checkedActionsCost(final MOB mob, final List<String> cmds)
	{
		if(mob!=null)
			return mob.isInCombat() ? combatActionsCost(mob,cmds) : actionsCost(mob,cmds);
		return actionsCost(mob,cmds);
	}
}
