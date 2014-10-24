package com.suscipio_solutions.consecro_mud.Abilities.Languages;
import java.util.Enumeration;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Language;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;



@SuppressWarnings("rawtypes")
public class Common extends StdLanguage
{
	@Override public String ID() { return "Common"; }
	private final static String localizedName = CMLib.lang().L("Common");
	@Override public String name() { return localizedName; }
	@Override public boolean isAutoInvoked(){return false;}
	@Override public boolean canBeUninvoked(){return canBeUninvoked;}
	public Common()
	{
		super();
		proficiency=100;
	}
	@Override public int proficiency(){return 100;}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		boolean anythingDone=false;
		for(final Enumeration<Ability> a=mob.effects();a.hasMoreElements();)
		{
			final Ability A=a.nextElement();
			if((A!=null)&&(A instanceof Language))
				if(((Language)A).beingSpoken(ID()))
				{
					anythingDone=true;
					((Language)A).setBeingSpoken(ID(),false);
				}

		}
		isAnAutoEffect=false;
		if(!auto)
		{
			String msg=null;
			if(!anythingDone)
				msg="already speaking "+name()+".";
			else
				msg="now speaking "+name()+".";
			mob.tell(L("You are @x1",msg));
			if((mob.isMonster())&&(mob.amFollowing()!=null))
				CMLib.commands().postSay(mob,L("I am @x1",msg));
		}
		return true;
	}
}
