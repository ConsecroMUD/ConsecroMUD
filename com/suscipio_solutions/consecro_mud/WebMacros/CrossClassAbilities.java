package com.suscipio_solutions.consecro_mud.WebMacros;

import java.util.Enumeration;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.CharClasses.interfaces.CharClass;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMProps;
import com.suscipio_solutions.consecro_web.interfaces.HTTPRequest;


@SuppressWarnings({"unchecked","rawtypes"})
public class CrossClassAbilities extends StdWebMacro
{
	@Override public String name()	{return "CrossClassAbilities";}

	@Override
	public String runMacro(HTTPRequest httpReq, String parm)
	{
		final Vector rowsFavoring=new Vector();
		final Vector allOtherRows=new Vector();
		final String sort=httpReq.getUrlParameter("SORTBY");
		int sortByClassNum=-1;
		if((sort!=null)&&(sort.length()>0))
		{
			int cnum=0;
			for(final Enumeration c=CMClass.charClasses();c.hasMoreElements();)
			{
				final CharClass C=(CharClass)c.nextElement();
				if((C.ID().equals(sort))&&(CMProps.isTheme(C.availabilityCode())))
					sortByClassNum=cnum;
				cnum++;
			}
		}
		for(final Enumeration<Ability> a=CMClass.abilities();a.hasMoreElements();)
		{
			final Ability A=a.nextElement();
			final StringBuffer buf=new StringBuffer("");
			int numFound=0;
			for(final Enumeration c=CMClass.charClasses();c.hasMoreElements();)
			{
				final CharClass C=(CharClass)c.nextElement();
				if(CMProps.isTheme(C.availabilityCode())
				   &&(CMLib.ableMapper().getQualifyingLevel(C.ID(),true,A.ID())>=0))
					if((++numFound)>0) break;
			}
			if(numFound>0)
			{
				buf.append("<TR><TD><B>"+A.name()+"</B></TD>");
				int cnum=0;
				for(final Enumeration c=CMClass.charClasses();c.hasMoreElements();)
				{
					final CharClass C=(CharClass)c.nextElement();
					if(CMProps.isTheme(C.availabilityCode()))
					{
						final int qual=CMLib.ableMapper().getQualifyingLevel(C.ID(),true,A.ID());
						if(qual>=0)
						{
							buf.append("<TD>"+qual+"</TD>");
							if((cnum==sortByClassNum)&&(!rowsFavoring.contains(buf)))
								rowsFavoring.addElement(buf);
						}
						else
							buf.append("<TD><BR></TD>");
					}
					cnum++;
				}
				if(!rowsFavoring.contains(buf))
					allOtherRows.addElement(buf);
				buf.append("</TR>");
			}
		}
		final StringBuffer buf=new StringBuffer("");
		for(int i=0;i<rowsFavoring.size();i++)
			buf.append((StringBuffer)rowsFavoring.elementAt(i));
		for(int i=0;i<allOtherRows.size();i++)
			buf.append((StringBuffer)allOtherRows.elementAt(i));
		return clearWebMacros(buf);
	}

}
