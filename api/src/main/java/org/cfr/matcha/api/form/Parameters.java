/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.cfr.matcha.api.form;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Modifiable list of entries with many helper methods. Note that this class
 * uses the Parameter class as the template type. This allows you to use an
 * instance of this class as any other java.util.List, in particular all the
 * helper methods in java.util.Collections.
 * 
 * @param <E> The contained type
 * @see java.util.Collections
 * @see java.util.List
 */
public abstract class Parameters<E extends Parameter> extends ArrayList<E> {

    /**
     * 
     */
    private static final long serialVersionUID = 2230241905213874484L;

    /**
     * A marker for empty values to differentiate from non existing values
     * (null).
     */
    public static final Object EMPTY_VALUE = new Object();

    /**
     * Returns an unmodifiable view of the specified series. Attempts to call a
     * modification method will throw an UnsupportedOperationException.
     * 
     * @param series The series for which an unmodifiable view should be returned.
     * @return The unmodifiable view of the specified series.
     */
    @SuppressWarnings("unchecked")
    public static Parameters<? extends Parameter> unmodifiableSeries(final Parameters<? extends Parameter> series) {
        return new Form((List<Parameter>) series);
    }

    /**
     * Constructor.
     */
    public Parameters() {
        super();
    }

    /**
     * Constructor.
     * 
     * @param initialCapacity The initial list capacity.
     */
    public Parameters(final int initialCapacity) {
        super(initialCapacity);
    }

    /**
     * Constructor.
     * 
     * @param list The list.
     */
    public Parameters(final List<E> list) {
        super(list);
    }

    /**
     * Creates then adds a parameter at the end of the list.
     * 
     * @param name The parameter name.
     * @param value The parameter value.
     * @return True (as per the general contract of the Collection.add method).
     */
    public boolean add(final String name, final String value) {
        return add(createParameter(name, value));
    }

    /**
     * Copies the parameters whose name is a key in the given map.<br>
     * If a matching parameter is found, its value is put in the map.<br>
     * If multiple values are found, a list is created and set in the map.
     * 
     * @param params The map controlling the copy.
     */
    @SuppressWarnings("unchecked")
    public void copyTo(final Map<String, Object> params) {
        Parameter param;
        Object currentValue = null;
        for (final Iterator<E> iter = iterator(); iter.hasNext();) {
            param = iter.next();

            if (params.containsKey(param.getName())) {
                currentValue = params.get(param.getName());

                if (currentValue != null) {
                    List<Object> values = null;

                    if (currentValue instanceof List) {
                        // Multiple values already found for this entry
                        values = (List<Object>) currentValue;
                    } else {
                        // Second value found for this entry
                        // Create a list of values
                        values = new ArrayList<Object>();
                        values.add(currentValue);
                        params.put(param.getName(), values);
                    }

                    if (param.getValue() == null) {
                        values.add(Parameters.EMPTY_VALUE);
                    } else {
                        values.add(param.getValue());
                    }
                } else {
                    if (param.getValue() == null) {
                        params.put(param.getName(), Parameters.EMPTY_VALUE);
                    } else {
                        params.put(param.getName(), param.getValue());
                    }
                }
            }
        }
    }

    /**
     * Creates a new entry.
     * 
     * @param name The name of the entry.
     * @param value The value of the entry.
     * @return A new entry.
     */
    public abstract E createParameter(String name, String value);

    /**
     * Creates a new series.
     * 
     * @param delegate
     *            Optional delegate series.
     * @return A new series.
     */
    public abstract Parameters<E> createSeries(List<E> delegate);

    /**
     * Tests the equality of two string, potentially null, which a case
     * sensitivity flag.
     * 
     * @param value1 The first value.
     * @param value2 The second value.
     * @param ignoreCase
     *            Indicates if the test should be case insensitive.
     * @return True if both values are equal.
     */
    private boolean equals(final String value1, final String value2, final boolean ignoreCase) {
        boolean result = value1 == value2;

        if (!result) {
            if (value1 != null
                    && value2 != null) {
                if (ignoreCase) {
                    result = value1.equalsIgnoreCase(value2);
                } else {
                    result = value1.equals(value2);
                }
            }
        }

        return result;
    }

    /**
     * Returns the first parameter found with the given name.
     * 
     * @param name The parameter name (case sensitive).
     * @return The first parameter found with the given name.
     */
    public E getFirst(final String name) {
        return getFirst(name, false);
    }

    /**
     * Returns the first parameter found with the given name.
     * 
     * @param name The parameter name.
     * @param ignoreCase Indicates if the name comparison is case insensitive.
     * @return The first parameter found with the given name.
     */
    public E getFirst(final String name, final boolean ignoreCase) {
        for (final E param : this) {
            if (equals(param.getName(), name, ignoreCase)) {
                return param;
            }
        }

        return null;
    }

    /**
     * Returns the value of the first parameter found with the given name.
     * 
     * @param name The parameter name (case sensitive).
     * @return The value of the first parameter found with the given name.
     */
    public String getFirstValue(final String name) {
        return getFirstValue(name, false);
    }

    /**
     * Returns the value of the first parameter found with the given name.
     * 
     * @param name The parameter name.
     * @param ignoreCase Indicates if the name comparison is case sensitive.
     * @return The value of the first parameter found with the given name.
     */
    public String getFirstValue(final String name, final boolean ignoreCase) {
        return getFirstValue(name, ignoreCase, null);
    }

    /**
     * Returns the value of the first parameter found with the given name.
     * 
     * @param name The parameter name.
     * @param ignoreCase Indicates if the name comparison is case sensitive.
     * @param defaultValue The default value to return if no matching parameter found or 
     * if the parameter has a null value.
     * @return The value of the first parameter found with the given name or the
     *         default value.
     */
    public String getFirstValue(final String name, final boolean ignoreCase, final String defaultValue) {
        String result = defaultValue;
        final Parameter param = getFirst(name, ignoreCase);

        if (param != null
                && param.getValue() != null) {
            result = param.getValue();
        }

        return result;
    }

    /**
     * Returns the value of the first parameter found with the given name.
     * 
     * @param name The parameter name (case sensitive).
     * @param defaultValue The default value to return if no matching parameter found or
     * if the parameter has a null value.
     * @return The value of the first parameter found with the given name or the
     *         default value.
     */
    public String getFirstValue(final String name, final String defaultValue) {
        return getFirstValue(name, false, defaultValue);
    }

    /**
     * Returns the set of parameter names (case sensitive).
     * 
     * @return The set of parameter names.
     */
    public Set<String> getNames() {
        final Set<String> result = new HashSet<String>();

        for (final Parameter param : this) {
            result.add(param.getName());
        }

        return result;
    }

    /**
     * Returns the values of the parameters with a given name. If multiple
     * parameters with the same name are found, all values are concatenated and
     * separated by a comma (like for HTTP message headers).
     * 
     * @param name The parameter name (case insensitive).
     * @return The values of the parameters with a given name.
     */
    public String getValues(final String name) {
        return getValues(name, ",", true);
    }

    /**
     * Returns the parameter values with a given name. If multiple parameters
     * with the same name are found, all values are concatenated and separated
     * by the given separator.
     * 
     * @param name The parameter name.
     * @param separator The separator character.
     * @param ignoreCase Indicates if the name comparison is case sensitive.
     * @return The sequence of values.
     */
    public String getValues(final String name, final String separator, final boolean ignoreCase) {
        String result = null;
        StringBuilder sb = null;

        for (final E param : this) {
            if (ignoreCase
                    && param.getName().equalsIgnoreCase(name) || param.getName().equals(name)) {
                if (sb == null) {
                    if (result == null) {
                        result = param.getValue();
                    } else {
                        sb = new StringBuilder();
                        sb.append(result).append(separator).append(param.getValue());
                    }
                } else {
                    sb.append(separator).append(param.getValue());
                }
            }
        }

        if (sb != null) {
            result = sb.toString();
        }

        return result;
    }

    /**
     * Returns an array of all the values associated to the given parameter
     * name.
     * 
     * @param name The parameter name to match.
     * @return The array of values.
     */
    public String[] getValuesArray(final String name) {
        return getValuesArray(name, false);
    }

    /**
     * Returns an array of all the values associated to the given parameter
     * name.
     * 
     * @param name
     *            The parameter name to match.
     * @param ignoreCase
     *            Indicates if the name comparison is case sensitive.
     * @return The array of values.
     */
    public String[] getValuesArray(final String name, final boolean ignoreCase) {
        List<E> params = subList(name, ignoreCase);
        String[] result = new String[params.size()];

        for (int i = 0; i < params.size(); i++) {
            result[i] = params.get(i).getValue();
        }

        return result;
    }

    /**
     * Returns a map of name, value pairs. The order of the map keys is
     * respected based on the series order. When a name has multiple values,
     * only the first one is put in the map.
     * 
     * @return The map of name, value pairs.
     */
    public Map<String, String> getValuesMap() {
        final Map<String, String> result = new LinkedHashMap<String, String>();

        for (final Parameter param : this) {
            if (!result.containsKey(param.getName())) {
                result.put(param.getName(), param.getValue());
            }
        }

        return result;
    }

    /**
     * Removes all the parameters with a given name.
     * 
     * @param name
     *            The parameter name (case sensitive).
     * @return True if the list changed.
     */
    public boolean removeAll(final String name) {
        return removeAll(name, false);
    }

    /**
     * Removes all the parameters with a given name.
     * 
     * @param name
     *            The parameter name.
     * @param ignoreCase
     *            Indicates if the name comparison is case insensitive.
     * @return True if the list changed.
     */
    public boolean removeAll(final String name, final boolean ignoreCase) {
        boolean changed = false;
        Parameter param = null;

        for (final Iterator<E> iter = iterator(); iter.hasNext();) {
            param = iter.next();
            if (equals(param.getName(), name, ignoreCase)) {
                iter.remove();
                changed = true;
            }
        }

        return changed;
    }

    /**
     * Removes from this list the first entry whose name equals the specified
     * name ignoring the case.
     * 
     * @param name The name of the entries to be removed (case sensitive).
     * @return false if no entry has been removed, true otherwise.
     */
    public boolean removeFirst(final String name) {
        return removeFirst(name, false);
    }

    /**
     * Removes from this list the first entry whose name equals the specified
     * name ignoring the case or not.
     * 
     * @param name The name of the entries to be removed.
     * @param ignoreCase Indicates if the name comparison is case insensitive.
     * @return false if no entry has been removed, true otherwise.
     */
    public boolean removeFirst(final String name, final boolean ignoreCase) {
        boolean changed = false;
        Parameter param = null;

        for (final Iterator<E> iter = iterator(); iter.hasNext()
                && !changed;) {
            param = iter.next();
            if (equals(param.getName(), name, ignoreCase)) {
                iter.remove();
                changed = true;
            }
        }

        return changed;
    }

    /**
     * Replaces the value of the first parameter with the given name and removes
     * all other parameters with the same name. The name matching is case
     * sensitive.
     * 
     * @param name The parameter name.
     * @param value The value to set.
     * @return The parameter set or added.
     */
    public E set(final String name, final String value) {
        return set(name, value, false);
    }

    /**
     * Replaces the value of the first parameter with the given name and removes
     * all other parameters with the same name.
     * 
     * @param name The parameter name.
     * @param value The value to set.
     * @param ignoreCase Indicates if the name comparison is case insensitive.
     * @return The parameter set or added.
     */
    public E set(final String name, final String value, final boolean ignoreCase) {
        E result = null;
        E param = null;
        boolean found = false;

        for (final Iterator<E> iter = iterator(); iter.hasNext();) {
            param = iter.next();

            if (equals(param.getName(), name, ignoreCase)) {
                if (found) {
                    // Remove other entries with the same name
                    iter.remove();
                } else {
                    // Change the value of the first matching entry
                    found = true;
                    param.setValue(value);
                    result = param;
                }
            }
        }

        if (!found) {
            add(name, value);
        }

        return result;
    }

    /**
     * Returns a view of the portion of this list between the specified
     * fromIndex, inclusive, and toIndex, exclusive.
     * 
     * @param fromIndex The start position.
     * @param toIndex The end position (exclusive).
     * @return The sub-list.
     */
    @Override
    public Parameters<E> subList(final int fromIndex, final int toIndex) {
        return createSeries(subList(fromIndex, toIndex));
    }

    /**
     * Returns a list of all the values associated to the parameter name.
     * 
     * @param name The parameter name (case sensitive).
     * @return The list of values.
     */
    public Parameters<E> subList(final String name) {
        return subList(name, false);
    }

    /**
     * Returns a list of all the values associated to the parameter name.
     * 
     * @param name The parameter name.
     * @param ignoreCase Indicates if the name comparison is case insensitive.
     * @return The list of values.
     */
    public Parameters<E> subList(final String name, final boolean ignoreCase) {
        final Parameters<E> result = createSeries(null);

        for (final E param : this) {
            if (equals(param.getName(), name, ignoreCase)) {
                result.add(param);
            }
        }

        return result;
    }

}
