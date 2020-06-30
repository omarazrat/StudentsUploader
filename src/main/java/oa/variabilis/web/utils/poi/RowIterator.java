/*
 *  Variabilis - Un software estadístico para aplicación de la batería de riesgo psicosocial en Colombia.
 *  El presente software se provee bajo una licencia comercial y está cubierto por derechos de autor.
 *  No se autoriza su uso directo o indirecto , descompilación o uso del código fuente sin el consentimiento expreso del autor de la obra.
 *  Nestor Arias-2015
 */
package oa.variabilis.web.utils.poi;

import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

/**
 *
 * @author Nestor Arias <nestor_arias@hotmail.com>
 */
public class RowIterator implements Iterator<Row> {

    /**
     * La hoja de cálculo sobre la cual se itera.
     */
    private final Sheet sheet;
    private int index;
    static final Logger log = Logger.getLogger(RowIterator.class.getSimpleName());

    public RowIterator(Sheet sheet) {
        this.sheet = sheet;
        index = 0;
    }

    @Override
    public boolean hasNext() {
//        log.info("sheet.getLastRowNum():"+sheet.getLastRowNum());
        if (sheet.getLastRowNum() < index) {
            return false;
        }
//        log.info("index:"+index);
        final Row row = sheet.getRow(index);
        final Cell cell = row.getCell(0);
//        log.info(cell+" == null || "+ (cell==null?"":cell.getCellTypeEnum())+".equals("+CellType.BLANK+")");
        if (cell == null || cell.getCellTypeEnum().equals(CellType.BLANK)) {
            return false;
        }
//        log.info(cell.getAddress().formatAsString());
        switch (cell.getCellTypeEnum()) {
            case BOOLEAN:
            case ERROR:
            case FORMULA:
//                log.info("returning false");
                return false;
            case NUMERIC:
                try{
                    cell.getNumericCellValue();
                    return true;
                }catch(NumberFormatException nfe){
                    return false;
                }
            case STRING:
            default:
                try {
                    final String value = cell.getStringCellValue();
//                    log.info("value:"+value);
                    return value != null && !value.isEmpty();
                } catch (Exception e) {
                    log.log(Level.SEVERE, null, e);
                    return false;
                }
        }
    }

    @Override
    public Row next() {
        Row resp = sheet.getRow(index++);
//        Cell cell = resp.getCell(0);
//        System.out.println(cell==null?"X":cell.getAddress().formatAsString());
        return resp;
    }
}
