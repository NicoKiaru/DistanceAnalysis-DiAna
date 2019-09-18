package DiAna.scijavagui;

import mcib3d.geom.Objects3DPopulation;
import org.scijava.display.AbstractDisplay;
import org.scijava.display.Display;
import org.scijava.plugin.Plugin;

@Plugin(type = Display.class)
public class Objects3DPopulationDisplay extends AbstractDisplay<Objects3DPopulation> {
    public Objects3DPopulationDisplay() {
        super(Objects3DPopulation.class);
    }
}