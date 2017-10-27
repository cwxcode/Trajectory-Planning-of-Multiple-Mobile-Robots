package cz.agents.alite;

import cz.agents.alite.creator.Creator;
import cz.agents.alite.creator.CreatorFactory;


/**
 * The main class of the application runs the {@link CreatorFactory}, which
 * creates and runs an initial {@link Creator} according to the application
 * arguments.
 *
 *
 * @author Antonin Komenda
 */
public class Main {

    public static void main(String[] args) {
        CreatorFactory.createCreator(args).create();
    }

}
