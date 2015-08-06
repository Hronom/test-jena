package com.github.hronom.test.jena;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.NodeFactory;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ReadWrite;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.sparql.core.DatasetGraph;
import com.hp.hpl.jena.tdb.TDBFactory;
import com.hp.hpl.jena.update.UpdateAction;
import com.hp.hpl.jena.update.UpdateFactory;
import com.hp.hpl.jena.update.UpdateRequest;

import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;

public class TestTdbTransactions {
    public static void main(String[] args) {
        Dataset dataset = TDBFactory.createDataset();
        dataset.begin(ReadWrite.WRITE);
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

        // Test.
        UpdateRequest request = UpdateFactory.create(
            "INSERT { ?s <http://example/value> '1' } WHERE { ?s <http://example/creationYear> <http://example/2015> . }"
        );
        UpdateAction.execute(request, dataset);

        dataset.commit();
        dataset.end();

        RDFDataMgr.write(System.out, graph, RDFFormat.NTRIPLES);
    }
}