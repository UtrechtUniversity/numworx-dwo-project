/*
 * Created on Mar 17, 2005
 *
 */
package fi.beans.tooltip;

/**
 * The thread for showing and hiding the tooltip.
 * @author M.J.B. Kupers
 *  
 */
public class ToolTipThread extends Thread {

    private ToolTipManager toolTipManager;

    private int msShow, msHide;

    /**
     * Creates a new ToolTipThread. The thread is started immediately.
     * After the specified msShow a callback to the ToolTipManager is made, to show the tooltip.
     * After another msHide a callback to the ToolTipManager is made, to hide the tooltip.
     * @param toolTipManager The toolTipManager to call back.
     * @param msShow The number of milliseconds to show the tooltip.
     * @param msHide The number of milliseconds to hide the tooltip.
     */
    public ToolTipThread(ToolTipManager toolTipManager, int msShow, int msHide) {
        super();
        this.toolTipManager = toolTipManager;
        this.msShow = msShow;
        this.msHide = msHide;
        this.start();

    }

    /**
     * Creates a new ToolTipThread with the default msShow(700 ms) and msHide(3000 ms). The thread is started immediately.
     * After the specified msShow a callback to the ToolTipManager is made, to show the tooltip.
     * After another msHide a callback to the ToolTipManager is made, to hide the tooltip.
     * @param toolTipManager The toolTipManager to call back.
     */
    public ToolTipThread(ToolTipManager toolTipManager) {
        this(toolTipManager, 700, 3000);
    }

    /**
     * Runs the ToolTipThread.
     * Sleeps the specified ms before showing the tooltip. 
     * Then sleeps some ms before hiding the tooltip. 
     */
    public void run() {
        try {
            Thread.sleep(msShow);
        } catch (Exception ex) {
        }

        toolTipManager.showToolTip();

        if (msHide > 0) {
            try {
                Thread.sleep(msHide);
            } catch (Exception ex) {
            }
            toolTipManager.hideToolTip();
        }
    }

}