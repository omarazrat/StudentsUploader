/*
 *  StudentsUploader
 *  Cargador de datos de estudiantes a formularios
 *  Este programa fue diseñado para la Santa Iglesia Gnostica Universal, por Nestor Arias -nestor_arias@hotmail.com
 */
package oa.gnosis.selenium.actionrunners;

import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import static java.util.stream.Collectors.toList;
import javax.swing.JOptionPane;
import oa.gnosis.selenium.interfaces.Action;
import oa.variabilis.web.utils.RBHelper;
import oa.variabilis.web.utils.SysPropertiesHelper;
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
public class GroupInviteSender implements Action {

    private RBHelper props = new RBHelper("plugin.GroupInviteSender.");
    private WebDriver driver;
    private final static int DEF_TIMEOUT = 5;

    @Override
    public void run(WebDriver driver) {
        this.driver = driver;
        String url = "";
        final String RET_CARRIAGE=Pattern.quote("\n"),
                PIPE=Pattern.quote("|");
        //Pide dirección origen
        do {
            url = JOptionPane.showInputDialog(props.getString("enterUrl"))
                    .replace("/f/i", "/f");
        } while (!valid(url));
        //Transforma la dir. para bajar todos los telefonos
        String parsedUrl = changeToAllUrl(url);
        driver.get(parsedUrl);
        String mainTab = driver.getWindowHandle();
//        driver.switchTo().window(mainTab);
        //#table > tbody > tr:nth-child(1) > td:nth-child(6) > input[type=text]
        //Numero, Nombre
        final Map<String, String> contacts = driver.findElements(By.cssSelector(
                "#table > tbody > tr"))
                .parallelStream()
//                .map(i -> {
//                    System.out.println("recorriendo fila: " + i.getText() + "/");
//                    return i;
//                })
                .collect(Collectors.toMap(row -> {
                    String href = row.findElement(By.cssSelector("td:nth-child(6) > a")).getAttribute("href");
                    final int idx = href.indexOf("=");
                    return href.substring(idx + 1);
                    },
                    row -> {
                        String line = row.getText().split(RET_CARRIAGE)[4];
                        String []opts= line.split(PIPE);
                        //Viene vacìo?
                        return opts.length>0?opts[0]:"";
                    },
                    (num1, num2) -> {
                       System.err.println("Conflicto entre \"" + num1 + "\" y \"" + num2 + "\"");
                       return num1;
                }));
        openWhatsAppTab();
//        driver.switchTo().window(WAtab);
        String message = SysPropertiesHelper.getProp("plugin.GroupInviteSender.message");
        for (String number : contacts.keySet()) {
            if(number.isBlank()){
                continue;
            }
            String name = contacts.get(number);
            String contactMsg = message.replace("{STUDENT}", name);
            click("span[data-icon='search']", driver);
            pause(0.3f);
            write(number, driver);
            pause(0.3f);
            waitNClick("#pane-side > div:nth-child(1) > div > div > div:nth-child(2) > div", driver);
            //Escribe mensaje
            //         #main > footer > div._3ee1T._1LkpH.copyable-area > div._3uMse > div > div._3FRCZ.copyable-text.selectable-text
            waitNWrite("#main > footer > div:nth-child(1) > div:nth-child(2) > div > div:nth-child(2)",
                     contactMsg, driver);
            boolean send=false;
            if(send){
                waitNClick("#main > footer > div:nth-child(1) > div:nth-child(3) > button", driver);
            }
        }
        /**
         * {origen}
         * https://ac.gnosis.is/f/3309/course-of-self-knowledge-on-line-congo
         * {listado de todos}
         * https://ac.gnosis.is/f/ipaginar/3309/course-of-self-knowledge-on-line-congo/all
         * Caja de búsqueda en WA: #side > div:nth-child(1) > div > label > div
         * > div:nth-child(1) Primera coincidencia de búsqueda: #pane-side >
         * div:nth-child(1) > div > div > div:nth-child(2) > div Caja para
         * escribir: #main > footer > div._3ee1T._1LkpH.copyable-area >
         * div._3uMse > div > div._3FRCZ.copyable-text.selectable-text
         */

    }

    /**
     * Verifica si es una URL válida de acuerdo con el modelo. Si falla, muestra
     * mensaje al usuario.
     */
    private boolean valid(String url) {
        Pattern pattern = Pattern.compile(props.getString("urlRegex"));
        Matcher matcher = pattern.matcher(url);
        if (!matcher.matches()) {
            String message = props.getString("invalidUrl")
                    .replace("{0}", props.getString("sampleUrl"));
            final String errTitle = RBHelper.sgetString("general.error");
            JOptionPane.showMessageDialog(null, message, errTitle, JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    /**
     * Cambia una url validada con {@link #valid(java.lang.String) }
     *
     * @param url
     * @return
     */
    private String changeToAllUrl(String url) {
        //  https://ac.gnosis.is/f/3309/course-of-self-knowledge-on-line-congo
        //  https://ac.gnosis.is/f/ipaginar/3309/course-of-self-knowledge-on-line-congo/all
        Pattern pattern = Pattern.compile(props.getString("urlRegex"));
        Matcher matcher = pattern.matcher(url);
        int idx = 1;
        if (!matcher.matches()) {
            return null;
        }
        final String gcode = matcher.group(idx++);
        return url
                .replaceAll(gcode, "ipaginar/" + gcode)
                + "/all";
    }

    private String openWhatsAppTab() {
        String link = RBHelper.sgetString("whatsapp.url");
//        String js = "window.open('" + link + "','_wa');";  // replace link with your desired link
//        ((JavascriptExecutor) driver).executeScript(js);
        driver.get(link);
        wait(By.tagName("body"), driver);
        final String title = RBHelper.sgetString("whatsapp");
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

    private static void waitNWrite(final String selector, String text, WebDriver driver) {
        final WebElement element = driver.findElement(By.cssSelector(selector));
        while (true) {
            try {
                write(selector, text, driver);
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
    /**
     * Escribe texto en el elemento que esté activo en el navegador, no importa cual sea.
     * @param text
     * @param driver 
     */
    private static void write(String text,WebDriver driver){
        Actions action = new Actions(driver);
        action.sendKeys(text).build().perform();
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

    private void pause(float time) {
        try {
            Thread.sleep((long) (time*1000));
        } catch (InterruptedException ex) {
            Logger.getLogger(GroupInviteSender.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
