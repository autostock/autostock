/**
 * Copyright (c) iCD GmbH & Co. KG, Frechen, Germany, 2013, http://www.icd.eu
 * Author: wrossner
 */
package eu.icd.p999.gen1;

import eu.icd.util.application.AppOption;
import eu.icd.util.application.AppStarter;

/**
 * @author wrossner
 */
public class Gen1 {
    //TODO Auto-generated field
    int templateAnzahl = 1;

    //TODO Auto-generated field
    boolean templateIndent = false;

    //TODO Auto-generated field
    String templateText = "";

    public static void main(final String[] args) {
        AppStarter.runAndExit(Gen1.class, args);
    }

    public Gen1() {
        super();
    }

    public void execute() {
        //TODO Auto-generated method stub
        for (int i = 0; i < templateAnzahl; i++) {
            if (templateIndent) {
                System.out.print("    ");
            }
            System.out.println(templateText);
        }
    }

    // TODO Auto-generated method
    @AppOption(description = "wie oft", shortOpt = "N", argName = "anzahl")
    public void setTemplateAnzahl(final int anzahl) {
        this.templateAnzahl = anzahl;
    }

    // TODO Auto-generated method
    @AppOption(description = "soll der Text eingerückt werden?", shortOpt = "I")
    public void setTemplateIndent(final boolean indent) {
        this.templateIndent = indent;
    }

    // TODO Auto-generated method
    @AppOption(description = "der auszudruckende Text", shortOpt = "T", argName = "text", required = true)
    public void setTemplateText(final String text) {
        this.templateText = text;
    }
}
