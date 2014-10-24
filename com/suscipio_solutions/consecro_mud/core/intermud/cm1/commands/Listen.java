package com.suscipio_solutions.consecro_mud.core.intermud.cm1.commands;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Areas.interfaces.Area;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Libraries.interfaces.ChannelsLibrary;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.CMSecurity;
import com.suscipio_solutions.consecro_mud.core.CMStrings;
import com.suscipio_solutions.consecro_mud.core.Log;
import com.suscipio_solutions.consecro_mud.core.collections.SLinkedList;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.MsgMonitor;
import com.suscipio_solutions.consecro_mud.core.interfaces.PhysicalAgent;
import com.suscipio_solutions.consecro_mud.core.intermud.cm1.RequestHandler;


public class Listen extends CM1Command
{
	@Override public String getCommandWord(){ return "LISTEN";}
	protected static enum STATTYPE {CHANNEL,LOGINS,MOB,ROOM,PLAYER,ABILITY,ITEM,TARGET,SOURCE,TOOL,TARGETCODE,SOURCECODE,OTHERSCODE,AREA,TARGETMASK,SOURCEMASK,OTHERSMASK}
	protected static SLinkedList<Listener> listeners=new SLinkedList<Listener>();

	public Listen(RequestHandler req, String parameters)
	{
		super(req, parameters);
	}

	protected void sendMsg(Listener listener, String msg) throws IOException
	{
		req.sendMsg("[MESSAGE "+listener.channelName+": "+msg+"]");
	}

	protected static class ListenCriterium
	{
		public final STATTYPE statType;
		public final Environmental obj;
		public final String parm;
		public final int parmInt;
		public ListenCriterium(STATTYPE statType, Environmental obj, String parm)
		{
			this.statType=statType;
			this.obj=obj;
			switch(statType)
			{
			case CHANNEL:
				this.parm=(parm==null)?"":parm.toUpperCase().trim();
				parmInt=CMLib.channels().getChannelIndex(this.parm);
				break;
			case SOURCECODE:
			case TARGETCODE:
			case OTHERSCODE:
				this.parm=(parm==null)?"":parm.toUpperCase().trim();
				parmInt= CMParms.indexOf(CMMsg.TYPE_DESCS,this.parm);
				break;
			case SOURCEMASK:
			case TARGETMASK:
			case OTHERSMASK:
				this.parm=(parm==null)?"":parm.toUpperCase().trim();
				if(CMParms.indexOf(CMMsg.MASK_DESCS,this.parm)>=0)
				{
					final Integer I=CMMsg.Desc.getMSGTYPE_DESCS().get(this.parm);
					if(I!=null)
						parmInt=I.intValue();
					else
						parmInt=-1;
				}
				else
					parmInt=-1;
				break;
			default:
				parmInt=0;
				this.parm=(parm==null)?"":parm;
				break;
			}
		}
	}

	protected class Listener implements MsgMonitor
	{
		public final String channelName;
		private final ListenCriterium crits[];
		public final List<String> msgs=new LinkedList<String>();

		public Listener(String channelName, ListenCriterium[] crits)
		{
			this.channelName=channelName.toUpperCase().trim();
			this.crits=crits;
		}

		public boolean doesMonitor(final ListenCriterium crit, final Room room, final CMMsg msg)
		{
			switch(crit.statType)
			{
			case CHANNEL:
				return (msg.othersMajor(CMMsg.MASK_CHANNEL))
					&& (crit.parmInt==(msg.othersMinor()-CMMsg.TYP_CHANNEL));
			case LOGINS: return (msg.othersMinor()==CMMsg.TYP_LOGIN)||(msg.othersMinor()==CMMsg.TYP_QUIT);
			case MOB: return msg.source()==crit.obj;
			case ROOM: return room==crit.obj;
			case AREA: return room.getArea()==crit.obj;
			case PLAYER: return ((MOB)crit.obj).location()==room;
			case ABILITY: return msg.tool()==crit.obj;
			case ITEM: return (msg.target()==crit.obj);
			case TARGET: return (msg.target()==crit.obj);
			case SOURCE: return (msg.source()==crit.obj);
			case TOOL: return (msg.tool()==crit.obj);
			case SOURCECODE: return msg.sourceMinor()==crit.parmInt;
			case TARGETCODE: return msg.targetMinor()==crit.parmInt;
			case OTHERSCODE: return msg.othersMinor()==crit.parmInt;
			case SOURCEMASK: return msg.sourceMajor(crit.parmInt);
			case TARGETMASK: return msg.targetMajor(crit.parmInt);
			case OTHERSMASK: return msg.othersMajor(crit.parmInt);
			}
			return false;
		}

		public boolean doesMonitor(final Room room, final CMMsg msg)
		{
			for(final ListenCriterium crit : crits)
				if(!doesMonitor(crit,room,msg))
					return false;
			return true;
		}

		private String minorDesc(int code)
		{
			String desc = CMMsg.Desc.getMSGDESC_TYPES().get(Integer.valueOf(code));
			if(desc==null) desc = "?";
			return desc;

		}

		public String messageToString(final CMMsg msg)
		{
			switch(crits[0].statType)
			{
			case CHANNEL:
				return CMLib.coffeeFilter().fullOutFilter(null, CMLib.map().deity(), msg.source(), msg.target(), msg.tool(), msg.othersMessage(), false);
			case LOGINS:
				if(msg.othersMinor()==CMMsg.TYP_LOGIN)
					return "LOGIN "+msg.source().Name();
				else
					return "LOGOUT "+msg.source().Name();
			default:
			{
				final StringBuilder cmd=new StringBuilder("");
				cmd.append('\"').append(msg.source().Name()).append('\"').append(' ');
				cmd.append(minorDesc(msg.sourceMinor())).append(' ');
				if(msg.target()!=null)
					cmd.append('\"').append(msg.target().Name()).append('\"').append(' ');
				else
					cmd.append("NULL ");
				cmd.append(minorDesc(msg.targetMinor())).append(' ');
				if(msg.tool()!=null)
					cmd.append('\"').append(msg.tool().Name()).append('\"').append(' ');
				else
					cmd.append("NULL ");
				cmd.append(minorDesc(msg.othersMinor())).append(' ');
				cmd.append(Integer.toString(msg.value())).append(' ');
				cmd.append(CMStrings.removeColors(CMLib.coffeeFilter().fullOutFilter(null, CMLib.map().deity(), msg.source(), msg.target(), msg.tool(), msg.othersMessage(), false)));
				return cmd.toString();
			}
			}
		}

		@Override
		public void monitorMsg(Room room, CMMsg msg)
		{
			try
			{
				if(doesMonitor(room,msg))
					sendMsg(this, messageToString(msg));
			}
			catch(final IOException ioe)
			{
				CMLib.commands().delGlobalMonitor(this);
				req.delDependent(channelName);
				listeners.remove(this);
			}
		}
	}

	public boolean securityCheck(MOB user, ListenCriterium crit)
	{
		switch(crit.statType)
		{
		case CHANNEL:
		{
			if(crit.parmInt<0)
				return false;
			final ChannelsLibrary.CMChannel chan=CMLib.channels().getChannel(crit.parmInt);
			if(!CMLib.masking().maskCheck(chan.mask,user,true))
				return false;
			final Set<ChannelsLibrary.ChannelFlag> flags=chan.flags;
			if(flags.contains(ChannelsLibrary.ChannelFlag.CLANONLY)||flags.contains(ChannelsLibrary.ChannelFlag.CLANALLYONLY))
				return CMSecurity.isAllowedAnywhere(user, CMSecurity.SecFlag.STAT);
			return true;
		}
		case SOURCECODE:
		case TARGETCODE:
		case OTHERSCODE:
		case SOURCEMASK:
		case TARGETMASK:
		case OTHERSMASK:
			if(crit.parmInt<0)
				return false;
			return true;
		case PLAYER:  return CMSecurity.isAllowedEverywhere(user, CMSecurity.SecFlag.CMDPLAYERS);
		case MOB:
		case ROOM:
		case AREA:
		case ABILITY:
		case ITEM:
		case TARGET:
		case SOURCE:
		case TOOL:
		default:
			return true;
		}
	}

	public boolean parameterCheck(MOB user, ListenCriterium crit)
	{
		switch(crit.statType)
		{
		case CHANNEL:
		{
			if(crit.parmInt<0)
				return false;
			return true;
		}
		case SOURCECODE:
		case TARGETCODE:
		case OTHERSCODE:
		case SOURCEMASK:
		case TARGETMASK:
		case OTHERSMASK:
			if(crit.parmInt<0)
				return false;
			return true;
		case MOB: return (crit.obj instanceof MOB)&&(!CMLib.players().playerExists(crit.obj.Name()));
		case ROOM:  return crit.obj instanceof Room;
		case AREA:  return crit.obj instanceof Area;
		case PLAYER:  return (crit.obj instanceof MOB)&&(CMLib.players().playerExists(crit.obj.Name()));
		case ABILITY: return crit.obj instanceof Ability;
		case ITEM:  return crit.obj instanceof Item;
		case TARGET: return crit.obj != null;
		case SOURCE:  return (crit.obj instanceof MOB)&&(!CMLib.players().playerExists(crit.obj.Name()));
		case TOOL:  return crit.obj != null;
		default:
			return true;
		}
	}

	public List<ListenCriterium> getCriterium(String rest) throws IOException
	{
		final List<ListenCriterium> list=new Vector<ListenCriterium>();
		while(rest.length()>0)
		{
			String codeStr;
			int x=rest.indexOf(' ');
			if(x>0)
			{
				codeStr=rest.substring(0,x).toUpperCase().trim();
				if(codeStr.trim().length()==0)
					codeStr=null;
				else
					rest=rest.substring(x+1).trim();
			}
			else
			if(rest.trim().length()>0)
			{
				codeStr=rest.toUpperCase().trim();
				rest="";
			}
			else
				codeStr=null;
			try
			{
				STATTYPE.valueOf(codeStr);
			}
			catch(final Exception iox)
			{
				req.sendMsg("[FAIL "+codeStr+" NOT "+CMParms.toStringList(STATTYPE.values())+"]");
				return null;
			}
			String parm=null;
			x=rest.indexOf(' ');
			if(x>0)
			{
				parm=rest.substring(0,x).trim();
				try
				{
					STATTYPE.valueOf(parm.toUpperCase().trim());
					parm="";
				}catch(final java.lang.IllegalArgumentException ix)
				{
					rest=rest.substring(x+1).trim();
				}
			}
			else
			if(rest.trim().length()>0)
			{
				try
				{
					STATTYPE.valueOf(rest.toUpperCase().trim());
					parm="";
				}catch(final java.lang.IllegalArgumentException ix)
				{
					parm=rest;
					rest="";
				}
			}
			else
			{
				parm="";
				rest="";
			}
			final ListenCriterium crit=new ListenCriterium(STATTYPE.valueOf(codeStr),req.getTarget(),parm);
			if(!parameterCheck(req.getUser(),crit))
			{
				req.sendMsg("[FAIL "+codeStr+" PARAMETERS]");
				return null;
			}
			if(!securityCheck(req.getUser(),crit))
			{
				req.sendMsg("[FAIL "+codeStr+" UNAUTHORIZED]");
				return null;
			}
			list.add(crit);
		}
		return list;
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
				req.sendMsg("[FAIL NOT "+CMParms.toStringList(STATTYPE.values())+"]");
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

	// depends on what you want to listen to
	@Override
	public boolean passesSecurityCheck(MOB user, PhysicalAgent target)
	{
		return (user != null);
	}

	@Override
	public String getHelp(MOB user, PhysicalAgent target, String rest)
	{
		return "USAGE: "+getCommandWord()+" <"+getCommandWord()+"ER NAME> "+CMParms.toStringList(STATTYPE.values());
	}
}
