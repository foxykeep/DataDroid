/**
 * 2012 Foxykeep (http://datadroid.foxykeep.com)
 * <p>
 * Licensed under the Beerware License : <br />
 * As long as you retain this notice you can do whatever you want with this stuff. If we meet some
 * day, and you think this stuff is worth it, you can buy me a beer in return
 */

package com.foxykeep.datadroid.requestmanager;

import com.foxykeep.datadroid.service.RequestService;
import com.foxykeep.datadroid.util.ObjectUtils;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Class used to store your request information : request type as well as parameters.
 *
 * @author Foxykeep
 */
public final class Request implements Parcelable {

    private static final int TYPE_BOOLEAN = 1;
    private static final int TYPE_BYTE = 2;
    private static final int TYPE_CHAR = 3;
    private static final int TYPE_SHORT = 4;
    private static final int TYPE_INT = 5;
    private static final int TYPE_LONG = 6;
    private static final int TYPE_FLOAT = 7;
    private static final int TYPE_DOUBLE = 8;
    private static final int TYPE_STRING = 9;
    private static final int TYPE_CHARSEQUENCE = 10;
    private static final int TYPE_PARCELABLE = 11;

    private int mRequestType = -1;
    private boolean mMemoryCacheDataEnabled = false;
    private final ArrayList<String> mParamList = new ArrayList<String>();
    private final ArrayList<Integer> mTypeList = new ArrayList<Integer>();
    private Bundle mBundle = new Bundle();

    /**
     * Constructor
     *
     * @param requestType The request type.
     */
    public Request(int requestType) {
        mRequestType = requestType;
    }

    /**
     * Return the request type.
     *
     * @return The request type.
     */
    public int getRequestType() {
        return mRequestType;
    }

    /**
     * Set whether the data returned from the {@link RequestService} must be cached in memory or
     * not.
     *
     * @param enabled Whether the data returned from the {@link RequestService} must be cached in
     *            memory or not.
     */
    public void setMemoryCacheEnabled(boolean enabled) {
        mMemoryCacheDataEnabled = enabled;
    }

    /**
     * Return whether the data returned from the {@link RequestService} must be cached in memory or
     * not.
     *
     * @return Whether the data returned from the {@link RequestService} must be cached in memory or
     *         not.
     */
    public boolean isMemoryCacheEnabled() {
        return mMemoryCacheDataEnabled;
    }

    /**
     * Add a boolean parameter to the request, replacing any existing value for the given name.
     *
     * @param name The parameter name.
     * @param value The parameter value.
     * @return This RequestData.
     */
    public Request put(String name, boolean value) {
        removeFromRequestData(name);
        mParamList.add(name);
        mTypeList.add(TYPE_BOOLEAN);
        mBundle.putBoolean(name, value);
        return this;
    }

    /**
     * Add a byte parameter to the request, replacing any existing value for the given name.
     *
     * @param name The parameter name.
     * @param value The parameter value.
     * @return This RequestData.
     */
    public Request put(String name, byte value) {
        removeFromRequestData(name);
        mParamList.add(name);
        mTypeList.add(TYPE_BYTE);
        mBundle.putByte(name, value);
        return this;
    }

    /**
     * Add a char parameter to the request, replacing any existing value for the given name.
     *
     * @param name The parameter name.
     * @param value The parameter value.
     * @return This RequestData.
     */
    public Request put(String name, char value) {
        removeFromRequestData(name);
        mParamList.add(name);
        mTypeList.add(TYPE_CHAR);
        mBundle.putChar(name, value);
        return this;
    }

    /**
     * Add a short parameter to the request, replacing any existing value for the given name.
     *
     * @param name The parameter name.
     * @param value The parameter value.
     * @return This RequestData.
     */
    public Request put(String name, short value) {
        removeFromRequestData(name);
        mParamList.add(name);
        mTypeList.add(TYPE_SHORT);
        mBundle.putShort(name, value);
        return this;
    }

    /**
     * Add a int parameter to the request, replacing any existing value for the given name.
     *
     * @param name The parameter name.
     * @param value The parameter value.
     * @return This RequestData.
     */
    public Request put(String name, int value) {
        removeFromRequestData(name);
        mParamList.add(name);
        mTypeList.add(TYPE_INT);
        mBundle.putInt(name, value);
        return this;
    }

    /**
     * Add a long parameter to the request, replacing any existing value for the given name.
     *
     * @param name The parameter name.
     * @param value The parameter value.
     * @return This RequestData.
     */
    public Request put(String name, long value) {
        removeFromRequestData(name);
        mParamList.add(name);
        mTypeList.add(TYPE_LONG);
        mBundle.putLong(name, value);
        return this;
    }

    /**
     * Add a float parameter to the request, replacing any existing value for the given name.
     *
     * @param name The parameter name.
     * @param value The parameter value.
     * @return This RequestData.
     */
    public Request put(String name, float value) {
        removeFromRequestData(name);
        mParamList.add(name);
        mTypeList.add(TYPE_FLOAT);
        mBundle.putFloat(name, value);
        return this;
    }

    /**
     * Add a double parameter to the request, replacing any existing value for the given name.
     *
     * @param name The parameter name.
     * @param value The parameter value.
     * @return This RequestData.
     */
    public Request put(String name, double value) {
        removeFromRequestData(name);
        mParamList.add(name);
        mTypeList.add(TYPE_DOUBLE);
        mBundle.putDouble(name, value);
        return this;
    }

    /**
     * Add a String parameter to the request, replacing any existing value for the given name.
     *
     * @param name The parameter name.
     * @param value The parameter value.
     * @return This RequestData.
     */
    public Request put(String name, String value) {
        removeFromRequestData(name);
        mParamList.add(name);
        mTypeList.add(TYPE_STRING);
        mBundle.putString(name, value);
        return this;
    }

    /**
     * Add a CharSequence parameter to the request, replacing any existing value for the given name.
     *
     * @param name The parameter name.
     * @param value The parameter value.
     * @return This RequestData.
     */
    public Request put(String name, CharSequence value) {
        removeFromRequestData(name);
        mParamList.add(name);
        mTypeList.add(TYPE_CHARSEQUENCE);
        mBundle.putCharSequence(name, value);
        return this;
    }

    /**
     * Add a Parcelable parameter to the request, replacing any existing value for the given name.
     *
     * @param name The parameter name.
     * @param value The parameter value.
     * @return This RequestData.
     */
    public Request put(String name, Parcelable value) {
        removeFromRequestData(name);
        mParamList.add(name);
        mTypeList.add(TYPE_PARCELABLE);
        mBundle.putParcelable(name, value);
        return this;
    }

    private void removeFromRequestData(String name) {
        if (mParamList.contains(name)) {
            final int index = mParamList.indexOf(name);
            mParamList.remove(index);
            mTypeList.remove(index);
            mBundle.remove(name);
        }
    }

    /**
     * Check whether the request has an existing value for the given name.
     *
     * @param name The parameter name.
     * @return Whether the request has an existing value for the given name.
     */
    public boolean contains(String name) {
        return mParamList.contains(name);
    }

    /**
     * Returns the value associated with the given name, or false if no mapping of the desired type
     * exists for the given name.
     *
     * @param name The parameter name.
     * @return A boolean value.
     */
    public boolean getBoolean(String name) {
        return mBundle.getBoolean(name);
    }

    /**
     * Returns the value associated with the given name transformed as "1" if true or "0" if false.
     * If no mapping of the desired type exists for the given name, "0" is returned.
     *
     * @param name The parameter name.
     * @return The int String representation of the boolean value.
     */
    public String getBooleanAsIntString(String name) {
        boolean value = getBoolean(name);
        return value ? "1" : "0";
    }

    /**
     * Returns the value associated with the given name transformed as a String (using
     * {@link String#valueOf(boolean)}), or "false" if no mapping of the desired type exists for the
     * given name.
     *
     * @param name The parameter name.
     * @return The String representation of the boolean value.
     */
    public String getBooleanAsString(String name) {
        boolean value = getBoolean(name);
        return String.valueOf(value);
    }

    /**
     * Returns the value associated with the given name, or (byte) 0 if no mapping of the desired
     * type exists for the given name.
     *
     * @param name The parameter name.
     * @return A byte value.
     */
    public byte getByte(String name) {
        return mBundle.getByte(name);
    }

    /**
     * Returns the value associated with the given name, or (char) 0 if no mapping of the desired
     * type exists for the given name.
     *
     * @param name The parameter name.
     * @return A char value.
     */
    public char getChar(String name) {
        return mBundle.getChar(name);
    }

    /**
     * Returns the value associated with the given name transformed as a String (using
     * {@link String#valueOf(char)}), or "0" if no mapping of the desired type exists for the given
     * name.
     *
     * @param name The parameter name.
     * @return The String representation of the boolean value.
     */
    public String getCharAsString(String name) {
        char value = getChar(name);
        return String.valueOf(value);
    }

    /**
     * Returns the value associated with the given name, or (short) 0 if no mapping of the desired
     * type exists for the given name.
     *
     * @param name The parameter name.
     * @return A short value.
     */
    public short getShort(String name) {
        return mBundle.getShort(name);
    }

    /**
     * Returns the value associated with the given name transformed as a String (using
     * {@link String#valueOf(int)}), or "0" if no mapping of the desired type exists for the given
     * name.
     *
     * @param name The parameter name.
     * @return The String representation of the boolean value.
     */
    public String getShortAsString(String name) {
        short value = getShort(name);
        return String.valueOf(value);
    }

    /**
     * Returns the value associated with the given name, or 0 if no mapping of the desired type
     * exists for the given name.
     *
     * @param name The parameter name.
     * @return An int value.
     */
    public int getInt(String name) {
        return mBundle.getInt(name);
    }

    /**
     * Returns the value associated with the given name transformed as a String (using
     * {@link String#valueOf(int)}), or "0" if no mapping of the desired type exists for the given
     * name.
     *
     * @param name The parameter name.
     * @return The String representation of the boolean value.
     */
    public String getIntAsString(String name) {
        int value = getInt(name);
        return String.valueOf(value);
    }

    /**
     * Returns the value associated with the given name, or 0L if no mapping of the desired type
     * exists for the given name.
     *
     * @param name The parameter name.
     * @return A long value.
     */
    public long getLong(String name) {
        return mBundle.getLong(name);
    }

    /**
     * Returns the value associated with the given name transformed as a String (using
     * {@link String#valueOf(long)}), or "0" if no mapping of the desired type exists for the given
     * name.
     *
     * @param name The parameter name.
     * @return The String representation of the boolean value.
     */
    public String getLongAsString(String name) {
        long value = getLong(name);
        return String.valueOf(value);
    }

    /**
     * Returns the value associated with the given name, or 0.0f if no mapping of the desired type
     * exists for the given name.
     *
     * @param name The parameter name.
     * @return A float value.
     */
    public float getFloat(String name) {
        return mBundle.getFloat(name);
    }

    /**
     * Returns the value associated with the given name transformed as a String (using
     * {@link String#valueOf(float)}), or "0" if no mapping of the desired type exists for the given
     * name.
     *
     * @param name The parameter name.
     * @return The String representation of the boolean value.
     */
    public String getFloatAsString(String name) {
        float value = getFloat(name);
        return String.valueOf(value);
    }

    /**
     * Returns the value associated with the given name, or 0.0 if no mapping of the desired type
     * exists for the given name.
     *
     * @param name The parameter name.
     * @return A double value.
     */
    public double getDouble(String name) {
        return mBundle.getDouble(name);
    }

    /**
     * Returns the value associated with the given name transformed as a String (using
     * {@link String#valueOf(double)}), or "0" if no mapping of the desired type exists for the
     * given name.
     *
     * @param name The parameter name.
     * @return The String representation of the boolean value.
     */
    public String getDoubleAsString(String name) {
        double value = getDouble(name);
        return String.valueOf(value);
    }

    /**
     * Returns the value associated with the given name, or null if no mapping of the desired type
     * exists for the given name.
     *
     * @param name The parameter name.
     * @return A String value.
     */
    public String getString(String name) {
        return mBundle.getString(name);
    }

    /**
     * Returns the value associated with the given name, or null if no mapping of the desired type
     * exists for the given name.
     *
     * @param name The parameter name.
     * @return A CharSequence value.
     */
    public CharSequence getCharSequence(String name) {
        return mBundle.getCharSequence(name);
    }

    /**
     * Returns the value associated with the given name, or null if no mapping of the desired type
     * exists for the given name.
     *
     * @param name The parameter name.
     * @return A Parcelable value.
     */
    public Parcelable getParcelable(String name) {
        return mBundle.getParcelable(name);
    }

    /**
     * Sets the ClassLoader to use by the underlying Bundle when getting Parcelable objects.
     *
     * @param classLoader The ClassLoader to use by the underlying Bundle when getting Parcelable
     *            objects.
     */
    public void setClassLoader(ClassLoader classLoader) {
        mBundle.setClassLoader(classLoader);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Request) {
            Request oParams = (Request) o;
            if (mRequestType != oParams.mRequestType) {
                return false;
            }

            if (mParamList.size() != oParams.mParamList.size()) {
                return false;
            }

            for (int i = 0, length = mParamList.size(); i < length; i++) {
                String param = mParamList.get(i);
                if (!oParams.mParamList.contains(param)) {
                    return false;
                }

                int type = mTypeList.get(i);
                if (oParams.mTypeList.get(i) != type) {
                    return false;
                }

                switch (mTypeList.get(i)) {
                    case TYPE_BOOLEAN:
                        if (mBundle.getBoolean(param) != oParams.mBundle.getBoolean(param)) {
                            return false;
                        }
                        break;
                    case TYPE_BYTE:
                        if (mBundle.getByte(param) != oParams.mBundle.getByte(param)) {
                            return false;
                        }
                        break;
                    case TYPE_CHAR:
                        if (mBundle.getChar(param) != oParams.mBundle.getChar(param)) {
                            return false;
                        }
                        break;
                    case TYPE_SHORT:
                        if (mBundle.getShort(param) != oParams.mBundle.getShort(param)) {
                            return false;
                        }
                        break;
                    case TYPE_INT:
                        if (mBundle.getInt(param) != oParams.mBundle.getInt(param)) {
                            return false;
                        }
                        break;
                    case TYPE_LONG:
                        if (mBundle.getLong(param) != oParams.mBundle.getLong(param)) {
                            return false;
                        }
                        break;
                    case TYPE_FLOAT:
                        if (mBundle.getFloat(param) != oParams.mBundle.getFloat(param)) {
                            return false;
                        }
                        break;
                    case TYPE_DOUBLE:
                        if (mBundle.getDouble(param) != oParams.mBundle.getDouble(param)) {
                            return false;
                        }
                        break;
                    case TYPE_STRING:
                        if (!ObjectUtils.safeEquals(mBundle.getString(param),
                                oParams.mBundle.getString(param))) {
                            return false;
                        }
                        break;
                    case TYPE_CHARSEQUENCE:
                        if (!ObjectUtils.safeEquals(mBundle.getCharSequence(param),
                                oParams.mBundle.getCharSequence(param))) {
                            return false;
                        }
                        break;
                    case TYPE_PARCELABLE:
                        if (!ObjectUtils.safeEquals(mBundle.getParcelable(param),
                                oParams.mBundle.getParcelable(param))) {
                            return false;
                        }
                        break;
                    default:
                        // We should never arrive here normally.
                        throw new IllegalArgumentException(
                                "The type of the field is not a valid one");
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        ArrayList<Object> objectList = new ArrayList<Object>();
        objectList.add(mRequestType);
        for (int i = 0, length = mParamList.size(); i < length; i++) {
            objectList.add(mBundle.get(mParamList.get(i)));
        }
        return objectList.hashCode();
    }

    // Parcelable management
    private Request(final Parcel in) {
        mRequestType = in.readInt();
        mMemoryCacheDataEnabled = in.readInt() == 1;
        in.readStringList(mParamList);
        for (int i = 0, n = in.readInt(); i < n; i++) {
            mTypeList.add(in.readInt());
        }
        mBundle = in.readBundle();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mRequestType);
        dest.writeInt(mMemoryCacheDataEnabled ? 1 : 0);
        dest.writeStringList(mParamList);
        dest.writeInt(mTypeList.size());
        for (int i = 0, length = mTypeList.size(); i < length; i++) {
            dest.writeInt(mTypeList.get(i));
        }
        dest.writeBundle(mBundle);
    }

    public static final Creator<Request> CREATOR = new Creator<Request>() {
        public Request createFromParcel(final Parcel in) {
            return new Request(in);
        }

        public Request[] newArray(final int size) {
            return new Request[size];
        }
    };
}
