
package com.foxykeep.datadroidpoc.data.provider.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class ProviderCriteria {

    private static final String AND = " AND ";
    private static final String OR = " OR ";

    private static final int TYPE_EQ = 1;
    private static final int TYPE_NE = 2;
    private static final int TYPE_LT = 3;
    private static final int TYPE_LTOE = 4;
    private static final int TYPE_GT = 5;
    private static final int TYPE_GTOE = 6;
    private static final int TYPE_LIKE = 7;
    private static final int TYPE_IN = 8;

    private StringBuilder mWhereSb = new StringBuilder();
    private List<String> mWhereParamList = new ArrayList<String>();
    private boolean mIsWhereFirstElement = true;

    // True = AND, false = OR
    private Stack<Boolean> mOperandStack = new Stack<Boolean>();

    private StringBuilder mOrderSb = new StringBuilder();
    private boolean mIsOrderFirstElement = true;

    public ProviderCriteria() {
        mOperandStack.push(true);
    }

    public ProviderCriteria(final ColumnMetadata metadata, final int value) {
        this();
        addEq(metadata, value);
    }

    public ProviderCriteria(final ColumnMetadata metadata, final long value) {
        this();
        addEq(metadata, value);
    }

    public ProviderCriteria(final ColumnMetadata metadata, final String value) {
        this();
        addEq(metadata, value);
    }

    public ProviderCriteria(final ColumnMetadata metadata, final boolean value) {
        this();
        addEq(metadata, value);
    }

    public ProviderCriteria addEq(final ColumnMetadata metadata, final int value) {
        addOperand();
        addCriteria(metadata.getName(), value, TYPE_EQ);
        return this;
    }

    public ProviderCriteria addEq(final ColumnMetadata metadata, final long value) {
        addOperand();
        addCriteria(metadata.getName(), value, TYPE_EQ);
        return this;
    }

    public ProviderCriteria addEq(final ColumnMetadata metadata, final String value) {
        addOperand();
        addCriteria(metadata.getName(), value, TYPE_EQ);
        return this;
    }

    public ProviderCriteria addEq(final ColumnMetadata metadata, final boolean value) {
        addOperand();
        addCriteria(metadata.getName(), value ? 1 : 0, TYPE_EQ);
        return this;
    }

    /**
     * @param metadata
     * @param idList comma separated String of the values
     * @return
     */
    public <T extends Object> ProviderCriteria addInList(final ColumnMetadata metadata,
            final List<T> objectList) {
        addOperand();
        final StringBuilder sb = new StringBuilder();
        boolean isFirst = true;
        for (T object : objectList) {
            if (isFirst) {
                isFirst = false;
            } else {
                sb.append(",");
            }
            sb.append(object);
        }
        addCriteria(metadata.getName(), sb.toString(), TYPE_IN);
        return this;
    }

    public ProviderCriteria addNe(final ColumnMetadata metadata, final int value) {
        addOperand();
        addCriteria(metadata.getName(), value, TYPE_NE);
        return this;
    }

    public ProviderCriteria addNe(final ColumnMetadata metadata, final String value) {
        addOperand();
        addCriteria(metadata.getName(), value, TYPE_NE);
        return this;
    }

    public ProviderCriteria addLike(final ColumnMetadata metadata, final String value) {
        addOperand();
        addCriteria(metadata.getName(), value, TYPE_LIKE);
        return this;
    }

    public ProviderCriteria addLt(final ColumnMetadata metadata, final int value,
            final boolean orEqual) {
        addOperand();
        addCriteria(metadata.getName(), value, orEqual ? TYPE_LTOE : TYPE_LT);
        return this;
    }

    public ProviderCriteria addGt(final ColumnMetadata metadata, final int value,
            final boolean orEqual) {
        addOperand();
        addCriteria(metadata.getName(), value, orEqual ? TYPE_GTOE : TYPE_GT);
        return this;
    }

    public ProviderCriteria startSubCriteria() {
        addOperand();
        mWhereSb.append("(");
        mOperandStack.push(true);
        mIsWhereFirstElement = true;
        return this;
    }

    public ProviderCriteria endSubCriteria() {
        mWhereSb.append(")");
        mOperandStack.pop();
        mIsWhereFirstElement = false;
        return this;
    }

    public ProviderCriteria startOr() {
        mOperandStack.pop();
        mOperandStack.push(false);
        return this;
    }

    public ProviderCriteria endOr() {
        mOperandStack.pop();
        mOperandStack.push(true);
        return this;
    }

    public ProviderCriteria addSortOrder(final ColumnMetadata metadata, final boolean isAscendant) {
        if (mIsOrderFirstElement) {
            mIsOrderFirstElement = false;
        } else {
            mOrderSb.append(",");
        }
        mOrderSb.append(metadata.getName()).append(isAscendant ? " ASC" : " DESC");
        return this;
    }

    public String getWhereClause() {
        return mWhereSb.length() == 0 ? null : mWhereSb.toString();
    }

    public String[] getWhereParams() {
        final int paramCount = mWhereParamList.size();
        final String[] paramArray = new String[paramCount];
        for (int i = 0; i < paramCount; i++) {
            paramArray[i] = mWhereParamList.get(i);
        }
        return paramArray;
    }

    public String getOrderClause() {
        return mOrderSb.length() == 0 ? null : mOrderSb.toString();
    }

    private void addCriteria(final String column, final Object value, final int type) {
        mWhereSb.append(column);
        switch (type) {
            case TYPE_LIKE:
                mWhereSb.append(" like ?");
                break;
            case TYPE_IN:
                mWhereSb.append(" in (?)");
                break;
            case TYPE_EQ:
                mWhereSb.append(" = ?");
                break;
            case TYPE_NE:
                mWhereSb.append(" != ?");
                break;
            case TYPE_LT:
                mWhereSb.append(" < ?");
                break;
            case TYPE_LTOE:
                mWhereSb.append(" <= ?");
                break;
            case TYPE_GT:
                mWhereSb.append(" > ?");
                break;
            case TYPE_GTOE:
                mWhereSb.append(" >= ?");
                break;
        }
        mWhereParamList.add(String.valueOf(value));
    }

    private void addOperand() {
        if (mIsWhereFirstElement) {
            mIsWhereFirstElement = false;
        } else {
            mWhereSb.append(mOperandStack.peek() ? AND : OR);
        }
    }
}
