package com.suscipio_solutions.consecro_mud.WebMacros;

import java.util.Enumeration;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.CharClasses.interfaces.CharClass;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMProps;
import com.suscipio_solutions.consecro_web.interfaces.HTTPRequest;


@SuppressWarnings({"unchecked","rawtypes"})
public class BaseCharClassNext extends StdWebMacro
{
	@Override public String name() { return "BaseCharClassNext"; }

	@Override
	public String runMacro(HTTPRequest httpReq, String parm)
	{
		final java.util.Map<String,String> parms=parseParms(parm);
		final String last=httpReq.getUrlParameter("BASECLASS");
		if(parms.containsKey("RESET"))
		{
			if(last!=null) httpReq.removeUrlParameter("BASECLASS");
			return "";
		}
		String lastID="";
		final Vector baseClasses=new Vector();
		for(final Enumeration c=CMClass.charClasses();c.hasMoreElements();)
		{
			final CharClass C=(CharClass)c.nextElement();
			if((CMProps.isTheme(C.availabilityCode()))||(parms.containsKey("ALL")))
			{
				if(!baseClasses.contains(C.baseClass()))
				   baseClasses.addElement(C.baseClass());
			}
		}
		for(int i=0;i<baseClasses.size();i++)
		{
			final String C=(String)baseClasses.elementAt(i);
			if((last==null)||((last.length()>0)&&(last.equals(lastID))&&(!C.equals(lastID))))
			{
				httpReq.addFakeUrlParameter("BASECLASS",C);
				return "";
			}
			lastID=C;
		}
		httpReq.addFakeUrlParameter("BASECLASS","");
		if(parms.containsKey("EMPTYOK"))
			return "<!--EMPTY-->";
		return " @break@";
	}
}
