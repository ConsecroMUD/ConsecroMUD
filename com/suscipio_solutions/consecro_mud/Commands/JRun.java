package com.suscipio_solutions.consecro_mud.Commands;
import java.util.Vector;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.ScriptableObject;

import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMFile;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.CMSecurity;
import com.suscipio_solutions.consecro_mud.core.interfaces.CMObject;


@SuppressWarnings("rawtypes")
public class JRun extends StdCommand
{
	public JRun(){}

	private final String[] access=I(new String[]{"JRUN"});
	@Override public String[] getAccessWords(){return access;}
	@Override
	public boolean execute(MOB mob, Vector commands, int metaFlags)
		throws java.io.IOException
	{
		if(commands.size()<2)
		{
			mob.tell(L("jrun filename1 parm1 parm2 ..."));
			return false;
		}
		commands.removeElementAt(0);

		final String fn = (String)commands.elementAt(0);
		final StringBuffer ft = new CMFile(fn,mob,CMFile.FLAG_LOGERRORS).text();
		if((ft==null)||(ft.length()==0))
		{
			mob.tell(L("File '@x1' could not be found.",fn));
			return false;
		}
		commands.removeElementAt(0);
		final Context cx = Context.enter();
		try
		{
			final JScriptWindow scope = new JScriptWindow(mob,commands);
			cx.initStandardObjects(scope);
			scope.defineFunctionProperties(JScriptWindow.functions,
										   JScriptWindow.class,
										   ScriptableObject.DONTENUM);
			cx.evaluateString(scope, ft.toString(),"<cmd>", 1, null);
		}
		catch(final Exception e)
		{
			mob.tell(L("JavaScript error: @x1",e.getMessage()));
		}
		Context.exit();
		return false;
	}

	protected static class JScriptWindow extends ScriptableObject
	{
		@Override public String getClassName(){ return "JScriptWindow";}
		static final long serialVersionUID=45;
		MOB s=null;
		Vector v=null;
		public MOB mob(){return s;}
		public int numParms(){return (v==null)?0:v.size();}
		public String getParm(int i)
		{
			if(v==null) return "";
			if((i<0)||(i>=v.size())) return "";
			return (String)v.elementAt(i);
		}
		public static String[] functions = { "mob", "numParms", "getParm", "getParms", "toJavaString"};
		public String getParms(){return (v==null)?"":CMParms.combineQuoted(v,0);}
		public JScriptWindow(MOB executor, Vector parms){s=executor; v=parms;}
		public String toJavaString(Object O){return Context.toString(O);}
	}


	@Override public boolean canBeOrdered(){return false;}
	@Override public boolean securityCheck(MOB mob){return CMSecurity.isAllowed(mob,mob.location(),CMSecurity.SecFlag.JSCRIPTS);}

	@Override public int compareTo(CMObject o){ return CMClass.classID(this).compareToIgnoreCase(CMClass.classID(o));}

}
