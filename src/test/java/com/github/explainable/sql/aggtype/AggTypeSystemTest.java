/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Gabriel Bender
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.github.explainable.sql.aggtype;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests for {@link AggTypeSystem}.
 */
public class AggTypeSystemTest {
	private Aggregate aggregate;

	private NonAggregate nonAggregate;

	private AggBottom aggregateOrNot;

	@Before
	public void setUp() {
		this.aggregate = AggTypeSystem.agg();
		this.nonAggregate = AggTypeSystem.nonAgg();
		this.aggregateOrNot = AggTypeSystem.aggOrNot();
	}

	@After
	public void tearDown() {
		this.aggregate = null;
		this.nonAggregate = null;
		this.aggregateOrNot = null;
	}

	@Test
	public void testEquals() {
		// We deliberately create new instances of each AggType, and compare the old instances to the
		// new instances to ensure that equality checks are performed correctly among different
		// instances of the same type.
		AggType aggregate2 = AggTypeSystem.agg();
		AggType nonAggregate2 = AggTypeSystem.nonAgg();
		AggType aggregateOrNot2 = AggTypeSystem.aggOrNot();

		assertTrue(aggregate.equals(aggregate2));
		assertFalse(aggregate.equals(nonAggregate2));
		assertFalse(aggregate.equals(aggregateOrNot2));

		assertFalse(nonAggregate.equals(aggregate2));
		assertTrue(nonAggregate.equals(nonAggregate2));
		assertFalse(nonAggregate.equals(aggregateOrNot2));

		assertFalse(aggregateOrNot.equals(aggregate2));
		assertFalse(aggregateOrNot.equals(nonAggregate2));
		assertTrue(aggregateOrNot.equals(aggregateOrNot2));
	}

	@Test
	public void testToAggregate() {
		assertEquals(aggregate, aggregate.toAggregate());
		assertNull(nonAggregate.toAggregate());
		assertEquals(aggregateOrNot, aggregateOrNot.toAggregate());
	}

	@Test
	public void testToNonAggregate() {
		assertNull(aggregate.toNonAggregate());
		assertEquals(nonAggregate, nonAggregate.toNonAggregate());
		assertEquals(aggregateOrNot, aggregateOrNot.toNonAggregate());
	}
}
