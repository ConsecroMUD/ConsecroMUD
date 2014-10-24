package com.suscipio_solutions.consecro_mud.WebMacros;

import java.util.List;

import com.suscipio_solutions.consecro_mud.Libraries.interfaces.DatabaseEngine.PlayerData;
import com.suscipio_solutions.consecro_mud.core.B64Encoder;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.Resources;
import com.suscipio_solutions.consecro_mud.core.exceptions.HTTPServerException;
import com.suscipio_solutions.consecro_web.interfaces.HTTPRequest;


public class PlayerPortrait extends StdWebMacro
{
	@Override public String name() { return "PlayerPortrait"; }

	@Override public boolean isAWebPath(){return true;}
	@Override public boolean preferBinary(){return true;}

	@Override
	public String getFilename(HTTPRequest httpReq, String filename)
	{
		final String foundFilename=httpReq.getUrlParameter("FILENAME");
		if((foundFilename!=null)&&(foundFilename.length()>0))
			return foundFilename;
		return filename;
	}

	@Override
	public byte[] runBinaryMacro(HTTPRequest httpReq, String parm) throws HTTPServerException
	{
		final String last=httpReq.getUrlParameter("PLAYER");
		if(last==null) return null; // for binary macros, null is BREAK
		byte[] img=null;
		if(last.length()>0)
		{
			img=(byte[])Resources.getResource("CMPORTRAIT-"+last);
			if(img==null)
			{
				final List<PlayerData> data=CMLib.database().DBReadData(last,"CMPORTRAIT");
				if((data!=null)&&(data.size()>0))
				{
					final String encoded=data.get(0).xml;
					img=B64Encoder.B64decode(encoded);
					if(img!=null)
						Resources.submitResource("CMPORTRAIT-"+last,img);
				}
			}
		}
		return img;
	}

	@Override
	public String runMacro(HTTPRequest httpReq, String parm) throws HTTPServerException
	{
		return "[Unimplemented string method!]";
	}
}
