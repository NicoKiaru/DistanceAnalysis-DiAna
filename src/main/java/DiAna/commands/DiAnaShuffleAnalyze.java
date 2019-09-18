package DiAna.commands;

import ij.ImagePlus;
import mcib3d.geom.Objects3DPopulation;
import org.scijava.command.Command;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

@Plugin(type = Command.class,menuPath = "Plugins>DiAna>SciJava>DiAna Analyze Population (Shuffle)")
public class DiAnaShuffleAnalyze implements Command {

    @Parameter(required = false)
    ImagePlus mask;

    @Parameter
    Objects3DPopulation popA;

    @Parameter
    Objects3DPopulation popB;

    @Override
    public void run() {

    }
}
