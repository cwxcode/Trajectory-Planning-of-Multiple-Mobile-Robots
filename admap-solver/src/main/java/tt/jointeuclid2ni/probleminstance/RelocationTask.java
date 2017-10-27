package tt.jointeuclid2ni.probleminstance;

import tt.euclid2i.Point;

public interface RelocationTask {
	int getIssueTime();
	int getAgentId();
	Point getDestination();
}
