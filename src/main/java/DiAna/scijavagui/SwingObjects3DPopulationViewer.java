package DiAna.scijavagui;

import mcib3d.geom.Objects3DPopulation;
import org.scijava.plugin.Plugin;
import org.scijava.ui.swing.viewer.EasySwingDisplayViewer;
import org.scijava.ui.viewer.DisplayViewer;

import javax.swing.*;
import java.awt.*;

@Plugin(type = DisplayViewer.class)
public class SwingObjects3DPopulationViewer extends
        EasySwingDisplayViewer<Objects3DPopulation> {


    public SwingObjects3DPopulationViewer()
    {
        super( Objects3DPopulation.class );
    }

    @Override
    protected boolean canView(Objects3DPopulation objects3DPopulation) {
        return true;
    }

    @Override
    protected void redoLayout() {

    }

    @Override
    protected void setLabel(String s) {

    }

    @Override
    protected void redraw() {

    }

    JPanel panelInfo;
    JLabel nameLabel;
    JTextArea textInfo;

    Objects3DPopulation pop;

    @Override
    protected JPanel createDisplayPanel(Objects3DPopulation objects3DPopulation) {
        pop = objects3DPopulation;

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panelInfo = new JPanel();
        panel.add(panelInfo, BorderLayout.CENTER);
        nameLabel = new JLabel(pop.toString());
        panel.add(nameLabel, BorderLayout.NORTH);
        textInfo = new JTextArea();
        textInfo.setEditable(false);

        this.redraw();
        return panel;
    }
}
