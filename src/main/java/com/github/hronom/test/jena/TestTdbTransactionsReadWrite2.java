package com.github.hronom.test.jena;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.query.ReadWrite;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;
import org.apache.jena.sparql.core.Quad;
import org.apache.jena.tdb.StoreConnection;
import org.apache.jena.tdb.transaction.DatasetGraphTxn;
import org.apache.jena.util.IteratorCollection;

import java.util.Iterator;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Semaphore;

public class TestTdbTransactionsReadWrite2 {
    public static void main(String[] args) {
        final StoreConnection connection = StoreConnection.createMemUncached();
        {
            DatasetGraphTxn datasetGraph = connection.begin(ReadWrite.WRITE);
            // Fill graph.
            datasetGraph.add(new Quad(
                Quad.defaultGraphNodeGenerated,
                NodeFactory.createURI("http://example/unit13"),
                NodeFactory.createURI("http://example/name"),
                NodeFactory.createLiteral("Unit 13", "en")
            ));

            datasetGraph.add(new Quad(
                Quad.defaultGraphNodeGenerated,
                NodeFactory.createURI("http://example/unit13"),
                NodeFactory.createURI("http://example/type"),
                NodeFactory.createURI("http://example/robot")
            ));

            datasetGraph.add(new Quad(
                Quad.defaultGraphNodeGenerated,
                NodeFactory.createURI("http://example/unit13"),
                NodeFactory.createURI("http://example/creationYear"),
                NodeFactory.createURI("http://example/2015")
            ));

            datasetGraph.commit();
            datasetGraph.end();
        }

        int availableProcessors = Runtime.getRuntime().availableProcessors();
        ForkJoinPool executor = new ForkJoinPool(availableProcessors);
        Semaphore semaphore = new Semaphore(availableProcessors);

        // Run tokenization task.
        for (int i = 0; i < availableProcessors; i++) {
            try {
                semaphore.acquire();
            } catch (InterruptedException exception) {
                exception.printStackTrace();
            }

            executor.execute(new Runnable() {
                @Override
                public void run() {
                    DatasetGraphTxn datasetGraph = connection.begin(ReadWrite.WRITE);
                    // Test.
                    Iterator<Quad> iter = IteratorCollection.iteratorToList(datasetGraph.find(
                        Quad.defaultGraphNodeGenerated,
                        Node.ANY,
                        NodeFactory.createURI("http://example/name"),
                        Node.ANY
                    )).iterator();

                    while (iter.hasNext()) {
                        Quad quad = iter.next();
                        datasetGraph.add(
                            quad.getGraph(),
                            quad.getSubject(),
                            NodeFactory.createURI("http://example/threadID" + String.valueOf(Thread.currentThread().getId())),
                            NodeFactory.createLiteral(String.valueOf(Thread.currentThread().getId()))
                        );
                    }

                    System.out.println(String.valueOf(Thread.currentThread().getId()));

                    datasetGraph.commit();
                    datasetGraph.end();
                    datasetGraph.close();

                    semaphore.release();
                }
            });
        }

        // Wait until all tasks completed.
        try {
            semaphore.acquire(availableProcessors);
            semaphore.release(availableProcessors);
        } catch (InterruptedException exception) {
            exception.printStackTrace();
        }

        DatasetGraphTxn datasetGraph = connection.begin(ReadWrite.READ);
        RDFDataMgr.write(System.out, datasetGraph.getDefaultGraph(), RDFFormat.NTRIPLES);
    }
}
