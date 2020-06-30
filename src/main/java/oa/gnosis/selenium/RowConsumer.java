/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package oa.gnosis.selenium;

import java.util.Arrays;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import lombok.Data;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

/**
 *
 * @author nesto
 */
@Data
public class RowConsumer {

    private String city;
    private Cell mobileCell;
    private String mobile;
    private String userLogName;
    private String firstName;
    private String lastName;
    private String url;
    private String email = "";
    private String countryCode;

    public RowConsumer(Row row, Logger log) {
        int col = 0;
        city = row.getCell(col++).getStringCellValue();
        final String cellNames = row.getCell(col++).getStringCellValue();
        final String[] names = cellNames.split(" ");
        final Cell mobileCell = row.getCell(col++);
        url = row.getCell(col++).getStringCellValue();
        mobile = getStringOrNumber(mobileCell).trim();
        try {
            email = row.getCell(col++).getStringCellValue();
            countryCode = getStringOrNumber(row.getCell(col++)).trim();
            if(mobile.startsWith(countryCode)){
                mobile=mobile.substring(countryCode.length());
            }
//            log.info(cellNames+"/cel:"+mobile+"/url:"+url+"/email:"+email+"/countryCode:"+countryCode);
        } catch (Exception e) {
//            log.severe(e.toString());
        }
        userLogName = cellNames + " " + mobile + " " + city;
        firstName = names[0];
        if (names.length < 2) {
            lastName = firstName;
//                log.severe(userLogName + " no pudo registrarse: no hay nombre y apellido");
//                return;
        } else {
            lastName = Arrays.asList(names).stream().filter(m -> m != firstName).collect(Collectors.joining(" "));
        }
//            url ="https://ac.gnosis.is/f/registrar-inscripcion";
    }

    private String getStringOrNumber(Cell mobileCell) {
        switch (mobileCell.getCellType()) {
            case NUMERIC:
            case FORMULA:
                return "" + (long) mobileCell.getNumericCellValue();
            default:
                return mobileCell.getStringCellValue();
        }
    }
}
