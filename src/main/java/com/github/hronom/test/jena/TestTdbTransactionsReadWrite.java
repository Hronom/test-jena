package com.github.hronom.test.jena;

import org.apache.jena.graph.Graph;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ReadWrite;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;
import org.apache.jena.sparql.core.DatasetGraph;
import org.apache.jena.tdb.TDBFactory;

public class TestTdbTransactionsReadWrite {
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

        dataset.begin(ReadWrite.WRITE);
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
