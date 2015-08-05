package com.github.hronom.test.jena;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.NodeFactory;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.sparql.core.DatasetGraph;
import com.hp.hpl.jena.tdb.TDBFactory;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

public class App {
    public static void main(String[] args) {
        DatasetGraph datasetGraph = TDBFactory.createDatasetGraph();
        Graph graph = datasetGraph.getDefaultGraph();

        // Fill graph.
        graph.add(
            new Triple(
                NodeFactory.createURI("www.test.org/unit13"),
                NodeFactory.createURI("name"),
                NodeFactory.createLiteral("Unit 13", "en")
            )
        );

        graph.add(
            new Triple(
                NodeFactory.createURI("www.test.org/unit13"),
                NodeFactory.createURI("type"),
                NodeFactory.createURI("robot")
            )
        );

        graph.add(
            new Triple(
                NodeFactory.createURI("www.test.org/unit13"),
                NodeFactory.createURI("creationYear"),
                NodeFactory.createURI("2015")
            )
        );

        // Test
        ExtendedIterator<Triple> iter = graph.find(
            Node.ANY,
            NodeFactory.createURI("creationYear"),
            NodeFactory.createURI("2015")
        );

        while (iter.hasNext()) {
            Triple triple = iter.next();
            Node subject = triple.getSubject();

            // Exception here.
            graph.add(
                new Triple(
                    subject, NodeFactory.createURI("status"), NodeFactory.createURI("available")
                )
            );
        }
    }
}
