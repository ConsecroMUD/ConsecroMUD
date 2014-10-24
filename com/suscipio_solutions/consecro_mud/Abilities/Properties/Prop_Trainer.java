package com.suscipio_solutions.consecro_mud.Abilities.Properties;
import java.util.Enumeration;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.CharClasses.interfaces.CharClass;
import com.suscipio_solutions.consecro_mud.Libraries.interfaces.ExpertiseLibrary;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.interfaces.Tickable;


@SuppressWarnings({"unchecked","rawtypes"})
public class Prop_Trainer extends Prop_StatTrainer
{
	@Override public String ID() { return "Prop_Trainer"; }
	@Override public String name(){ return "THE Training MOB";}
	@Override protected int canAffectCode(){return Ability.CAN_MOBS;}
	@Override public String accountForYourself() { return "Trainer";	}

	private boolean built=false;

	private void addCharClassIfNotFound(MOB mob, CharClass C)
	{
		boolean found=false;
		for(int n=0;n<mob.baseCharStats().numClasses();n++)
			if(mob.baseCharStats().getMyClass(n).ID().equals(C.ID()))
			{ found=true; break;}
		if((!found)&&(C.availabilityCode()!=0))
		{
			mob.baseCharStats().setCurrentClass(C);
			mob.baseCharStats().setClassLevel(C,0);
		}
	}

	@Override
	public boolean tick(Tickable ticking, int tickID)
	{
		if((!built)&&(affected instanceof MOB))
		{
			built=true;
			CharClass C=null;
			final Vector allowedClasses=new Vector();
			final Vector allowedExpertises=new Vector();
			final Vector<String> V=CMParms.parse(text());
			String s=null;
			for(int v=0;v<V.size();v++)
			{
				s=V.elementAt(v);
				if(s.equalsIgnoreCase("all")) continue;
				C=CMClass.getCharClass(s);
				if(C!=null)
				{
					if((v>0)&&(V.elementAt(v-1).equalsIgnoreCase("ALL")))
					{
						final String baseClass=C.baseClass();
						for(final Enumeration c=CMClass.charClasses();c.hasMoreElements();)
						{
							C=(CharClass)c.nextElement();
							if((C.baseClass().equalsIgnoreCase(baseClass))
							&&(!allowedClasses.contains(C)))
								allowedClasses.addElement(C);
						}
					}
					else
						allowedClasses.addElement(C);
				}
				else
				{
					final ExpertiseLibrary.ExpertiseDefinition def=CMLib.expertises().getDefinition(s);
					if(def!=null) allowedExpertises.addElement(def);
				}
			}
			if(allowedClasses.size()==0)
			for(final Enumeration c=CMClass.charClasses();c.hasMoreElements();)
				allowedClasses.addElement(c.nextElement());
			if(allowedExpertises.size()==0)
			for(final Enumeration e=CMLib.expertises().definitions();e.hasMoreElements();)
				allowedExpertises.addElement(e.nextElement());


			final MOB mob=(MOB)affected;
			for(int c=0;c<allowedClasses.size();c++)
			{
				C=(CharClass)allowedClasses.elementAt(c);
				addCharClassIfNotFound(mob,C);
			}
			for(int e=0;e<allowedExpertises.size();e++)
				mob.addExpertise(((ExpertiseLibrary.ExpertiseDefinition)allowedExpertises.elementAt(e)).ID);
			mob.recoverCharStats();
			mob.recoverPhyStats();
			mob.recoverMaxState();
		}
		return super.tick(ticking,tickID);
	}
}
