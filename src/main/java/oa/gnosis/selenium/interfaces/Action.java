/*
 *  StudentsUploader
 *  Cargador de datos de estudiantes a formularios
 *  Este programa fue dise�ado para la Santa Iglesia Gnostica Universal, por Nestor Arias -nestor_arias@hotmail.com
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
     * El nombre de esta acci�n.
     * @return 
     */
    public default  String getName(){
        return RBHelper.sgetString("plugin."+getClass().getSimpleName());
    }
    /**
     * Ejecuta una acci�n con el driver proporcionado.
     * @param driver
     */
    abstract void run(WebDriver driver);
}
