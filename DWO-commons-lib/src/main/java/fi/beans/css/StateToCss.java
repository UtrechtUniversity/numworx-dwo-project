package fi.beans.css;

import java.awt.Color;
import java.util.Map;

import fi.beans.private_base64code.StringCodeObject;

public class StateToCss {

  // /**
  // * @deprecated Use {@link #createCssFromAllStyles(Map)} instead
  // */
  // public static String createCssFromAllStyles()
  // {
  // return createCssFromAllStyles(TekstVakPanel.styles);
  // }

  /**
   * Launchdata, zoals in de WiskOpdr/DWOJClient als ook als JSONObject in DWOplayer/DWOServer.
   * 
   * @param launchData Hashtable of JSONObject
   * @param cl classloader or null
   * @return
   */
  @SuppressWarnings({"unchecked"})
  public static String createCssFromInstellingen(Map<String, Object> launchData, ClassLoader cl) {
    Object instellingen = launchData.get("instellingen");
    Map<String, Object> map;
    if (instellingen instanceof String)
      map = (Map<String, Object>) StringCodeObject.decodeStringToObject((String) instellingen, cl);
    else if (instellingen instanceof Map)
      map = (Map<String, Object>) instellingen;
    else
      return "";
    Object styles = map.get("TekstVakPanelStyles");
    if (styles instanceof Map)
      return createCssFromAllStyles((Map<String, Map<String, Object>>) styles);
    return "";
  }


  public static String createCssFromAllStyles(Map<String, Map<String, Object>> styles) {
    if (styles == null) return "";

    String cssString = "@namespace svg url(http://www.w3.org/2000/svg);\n"
    		+ "svg|text {\n\tstroke: none;\n}\n\nsvg|rect {\n\tstroke:none;\n }\n\n";
    for (String key : styles.keySet())
      cssString += createCssFromStyle(key, styles.get(key));
    return cssString;
  }

  public static String createCssFromStyle(String styleString, Map<String, Object> style) {
    String cssOutput = "";

    String cssMain = "." + styleString + "-main {\n";
    int a = 0;
    int b = 0;
    int c = 0;
    if (style.containsKey("cellSpaceColumn"))
      a = ((Number) style.get("cellSpaceColumn")).intValue();
    if (style.containsKey("cellSpaceRow")) b = ((Number) style.get("cellSpaceRow")).intValue();
    if (style.containsKey("randDikte")) c = ((Number) style.get("randDikte")).intValue();
    cssMain += "\tborder-spacing:" + a + "px " + b + "px;\n";
    cssMain += "\tmargin:" + (-(b + c)) + "px " + (-(a + c)) + "px;\n";
    cssMain += "}\n\n";
    cssOutput += cssMain;

    String cssMain2 = "." + styleString + "-main2 {\n";

    a = 0;
    if (style.containsKey("bgColorZichtbaar")
        && ((Boolean) style.get("bgColorZichtbaar")).booleanValue())
      a = 1;

    int r = 255;
    int g = 255;
    b = 255;
    if (style.containsKey("bgColor_red")) {
      r = ((Number) style.get("bgColor_red")).intValue();
      if (style.containsKey("bgColor_green")) g = ((Number) style.get("bgColor_green")).intValue();
      if (style.containsKey("bgColor_blue")) b = ((Number) style.get("bgColor_blue")).intValue();
    } else if (style.containsKey("bgColor")) {
      Object object = style.get("bgColor");
      if (object instanceof Color) {
        Color bgColor = (Color) object;
        r = bgColor.getRed();
        g = bgColor.getGreen();
        b = bgColor.getBlue();
      } else if (object instanceof Map) {
        Map<?, ?> bgColor = (Map<?, ?>) object;
        r = ((Number) bgColor.get("red")).intValue();
        g = ((Number) bgColor.get("green")).intValue();
        b = ((Number) bgColor.get("blue")).intValue();
      }
    }
    cssMain2 += "\tbackground-color: rgba(" + r + "," + g + "," + b + "," + a + ");\n";

    a = 0;
    if (style.containsKey("randZichtbaar") && ((Boolean) style.get("randZichtbaar")).booleanValue())
      a = 1;
    if (style.containsKey("randColor_red")) {
      r = ((Number) style.get("randColor_red")).intValue();
      if (style.containsKey("randColor_green"))
        g = ((Number) style.get("randColor_green")).intValue();
      if (style.containsKey("randColor_blue"))
        b = ((Number) style.get("randColor_blue")).intValue();
    } else if (style.containsKey("randColor")) {
      Object object = style.get("randColor");
      if (object instanceof Color) {
        Color randColor = (Color) object;
        r = randColor.getRed();
        g = randColor.getGreen();
        b = randColor.getBlue();
      } else if (object instanceof Map) {
        Map<?, ?> randColor = (Map<?, ?>) object;
        r = ((Number) randColor.get("red")).intValue();
        g = ((Number) randColor.get("green")).intValue();
        b = ((Number) randColor.get("blue")).intValue();
      }
    }
    String randColor = "rgba(" + r + "," + g + "," + b + ",";
    cssMain2 += "\tborder-color:" + randColor + a + ");\n";

    if (style.containsKey("randDikte")) {
      a = ((Number) style.get("randDikte")).intValue();
      cssMain2 += "\tborder-width: " + a + "px;\n";
    }
    cssMain2 += "\tborder-style:solid;\n";

    if (style.containsKey("ronding")) {
      a = ((Number) style.get("ronding")).intValue();
      double d = a / 2;
      cssMain2 += "\tborder-radius:" + d + "px;\n";
    }

    if (style.containsKey("hoek")) {
      a = ((Number) style.get("hoek")).intValue();
      cssMain2 += "\ttransform:rotate(" + a + "deg);\n";
      cssMain2 += "\t-webkit-transform:rotate(" + a + "deg);\n";
    }
    cssMain2 += "}\n\n";
    cssOutput += cssMain2;

    String cssBorder = "." + styleString + "-border {\n";
    cssBorder += "\tborder-style: solid;\n";
    a = 0;
    if (style.containsKey("tableBorders") && ((Boolean) style.get("tableBorders")).booleanValue())
      a = 1;
    cssBorder += "\tborder-color:" + randColor + a + ");\n";
    cssBorder += "}\n\n";
    cssOutput += cssBorder;

    // Possibly replace horizontalBorders and verticalBorders by borders of the grid itself.
    // For now, these borders do not end up in exactly the same positions, so I leave it for later.
    /*
     * String cssMainCell = "." + styleString + "-main-cell {\n";
     * 
     * cssMainCell += "border-collapse:collapse;\n";
     * 
     * if(style.containsKey("tableBorders") && style.getBoolean("tableBorders")) { cssMainCell +=
     * "border-style:solid;\n"; cssMainCell += "border-color:" + randColor + ";\n"; cssMainCell +=
     * "border-width: 1px;\n"; } else { cssMainCell += "border-style:none;\n"; } cssMainCell +=
     * "}\n";
     * 
     * System.out.println(cssMainCell);
     */

    // String cssTekstVak = "." + styleString + "-tekstvak {\n";
    // cssTekstVak += "}\n";
    // System.out.println(cssTekstVak);
    //
    String cssTekstRegel = "." + styleString + "-tekstregel svg {\n";
    r = 0;
    g = 0;
    b = 0;
    if (style.containsKey("fgColor_red")) {
      r = ((Number) style.get("fgColor_red")).intValue();
      if (style.containsKey("fgColor_green")) g = ((Number) style.get("fgColor_green")).intValue();
      if (style.containsKey("fgColor_blue")) b = ((Number) style.get("fgColor_blue")).intValue();
    }
    if (style.containsKey("anderFont") && ((Boolean) style.get("anderFont")).booleanValue()
        && style.containsKey("fgColor")) {
      Object object = style.get("fgColor");
      if (object instanceof Color) {
        Color fgColor = (Color) object;
        r = fgColor.getRed();
        g = fgColor.getGreen();
        b = fgColor.getBlue();
      } else if (object instanceof Map) {
        Map<?, ?> fgColor = (Map<?, ?>) object;
        r = ((Number) fgColor.get("red")).intValue();
        g = ((Number) fgColor.get("green")).intValue();
        b = ((Number) fgColor.get("blue")).intValue();
      }
    }
    cssTekstRegel += "\tfill: rgb(" + r + "," + g + "," + b + ");\n";
    cssTekstRegel += "\tstroke: rgb(" + r + "," + g + "," + b + ");\n";

    cssTekstRegel += "}\n";
    cssOutput += cssTekstRegel;

    return cssOutput;
  }
}
