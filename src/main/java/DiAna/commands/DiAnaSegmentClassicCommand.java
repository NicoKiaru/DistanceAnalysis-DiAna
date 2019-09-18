package DiAna.commands;

import DiAna.Segment;
import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.process.AutoThresholder;
import ij.process.ImageStatistics;
import mcib3d.geom.Objects3DPopulation;
import org.apache.commons.lang3.math.NumberUtils;
import org.scijava.ItemIO;
import org.scijava.command.Command;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

@Plugin(type = Command.class,menuPath = "Plugins>DiAna>SciJava>DiAna Classic Segmentation")
public class DiAnaSegmentClassicCommand implements Command {
    @Parameter(label = "image to analyze")
    ImagePlus input_image;

    @Parameter(type = ItemIO.OUTPUT)
    ImagePlus output_pop_image;

    @Parameter(label = "channel index for multichannel image", required = false)
    Integer ch;

    @Parameter(label = "Filter type", choices = {"gaussian", "median", "mean", "none"})
    String filter_type;

    @Parameter(label = "Radius")
    double radius;

    @Parameter(label = "Threshold: method name or absolute value")
    String threshold;

    @Parameter(label = "Min. Object Size (px)")
    Integer min_object_size;

    @Parameter(label = "Max. Object Size (px)")
    Integer max_object_size;

    @Parameter(label = "Exclude objects on XY edges")
    boolean exclude_objects_on_xy_edges;

    @Parameter(label = "Exclude objects on Z edges")
    boolean exclude_objects_on_z_edges;

    @Parameter(type = ItemIO.OUTPUT)
    Objects3DPopulation pop;

    @Parameter(label = "Show output population")
    boolean display_output_population;

    @Override
    public void run() {
        // Check validity of input argument with regards to multiple channels
        if (ch==null) {
            if (input_image.getNChannels()==1) {
                ch = 1;
            } else {
                System.err.println("Error in DiAna Segment Command : Channel index not specified for multichannel image.");
                return;
            }
        }

        input_image = DiAnaRoiHelper.cropRoiChannel(input_image, ch);

        // Fetch the value of the threshold
        double valueOfThreshold;
        // Is the input argument a number ?
        if (NumberUtils.isCreatable(threshold)) {
            // Yes -> absolute threshold
            valueOfThreshold = Integer.valueOf(threshold);
        } else {
            // No -> Automatic thresholding method
            ImageStatistics stat = input_image.getRawStatistics();
            valueOfThreshold = (new AutoThresholder()).getThreshold(threshold, stat.histogram);
        }

        pop = new Segment().segClassic( input_image ,
                (int) valueOfThreshold,
                min_object_size,
                max_object_size,
                exclude_objects_on_xy_edges,
                exclude_objects_on_z_edges);

        if (display_output_population) {
            int[] dim = input_image.getDimensions();
            output_pop_image = IJ.createImage(input_image.getTitle()+"_Classic", dim[0], dim[1], dim[3], 16);

            ImageStack spots_stk = output_pop_image.getImageStack();
            pop.draw(spots_stk);

            output_pop_image.setStack(spots_stk);
            IJ.run(output_pop_image, "3-3-2 RGB", "");
            IJ.resetMinAndMax(output_pop_image);
            output_pop_image.show();
        }
    }
}
