/*
 *  StudentsUploader
 *  Cargador de datos de estudiantes a formularios
 *  Este programa fue diseñado para la Santa Iglesia Gnostica Universal, por Nestor Arias -nestor_arias@hotmail.com
 */
package oa.variabilis.web.utils;

import java.util.prefs.Preferences;

/**
 *
 * @author nesto
 */
public abstract class SysPropertiesHelper {

    public static void setProp(String key, String value) {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                Preferences.userRoot().put(key, value);
            }
        });
        t.start();
    }

    public static String getProp(String key) {
        return getProp(key);
    }

    public static String getProp(String key, String defValue) {
        return Preferences.userRoot().get(key, defValue);
    }

}
