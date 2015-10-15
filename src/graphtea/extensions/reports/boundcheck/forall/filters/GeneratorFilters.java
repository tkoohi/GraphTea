package graphtea.extensions.reports.boundcheck.forall.filters;

import graphtea.extensions.reports.boundcheck.forall.ForAllParameterShower;
import graphtea.extensions.reports.boundcheck.forall.IterGraphs;
import graphtea.extensions.reports.boundcheck.forall.IterProgressBar;
import graphtea.extensions.reports.boundcheck.forall.ToCall;
import graphtea.graph.graph.GraphModel;
import graphtea.graph.graph.RendTable;
import graphtea.library.util.Pair;
import graphtea.platform.Application;
import graphtea.platform.core.AbstractAction;
import graphtea.platform.extension.Extension;
import graphtea.platform.extension.ExtensionLoader;
import graphtea.platform.lang.ArrayX;
import graphtea.platform.parameter.Parameter;
import graphtea.platform.parameter.Parametrizable;
import graphtea.plugins.graphgenerator.GraphGenerator;
import graphtea.plugins.graphgenerator.core.SimpleGeneratorInterface;
import graphtea.plugins.graphgenerator.core.extension.GraphGeneratorExtension;
import graphtea.plugins.main.GraphData;
import graphtea.ui.ParameterShower;
import graphtea.ui.extension.AbstractExtensionAction;

import javax.swing.*;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Vector;

/**
 * Created by rostam on 14.10.15.
 * Generator Filters
 */
public class GeneratorFilters {
    public static HashMap<String, AbstractAction> hm = ExtensionLoader.loadedInstances;
    public static HashMap<String, String>  nameToClass= new HashMap<>();

    public static ArrayX<String> getGenFilters() {
        ArrayX ax = new ArrayX("");
        for (String s : hm.keySet()) {
            if (s.contains("graphtea.extensions.generators.")) {
                Extension ext = ((AbstractExtensionAction) hm.get(s)).getTarget();
                ax.addValidValue(ext.getName());
                nameToClass.put(ext.getName(),s);
            }
        }
        return ax;
    }

    public static RendTable generateGraphs(String name,ToCall f, String bound) {
        RendTable ret = new RendTable();
        int[] result = null;
        RendTable retForm = new RendTable();
        retForm.add(new Vector<>());

        Extension ext = ((AbstractExtensionAction) hm.get(
                nameToClass.get(name))).getTarget();
        //ForAllParameterShower ps = new ForAllParameterShower((Parametrizable) ext);
        Vector<JTextField> v = new Vector<>();
        JPanel myPanel = new JPanel();
        Parametrizable o = (Parametrizable) ext;
        Vector<String> names = new Vector<>();
        for (Field ff : o.getClass().getFields()) {
            Parameter anot = ff.getAnnotation(Parameter.class);
            if (anot != null) {
                JTextField xField = new JTextField(5);
                v.add(xField);
                myPanel.add(new JLabel(anot.name() + ":"));
                myPanel.add(xField);
                names.add(anot.name());
            }
        }
        int output = JOptionPane.showConfirmDialog(null, myPanel,
                "Please enter bound values", JOptionPane.OK_CANCEL_OPTION);
        if (output == JOptionPane.OK_OPTION) {
            Vector<Pair<Integer, Integer>> res = new Vector<>();
            for (int i = 0; i < v.size(); i++) {
                Scanner sc = new Scanner(v.get(i).getText());
                sc.useDelimiter(":");
                res.add(new Pair<Integer, Integer>(
                        Integer.parseInt(sc.next()),
                        Integer.parseInt(sc.next())));

            }
            int from = res.get(0).first;
            int to = res.get(0).second;
            IterProgressBar pb = new IterProgressBar(to-from+1);
            pb.setVisible(true);
            for (int i = from; i <= to; i++) {
                try {
                    o.getClass().getDeclaredField(names.get(0)).set(o, i);
                    GraphModel g = GraphGenerator.getGraph(false, (SimpleGeneratorInterface) ext);
                    pb.setValue(i);
                    pb.validate();
                    ret=(RendTable)f.f(g);
                    if(retForm.size()==1) {
                        retForm.get(0).add("Counter");
                        retForm.get(0).addAll(ret.get(0));
                    }
                    retForm.add(new Vector<>());
                    retForm.lastElement().add(i);
                    retForm.lastElement().addAll(ret.get(1));

                    if (ret.get(0).size() <= 2) return null;
                    if (result == null) {
                        result = new int[ret.get(0).size()];
                    }
                    for (int ii = 1; ii < ret.get(0).size();ii++) {
                        IterGraphs.checkTypeOfBounds(ret, result, ii, bound);
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
}
