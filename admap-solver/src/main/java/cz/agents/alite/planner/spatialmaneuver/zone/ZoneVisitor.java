package cz.agents.alite.planner.spatialmaneuver.zone;

public interface ZoneVisitor {

    public void visit(CylinderZone zone);

    public void visit(EmptyZone zone);

    public void visit(GroupZone zone);

    public void visit(TransformZone zone);

    public void visit(EllipsoidZone zone);

    public void visit(BoxZone zone);

    public void visit(PrismZone zone);

}
