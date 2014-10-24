package com.suscipio_solutions.consecro_mud.WebMacros;

import java.util.Enumeration;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Areas.interfaces.Area;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMProps;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_web.interfaces.HTTPRequest;


@SuppressWarnings({"unchecked","rawtypes"})
public class AreaTbl extends StdWebMacro
{
	@Override public String name()	{return "AreaTbl";}

	protected static final int AT_MAX_COL = 3;

	@Override
	public String runMacro(HTTPRequest httpReq, String parm)
	{
		// have to check, otherwise we'll be stuffing a blank string into resources
		if(!CMProps.getBoolVar(CMProps.Bool.MUDSTARTED))
		{
			return "<TR><TD colspan=\"" + AT_MAX_COL + "\" class=\"cmAreaTblEntry\"><I>Game is not running - unable to get area list!</I></TD></TR>";
		}

		final Vector areasVec=new Vector();

		for(final Enumeration a=CMLib.map().areas();a.hasMoreElements();)
		{
			final Area A=(Area)a.nextElement();
			if((!CMLib.flags().isHidden(A))&&(!CMath.bset(A.flags(),Area.FLAG_INSTANCE_CHILD)))
				areasVec.addElement(A.name());
		}
		final StringBuffer msg=new StringBuffer("\n\r");
		int col=0;
		int percent = 100/AT_MAX_COL;
		for(int i=0;i<areasVec.size();i++)
		{
			if (col == 0)
			{
				msg.append("<tr>");
				// the bottom elements can be full width if there's
				//  not enough to fill one row
				// ie.   -X- -X- -X-
				//  	 -X- -X- -X-
				//  	 -----X-----
				//  	 -----X-----
				if (i > areasVec.size() - AT_MAX_COL)
					percent = 100;
			}

			msg.append("<td");

			if (percent == 100)
				msg.append(" colspan=\"" + AT_MAX_COL + "\"");	//last element is width of remainder
			else
				msg.append(" width=\"" + percent + "%\"");

			msg.append(L(" class=\"cmAreaTblEntry\">"));
			msg.append((String)areasVec.elementAt(i));
			msg.append("</td>");
			// finish the row
			if((percent == 100) || (++col)> (AT_MAX_COL-1 ))
			{
				msg.append("</tr>\n\r");
				col=0;
			}
		}
		if (!msg.toString().endsWith("</tr>\n\r"))
			msg.append("</tr>\n\r");
		return clearWebMacros(msg);
	}

}
