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
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.sparql.core.DatasetGraph;
import com.hp.hpl.jena.tdb.TDBFactory;

import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;

public class TestTdbTransactionsReadWrite {
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
        String qs1 = "SELECT * { ?s <http://example/name> ?o }";
        try (QueryExecution qExec = QueryExecutionFactory.create(qs1, dataset)) {
            ResultSet rs = qExec.execSelect();

            while (rs.hasNext()) {
                QuerySolution qs = rs.next();
                RDFNode nodeSubject = qs.get("s");
                RDFNode nodeObject = qs.get("o");
                if (nodeSubject.isURIResource() && nodeObject.isLiteral()) {
                    String str = nodeObject.asLiteral().getString();

                    graph.add(
                        new Triple(
                            NodeFactory.createURI(nodeSubject.asResource().getURI()),
                            NodeFactory.createURI("http://example/value"),
                            NodeFactory.createLiteral(String.valueOf(str.length()))
                        )
                    );
                }
            }

            dataset.commit();
        } finally {
            dataset.end();
        }

        RDFDataMgr.write(System.out, graph, RDFFormat.NTRIPLES);
    }
}