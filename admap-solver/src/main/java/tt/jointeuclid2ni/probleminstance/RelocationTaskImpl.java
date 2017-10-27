package tt.jointeuclid2ni.probleminstance;

import tt.euclid2i.Point;

public class RelocationTaskImpl implements RelocationTask {

	private int issueTime;
	private int agentId;
	private Point destination;

	public RelocationTaskImpl(int issueTime, int agentId, Point destination) {
		super();
		this.issueTime = issueTime;
		this.agentId = agentId;
		this.destination = destination;
	}

	@Override
	public int getIssueTime() {
		return issueTime;
	}

	@Override
	public int getAgentId() {
		return agentId;
	}

	@Override
	public Point getDestination() {
		return destination;
	}

	@Override
	public String toString() {
		return "RelocationTaskImpl [issueTime=" + issueTime + ", agentId="
				+ agentId + ", destination=" + destination + "]";
	}
		

}
