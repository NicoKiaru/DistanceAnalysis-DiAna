package DiAna.commands;

import ij.gui.Plot;
import ij.macro.Variable;
import ij.measure.ResultsTable;
import mcib3d.geom.Object3D;
import mcib3d.geom.Objects3DPopulation;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.math3.stat.StatUtils;
import org.scijava.ItemIO;
import org.scijava.command.Command;
import org.scijava.object.ObjectService;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.BiFunction;

@Plugin(type = Command.class,menuPath = "Plugins>DiAna>SciJava>DiAna Analyze Population")
public class DiAnaAnalyzeCommand implements Command {

    @Parameter
    Objects3DPopulation popA;

    @Parameter
    Objects3DPopulation popB;

    @Parameter(style = "text area")
    String measurementOutput = "Dist CenterA-CenterB:mean,mode,sd\n" +
            "Dist min EdgeA-EdgeB:mean,mode,sd\n"+
            "Dist min CenterA-EdgeB:mean,mode,sd\n"+
            "Dist min EdgeA-CenterB:mean,mode,sd\n"; //mean sd mode values graph

    @Parameter(type = ItemIO.OUTPUT)
    ArrayList<DiAnaMeasurement> measurements = new ArrayList<>();

    @Parameter(required = false)
    ResultsTable rtv;

    @Parameter(required = false)
    ResultsTable rts;

    @Parameter(required = false)
    Plot pv;

    @Parameter
    boolean appendToSummaryPlot;

    @Parameter
    String colorSummary;

    @Parameter
    String colorValues;

    @Parameter
    boolean spreadPlotValues;

    @Parameter(required = false)
    Plot ps;

    @Parameter
    ObjectService os;

    Plot getPlotValues() {
        if (pv==null) {
            pv = new Plot("values", "m", "dist");
        }
        return pv;
    }

    Plot getPlotSummary() {
        if (ps==null) {
            ps = new Plot("summary", "m", "dist");
        }
        return ps;
    }

    ResultsTable getRTValues() {
        if (rtv ==null) {
            rtv = new ResultsTable();
        };
        return rtv;
    }

    ResultsTable getRTSummary() {
        if (rts ==null) {
            rts = new ResultsTable();
        };
        return rts;
    }

    @Override
    public void run() {

        String[] measures = measurementOutput.split("\n");

        getRTSummary().incrementCounter();

        for (String str:measures) {
            DiAnaMeasurement dm = new DiAnaMeasurement();
            measurements.add(dm);

            String[] measureNameAndParam = str.split(":");
            String measureName = measureNameAndParam[0];
            String[] measureParams = measureNameAndParam[1].split(",");

            dm.name = measureName;
            for (String p: measureParams) {
                dm.paramsToMeasure.add(p);
            }

            dm.compute(popA,popB);


            dm.paramsToMeasure.forEach(key-> {
                if ((!key.equals("values"))&&(!key.equals("graph"))) {
                    ResultsTable rt = getRTSummary();
                    rt.addValue(measureName+"."+key, dm.measuredValues.get(key));
                    //System.out.println(str+"."+key+"="+dm.measuredValues.get(key));
                    if (appendToSummaryPlot) {
                        Plot p = getPlotSummary();
                        p.setColor(colorSummary);
                        p.add("cross",new double[]{dm.measuredValues.get(key)});
                        p.show();

                    }
                }
            });

            if (dm.paramsToMeasure.contains("values")) {
                ResultsTable rt = getRTValues();
                rt.incrementCounter();
                Variable[] vars = new Variable[dm.allMeasurements.size()];
                for (int i=0;i<dm.allMeasurements.size();i++) {
                    vars[i] = new Variable(dm.allMeasurements.get(i));
                }
                rt.setColumn(dm.name, vars );

            }

        }

        if (rtv!=null) {
            getRTValues().show(popA+":"+popB);
            if (!os.getObjects(ResultsTable.class).contains(rtv)){
                os.addObject(rtv);
            }
        }

        if (rts!=null) {
            getRTSummary().show(popA+":"+popB+" - Summary");
            if (!os.getObjects(ResultsTable.class).contains(rts)){
                os.addObject(rts);
            }
        }

        if (pv!=null) {
            if (!os.getObjects(Plot.class).contains(pv)){
                os.addObject(pv);
            }
        }

        if (ps!=null) {
            if (!os.getObjects(Plot.class).contains(ps)){
                os.addObject(ps);
            }
        }

    }

    public class DiAnaMeasurement {
        public String name;
        public ArrayList<String> paramsToMeasure = new ArrayList<>();
        public HashMap<String, Double> measuredValues = new HashMap<>();
        public ArrayList<Double> allMeasurements = new ArrayList<>();
        public ArrayList<Double> objectSize = new ArrayList<>();

        public DiAnaMeasurement() {

        }

        public void compute(Objects3DPopulation popA, Objects3DPopulation popB) {

            final BiFunction<Object3D,Object3D,Double> m;

            switch (name) {
                case "Dist CenterA-CenterB":
                    m = (o1,o2) -> o1.distCenterUnit(o2);
                    break;
                case "Dist min EdgeA-EdgeB":
                    m = (o1,o2) -> o1.distBorderUnit(o2);
                    break;
                case "Dist min CenterA-EdgeB":
                    m = (o1,o2) -> o1.distCenterBorderUnit(o2);
                    break;
                case "Dist min EdgeA-CenterB":
                    m = (o1,o2) -> o2.distCenterBorderUnit(o1);
                    break;
                default:
                    m = (o1,o2) -> Double.NaN;
            }

            popA.getObjectsList().forEach(obA -> {
                Object3D obB = popB.closestCenter(obA, 0.010);
                objectSize.add((double) obA.getVolumePixels());
                allMeasurements.add(m.apply(obA,obB));
            });

            double[] listM = ArrayUtils.toPrimitive(
                    allMeasurements.toArray(new Double[allMeasurements.size()]));

            paramsToMeasure.forEach(key -> {
                switch (key) {
                    case "mean":
                        measuredValues.put(key, StatUtils.mean(listM));
                    break;
                    case "mode":
                        measuredValues.put(key, StatUtils.mode(listM)[0]);
                    break;
                    case "sd":
                        measuredValues.put(key, StatUtils.populationVariance(listM));
                        break;
                    case "graph":
                        Plot p = getPlotValues();
                        p.setColor(colorValues);
                        double[] xs = new double[listM.length];
                        for (int i = 0;i<xs.length;i++) {
                            if (spreadPlotValues) {
                                xs[i] = objectSize.get(i);
                            } else {
                                xs[i] = 0;
                            }
                        }
                        p.add("cross", xs,listM);
                        p.show();
                        break;
                    default:
                }
            });

        }

    }

}
