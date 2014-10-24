package com.suscipio_solutions.consecro_mud.Races;
import com.suscipio_solutions.consecro_mud.Areas.interfaces.Area;

public class Hobbit extends Halfling
{
	@Override public String ID(){	return "Hobbit"; }
	@Override public String name(){ return "Hobbit"; }
	@Override public int availabilityCode(){return Area.THEME_FANTASY|Area.THEME_SKILLONLYMASK;}
}
