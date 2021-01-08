/*
 *  StudentsUploader
 *  Cargador de datos de estudiantes a formularios
 *  Este programa fue diseñado para la Santa Iglesia Gnostica Universal, por Nestor Arias -nestor_arias@hotmail.com
 */
package oa.gnosis.selenium;

import java.awt.Dimension;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import javax.swing.JFrame;
import oa.gnosis.selenium.swing.ToolBoxPanel;

/**
 *
 * @author nesto
 */
public class Main {

    public static void main(String[] args) {
        ToolBoxPanel panel = new ToolBoxPanel();
        JFrame frame = new JFrame();
        frame.add(panel);
        frame.setPreferredSize(new Dimension(710, 450));
        frame.setSize(frame.getPreferredSize());
        frame.setResizable(false);
        frame.setVisible(true);
        frame.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {
                ;
            }

            @Override
            public void windowClosing(WindowEvent e) {
                //panel.dispose();
                panel.closeDriver();
                System.exit(0);
            }

            @Override
            public void windowClosed(WindowEvent e) {
                ;
            }

            @Override
            public void windowIconified(WindowEvent e) {
                ;
            }

            @Override
            public void windowDeiconified(WindowEvent e) {
                ;
            }

            @Override
            public void windowActivated(WindowEvent e) {
                ;
            }

            @Override
            public void windowDeactivated(WindowEvent e) {
                ;
            }
        });
    }
}
