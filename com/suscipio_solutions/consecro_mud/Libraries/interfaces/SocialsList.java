package com.suscipio_solutions.consecro_mud.Libraries.interfaces;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.suscipio_solutions.consecro_mud.Common.interfaces.Social;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.Resources;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;

public interface SocialsList extends CMLibrary
{
	public final String filename=Resources.buildResourcePath("")+"socials.txt";

	public boolean isLoaded();

	public void put(String name, Social S);
	public void remove(String name);
	public void addSocial(Social S);

	public void modifySocialOthersCode(MOB mob, Social me, int showNumber, int showFlag)
		throws IOException;
	public void modifySocialTargetCode(MOB mob, Social me, int showNumber, int showFlag)
		throws IOException;
	public void modifySocialSourceCode(MOB mob, Social me, int showNumber, int showFlag)
		throws IOException;
	public boolean modifySocialInterface(MOB mob, String socialString)
		throws IOException;

	public Social fetchSocial(String name, boolean exactOnly);
	public Social fetchSocial(String baseName, Environmental targetE, boolean exactOnly);
	public Social fetchSocial(List<String> C, boolean exactOnly, boolean checkItemTargets);
	public Social fetchSocial(List<Social> set, String name, boolean exactOnly);
	public String findSocialName(String named, boolean exactOnly);
	public Social fetchSocialFromSet(final Map<String,List<Social>> soc, List<String> C, boolean exactOnly, boolean checkItemTargets);

	public void putSocialsInHash(final Map<String,List<Social>> soc, final List<String> lines);

	public List<Social> getSocialsSet(String named);
	public int numSocialSets();
	public List<Social> enumSocialSet(int index);

	public void save(MOB whom);
	public List<String> getSocialsList();
	public String getSocialsHelp(MOB mob, String named, boolean exact);
	public String getSocialsTable();
	public Social makeDefaultSocial(String name, String type);

	public void unloadSocials();
}
