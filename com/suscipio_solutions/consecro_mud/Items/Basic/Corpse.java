package com.suscipio_solutions.consecro_mud.Items.Basic;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CharStats;
import com.suscipio_solutions.consecro_mud.Items.interfaces.DeadBody;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.Libraries.interfaces.TimeManager;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMProps;
import com.suscipio_solutions.consecro_mud.core.CMSecurity;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;


public class Corpse extends GenContainer implements DeadBody
{
	@Override public String ID(){	return "Corpse";}
	protected CharStats charStats=null;
	protected String mobName="";
	protected String mobDescription="";
	protected String killerName="";
	protected boolean killerPlayer=false;
	protected String lastMessage="";
	protected Environmental killingTool=null;
	protected boolean destroyAfterLooting=false;
	protected boolean playerCorpse=false;
	protected long timeOfDeath=System.currentTimeMillis();
	protected boolean mobPKFlag=false;
	protected MOB savedMOB=null;

	public Corpse()
	{
		super();

		setName("the body of someone");
		setDisplayText("the body of someone lies here.");
		setDescription("Bloody and bruised, obviously mistreated.");
		properWornBitmap=0;
		basePhyStats.setWeight(150);
		capacity=5;
		baseGoldValue=0;
		recoverPhyStats();
		material=RawMaterial.RESOURCE_MEAT;
	}
	@Override
	public void setMiscText(String newText)
	{
		miscText="";
		if(newText.length()>0)
			super.setMiscText(newText);
	}

	@Override
	public CharStats charStats()
	{
		if(charStats==null)
			charStats=(CharStats)CMClass.getCommon("DefaultCharStats");
		return charStats;
	}
	@Override
	public void setCharStats(CharStats newStats)
	{
		charStats=newStats;
		if(charStats!=null) charStats=(CharStats)charStats.copyOf();
	}

	@Override
	public void setSecretIdentity(String newIdentity)
	{
		if(newIdentity.indexOf('/')>0)
		{
			playerCorpse=false;
			final int x=newIdentity.indexOf('/');
			if(x>=0)
			{
				mobName=newIdentity.substring(0,x);
				mobDescription=newIdentity.substring(x+1);
				playerCorpse=true;
			}
		}
		else
			super.setSecretIdentity(newIdentity);
	}

	@Override
	public void destroy()
	{
		super.destroy();
		if(savedMOB!=null)
			savedMOB.destroy();
		savedMOB=null;
	}

	@Override public String mobName(){ return mobName;}
	@Override public void setMobName(String newName){mobName=newName;}
	@Override public String mobDescription(){return mobDescription;}
	@Override public void setMobDescription(String newDescription){mobDescription=newDescription;}
	@Override public boolean mobPKFlag(){return mobPKFlag;}
	@Override public void setMobPKFlag(boolean truefalse){mobPKFlag=truefalse;}
	@Override public String killerName(){return killerName;}
	@Override public void setKillerName(String newName){killerName=newName;}
	@Override public boolean killerPlayer(){return killerPlayer;}
	@Override public void setKillerPlayer(boolean trueFalse){killerPlayer=trueFalse;}
	@Override public boolean playerCorpse(){return playerCorpse;}
	@Override public void setPlayerCorpse(boolean truefalse){playerCorpse=truefalse;}
	@Override public String lastMessage(){return lastMessage;}
	@Override public void setLastMessage(String lastMsg){lastMessage=lastMsg;}
	@Override public Environmental killingTool(){return killingTool;}
	@Override public void setKillingTool(Environmental tool){killingTool=tool;}
	@Override public boolean destroyAfterLooting(){return destroyAfterLooting;}
	@Override public void setDestroyAfterLooting(boolean truefalse){destroyAfterLooting=truefalse;}
	@Override public long timeOfDeath(){return timeOfDeath;}
	@Override public void setTimeOfDeath(long time){timeOfDeath=time;}
	@Override public void setSavedMOB(MOB mob){savedMOB=mob;}
	@Override public MOB savedMOB(){return savedMOB;}

	@Override
	public void executeMsg(final Environmental myHost, final CMMsg msg)
	{
		if((msg.targetMinor()==CMMsg.TYP_SIT)
		&&(msg.source().Name().equalsIgnoreCase(mobName()))
		&&(msg.amITarget(this)||(msg.tool()==this))
		&&(CMLib.flags().isGolem(msg.source()))
		&&(msg.source().phyStats().height()<0)
		&&(msg.source().phyStats().weight()<=0)
		&&(playerCorpse())
		&&(mobName().length()>0))
		{
			CMLib.utensils().resurrect(msg.source(),msg.source().location(),this,-1);
			return;
		}
		if(msg.amITarget(this)&&(msg.targetMinor()==CMMsg.TYP_SNIFF)
		&&((System.currentTimeMillis()-timeOfDeath())>(TimeManager.MILI_HOUR/2)))
			msg.source().tell(L("@x1 has definitely started to decay.",name()));
		super.executeMsg(myHost, msg);

	}

	@Override
	public boolean okMessage(final Environmental myHost, final CMMsg msg)
	{
		if((msg.amITarget(this)||(msg.tool()==this))
		&&(playerCorpse())
		&&(mobName().length()>0))
		{
			if((msg.targetMinor()==CMMsg.TYP_SIT)
			&&(msg.source().name().equalsIgnoreCase(mobName()))
			&&(CMLib.flags().isGolem(msg.source()))
			&&(msg.source().phyStats().height()<0)
			&&(msg.source().phyStats().weight()<=0))
				return true;

			if(!super.okMessage(myHost,msg))
				return false;

			if(((msg.targetMinor()==CMMsg.TYP_GET)
				||((msg.tool() instanceof Ability)
					&&(!msg.tool().ID().equalsIgnoreCase("Prayer_Resurrect"))
					&&(!msg.tool().ID().equalsIgnoreCase("Prayer_PreserveBody"))
					&&(!msg.tool().ID().equalsIgnoreCase("Song_Rebirth"))))
			&&(CMProps.getVar(CMProps.Str.CORPSEGUARD).length()>0)
			&&(!msg.targetMajor(CMMsg.MASK_INTERMSG)))
			{
				if(CMSecurity.isAllowed(msg.source(),msg.source().location(),CMSecurity.SecFlag.CMDITEMS))
					return true;

				final MOB ultimateFollowing=msg.source().amUltimatelyFollowing();
				if((msg.source().isMonster())
				&&((ultimateFollowing==null)||(ultimateFollowing.isMonster())))
					return true;
				if(CMProps.getVar(CMProps.Str.CORPSEGUARD).equalsIgnoreCase("ANY"))
					return true;
				if (mobName().equalsIgnoreCase(msg.source().Name()))
					return true;
				else
				if(CMProps.getVar(CMProps.Str.CORPSEGUARD).equalsIgnoreCase("SELFONLY"))
				{
					msg.source().tell(L("You may not loot another players corpse."));
					return false;
				}
				else
				if(CMProps.getVar(CMProps.Str.CORPSEGUARD).equalsIgnoreCase("PKONLY"))
				{
					if(!((msg.source()).isAttribute(MOB.Attrib.PLAYERKILL)))
					{
						msg.source().tell(L("You can not get that.  You are not a player killer."));
						return false;
					}
					else
					if(mobPKFlag())
					{
						msg.source().tell(L("You can not get that.  @x1 was not a player killer.",mobName()));
						return false;
					}
				}
			}
			return true;
		}
		return super.okMessage(myHost, msg);
	}
}
