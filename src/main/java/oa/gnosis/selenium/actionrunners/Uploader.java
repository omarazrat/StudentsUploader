/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package oa.gnosis.selenium.actionrunners;

import java.io.File;
import java.io.IOException;
import java.util.function.Consumer;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import oa.gnosis.selenium.RowConsumer;
import oa.gnosis.selenium.interfaces.Action;
import oa.variabilis.web.utils.poi.RowStream;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 *
 * @author nesto
 */
public class Uploader implements Action {

    private static Logger log;
    private static WebDriver driver;

    /**
     * Procesador de filas de hoja de cálculo Por cada fila toma los datos de
     * una persona y los registra en la página del curso.
     */
    private static final Consumer<Row> SeleniumProcessor = (row) -> {
        RowConsumer consumer = new RowConsumer(row, log);
        if (consumer.getFirstName().trim().isBlank()) {
            log.log(Level.INFO, "{0} WARN: sin nombre, asignando ''SIN NOMBRE''", consumer.getUserLogName());
            consumer.setFirstName("SIN");
            consumer.setLastName("NOMBRE");
        }
        driver.get(consumer.getUrl());
        Actions actionObject = new Actions(driver);
        Actions acts = actionObject.keyDown(Keys.SHIFT).sendKeys(Keys.F5).keyUp(Keys.SHIFT); //.perform??();
        acts.perform();
        final WebElement fnameF = new WebDriverWait(driver, 10000)
                .until(ExpectedConditions.elementToBeClickable(By.cssSelector("#nombre")));
        fnameF.sendKeys(consumer.getFirstName());
        final WebElement lnameF = driver.findElement(By.cssSelector("#apellido"));
        lnameF.sendKeys(consumer.getLastName());
        driver.findElement(By.cssSelector("div.iti__selected-flag")).click();
        try {
            driver.findElement(By.cssSelector("#form_inscripcion div li[data-dial-code='" + consumer.getCountryCode() + "'] > span.iti__country-name")).click();
        } catch (Exception e) {
            log.log(Level.INFO, "{0} WARN: cod pais no encontrado", consumer.getUserLogName());
        }
        final WebElement mobileF = driver.findElement(By.cssSelector("#celular"));
        mobileF.sendKeys(consumer.getMobile());
        final WebElement cityf = driver.findElement(By.cssSelector("#ciudad"));
        cityf.sendKeys(consumer.getCity());
        try {
            final WebElement emailF = driver.findElement(By.cssSelector("#email_correo"));
            if (emailF != null) {
                emailF.sendKeys(consumer.getEmail());
            }
            final WebElement registerBtn = driver.findElement(By.cssSelector("form button"));
            registerBtn.click();
            log.info(consumer.getUserLogName() + " OK");
        } catch (Exception nse) {
            log.severe(consumer.getUserLogName() + " ERROR");
        }
    };

    @Override
    public void run(WebDriver driver) {
        Uploader.driver = driver;
        log = Logger.getLogger(Uploader.class.getName());
        FileHandler fh = null;
        try {
            fh = new FileHandler(Uploader.class.getName() + ".log");
        } catch (IOException ex) {
            Logger.getLogger(Uploader.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            Logger.getLogger(Uploader.class.getName()).log(Level.SEVERE, null, ex);
        }
        fh.setFormatter(new SimpleFormatter());
        log.addHandler(fh);
        Workbook wb = null;
        try {
//            log.info("args.length:"+args.length+args[0]);
            File file = null;
            JFileChooser chooser = new JFileChooser();
            chooser.addChoosableFileFilter(new FileNameExtensionFilter("Libros de Excel", "xlsx", "xls"));
            chooser.setDialogTitle("Seleccione el archivo excel a cargar");
            if (chooser.showOpenDialog(null) != JFileChooser.APPROVE_OPTION) {
                return;
            }
            file = chooser.getSelectedFile();
            wb = WorkbookFactory.create(file);
        } catch (IOException ex) {
            Logger.getLogger(Uploader.class.getName()).log(Level.SEVERE, null, ex);
        } catch (EncryptedDocumentException ex) {
            Logger.getLogger(Uploader.class.getName()).log(Level.SEVERE, null, ex);
        }
        final Sheet sheet = wb.getSheetAt(0);
        RowStream rowst = new RowStream(sheet);
//        rowst.parallel().forEach(CURLProcessor);
        rowst.forEach(SeleniumProcessor);
    }

}
