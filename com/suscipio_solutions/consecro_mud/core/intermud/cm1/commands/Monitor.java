package com.suscipio_solutions.consecro_mud.core.intermud.cm1.commands;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.Log;
import com.suscipio_solutions.consecro_mud.core.intermud.cm1.RequestHandler;


public class Monitor extends Listen
{
	@Override public String getCommandWord(){ return "MONITOR";}

	public Monitor(RequestHandler req, String parameters)
	{
		super(req, parameters);
	}

	@Override
	protected void sendMsg(Listener listener, String msg) throws IOException
	{
		synchronized(listener)
		{
			listener.msgs.add(listener.channelName+": "+msg);
		}
	}

	@Override
	public void run()
	{
		try
		{
			String name;
			String rest="";
			final int x=parameters.indexOf(' ');
			if(x>0)
			{
				name=parameters.substring(0,x).trim();
				if(name.trim().length()==0)
					name=null;
				else
					rest=parameters.substring(x+1).trim();
			}
			else
				name=null;
			if(name==null)
			{
				req.sendMsg("[FAIL No "+getCommandWord()+"ER name given]");
				return;
			}
			final List<ListenCriterium> crit=getCriterium(rest);
			if(crit==null)
				return;
			else
			if(crit.size()==0)
			{
				final List<String> msgs=new LinkedList<String>();
				for(final Listener l : listeners)
					if(l.channelName.equalsIgnoreCase(name))
					{
						synchronized(l)
						{
							for(final Iterator<String> i = l.msgs.iterator();i.hasNext();)
							{
								final String s=i.next();
								msgs.add(s);
								i.remove();
							}
						}
					}
				if(msgs.size()==0)
					req.sendMsg("[FAIL NONE]");
				else
				{
					req.sendMsg("[OK /MESSAGES:"+msgs.size()+"]");
					for(final String s : msgs)
						req.sendMsg("[MESSAGE "+s+"]");
				}
			}
			else
			{
				final Listener newListener = new Listener(name,crit.toArray(new ListenCriterium[0]));
				CMLib.commands().addGlobalMonitor(newListener);
				req.addDependent(newListener.channelName, newListener);
				listeners.add(newListener);
				req.sendMsg("[OK]");
			}
		}
		catch(final Exception ioe)
		{
			Log.errOut(className,ioe);
			req.close();
		}
	}
}
