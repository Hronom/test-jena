package com.github.hronom.test.jena;

import org.apache.jena.graph.Graph;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;
import org.apache.jena.sparql.core.DatasetGraph;
import org.apache.jena.tdb.TDBFactory;

import java.nio.file.Path;
import java.nio.file.Paths;

public class TestReopenTdb {
    public static final Path pathToTdb = Paths.get("test_tdb");

    public static void main(String[] args) {
        Graph graph = openTdb(pathToTdb);

        // Fill graph.
        graph.add(
            new Triple(
                NodeFactory.createURI("http://example/unit13"),
                NodeFactory.createURI("http://example/name"),
                NodeFactory.createLiteral("Unit 13", "en")
            )
        );

        graph.add(
            new Triple(
                NodeFactory.createURI("http://example/unit13"),
                NodeFactory.createURI("http://example/type"),
                NodeFactory.createURI("http://example/robot")
            )
        );

        graph.add(
            new Triple(
                NodeFactory.createURI("http://example/unit13"),
                NodeFactory.createURI("http://example/creationYear"),
                NodeFactory.createURI("http://example/2015")
            )
        );

        RDFDataMgr.write(System.out, graph, RDFFormat.NTRIPLES);

        graph.close();

        graph = openTdb(pathToTdb);

        graph.add(
            new Triple(
                NodeFactory.createURI("http://example/unit15"),
                NodeFactory.createURI("http://example/creationYear"),
                NodeFactory.createURI("http://example/2015")
            )
        );

        graph.close();
    }

    /**
     * Open specified TDB as Graph. If TDB was already opened then return him.
     *
     * @param tdbPath Path to TDB directory.
     * @return Opened Graph.
     */
    private static Graph openTdb(Path tdbPath) {
        // Open TDB.
        DatasetGraph datasetGraph = TDBFactory.createDatasetGraph(tdbPath.toString());
        return datasetGraph.getDefaultGraph();
    }
}
