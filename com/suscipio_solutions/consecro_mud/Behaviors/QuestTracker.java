package com.suscipio_solutions.consecro_mud.Behaviors;
import java.util.Vector;



@SuppressWarnings("rawtypes")
public class QuestTracker extends StdBehavior
{
	@Override public String ID(){return "QuestTracker";}

	public Vector questObjects=new Vector();
}
