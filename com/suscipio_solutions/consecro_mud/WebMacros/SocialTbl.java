package com.suscipio_solutions.consecro_mud.WebMacros;

import java.util.List;

import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.Resources;
import com.suscipio_solutions.consecro_web.interfaces.HTTPRequest;


public class SocialTbl extends StdWebMacro
{
	@Override public String name()	{return "SocialTbl";}

	protected static final int AT_MAX_COL = 6;

	@Override
	public String runMacro(HTTPRequest httpReq, String parm)
	{
		final StringBuffer TBL=(StringBuffer)Resources.getResource("WEB SOCIALS TBL");
		if(TBL!=null) return TBL.toString();

		final List<String> socialVec=CMLib.socials().getSocialsList();
		final StringBuffer msg=new StringBuffer("\n\r");
		int col=0;
		int percent = 100/AT_MAX_COL;
		for(int i=0;i<socialVec.size();i++)
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
				if (i > socialVec.size() - AT_MAX_COL)
					percent = 100;
			}

			msg.append("<td");

			if (percent == 100)
				msg.append(" colspan=\"" + AT_MAX_COL + "\"");	//last element is width of remainder
			else
				msg.append(" width=\"" + percent + "%\"");

			msg.append(">");
			msg.append(socialVec.get(i));
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
		Resources.submitResource("WEB SOCIALS TBL",msg);
		return clearWebMacros(msg);
	}

}
