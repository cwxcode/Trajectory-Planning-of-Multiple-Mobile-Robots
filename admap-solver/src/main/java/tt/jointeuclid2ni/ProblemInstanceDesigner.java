package tt.jointeuclid2ni;

import java.awt.Color;
import java.awt.Rectangle;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.Writer;

import javax.vecmath.Point2d;

import tt.jointeuclid2ni.probleminstance.EarliestArrivalProblem;
import tt.jointeuclid2ni.probleminstance.TrajectoryCoordinationProblem;
import tt.jointeuclid2ni.probleminstance.TrajectoryCoordinationProblemXMLDeserializer;
import tt.jointeuclid2ni.probleminstance.TrajectoryCoordinationProblemXMLSerializer;
import tt.jointeuclid2ni.probleminstance.VisUtil;
import tt.vis.PictureLayer;
import tt.vis.problemcreator.ProblemCreatedListener;
import tt.vis.problemcreator.ProblemDesignerLayer;
import cz.agents.alite.vis.VisManager;
import cz.agents.alite.vis.layer.common.ColorLayer;
import cz.agents.alite.vis.layer.common.VisInfoLayer;
import cz.agents.alite.vis.layer.terminal.ImageLayer;

public class ProblemInstanceDesigner {

	public static void main(String[] args) throws FileNotFoundException {
		// ---- initialize vizualization itself

		VisManager.setInitParam("Problem Instance Designer", 1024, 768, 200,
				200);
		VisManager.setSceneParam(new VisManager.SceneParams() {

			@Override
			public Point2d getDefaultLookAt() {
				return new Point2d(500, 500);
			}

			@Override
			public double getDefaultZoomFactor() {
				return 0.5;
			}
		});

		EarliestArrivalProblem inputProblem = null;
		// ---- extend an existing problem
		if (args.length > 1 && args[0].endsWith(".xml")) {
			String fileName = args[0];
			inputProblem = TrajectoryCoordinationProblemXMLDeserializer
					.deserialize(new FileInputStream(new File(fileName)));
		}

		// ---- initialize problem creator layer

		// ---- add underlying picture
		OutputStream outputStream = System.out;
		String outStreamName = "standard output";
		if (args.length == 2 && args[1].endsWith(".xml")) {
			String outFilename = args[1];
			outputStream = new FileOutputStream(new File(outFilename));
			System.out.println("Output will be written to " + outFilename);
			outStreamName = outFilename;
		}
		
		final OutputStream outputFinal = outputStream;
		final String outStringNameFinal = outStreamName;
		ProblemDesignerLayer problemCreatorLayer = ProblemDesignerLayer
				.create(inputProblem);
		problemCreatorLayer.addListener(new ProblemCreatedListener() {
			@Override
			public void handleProblem(TrajectoryCoordinationProblem problem) {
				System.out.println("Output was written to " + outStringNameFinal);
				TrajectoryCoordinationProblemXMLSerializer.serialize(problem, outputFinal);
			}
		});

		// ---- add all the layers

		VisManager.registerLayer(ColorLayer.create(Color.WHITE));

		if (inputProblem != null) {
			VisUtil.visualizeEarliestArrivalProblem(inputProblem);
		}

		// ---- add underlying picture
		if (args.length > 1 && args[0].endsWith(".png")) {
			VisManager.registerLayer(PictureLayer.create(ImageLayer
					.loadImage(new File(args[0])), new Rectangle(0, 0, 3756,
					5184)));
		}

		VisManager.registerLayer(problemCreatorLayer);
		VisManager.registerLayer(VisInfoLayer.create());
		VisManager.init();
	}
}
