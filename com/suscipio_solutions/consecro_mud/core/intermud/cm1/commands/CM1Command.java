package com.suscipio_solutions.consecro_mud.core.intermud.cm1.commands;
import com.suscipio_solutions.consecro_mud.Areas.interfaces.Area;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.Log;
import com.suscipio_solutions.consecro_mud.core.interfaces.PhysicalAgent;
import com.suscipio_solutions.consecro_mud.core.intermud.cm1.RequestHandler;


public abstract class CM1Command implements Runnable, Cloneable
{
	protected final String className="CM1"+getClass().getName().substring(getClass().getName().lastIndexOf('.'));
	protected final String parameters;
	protected final RequestHandler req;

	public CM1Command()
	{
		super();
		req=null;
		parameters="";
	}

	public CM1Command(final RequestHandler req, final String parameters)
	{
		super();
		this.parameters = parameters;
		this.req=req;
	}

	public static CM1Command newInstance(Class<? extends CM1Command> cls, RequestHandler req, String parms)
	{
		try
		{
			if(cls==null)
				return null;
			return cls.getConstructor(RequestHandler.class, String.class).newInstance(req,parms);
		}
		catch(final Exception e)
		{
			Log.errOut("CM1Command",e);
			return null;
		}
	}

	public PhysicalAgent getTarget(String parameters)
	{
		if(parameters.equalsIgnoreCase("USER"))
			return req.getUser();
		final int x=parameters.indexOf('@');
		String who=parameters;
		String where="";
		PhysicalAgent P=req.getTarget();
		if(x>0)
		{
			who=parameters.substring(0,x);
			where=parameters.substring(x+1);
			Room R=CMLib.map().getRoom(where);
			if(R==null)
			{
				final Area A=CMLib.map().getArea(where);
				if(A!=null) R=A.getRandomMetroRoom();
			}
			if(who.length()==0)
				P=R;
		}
		else
		{
			final MOB M=CMLib.players().getLoadPlayer(who);
			if(M!=null) return M;
		}
		final Room R=CMLib.map().roomLocation(P);
		if(R==null) CMLib.map().roomLocation(req.getTarget());
		if(R==null) return null;
		P=R.fetchFromRoomFavorMOBs(null,who);
		if((P==null)&&(req.getTarget() instanceof MOB))
			P=R.fetchFromRoomFavorMOBs(null,who);
		return P;
	}

	public abstract String getCommandWord();
	public abstract boolean passesSecurityCheck(MOB user, PhysicalAgent target);
	public abstract String getHelp(MOB user, PhysicalAgent target, String rest);
}
