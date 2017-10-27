package tt.jointeuclid2ni.solver;

public enum Algorithms {
    PP,
    KDPMD, // k-step distributed penalty method with discrete optimizers
    KDPMC, // k-step distributed penalty method with continuous optimizers
    ASFO,  // anytime penalty method
    IIHP,  // incremental homotopy optimizer
    ODPIN,
    ODCN,
    ORCAMARRT,
    PP_SIPP
}