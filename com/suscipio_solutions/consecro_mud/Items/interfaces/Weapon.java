package com.suscipio_solutions.consecro_mud.Items.interfaces;


public interface Weapon extends Item
{
	// weapon types
	public final static int TYPE_NATURAL=0;
	public final static int TYPE_SLASHING=1;
	public final static int TYPE_PIERCING=2;
	public final static int TYPE_BASHING=3;
	public final static int TYPE_BURNING=4;
	public final static int TYPE_BURSTING=5;
	public final static int TYPE_SHOOT=6;
	public final static int TYPE_FROSTING=7;
	public final static int TYPE_GASSING=8;
	public final static int TYPE_MELTING=9;
	public final static int TYPE_STRIKING=10;
	public final static int TYPE_LASERING=11;
	public final static int TYPE_SONICING=12;
	public final static String[] TYPE_DESCS={
	"NATURAL",
	"SLASHING",
	"PIERCING",
	"BASHING",
	"BURNING",
	"BURSTING",
	"SHOOTING",
	"FROSTING",
	"GASSING",
	"MELTING",
	"STRIKING",
	"LASERING",
	"SONICING"};

	// weapon classifications
	public final static int CLASS_AXE=0;
	public final static int CLASS_BLUNT=1;
	public final static int CLASS_EDGED=2;
	public final static int CLASS_FLAILED=3;
	public final static int CLASS_HAMMER=4;
	public final static int CLASS_NATURAL=5;
	public final static int CLASS_POLEARM=6;
	public final static int CLASS_RANGED=7;
	public final static int CLASS_SWORD=8;
	public final static int CLASS_DAGGER=9;
	public final static int CLASS_STAFF=10;
	public final static int CLASS_THROWN=11;
	public final static String[] CLASS_DESCS={
	"AXE",
	"BLUNT",
	"EDGED",
	"FLAILED",
	"HAMMER",
	"KARATE",
	"POLEARM",
	"RANGED",
	"SWORD",
	"DAGGER",
	"STAFF",
	"THROWN"};


	public int weaponType();
	public int weaponClassification();
	public void setWeaponType(int newType);
	public void setWeaponClassification(int newClassification);
	public void setRanges(int min, int max);
	public String hitString(int damageAmount);
	public String missString();
}
