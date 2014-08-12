package com.github.explainable.sql.ast.select;

import com.github.explainable.sql.SqlException;
import com.github.explainable.sql.aggtype.AggType;
import com.github.explainable.sql.aggtype.AggTypeSystem;
import com.github.explainable.sql.ast.SqlNode;
import com.github.explainable.sql.table.AggTypeForColumn;
import com.github.explainable.sql.table.BaseTable;
import com.github.explainable.sql.type.Type;
import com.github.explainable.sql.type.TypeSystem;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import javax.annotation.Nullable;
import java.util.Set;

/**
 * Class that represents a {@code JOIN} of two or more tables in the {@code FROM} clause of a SQL
 * query.
 */
public class SqlFromJoin extends SqlFrom {
	private final SqlFrom left;

	private final SqlFrom right;

	@Nullable
	private final SqlOnClause onClause;

	private final SqlJoinKind kind;

	public SqlFromJoin(
			SqlFrom left,
			SqlFrom right,
			@Nullable SqlOnClause onClause,
			SqlJoinKind kind) {
		this.left = Preconditions.checkNotNull(left);
		this.right = Preconditions.checkNotNull(right);
		this.kind = Preconditions.checkNotNull(kind);
		this.onClause = onClause;
	}

	@Override
	public void accept(SqlSelectVisitor visitor, SqlNode parent) {
		SqlSelectVisitor childVisitor = visitor.enter(this, parent);
		if (childVisitor != null) {
			left.accept(childVisitor, this);
			right.accept(childVisitor, this);
			if (onClause != null) {
				onClause.accept(childVisitor, this);
			}
		}
		visitor.leave(this, parent);
	}

	@Override
	protected AggType aggTypeCheckImpl(AggTypeForColumn typeForColumn) {
		if (onClause != null) {
			if (onClause.getAggType().toNonAggregate() == null) {
				throw new SqlException("Must be non-aggregate: " + onClause);
			}
		}
		return AggTypeSystem.nonAgg();
	}

	@Override
	protected Type typeCheckImpl() {
		return TypeSystem.table();
	}

	public SqlFrom left() {
		return left;
	}

	public SqlFrom right() {
		return right;
	}

	public SqlJoinKind kind() {
		return kind;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder()
				.append(left)
				.append(' ')
				.append(kind)
				.append(" JOIN ")
				.append(right);

		if (onClause != null) {
			builder.append(onClause);
		}

		return builder.toString();
	}

	@Override
	public boolean hasOnlyInnerJoins() {
		return (kind == SqlJoinKind.INNER) && left.hasOnlyInnerJoins() && right.hasOnlyInnerJoins();
	}

	@Override
	public Set<BaseTable> dependentTables() {
		Set<BaseTable> result;
		switch (kind) {
			case INNER:
				result = Sets.union(left.dependentTables(), right.dependentTables());
				break;
			case LEFT_OUTER:
				result = right.dependentTables();
				break;
			case RIGHT_OUTER:
				result = left.dependentTables();
				break;
			case FULL_OUTER:
				result = ImmutableSet.of();
				break;
			default:
				throw new AssertionError("Unknown join kind: " + kind);
		}

		return result;
	}

	public enum SqlJoinKind {
		INNER("INNER"),
		LEFT_OUTER("LEFT OUTER"),
		RIGHT_OUTER("RIGHT OUTER"),
		FULL_OUTER("FULL OUTER");

		private final String stringValue;

		SqlJoinKind(String stringValue) {
			this.stringValue = stringValue;
		}

		@Override
		public String toString() {
			return stringValue;
		}
	}
}
