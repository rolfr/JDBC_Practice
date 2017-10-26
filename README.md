# JDBC_Practice
<h2>Practice SQL access via JDBC</h2>
<p>This is a fun little project I've started for the purpose of getting back into Java, particularly working with SQL access.</p>
<p>I've so far set up a personal MySQL server at home and an Azure SQL server, and the master branch is running against MySQL.  The current code populates 3 tables with a specified number of records: Person, Class, and StudentClass.  A name generator produces random names for persons and classes.  That was actually fun to write up; I may play with that a bit more later to see if I can get even more easily pronouncable names.</p>
<p>I have a private branch that is converted to use Azure SQL (commenting out MySQL-specific code), but I'd rather pick a good design pattern for allowing a clean switch between the two at runtime.  So that's the next thing I'll be checking in.</p>
