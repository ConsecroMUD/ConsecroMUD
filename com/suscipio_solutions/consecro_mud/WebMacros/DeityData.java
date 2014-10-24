package com.suscipio_solutions.consecro_mud.WebMacros;

import java.net.URLEncoder;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.CharClasses.interfaces.CharClass;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.Deity;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.collections.DVector;
import com.suscipio_solutions.consecro_web.interfaces.HTTPRequest;


@SuppressWarnings({"unchecked","rawtypes"})
public class DeityData extends StdWebMacro
{
	@Override public String name() { return "DeityData"; }

	// valid parms include description, worshipreq, clericreq,
	// worshiptrig, clerictrig, worshipsintrig,clericsintrig,powertrig

	private DVector getDeityData(HTTPRequest httpReq, String deityName)
	{
		DVector folData=(DVector)httpReq.getRequestObjects().get("DEITYDATAFOR-"+deityName.toUpperCase().trim());
		if(folData!=null) return folData;
		folData = CMLib.database().worshippers(deityName);
		httpReq.getRequestObjects().put("DEITYDATAFOR-"+deityName.toUpperCase().trim(),folData);
		return folData;
	}

	@Override
	public String runMacro(HTTPRequest httpReq, String parm)
	{
		final java.util.Map<String,String> parms=parseParms(parm);
		final String last=httpReq.getUrlParameter("DEITY");
		if(last==null) return " @break@";
		if(last.length()>0)
		{
			Deity D=CMLib.map().getDeity(last);
			if(D!=null)
			{
				final StringBuffer str=new StringBuffer("");
				if(parms.containsKey("DESCRIPTION"))
					str.append(D.description()+", ");
				if(parms.containsKey("NAME"))
					str.append(D.Name()+", ");
				if(parms.containsKey("LOCATION"))
				{
					if(D.getStartRoom()==null)
						str.append("Nowhere, ");
					else
						str.append(CMLib.map().getExtendedRoomID(D.getStartRoom())+": "+D.getStartRoom().displayText()+", ");
				}
				if(parms.containsKey("AREA")&&(D.getStartRoom()!=null))
					if(parms.containsKey("ENCODED"))
						try {str.append(URLEncoder.encode(D.getStartRoom().getArea().Name(),"UTF-8")+", ");}catch(final Exception e){}
					else
						str.append(D.getStartRoom().getArea().Name()+", ");
				if(parms.containsKey("ROOM")&&(D.getStartRoom()!=null))
					if(parms.containsKey("ENCODED"))
						try {str.append(URLEncoder.encode(D.getStartRoom().roomID(),"UTF-8")+", ");}catch(final Exception e){}
					else
						str.append(D.getStartRoom().roomID()+", ");
				if(parms.containsKey("MOBCODE"))
				{
					final String roomID=D.getStartRoom().roomID();
					List classes=(List)httpReq.getRequestObjects().get("DEITYLIST-"+roomID);
					if(classes==null)
					{
						classes=new Vector();
						Room R=(Room)httpReq.getRequestObjects().get(roomID);
						if(R==null)
						{
							R=CMLib.map().getRoom(roomID);
							if(R==null)
								return "No Room?!";
							final Vector restoreDeities = new Vector();
							for(final Enumeration e=CMLib.map().deities();e.hasMoreElements();)
							{
								final Deity D2 = (Deity)e.nextElement();
								if((D2.getStartRoom()!=null)
								&&(CMLib.map().getExtendedRoomID(D2.getStartRoom()).equalsIgnoreCase(CMLib.map().getExtendedRoomID(R))))
									restoreDeities.addElement(D2);
							}
							CMLib.map().resetRoom(R);
							R=CMLib.map().getRoom(roomID);
							for(int d=restoreDeities.size()-1;d>=0;d--)
							{
								final Deity D2=(Deity)restoreDeities.elementAt(d);
								if(CMLib.map().getDeity(D2.Name())!=null)
									restoreDeities.removeElementAt(d);
							}
							for(final Enumeration e=restoreDeities.elements();e.hasMoreElements();)
							{
								final Deity D2=(Deity)e.nextElement();
								for(int i=0;i<R.numInhabitants();i++)
								{
									final MOB M=R.fetchInhabitant(i);
									if((M instanceof Deity)
									&&(M.Name().equals(D2.Name())))
										CMLib.map().registerWorldObjectLoaded(R.getArea(),R,M);
								}
							}
							httpReq.getRequestObjects().put(roomID,R);
							D=CMLib.map().getDeity(last);
						}
						synchronized(("SYNC"+roomID).intern())
						{
							R=CMLib.map().getRoom(R);
							for(int m=0;m<R.numInhabitants();m++)
							{
								final MOB M=R.fetchInhabitant(m);
								if(M.isSavable())
									classes.add(M);
							}
							RoomData.contributeMOBs(classes);
						}
						httpReq.getRequestObjects().put("DEITYLIST-"+roomID,classes);
					}
					if(parms.containsKey("ENCODED"))
						try {str.append(URLEncoder.encode(RoomData.getMOBCode(classes,D),"UTF-8")+", ");}catch(final Exception e){}
					else
						str.append(RoomData.getMOBCode(classes,D)+", ");
				}
				if(parms.containsKey("WORSHIPREQ"))
					str.append(D.getWorshipRequirementsDesc()+", ");
				if(parms.containsKey("CLERICREQ"))
					str.append(D.getClericRequirementsDesc()+", ");
				if(parms.containsKey("SERVICETRIG"))
					str.append(D.getServiceTriggerDesc()+", ");
				if(D.numCurses()>0)
				{
					if(parms.containsKey("WORSHIPSINTRIG"))
						str.append(D.getWorshipSinDesc()+", ");
					if(parms.containsKey("CLERICSINTRIG"))
						str.append(D.getClericSinDesc()+", ");
				}

				if(D.numPowers()>0)
				if(parms.containsKey("POWERTRIG"))
					str.append(D.getClericPowerupDesc()+", ");
				if(D.numBlessings()>0)
				{
					if(parms.containsKey("WORSHIPTRIG"))
						str.append(D.getWorshipTriggerDesc()+", ");
					if(parms.containsKey("CLERICTRIG"))
						str.append(D.getClericTriggerDesc()+", ");
				}
				if(parms.containsKey("NUMFOLLOWERS"))
				{
					final DVector data=getDeityData(httpReq,D.Name());
					final int num=data.size();
					str.append(num+", ");
				}
				if(parms.containsKey("NUMPRIESTS"))
				{
					final DVector data=getDeityData(httpReq,D.Name());
					int num=0;
					//DV.addElement(username, cclass, ""+level, race);
					for(int d=0;d<data.size();d++)
					{
						final CharClass C=CMClass.getCharClass((String)data.elementAt(d, 2));
						if((C!=null)&&(C.baseClass().equalsIgnoreCase("CLERIC")))
							num++;
					}
					str.append(num+", ");
				}
				String strstr=str.toString();
				if(strstr.endsWith(", "))
					strstr=strstr.substring(0,strstr.length()-2);
				return clearWebMacros(strstr);
			}
		}
		return "";
	}
}
