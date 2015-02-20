package fi.dwo.dwojapplet.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Image;

import javax.swing.BorderFactory;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.JTableHeader;
import javax.swing.border.Border;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

/**
 * Utility class for JTables
 *
 * @author Wim
 *
 */
public class TableUtil {

    private TableUtil() {
    }

    static void setJTableSizes(JTable jTable) {
        TableColumn column;
        JTableHeader header = jTable.getTableHeader();
        int h = 0;
        for (int j = 0; j < jTable.getColumnCount(); j++) {
            TableCellRenderer renderer;
            int max = 0;
            if (header != null) {
                renderer = jTable.getColumnModel().getColumn(j).getHeaderRenderer();
                if (renderer == null) {
                    renderer = header.getDefaultRenderer();
                }
                Component c = renderer.getTableCellRendererComponent(jTable, jTable.getColumnModel().getColumn(j).getHeaderValue(), false, false, -1, j);
                max = c.getPreferredSize().width + 3;
				//h = Math.max(h, c.getPreferredSize().height);

            }
            int len = jTable.getRowCount();
            for (int i = 0; i < len; i++) {
                renderer = jTable.getCellRenderer(i, j);
                Component c = renderer.getTableCellRendererComponent(jTable,
                        jTable.getValueAt(i, j), false, false, i, j);
                Dimension preferredSize = c.getPreferredSize();
                max = Math.max(max, preferredSize.width);//+3;
                h = Math.max(h, preferredSize.height);
            }
            column = jTable.getColumnModel().getColumn(j);
            column.setPreferredWidth(max + 3);
        }
        jTable.setRowHeight(h + jTable.getRowMargin());
    }

    static void setDefaults(JTable jTable, boolean header, TableCellRenderer imageRenderer, TableCellEditor imageEditor) {
        if (!header) {
            jTable.setTableHeader(null);
        }
        jTable.setDefaultRenderer(Image.class, imageRenderer);
        jTable.setDefaultEditor(Image.class, imageEditor);
        jTable.setBackground(GuiConstants.CELL_BACKGROUND);
        jTable.setGridColor(Color.white);
        jTable.setRowMargin(8);
        jTable.getColumnModel().setColumnMargin(2);
        jTable.setBorder(null);
    }

    /**
     * @param table
     * @param tbl
     * @param width TODO
     * @param height TODO
     */
    static void shrinkToFit(JTable table, JScrollPane tbl, int width, int height) {
        Dimension pref;
        table.setSize(table.getPreferredSize());
        table.setPreferredScrollableViewportSize(table.getSize());
        tbl.validate();
        tbl.getViewport().setBackground(GuiConstants.MAIN_BACKGROUND);
        pref = tbl.getPreferredSize();

        tbl.setSize(width, height);

        int headerHeight = 0;
        if (table.getTableHeader() != null) {
            headerHeight = table.getTableHeader().getHeight();
        }
        pref.height = Math.min(pref.height, tbl.getHeight() + headerHeight);
        pref.width = Math.min(pref.width + 40, tbl.getWidth());

        tbl.setSize(pref);
        table.setPreferredScrollableViewportSize(tbl.getSize());
    }

    public final static Border tableBorder = BorderFactory.createLineBorder(Color.white, 3);

    static void setBorder(JScrollPane pane) {
        pane.setViewportBorder(null);
        pane.setBorder(tableBorder);
    }

    static void setBorder(JTable table) {
        table.setBorder(tableBorder);
    }

}
