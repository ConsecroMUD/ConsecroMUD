package com.suscipio_solutions.consecro_mud.Commands;
import java.util.List;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Coins;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Items.interfaces.PackagedItems;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.CMProps;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.ItemPossessor;


@SuppressWarnings({"unchecked","rawtypes"})
public class Package extends StdCommand
{
	public Package(){}

	private final String[] access=I(new String[]{"PACKAGE"});
	@Override public String[] getAccessWords(){return access;}

	@Override
	public boolean execute(MOB mob, Vector commands, int metaFlags)
		throws java.io.IOException
	{
		if(commands.size()<2)
		{
			mob.tell(L("Package what?"));
			return false;
		}
		commands.removeElementAt(0);
		String whatName="";
		if(commands.size()>0)
			whatName=(String)commands.lastElement();
		final int maxToGet=CMLib.english().calculateMaxToGive(mob,commands,true,mob,false);
		if(maxToGet<0) return false;

		String whatToGet=CMParms.combine(commands,0);
		boolean allFlag=(commands.size()>0)?((String)commands.elementAt(0)).equalsIgnoreCase("all"):false;
		if(whatToGet.toUpperCase().startsWith("ALL.")){ allFlag=true; whatToGet="ALL "+whatToGet.substring(4);}
		if(whatToGet.toUpperCase().endsWith(".ALL")){ allFlag=true; whatToGet="ALL "+whatToGet.substring(0,whatToGet.length()-4);}
		final Vector<Item> V=new Vector<Item>();
		int addendum=1;
		String addendumStr="";
		do
		{
			Environmental getThis=null;
			getThis=mob.location().fetchFromRoomFavorItems(null,whatToGet+addendumStr);
			if(getThis==null) break;
			if((getThis instanceof Item)
			&&(CMLib.flags().canBeSeenBy(getThis,mob))
			&&((!allFlag)||CMLib.flags().isGettable(((Item)getThis))||(getThis.displayText().length()>0))
			&&(!V.contains(getThis)))
				V.addElement((Item)getThis);
			addendumStr="."+(++addendum);
		}
		while((allFlag)&&(addendum<=maxToGet));

		if(V.size()==0)
		{
			mob.tell(L("You don't see '@x1' here.",whatName));
			return false;
		}

		for(int i=0;i<V.size();i++)
		{
			final Item I=V.get(i);
			if((I instanceof Coins)
			||(CMLib.flags().isEnspelled(I))
			||(CMLib.flags().isOnFire(I)))
			{
				mob.tell(L("Items such as @x1 may not be packaged.",I.name(mob)));
				return false;
			}
		}
		final PackagedItems thePackage=(PackagedItems)CMClass.getItem("GenPackagedItems");
		if(thePackage==null) return false;
		if(!thePackage.isPackagable(V))
		{
			mob.tell(L("All items in a package must be absolutely identical.  Some here are not."));
			return false;
		}
		Item getThis=null;
		for(int i=0;i<V.size();i++)
		{
			getThis=V.elementAt(i);
			if((!mob.isMine(getThis))&&(!Get.get(mob,null,getThis,true,"get",true)))
				return false;
		}
		if(getThis==null)
			return false;
		final String name=CMLib.english().cleanArticles(getThis.name());
		final CMMsg msg=CMClass.getMsg(mob,getThis,null,CMMsg.MSG_NOISYMOVEMENT,L("<S-NAME> package(s) up @x1 <T-NAMENOART>(s).",""+V.size()));
		if(mob.location().okMessage(mob,msg))
		{
			mob.location().send(mob,msg);
			thePackage.setName(name);
			if(thePackage.packageMe(getThis,V.size()))
			{
				for(int i=0;i<V.size();i++)
					V.elementAt(i).destroy();
				mob.location().addItem(thePackage,ItemPossessor.Expire.Player_Drop);
				mob.location().recoverRoomStats();
				mob.location().recoverRoomStats();
			}
		}
		return false;
	}
	@Override public double combatActionsCost(final MOB mob, final List<String> cmds){return CMProps.getCommandCombatActionCost(ID());}
	@Override public double actionsCost(final MOB mob, final List<String> cmds){return CMProps.getCommandActionCost(ID());}
	@Override public boolean canBeOrdered(){return true;}


}
