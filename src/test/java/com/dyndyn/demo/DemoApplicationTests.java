package com.dyndyn.demo;

import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.T;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.umlg.sqlg.structure.SqlgGraph;

@RunWith(SpringRunner.class)

public class DemoApplicationTests {

	private Graph sqlgGraph;

	@Test
	public void sqlgGraphIT() {
		this.sqlgGraph = SqlgGraph.open("postgres.properties");

//		for (int i = 0; i < 10; i++) {
//			this.sqlgGraph.addVertex(T.label, "A", "name", "a" + i);
//		}
//		this.sqlgGraph.tx().commit();

		this.sqlgGraph.traversal().V().forEachRemaining(v -> v.property("name", "aaa"));
		this.sqlgGraph.tx().commit();
	}

}
