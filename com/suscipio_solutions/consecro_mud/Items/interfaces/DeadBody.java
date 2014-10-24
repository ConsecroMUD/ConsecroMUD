package com.suscipio_solutions.consecro_mud.Items.interfaces;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CharStats;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;

public interface DeadBody extends Container
{
	public CharStats charStats();
	public void setCharStats(CharStats newStats);
	public String mobName();
	public void setMobName(String newName);
	public String mobDescription();
	public void setMobDescription(String newDescription);
	public String killerName();
	public void setKillerName(String newName);
	public boolean killerPlayer();
	public void setKillerPlayer(boolean trueFalse);
	public String lastMessage();
	public void setLastMessage(String lastMsg);
	public Environmental killingTool();
	public void setKillingTool(Environmental tool);
	public boolean destroyAfterLooting();
	public void setDestroyAfterLooting(boolean truefalse);
	public boolean playerCorpse();
	public void setPlayerCorpse(boolean truefalse);
	public boolean mobPKFlag();
	public void setMobPKFlag(boolean truefalse);
	public long timeOfDeath();
	public void setTimeOfDeath(long time);
	public void setSavedMOB(MOB mob);
	public MOB savedMOB();
}

