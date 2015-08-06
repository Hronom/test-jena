package com.github.hronom.test.jena;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.NodeFactory;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.ReadWrite;
import com.hp.hpl.jena.sparql.core.DatasetGraph;
import com.hp.hpl.jena.sparql.core.Quad;
import com.hp.hpl.jena.tdb.TDBFactory;
import com.hp.hpl.jena.tdb.transaction.DatasetGraphTransaction;
import com.hp.hpl.jena.update.UpdateAction;
import com.hp.hpl.jena.update.UpdateFactory;
import com.hp.hpl.jena.update.UpdateRequest;

import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;

import java.util.Iterator;

public class App {
    public static void main(String[] args) {
        Dataset dataset = TDBFactory.createDataset();
        DatasetGraph datasetGraph = dataset.asDatasetGraph();
        Graph graph = datasetGraph.getDefaultGraph();

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

        testIterList(graph);
        testTransactions(dataset);
        //find(datasetGraph);

        RDFDataMgr.write(System.out, graph, RDFFormat.NTRIPLES);
    }

    private static void testIterList(Graph graph) {
        // Test
        Iterator<Triple> iter = graph.find(
            Node.ANY, NodeFactory.createURI("http://example/creationYear"), NodeFactory.createURI("http://example/2015")
        ).toList().iterator();

        while (iter.hasNext()) {
            Triple triple = iter.next();
            Node subject = triple.getSubject();

            // Exception here.
            graph.add(
                new Triple(subject, NodeFactory.createURI("http://example/value"), NodeFactory.createLiteral("1"))
            );
        }
    }

    private static void testTransactions(Dataset dataset) {
        // Test
        dataset.begin(ReadWrite.WRITE);

        UpdateRequest request = UpdateFactory
            .create("INSERT { ?s <http://example/value> '2' } WHERE { ?s <http://example/creationYear> <http://example/2015> . }");// WHERE { ?s <creationYear> <2015> . }");
        UpdateAction.execute(request, dataset);

        dataset.commit();
        dataset.end();
    }

    private static void find(DatasetGraph datasetGraph) {
        // Test
        Iterator<Quad> iter = datasetGraph
            .find(Node.ANY, Node.ANY, NodeFactory.createURI("http://example/name"), Node.ANY);

        DatasetGraphTransaction dgt = (DatasetGraphTransaction) datasetGraph;
        dgt.begin(ReadWrite.WRITE);
        while (iter.hasNext()) {
            Quad quad = iter.next();
            Node graph = quad.getGraph();
            Node subject = quad.getSubject();

            // Exception here.
            datasetGraph.add(
                new Quad(
                    graph, subject, NodeFactory.createURI("http://example/value"), NodeFactory.createLiteral("1")
                )
            );
        }
        dgt.commit();
        dgt.end();
    }
}
