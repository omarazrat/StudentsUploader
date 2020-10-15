/*
 *  StudentsUploader
 *  Cargador de datos de estudiantes a formularios
 *  Este programa fue diseñado para la Santa Iglesia Gnostica Universal, por Nestor Arias -nestor_arias@hotmail.com
 */
package oa.gnosis.selenium.actionrunners;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JOptionPane;
import oa.gnosis.selenium.interfaces.Action;
import oa.variabilis.web.utils.RBHelper;
import org.openqa.selenium.WebDriver;

/**
 *
 * @author nesto
 */
public class GroupInviteSender implements Action{
    private RBHelper props = new RBHelper("plugin.GroupInviteSender.");
    
    @Override
    public void run(WebDriver driver) {
        String url ="";
        do{
            url = JOptionPane.showInputDialog(props.getString("enterUrl"));
        }while(!valid(url));
        String parsedUrl = changeToAllUrl(url);
        /**
        {origen}
        https://ac.gnosis.is/f/3309/course-of-self-knowledge-on-line-congo
        {listado de todos}
        https://ac.gnosis.is/f/ipaginar/3309/course-of-self-knowledge-on-line-congo/all
        Caja de búsqueda en WA:
        #side > div:nth-child(1) > div > label > div > div:nth-child(1)
        Primera coincidencia de búsqueda:
        #pane-side > div:nth-child(1) > div > div > div:nth-child(2) > div
        Caja para escribir:
        #main > footer > div._3ee1T._1LkpH.copyable-area > div._3uMse > div > div._3FRCZ.copyable-text.selectable-text
         */

    }
    
    /**
     * Verifica si es una URL válida de acuerdo con el modelo.
     * Si falla, muestra mensaje al usuario.
     */
    private boolean valid(String url) {
        Pattern pattern = Pattern.compile(props.getString("urlRegex"));
        Matcher matcher = pattern.matcher(url);
        if (!matcher.matches()){
            String message = props.getString("invalidUrl")
                    .replace("{0}", props.getString("sampleUrl"));
            final String errTitle = RBHelper.sgetString("general.error");
            JOptionPane.showMessageDialog(null, message, errTitle,JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }
    /**
     * Cambia una url validada con {@link #valid(java.lang.String) }
     * @param url
     * @return 
     */
    private String changeToAllUrl(String url) {
        //        https://ac.gnosis.is/f/ipaginar/3309/course-of-self-knowledge-on-line-congo/all
        return null;
    }
    
}
