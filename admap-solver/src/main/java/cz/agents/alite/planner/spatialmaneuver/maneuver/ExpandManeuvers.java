package cz.agents.alite.planner.spatialmaneuver.maneuver;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class ExpandManeuvers implements Serializable {

    private final static long serialVersionUID = 18L;
    protected transient List<ExpandManeuver> maneuver;

    /**
     * Gets the value of the maneuver property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the maneuver property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getManeuver().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ExpandManeuver }
     *
     *
     */
    public List<ExpandManeuver> getManeuver() {
        if (maneuver == null) {
            maneuver = new ArrayList<ExpandManeuver>();
        }
        return this.maneuver;
    }

}
