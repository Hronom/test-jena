package com.github.hronom.test.jena;

import org.apache.jena.graph.Graph;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;
import org.apache.jena.query.Dataset;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;
import org.apache.jena.sparql.core.DatasetGraph;
import org.apache.jena.tdb.TDBFactory;
import org.apache.jena.util.IteratorCollection;

import java.util.Iterator;

public class TestIterList {
    public static void main(String[] args) {
        Dataset dataset = TDBFactory.createDataset();
        DatasetGraph datasetGraph = dataset.asDatasetGraph();
        Graph graph = datasetGraph.getDefaultGraph();

        // Fill graph.
        graph.add(new Triple(NodeFactory.createURI("http://example/unit13"),
            NodeFactory.createURI("http://example/name"),
            NodeFactory.createLiteral("Unit 13", "en")
        ));

        graph.add(new Triple(NodeFactory.createURI("http://example/unit13"),
            NodeFactory.createURI("http://example/type"),
            NodeFactory.createURI("http://example/robot")
        ));

        graph.add(new Triple(NodeFactory.createURI("http://example/unit13"),
            NodeFactory.createURI("http://example/creationYear"),
            NodeFactory.createURI("http://example/2015")
        ));

        // Test
        Iterator<Triple> iter = IteratorCollection.iteratorToList(graph.find(Node.ANY,
            NodeFactory.createURI("http://example/creationYear"),
            NodeFactory.createURI("http://example/2015")
        )).iterator();

        while (iter.hasNext()) {
            Triple triple = iter.next();
            Node subject = triple.getSubject();

            // Exception here.
            graph.add(new Triple(subject,
                NodeFactory.createURI("http://example/value"),
                NodeFactory.createLiteral("1")
            ));
        }

        RDFDataMgr.write(System.out, graph, RDFFormat.NTRIPLES);
    }
}
