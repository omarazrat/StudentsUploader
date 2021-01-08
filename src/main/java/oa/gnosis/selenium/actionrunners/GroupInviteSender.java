/*
 *  StudentsUploader
 *  Cargador de datos de estudiantes a formularios
 *  Este programa fue diseñado para la Santa Iglesia Gnostica Universal, por Nestor Arias -nestor_arias@hotmail.com
 */
package oa.gnosis.selenium.actionrunners;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Panel;
import java.awt.Window;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.StringWriter;
import java.util.Map;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.logging.StreamHandler;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.Timer;
import oa.gnosis.selenium.interfaces.Action;
import oa.variabilis.web.utils.MessageConsole;
import oa.variabilis.web.utils.RBHelper;
import oa.variabilis.web.utils.SysPropertiesHelper;
import org.apache.commons.io.output.WriterOutputStream;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.apache.commons.text.StringEscapeUtils;

/**
 *
 * @author nesto
 */
public class GroupInviteSender implements Action {

    //Número de veces a buscar antes de considerar que es un error.
    private static final int REATTEMPT_FIND = 4;
    private RBHelper props = new RBHelper("plugin.GroupInviteSender.");
    private WebDriver driver;
    private final static int DEF_TIMEOUT = 5;
    private final Logger log = Logger.getLogger(GroupInviteSender.class.getName());

    @Override
    public void run(WebDriver driver) {
        final String CONFIG_OPENCHAT = SysPropertiesHelper.getProp("plugin.GroupInviteSender.selOpenChat");
        final String CONFIG_SENDBTNCSSSELECTOR = SysPropertiesHelper.getProp("plugin.GroupInviteSender.selBtnSend");
        final String CONFIG_OPENWHATSAPPWEB = SysPropertiesHelper.getProp("plugin.GroupInviteSender.selOpenWaWeb");
        final boolean real_exec = !Boolean.valueOf(SysPropertiesHelper.getProp("plugin.testMode"));
        log.addHandler(new ConsoleHandler());
        this.driver = driver;
        String url = "";
        final String RET_CARRIAGE = Pattern.quote("\n"),
                PIPE = Pattern.quote("|");
        //Pide dirección origen
        do {
            url = JOptionPane.showInputDialog(props.getString("enterUrl"))
                    .replace("/f/i", "/f");
        } while (!valid(url));
        final String titleProcessingData = props.getString("processingData.title");
        //Le muestra al usuario la ventana de seguimiento de la construcción de datos
        Thread t = new Thread(
                new Runnable() {
            @Override
            public void run() {
                buildLogWnd(titleProcessingData);
            }
        }
        );
//        Window logFrame = buildLogWnd(titleProcessingData);
        t.start();
        //Transforma la dir. para bajar todos los telefonos
        String parsedUrl = changeToAllUrl(url);
        driver.get(parsedUrl);
        final Map<String, String> contacts = driver.findElements(By.cssSelector(
                "#table > tbody > tr"))
                .parallelStream()
                //                })
                .collect(Collectors.toMap(row -> {
                    String href = row.findElement(By.cssSelector("td:nth-child(6) > a")).getAttribute("href");
                    final int idx = href.indexOf("=");
                    return href.substring(idx + 1);
                },
                        row -> {
                            String line = row.getText().split(RET_CARRIAGE)[4];
                            String[] opts = line.split(PIPE);
                            //Viene vacìo?
                            return opts.length > 0 ? opts[0] : "";
                        },
                        (num1, num2) -> {
                            log.warning("Conflicto entre \"" + num1 + "\" y \"" + num2 + "\"");
                            return !num1.isBlank() ? num1 : num2;
                        }));

        logWnd = false;
        openWhatsAppTab();
        String message = SysPropertiesHelper.getProp("plugin.GroupInviteSender.message");
        for (String number : contacts.keySet()) {
            if (number.isBlank()) {
                continue;
            }
            String name = contacts.get(number);
            String contactMsg = message.replace("{STUDENT}", name);
            String MS_URL = "https://api.whatsapp.com/send?phone=" + number + "&text=" 
                    + StringEscapeUtils.escapeHtml4(contactMsg
                            .replace(System.getProperty("line.separator"), "%0a")
                            .replace("\n","%0a"));
    
            boolean _continue = true;
            while (_continue) {
                driver.get(MS_URL);
                final By btnSelector = By.cssSelector(CONFIG_SENDBTNCSSSELECTOR);
                final By btnGoChat = By.cssSelector(CONFIG_OPENCHAT);
                final By btnOpenWhatsAppWeb = By.cssSelector(CONFIG_OPENWHATSAPPWEB);
                try {
                    waitNClick(btnGoChat, driver);
                    wait(btnOpenWhatsAppWeb, driver);
                    if (driver.findElements(btnOpenWhatsAppWeb).isEmpty()) {
                        //No pasó el botón.
                        throw new Exception("Button go to WhatsApp had no effect!");
                    }
                    click(btnOpenWhatsAppWeb, driver);
                    wait(btnSelector, driver);
                    if (real_exec) {
                        click(btnSelector, driver);
                    } else {
                        final String testMsg = props.getString("testMode.message");
                        if (JOptionPane.showConfirmDialog(null, testMsg, "", JOptionPane.OK_CANCEL_OPTION)
                                == JOptionPane.CANCEL_OPTION) {
                            return;
            
                        }
                    }
                    _continue = false;
                } catch (/*Timeout*/Exception te) {
                    te.printStackTrace();
                    final String WarnAllowWAOpen = props.getString("allowWaAlwaysOpen");
                    //No aprobó abrir whatsapp URL.
                    _continue = JOptionPane.showConfirmDialog(null, WarnAllowWAOpen, "", JOptionPane.YES_NO_OPTION)
                            == JOptionPane.YES_OPTION;
                    if (!_continue) {
                        return;
                    }
                }
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
                    for (int i = 0; i < REATTEMPT_FIND; i++) {
                        try {
                            wait(DEF_TIMEOUT, elemSelector, driver);
                            pause(0.3f);
                        } catch (Exception e) {;
                        }
                        if (!driver.findElements(elemSelector).isEmpty()) {
                            break;
                        }
                    }
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
     * Escribe texto en el elemento que esté activo en el navegador, no importa
     * cual sea.
     *
     * @param text
     * @param driver
     */
    private static void write(String text, WebDriver driver) {
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

    
    /**
     * Pausa la aplicación una cantidad dada de tiempo en segundos.
     *
     * @param seconds
     */
    private static void pause(float seconds) {
        try {
            Thread.sleep((long) (seconds * 1000));
        } catch (InterruptedException ex) {
            Logger.getLogger(GroupInviteSender.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    volatile boolean logWnd;

    /**
     * Crea una ventana con cierto tìtulo que muestre la salida del log
     *
     * @param title
     * @return
     */
    private Window buildLogWnd(String title) {
        logWnd = true;
        //componentes
        JTextArea logTracer = new JTextArea(10, 110);
        JScrollPane logTracerSP = new JScrollPane(logTracer);
        logTracer.setMinimumSize(new Dimension(200, 50));
        logTracer.setPreferredSize(logTracer.getMinimumSize());
        final StringWriter stringWriter = new StringWriter();
        final WriterOutputStream writerOutputStream = new WriterOutputStream(stringWriter);
        //Seguimiento a swing
        MessageConsole mc = new MessageConsole(logTracer);
        mc.redirectOut();
        mc.setMessageLines(100);
        //seguimiento a flujo
        log.addHandler(new StreamHandler(writerOutputStream, new SimpleFormatter()));
        JPanel panel = new JPanel(new GridBagLayout());
        logTracer.setEditable(false);
//        panel.setLayout(new GridBagLayout());
        final GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy=gbc.gridx=0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        final JLabel label = new JLabel(props.getString("processingdata"));
        label.setMinimumSize(new Dimension(200,20));
        label.setPreferredSize(label.getMinimumSize());
        panel.add(label, gbc);
    
        gbc.gridy++;
        gbc.gridheight = 10;
        panel.add(logTracerSP, gbc);
        JFrame frame = new JFrame(title);
        frame.add(panel);
        frame.pack();
        frame.setVisible(true);
        Timer t = new Timer(100, (e) -> {
            panel.validate();
            panel.repaint();
            if (!logWnd) {
                frame.setVisible(false);
            }
        });
        t.start();
        return frame;
    }

}
