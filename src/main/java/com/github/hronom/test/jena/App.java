package com.github.hronom.test.jena;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.NodeFactory;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.ReadWrite;
import com.hp.hpl.jena.sparql.core.DatasetGraph;
import com.hp.hpl.jena.sparql.core.DatasetImpl;
import com.hp.hpl.jena.tdb.TDBFactory;

import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;

import java.util.Iterator;

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

        testIterList(graph);
        testTransactions(datasetGraph, graph);

        RDFDataMgr.write(System.out, graph, RDFFormat.NTRIPLES);
    }

    public static void testIterList(Graph graph) {
        // Test
        Iterator<Triple> iter = graph.find(
            Node.ANY, NodeFactory.createURI("creationYear"), NodeFactory.createURI("2015")
        ).toList().iterator();

        while (iter.hasNext()) {
            Triple triple = iter.next();
            Node subject = triple.getSubject();

            // Exception here.
            graph.add(
                new Triple(subject, NodeFactory.createURI("value"), NodeFactory.createLiteral("1"))
            );
        }
    }

    public static void testTransactions(DatasetGraph datasetGraph, Graph graph) {
        // Test
        Iterator<Triple> iter = graph.find(
            Node.ANY, NodeFactory.createURI("creationYear"), NodeFactory.createURI("2015")
        ).toList().iterator();

        Dataset dataset = DatasetImpl.wrap(datasetGraph);
        dataset.begin(ReadWrite.WRITE);
        while (iter.hasNext()) {
            Triple triple = iter.next();
            Node subject = triple.getSubject();

            // Exception here.
            graph.add(
                new Triple(subject, NodeFactory.createURI("value"), NodeFactory.createLiteral("2"))
            );
        }
        dataset.commit();
        dataset.end();
    }
}
