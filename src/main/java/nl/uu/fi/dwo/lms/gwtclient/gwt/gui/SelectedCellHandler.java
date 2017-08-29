package nl.uu.fi.dwo.lms.gwtclient.gwt.gui;

/**
 * SelectedCellHandler for onBrowserEvent of DwoClickCell's.
 * 
 * @author G.A.J. van der Plas
 */
public interface SelectedCellHandler {
//    public void addSelectedCellHandler(SelectedCellHandler aHandler);
    public void onSelectedCell(com.google.gwt.cell.client.Cell.Context context, String  value);
}
