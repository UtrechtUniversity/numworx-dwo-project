package nl.uu.fi.dwo.interaction.client;

public interface OpdrNavIF
{
	int OEFENEN = 0;
	int OEFENEN_STRAFPUNTEN = 1;
	int ZELFTOETS = 2;
	int EINDTOETS = 3;
	
	void setChanged(boolean fout);
	FormuleKeyboardIF getKeyboard();
	
	int getMode();
}
