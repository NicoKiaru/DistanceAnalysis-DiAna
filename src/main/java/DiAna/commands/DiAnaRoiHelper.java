package DiAna.commands;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.Roi;
import ij.plugin.ChannelSplitter;
import ij.plugin.Duplicator;

public class DiAnaRoiHelper {

    static public ImagePlus cropRoiChannel(ImagePlus img_in, int channel_index) {
        ImagePlus imp_copy;
        if (img_in.getRoi() != null) {
            Roi r = img_in.getRoi();

            ImagePlus imgTemp = img_in.duplicate();
                    imgTemp.setRoi(r);
            IJ.run(imgTemp, "Clear Outside", "");
            //img_in.setRoi((Roi)null);
            imp_copy = imgTemp.crop("stack");
            //imp_copy.setRoi(r);
        } else {
            imp_copy = img_in.duplicate();
        }
        ImagePlus[] channels = ChannelSplitter.split(imp_copy);
        return channels[channel_index-1];
    }
}
