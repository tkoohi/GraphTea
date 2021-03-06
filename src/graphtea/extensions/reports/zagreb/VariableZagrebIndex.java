// GraphTea Project: http://github.com/graphtheorysoftware/GraphTea
// Copyright (C) 2012 Graph Theory Software Foundation: http://GraphTheorySoftware.com
// Copyright (C) 2008 Mathematical Science Department of Sharif University of Technology
// Distributed under the terms of the GNU General Public License (GPL): http://www.gnu.org/licenses/
package graphtea.extensions.reports.zagreb;

import graphtea.graph.graph.GraphModel;
import graphtea.platform.lang.CommandAttitude;
import graphtea.platform.parameter.Parameter;
import graphtea.platform.parameter.Parametrizable;
import graphtea.plugins.reports.extension.GraphReportExtension;

import java.util.ArrayList;

/**
 * @author Ali Rostami

 */

@CommandAttitude(name = "var_zagreb_index", abbreviation = "_vzi")
public class VariableZagrebIndex implements GraphReportExtension, Parametrizable {
    public String getName() {
        return "All Variable Zagreb Indices";
    }

    @Parameter(name = "Alpha", description = "")
    public Double alpha = 1.0;

    public String getDescription() {
        return "All Variable Zagreb Indices";
    }

    public Object calculate(GraphModel g) {
        ArrayList<String> out = new ArrayList<>();
        ZagrebIndexFunctions zif = new ZagrebIndexFunctions(g);
        out.add("First Variable Zagreb Index : "+ zif.getFirstVariableZagrebIndex(alpha));
        out.add("Second Variable Zagreb Index : "+ zif.getSecondVariableZagrebIndex(alpha));
        return out;
    }

    public String checkParameters() {
        return null;
    }

    @Override
	public String getCategory() {
        return "Topological Indices-Zagreb Indices";
	}
}
