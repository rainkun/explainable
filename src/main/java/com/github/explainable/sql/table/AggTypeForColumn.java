package com.github.explainable.sql.table;

import com.github.explainable.sql.aggtype.AggType;
import com.github.explainable.sql.aggtype.AggTypeSystem;
import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

/**
 * Utility class for aggregate type-checking that assigns {@link AggType}s to table columns.
 */
public final class AggTypeForColumn {
	private final ImmutableSet<Column> aggregateColumns;

	private final ImmutableSet<Column> aggBottomColumns;

	private AggTypeForColumn(
			Iterable<? extends Column> aggregateColumns,
			Iterable<? extends Column> aggBottomColumns) {
		this.aggregateColumns = ImmutableSet.copyOf(aggregateColumns);
		this.aggBottomColumns = ImmutableSet.copyOf(aggBottomColumns);
		assert Sets.intersection(this.aggregateColumns, this.aggBottomColumns).isEmpty();
	}

	/**
	 * Create a new strategy in which all variables have type {@code NonAggregate}.
	 *
	 * @return the new strategy
	 */
	public static AggTypeForColumn allNonAggregate() {
		return new AggTypeForColumn(ImmutableList.<Column>of(), ImmutableSet.<Column>of());
	}

	/**
	 * Get the {@link AggType} of the specified column.
	 */
	public AggType getAggType(Column column) {
		if (aggregateColumns.contains(column)) {
			return AggTypeSystem.agg();
		} else if (aggBottomColumns.contains(column)) {
			return AggTypeSystem.aggOrNot();
		} else {
			return AggTypeSystem.nonAgg();
		}
	}

	/**
	 * Create a copy of the current object which is suitable for use in a subquery containing the
	 * specified correlated columns from the current scope.
	 */
	public AggTypeForColumn forSubQuery(Iterable<? extends Column> correlatedColumns) {
		// Correlated columns are treated as a constants in the subquery, and distinctions between
		// aggregate and non-aggregate columns in the parent query are ignored. (These are handled
		// separately by the parent query.)
		ImmutableSet<Column> allAggregateOrNot = ImmutableSet.<Column>builder()
				.addAll(this.aggBottomColumns)
				.addAll(correlatedColumns)
				.build();

		return new AggTypeForColumn(ImmutableSet.<Column>of(), allAggregateOrNot);
	}

	/**
	 * Create a copy of the current object in which the specified columns are aggregate and all the
	 * remaining columns are the same as for the current object. The current object is not modified.
	 *
	 * @return the modified strategy
	 */
	public AggTypeForColumn withAggregates(Iterable<? extends Column> aggregateColumns) {
		ImmutableSet<Column> allAggregateColumns = ImmutableSet.<Column>builder()
				.addAll(this.aggregateColumns)
				.addAll(aggregateColumns)
				.build();

		return new AggTypeForColumn(allAggregateColumns, aggBottomColumns);
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this)
				.add("aggregateColumns", aggregateColumns)
				.add("aggBottomColumns", aggBottomColumns)
				.toString();
	}
}