package com.suscipio_solutions.consecro_mud.Commands;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Areas.interfaces.Area;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Items.interfaces.AmmunitionWeapon;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Wearable;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMClass.CMObjectType;
import com.suscipio_solutions.consecro_mud.core.CMFile;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.CMProps;
import com.suscipio_solutions.consecro_mud.core.CMSecurity;
import com.suscipio_solutions.consecro_mud.core.Resources;
import com.suscipio_solutions.consecro_mud.core.collections.XVector;
import com.suscipio_solutions.consecro_mud.core.interfaces.CMObject;


@SuppressWarnings({"unchecked","rawtypes"})
public class Unload extends StdCommand
{
	public Unload(){}

	private final String[] access=I(new String[]{"UNLOAD"});
	@Override public String[] getAccessWords(){return access;}
	final String[] IMMORTAL_LIST={"CLASS", "HELP", "USER", "AREA", "FACTION", "ALL", "FILE", "RESOURCE", "INIFILE", "[FILENAME]"};


	@Override
	public boolean execute(MOB mob, Vector commands, int metaFlags)
		throws java.io.IOException
	{
		if(mob==null) return true;
		boolean tryImmortal=CMSecurity.isAllowed(mob,mob.location(),CMSecurity.SecFlag.LOADUNLOAD);
		if(commands.size()<2)
		{
			if(tryImmortal)
				mob.tell(L("UNLOAD what? Try @x1",CMParms.toStringList(IMMORTAL_LIST)));
			else
				mob.tell(L("Unload what?"));
			return false;
		}
		final String str=CMParms.combine(commands,1);
		if(tryImmortal)
		{
			final Item I=mob.fetchWieldedItem();
			if((I instanceof AmmunitionWeapon)&&((AmmunitionWeapon)I).requiresAmmunition())
				tryImmortal=false;
			for(final String aList : IMMORTAL_LIST)
				if(str.equalsIgnoreCase(aList))
					tryImmortal=true;
		}
		if(!tryImmortal)
		{
			commands.removeElementAt(0);
			final List<Item> baseItems=CMLib.english().fetchItemList(mob,mob,null,commands,Wearable.FILTER_ANY,false);
			final List<AmmunitionWeapon> items=new XVector<AmmunitionWeapon>();
			for (Item I : baseItems)
			{
				if((I instanceof AmmunitionWeapon)&&((AmmunitionWeapon)I).requiresAmmunition())
					items.add((AmmunitionWeapon)I);
			}
			if(baseItems.size()==0)
				mob.tell(L("You don't seem to have that."));
			else
			if(items.size()==0)
				mob.tell(L("You can't seem to unload that."));
			else
			for(final AmmunitionWeapon W : items)
			{
				final Item ammunition=CMLib.coffeeMaker().makeAmmunition(W.ammunitionType(),W.ammunitionRemaining());
				final CMMsg newMsg=CMClass.getMsg(mob,W,ammunition,CMMsg.MSG_UNLOAD,L("<S-NAME> unload(s) <O-NAME> from <T-NAME>."));
				if(mob.location().okMessage(mob,newMsg))
					mob.location().send(mob,newMsg);
			}
		}
		else
		{
			String what=(String)commands.elementAt(1);
			if((what.equalsIgnoreCase("CLASS")||(CMClass.findObjectType(what)!=null))
			&&(CMSecurity.isASysOp(mob)))
			{
				if(commands.size()<3)
				{
					mob.tell(L("Unload which @x1?",what));
					return false;
				}
				if(what.equalsIgnoreCase("CLASS"))
				{
					final Object O=CMClass.getObjectOrPrototype((String)commands.elementAt(2));
					if(O!=null)
					{
						final CMClass.CMObjectType x=CMClass.getObjectType(O);
						if(x!=null) what=x.toString();
					}
				}
				final CMObjectType whatType=CMClass.findObjectType(what);
				if(whatType==null)
					mob.tell(L("Don't know how to load a '@x1'.  Try one of the following: @x2",what,CMParms.toStringList(IMMORTAL_LIST)));
				else
				{
					commands.removeElementAt(0);
					commands.removeElementAt(0);
					for(int i=0;i<commands.size();i++)
					{
						final String name=(String)commands.elementAt(0);
						final Object O=CMClass.getObjectOrPrototype(name);
						if(!(O instanceof CMObject))
							mob.tell(L("Class '@x1' was not found in the class loader.",name));
						else
						if(!CMClass.delClass(whatType,(CMObject)O))
							mob.tell(L("Failed to unload class '@x1' from the class loader.",name));
						else
							mob.tell(L("Class '@x1' was unloaded.",name));
					}
				}
				return false;
			}
			else
			if(str.equalsIgnoreCase("help"))
			{
				final CMFile F=new CMFile("//resources/help",mob);
				if((F.exists())&&(F.canRead())&&(F.canWrite())&&(F.isDirectory()))
				{
					CMLib.help().unloadHelpFile(mob);
					return false;
				}
				mob.tell(L("No access to help."));
			}
			else
			if(str.equalsIgnoreCase("inifile"))
			{
				CMProps.instance().resetSecurityVars();
				CMProps.instance().resetSystemVars();
				mob.tell(L("INI file entries have been unloaded."));
			}
			else
			if((str.equalsIgnoreCase("all"))&&(CMSecurity.isASysOp(mob)))
			{
				mob.tell(L("All soft resources unloaded."));
				CMLib.factions().removeFaction(null);
				Resources.clearResources();
				CMProps.instance().resetSecurityVars();
				CMProps.instance().resetSystemVars();
				CMLib.help().unloadHelpFile(mob);
				return false;
			}
			else
			// User Unloading
			if((((String)commands.elementAt(1)).equalsIgnoreCase("USER"))
			&&(mob.session()!=null)
			&&(CMSecurity.isAllowed(mob,mob.location(),CMSecurity.SecFlag.CMDPLAYERS)))
			{
				final String which=CMParms.combine(commands,2);
				final Vector users=new Vector();
				if(which.equalsIgnoreCase("all"))
					for(final Enumeration e=CMLib.players().players();e.hasMoreElements();)
						users.addElement(e.nextElement());
				else
				{
					final MOB M=CMLib.players().getPlayer(which);
					if(M==null)
					{
						mob.tell(L("No such user as '@x1'!",which));
						return false;
					}
					users.addElement(M);
				}
				final boolean saveFirst=mob.session().confirm(L("Save first (Y/n)?"),L("Y"));
				for(int u=0;u<users.size();u++)
				{
					final MOB M=(MOB)users.elementAt(u);
					if(M.session()!=null)
					{
						if(M!=mob)
						{
							if(M.session()!=null) M.session().stopSession(false,false,false);
							while(M.session()!=null){try{Thread.sleep(100);}catch(final Exception e){}}
							if(M.session()!=null) M.session().stopSession(true,true,true);
						}
						else
							mob.tell(L("Can't unload yourself -- a destroy is involved, which would disrupt this process."));
					}
					if(saveFirst)
					{
						// important! shutdown their affects!
						M.delAllEffects(true);
						CMLib.database().DBUpdatePlayer(M);
						CMLib.database().DBUpdateFollowers(M);
					}
				}
				int done=0;
				for(int u=0;u<users.size();u++)
				{
					final MOB M=(MOB)users.elementAt(u);
					if(M!=mob)
					{
						done++;
						if(M.session()!=null) M.session().stopSession(true,true,true);
						CMLib.players().delPlayer(M);
						M.destroy();
					}
				}

				mob.tell(L("@x1 user(s) unloaded.",""+done));
				return true;
			}
			else
			// Faction Unloading
			if((((String)commands.elementAt(1)).equalsIgnoreCase("FACTION"))
			&&(CMSecurity.isAllowed(mob, mob.location(), CMSecurity.SecFlag.CMDFACTIONS)))
			{
				final String which=CMParms.combine(commands,2);
				if(which.length()==0)
				{
					// No factions specified.  That's fine, they must mean ALL FACTIONS!!! hahahahaha
					CMLib.factions().removeFaction(null);
				}
				else
				{
					if(CMLib.factions().removeFaction(which))
					{
						mob.tell(L("Faction '@x1' unloaded.",which));
						return false;
					}
					mob.tell(L("Unknown Faction '@x1'.  Use LIST FACTIONS.",which));
					return false;
				}
			}
			else
			// Area Unloading
			if((((String)commands.elementAt(1)).equalsIgnoreCase("AREA"))
			&&(CMSecurity.isAllowed(mob, mob.location(), CMSecurity.SecFlag.CMDAREAS)))
			{
				final String which=CMParms.combine(commands,2);
				Area A=null;
				if(which.length()>0)
					A=CMLib.map().getArea(which);
				if(A==null)
					mob.tell(L("Unknown Area '@x1'.  Use AREAS.",which));
				else
				{
					return false;
				}
			}
			else
			if(("EXPERTISE".startsWith(((String)commands.elementAt(1)).toUpperCase()))
			&&(CMSecurity.isAllowed(mob, mob.location(), CMSecurity.SecFlag.EXPERTISE)))
			{
				Resources.removeResource("skills/expertises.txt");
				CMLib.expertises().recompileExpertises();
				mob.tell(L("Expertise list unloaded and reloaded."));
				return false;
			}
			else
			if(((String)commands.elementAt(1)).equalsIgnoreCase("RESOURCE"))
			{
				final String which=CMParms.combine(commands,2);
				final Iterator<String> k=Resources.findResourceKeys(which);
				if(!k.hasNext())
				{
					mob.tell(L("Unknown resource '@x1'.  Use LIST RESOURCES.",which));
					return false;
				}
				for(;k.hasNext();)
				{
					final String key=k.next();
					Resources.removeResource(key);
					mob.tell(L("Resource '@x1' unloaded.",key));
				}
			}
			else
			if(((String)commands.elementAt(1)).equalsIgnoreCase("FILE"))
			{
				final String which=CMParms.combine(commands,2);
				CMFile F1=new CMFile(which,mob,CMFile.FLAG_FORCEALLOW);
				if(!F1.exists())
				{
					int x=which.indexOf(':');
					if(x<0) x=which.lastIndexOf(' ');
					if(x>=0) F1=new CMFile(which.substring(x+1).trim(),mob,CMFile.FLAG_FORCEALLOW);
				}
				if(!F1.exists())
				{
					F1=new CMFile(Resources.buildResourcePath(which),mob,CMFile.FLAG_FORCEALLOW);
					if(!F1.exists())
					{
						int x=which.indexOf(':');
						if(x<0) x=which.lastIndexOf(' ');
						if(x>=0) F1=new CMFile(Resources.buildResourcePath(which.substring(x+1).trim()),mob,CMFile.FLAG_FORCEALLOW);
					}
				}
				if(F1.exists())
				{
					final CMFile F2=new CMFile(F1.getVFSPathAndName(),mob,CMFile.FLAG_LOGERRORS);
					if((!F2.exists())||(!F2.canRead()))
					{
						mob.tell(L("Inaccessible file resource: '@x1'",which));
						return false;
					}
					final Iterator<String> k=Resources.findResourceKeys(which);
					if(!k.hasNext())
					{
						mob.tell(L("Unknown resource '@x1'.  Use LIST RESOURCES.",which));
						return false;
					}
					for(;k.hasNext();)
					{
						final String key=k.next();
						Resources.removeResource(key);
						mob.tell(L("Resource '@x1' unloaded.",key));
					}
				}
				else
					mob.tell(L("Unknown file resource: '@x1'",which));
			}
			else
			{
				CMFile F1=new CMFile(str,mob,CMFile.FLAG_FORCEALLOW);
				if(!F1.exists())
				{
					int x=str.indexOf(':');
					if(x<0) x=str.lastIndexOf(' ');
					if(x>=0) F1=new CMFile(str.substring(x+1).trim(),mob,CMFile.FLAG_FORCEALLOW);
				}
				if(!F1.exists())
				{
					F1=new CMFile(Resources.buildResourcePath(str),mob,CMFile.FLAG_FORCEALLOW);
					if(!F1.exists())
					{
						int x=str.indexOf(':');
						if(x<0) x=str.lastIndexOf(' ');
						if(x>=0) F1=new CMFile(Resources.buildResourcePath(str.substring(x+1).trim()),mob,CMFile.FLAG_FORCEALLOW);
					}
				}
				if(F1.exists())
				{
					final CMFile F2=new CMFile(F1.getVFSPathAndName(),mob,CMFile.FLAG_LOGERRORS);
					if((!F2.exists())||(!F2.canRead()))
					{
						mob.tell(L("Inaccessible file resource: '@x1'",str));
						return false;
					}
					final Iterator<String> k=Resources.findResourceKeys(str);
					if(!k.hasNext())
					{
						mob.tell(L("Unknown resource '@x1'.  Use LIST RESOURCES.",str));
						return false;
					}
					for(;k.hasNext();)
					{
						final String key=k.next();
						Resources.removeResource(key);
						mob.tell(L("Resource '@x1' unloaded.",key));
					}
				}
				else
					mob.tell(L("Unknown resource type '@x1. Try @x2.",((String)commands.elementAt(1)),CMParms.toStringList(IMMORTAL_LIST)));
			}
		}
		return false;
	}

	@Override public boolean canBeOrdered(){return true;}
	@Override public boolean securityCheck(MOB mob){return super.securityCheck(mob);}
	@Override public double combatActionsCost(final MOB mob, final List<String> cmds){return CMProps.getCommandCombatActionCost(ID());}
	@Override public double actionsCost(final MOB mob, final List<String> cmds){return CMProps.getCommandActionCost(ID());}
}
