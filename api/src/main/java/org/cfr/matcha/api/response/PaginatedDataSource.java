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
package org.cfr.matcha.api.response;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement()
public class PaginatedDataSource<T> extends DataSource<T> {

    private Collection<T> results = null;

    public PaginatedDataSource(List<T> data, int pageSize, int start, long totalCount) {
        super(data, pageSize, start, totalCount);
        this.results = null;
    }

    @Override
    public Collection<T> getData() {
        return getPagingData(this.getStart());
    }

    protected synchronized Collection<T> getPagingData(int start) {
        int length = list().size();

        if ((getPageSize() >= 0 || start >= 0) && length > 0) {
            if (results == null) {
                List<T> tmp = new ArrayList<T>(getPageSize());
                int end = start + getPageSize();

                if (end > length || end == 0) {
                    end = length;
                }

                for (int i = start; i < end; i++) {
                    tmp.add(list().get(i));
                }
                results = Collections.unmodifiableCollection(tmp);
            }

            return results;
        }

        return list();
    }
}
