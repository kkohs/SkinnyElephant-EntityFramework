/*
 * Copyright 2012  Kristaps Kohs<kristaps.kohs@gmail.com>
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package org.skinnyelephant.framework.util;

/** Entity ID generator interface. */
public interface EntityIdGenerator {
    /**
     * Method for getting unique id.
     *
     * @return id.
     */
    public long getId();

    /**
     * Method for removing id.
     *
     * @param id id to be removed.
     */
    public void removeId(long id);

    /** Method for resetting generator. */
    public void reset();
}
