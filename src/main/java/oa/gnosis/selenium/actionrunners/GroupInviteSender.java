/*
 *  StudentsUploader
 *  Cargador de datos de estudiantes a formularios
 *  Este programa fue diseñado para la Santa Iglesia Gnostica Universal, por Nestor Arias -nestor_arias@hotmail.com
 */
package oa.gnosis.selenium.actionrunners;

import java.util.List;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static java.util.stream.Collectors.toList;
import javax.swing.JOptionPane;
import oa.gnosis.selenium.interfaces.Action;
import oa.variabilis.web.utils.RBHelper;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 *
 * @author nesto
 */
public class GroupInviteSender implements Action{
    private RBHelper props = new RBHelper("plugin.GroupInviteSender.");
    private WebDriver driver;
    private final static int DEF_TIMEOUT=5;
    
    @Override
    public void run(WebDriver driver) {
        this.driver= driver;
        String url ="";
        //Pide dirección origen
        do{
            url = JOptionPane.showInputDialog(props.getString("enterUrl"));
        }while(!valid(url));
        //Transforma la dir. para bajar todos los telefonos
        String parsedUrl = changeToAllUrl(url);
        driver.get(parsedUrl);
        String mainTab = driver.getWindowHandle();
        String WAtab = openWhatsAppTab(parsedUrl);
        driver.switchTo().window(mainTab);
        //#table > tbody > tr:nth-child(1) > td:nth-child(6) > input[type=text]
        final List<WebElement> textFields = driver.findElements(By.cssSelector(
                "#table > tbody > tr:nth-child(1) > td:nth-child(6) > input[type=text]"));
        List<String> numbers = textFields.stream().map(WebElement::getText).collect(toList());
        driver.switchTo().window(WAtab);
        for (String number :numbers){
            waitNWrite("#side > div:nth-child(1) > div > label > div > div:nth-child(1)",
                    number,driver);
            
        }
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
        //  https://ac.gnosis.is/f/3309/course-of-self-knowledge-on-line-congo
        //  https://ac.gnosis.is/f/ipaginar/3309/course-of-self-knowledge-on-line-congo/all
        Pattern pattern = Pattern.compile(props.getString("urlRegex"));
        Matcher matcher = pattern.matcher(url);
        int idx = 0;
        final String gcode = matcher.group(idx++);
        return url
                .replaceAll(gcode, "/ipaginar/"+gcode)
                +"/all";
    }
    
    private String openWhatsAppTab(String link) {
        String js = "window.open('"+link+"','_wa');";  // replace link with your desired link
        ((JavascriptExecutor)driver).executeScript(js);
        wait(By.tagName("body"), driver);
        final String title = ResourceBundle.getBundle("global").getString("Whatsapp");
        JOptionPane.showMessageDialog(null, props.getString("enterWA"), title, JOptionPane.INFORMATION_MESSAGE);
        return driver.getWindowHandle();
    }

    private static void wait(String selector, WebDriver driver) {
        wait(By.cssSelector(selector), driver);
    }

    private static void wait(int timeout, final By elemSelector, final ExpectedCondition<WebElement> condition, WebDriver driver) {
        new WebDriverWait(driver, timeout)
                .until(condition);
    }

    private static void wait(int timeout, final By elemSelector, WebDriver driver) {
        final ExpectedCondition<WebElement> condition = ExpectedConditions.elementToBeClickable(elemSelector);
        wait(timeout, elemSelector, condition, driver);
    }

    private static void wait(final By elemSelector, WebDriver driver) {
        wait(DEF_TIMEOUT, elemSelector, driver);
    }

    private static void wait(final By elemSelector, final ExpectedCondition<WebElement> condition, WebDriver driver) {
        wait(DEF_TIMEOUT, elemSelector, condition, driver);
    }
    
    private static void waitNWrite(final String selector,String text,WebDriver driver){
        final WebElement element = driver.findElement(By.cssSelector(selector));
        while (true) {
            try {
                write(selector, text,driver);
                element.click();
                break;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }        
    }
    /**
     * Escribe texto en un compontente dado su selector css
     *
     * @param selector
     * @param text
     * @param driver
     */
    private static void write(String selector, String text, WebDriver driver) {
        final By elemSelector = By.cssSelector(selector);
        final WebElement element = driver.findElement(elemSelector);
        element.sendKeys(text);
    }
    private static void waitNClick(final String selector, WebDriver driver) {
        final By elemSelector = By.cssSelector(selector);
        waitNClick(elemSelector, driver);
    }

    private static void waitNClick(final By elemSelector, WebDriver driver) {
        final WebElement element = driver.findElement(elemSelector);
        while (true) {
            try {
                wait(elemSelector, driver);
                element.click();
                break;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static void click(final String selector, WebDriver driver) {
        click(By.cssSelector(selector), driver);
    }

    private static void click(final By selector, WebDriver driver) {
        final WebElement element = driver.findElement(selector);
        element.click();
    }

    /**
     * Doble-click en eun elemento dado su selector css
     *
     * @param selector
     * @param driver
     */
    private static void doubleclick(String selector, WebDriver driver) {
        final By elemSelector = By.cssSelector(selector);
        final WebElement element = driver.findElement(elemSelector);
        Actions actions = new Actions(driver);
        actions.doubleClick(element);
    }

}