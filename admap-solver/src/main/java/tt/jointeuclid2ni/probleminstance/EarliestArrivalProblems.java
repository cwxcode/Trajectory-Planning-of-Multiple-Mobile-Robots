package tt.jointeuclid2ni.probleminstance;

public class EarliestArrivalProblems {

    public static AgentMission[] agentMissions(EarliestArrivalProblem problem) {
        int agents = problem.nAgents();
        AgentMission[] missions = new AgentMission[agents];

        for (int i = 0; i < agents; i++) {
            missions[i] = new AgentMissionImpl(problem, i);
        }

        return missions;
    }

    public static int[] getRelativeSeparations(int[] bodyRadiuses, int agent) {
        int agents = bodyRadiuses.length;

        int[] separations = new int[agents - 1];
        int j = 0;
        for (int i = 0; i < agents; i++) {
            if (i != agent)
                separations[j++] = bodyRadiuses[i] + bodyRadiuses[agent];
        }

        return separations;
    }

    public static int[] getRelativeSeparations(AgentMission[] missions, int agent) {
        int agents = missions.length;

        int[] separations = new int[agents - 1];
        int j = 0;
        for (int i = 0; i < agents; i++) {
            if (i != agent)
                separations[j++] = missions[i].getBodyRadius() + missions[agent].getBodyRadius();
        }

        return separations;
    }

}
