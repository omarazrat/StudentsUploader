/*
 *  StudentsUploader
 *  Cargador de datos de estudiantes a formularios
 *  Este programa fue diseñado para la Santa Iglesia Gnostica Universal, por Nestor Arias -nestor_arias@hotmail.com
 */
package oa.gnosis.selenium.interfaces;

import oa.variabilis.web.utils.RBHelper;
import org.openqa.selenium.WebDriver;

/**
 *
 * @author nesto
 */
@FunctionalInterface
public interface Action {
    /**
     * El nombre de esta acciòn.
     * @return 
     */
    public default  String getName(){
        return RBHelper.sgetString("plugin."+getClass().getSimpleName());
    }
    /**
     * Ejecuta una acción con el driver proporcionado.
     * @param driver
     */
    abstract void run(WebDriver driver);
}
