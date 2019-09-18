import DiAna.Diana_SegmentGui;
import net.imagej.ImageJ;
import ij.IJ;

public class SimpleIJLaunch {
    static public void main(String... args) {
        // Arrange
        // create the ImageJ application context with all available services
        final ImageJ ij = new ImageJ();
        ij.ui().showUI();
        IJ.openImage("http://wsr.imagej.net/images/hela-cells.zip").show();
        new Diana_SegmentGui().setVisible(true);
    }
}
