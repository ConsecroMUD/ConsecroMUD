package com.suscipio_solutions.consecro_mud.Libraries.interfaces;
import java.util.List;
import java.util.Properties;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;

public interface HelpLibrary extends CMLibrary
{
	public List<String> getTopics(boolean immortalHelp, boolean standardHelp);
	public String getActualUsage(Ability A, int which, MOB forMOB);
	public String fixHelp(String tag, String str, MOB forMOB);
	public StringBuilder getHelpText(String helpStr, MOB forMOB, boolean favorIMMHelp);
	public StringBuilder getHelpText(String helpStr, MOB forMOB, boolean favorIMMHelp, boolean noFix);
	public StringBuilder getHelpText(String helpStr, Properties rHelpFile, MOB forMOB);
	public StringBuilder getHelpList(String helpStr,  Properties rHelpFile1, Properties rHelpFile2, MOB forMOB);
	public StringBuilder getHelpText(String helpStr, Properties rHelpFile, MOB forMOB, boolean noFix);
	public Properties getImmHelpFile();
	public Properties getHelpFile();
	public void unloadHelpFile(MOB mob);
	public boolean isPlayerSkill(String helpStr);
	public void addHelpEntry(String ID, String text, boolean immortal);
}
