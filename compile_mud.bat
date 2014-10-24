set Java_Home=C:\Program Files (x86)\Java\jdk1.7.0_67
set CLASSPATH=.;%Java_Home%\lib\dt.jar;%Java_Home%\lib\tools.jar;.\lib\js.jar;.\lib\jzlib.jar
SET JAVACPATH="%Java_Home%\bin\javac" -g -nowarn -deprecation

IF "%1" == "docs" GOTO :DOCS

%JAVACPATH% com/suscipio_solutions/fakedb/*.java
%JAVACPATH% com/suscipio_solutions/consecro_mud/Abilities/*.java
%JAVACPATH% com/suscipio_solutions/consecro_mud/application/*.java
%JAVACPATH% com/suscipio_solutions/consecro_mud/Areas/*.java
%JAVACPATH% com/suscipio_solutions/consecro_mud/Behaviors/*.java
%JAVACPATH% com/suscipio_solutions/consecro_mud/CharClasses/*.java
%JAVACPATH% com/suscipio_solutions/consecro_mud/Commands/*.java
%JAVACPATH% com/suscipio_solutions/consecro_mud/Common/*.java
%JAVACPATH% com/suscipio_solutions/consecro_mud/core/*.java
%JAVACPATH% com/suscipio_solutions/consecro_mud/core/collections/*.java
%JAVACPATH% com/suscipio_solutions/consecro_mud/core/database/*.java
%JAVACPATH% com/suscipio_solutions/consecro_mud/core/exceptions/*.java
%JAVACPATH% com/suscipio_solutions/consecro_mud/core/interfaces/*.java
%JAVACPATH% com/suscipio_solutions/consecro_mud/core/intermud/*.java
%JAVACPATH% com/suscipio_solutions/consecro_mud/core/intermud/cm1/*.java
%JAVACPATH% com/suscipio_solutions/consecro_mud/core/intermud/cm1/commands/*.java
%JAVACPATH% com/suscipio_solutions/consecro_mud/core/intermud/i3/*.java
%JAVACPATH% com/suscipio_solutions/consecro_mud/core/intermud/i3/net/*.java
%JAVACPATH% com/suscipio_solutions/consecro_mud/core/intermud/i3/packets/*.java
%JAVACPATH% com/suscipio_solutions/consecro_mud/core/intermud/i3/persist/*.java
%JAVACPATH% com/suscipio_solutions/consecro_mud/core/intermud/i3/server/*.java
%JAVACPATH% com/suscipio_solutions/consecro_mud/core/intermud/imc2/*.java
%JAVACPATH% com/suscipio_solutions/consecro_mud/core/smtp/*.java
%JAVACPATH% com/suscipio_solutions/consecro_mud/core/threads/*.java
%JAVACPATH% com/suscipio_solutions/consecro_mud/Exits/*.java
%JAVACPATH% com/suscipio_solutions/consecro_mud/Libraries/*.java
%JAVACPATH% com/suscipio_solutions/consecro_mud/Locales/*.java
%JAVACPATH% com/suscipio_solutions/consecro_mud/MOBS/*.java
%JAVACPATH% com/suscipio_solutions/consecro_mud/Races/*.java
%JAVACPATH% com/suscipio_solutions/consecro_mud/WebMacros/*.java
%JAVACPATH% com/suscipio_solutions/consecro_mud/Abilities/Immortal/*.java
%JAVACPATH% com/suscipio_solutions/consecro_mud/Abilities/Common/*.java
%JAVACPATH% com/suscipio_solutions/consecro_mud/Abilities/Diseases/*.java
%JAVACPATH% com/suscipio_solutions/consecro_mud/Abilities/Druid/*.java
%JAVACPATH% com/suscipio_solutions/consecro_mud/Abilities/Fighter/*.java
%JAVACPATH% com/suscipio_solutions/consecro_mud/Abilities/interfaces/*.java
%JAVACPATH% com/suscipio_solutions/consecro_mud/Abilities/Languages/*.java
%JAVACPATH% com/suscipio_solutions/consecro_mud/Abilities/Misc/*.java
%JAVACPATH% com/suscipio_solutions/consecro_mud/Abilities/Paladin/*.java
%JAVACPATH% com/suscipio_solutions/consecro_mud/Abilities/Poisons/*.java
%JAVACPATH% com/suscipio_solutions/consecro_mud/Abilities/Prayers/*.java
%JAVACPATH% com/suscipio_solutions/consecro_mud/Abilities/Properties/*.java
%JAVACPATH% com/suscipio_solutions/consecro_mud/Abilities/Ranger/*.java
%JAVACPATH% com/suscipio_solutions/consecro_mud/Abilities/Skills/*.java
%JAVACPATH% com/suscipio_solutions/consecro_mud/Abilities/Songs/*.java
%JAVACPATH% com/suscipio_solutions/consecro_mud/Abilities/Specializations/*.java
%JAVACPATH% com/suscipio_solutions/consecro_mud/Abilities/Spells/*.java
%JAVACPATH% com/suscipio_solutions/consecro_mud/Abilities/SuperPowers/*.java
%JAVACPATH% com/suscipio_solutions/consecro_mud/Abilities/Tech/*.java
%JAVACPATH% com/suscipio_solutions/consecro_mud/Abilities/Thief/*.java
%JAVACPATH% com/suscipio_solutions/consecro_mud/Abilities/Traps/*.java
%JAVACPATH% com/suscipio_solutions/consecro_mud/Areas/interfaces/*.java
%JAVACPATH% com/suscipio_solutions/consecro_mud/Behaviors/interfaces/*.java
%JAVACPATH% com/suscipio_solutions/consecro_mud/CharClasses/interfaces/*.java
%JAVACPATH% com/suscipio_solutions/consecro_mud/Commands/interfaces/*.java
%JAVACPATH% com/suscipio_solutions/consecro_mud/Common/interfaces/*.java
%JAVACPATH% com/suscipio_solutions/consecro_mud/Exits/interfaces/*.java
%JAVACPATH% com/suscipio_solutions/consecro_mud/Items/Armor/*.java
%JAVACPATH% com/suscipio_solutions/consecro_mud/Items/Basic/*.java
%JAVACPATH% com/suscipio_solutions/consecro_mud/Items/ClanItems/*.java
%JAVACPATH% com/suscipio_solutions/consecro_mud/Items/interfaces/*.java
%JAVACPATH% com/suscipio_solutions/consecro_mud/Items/MiscMagic/*.java
%JAVACPATH% com/suscipio_solutions/consecro_mud/Items/BasicTech/*.java
%JAVACPATH% com/suscipio_solutions/consecro_mud/Items/ShipTech/*.java
%JAVACPATH% com/suscipio_solutions/consecro_mud/Items/Software/*.java
%JAVACPATH% com/suscipio_solutions/consecro_mud/Items/Weapons/*.java
%JAVACPATH% com/suscipio_solutions/consecro_mud/Libraries/interfaces/*.java
%JAVACPATH% com/suscipio_solutions/consecro_mud/Libraries/layouts/*.java
%JAVACPATH% com/suscipio_solutions/consecro_mud/Locales/interfaces/*.java
%JAVACPATH% com/suscipio_solutions/consecro_mud/MOBS/interfaces/*.java
%JAVACPATH% com/suscipio_solutions/consecro_mud/Races/interfaces/*.java
%JAVACPATH% com/suscipio_solutions/consecro_mud/WebMacros/grinder/*.java
%JAVACPATH% com/suscipio_solutions/consecro_mud/WebMacros/interfaces/*.java
%JAVACPATH% com/suscipio_solutions/consecro_web/converters/*.java
%JAVACPATH% com/suscipio_solutions/consecro_web/http/*.java
%JAVACPATH% com/suscipio_solutions/consecro_web/interfaces/*.java
%JAVACPATH% com/suscipio_solutions/consecro_web/server/*.java
%JAVACPATH% com/suscipio_solutions/consecro_web/servlets/*.java
%JAVACPATH% com/suscipio_solutions/consecro_web/util/*.java
%JAVACPATH% com/suscipio_solutions/siplet/applet/*.java
%JAVACPATH% com/suscipio_solutions/siplet/support/*.java

pause

GOTO :FINISH

:DOCS

"%Java_Home%\bin\javadoc" -d .\docs -J-Xmx1024m -subpackages com.suscipio_solutions.consecro_mud 

:FINISH
