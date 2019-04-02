/*
 * Copyright 2016 peter.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package dev.area51.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @param <C>
 * @param <T>
 *
 * @author peter
 */
public interface CollectionBuilder<C extends Collection<T>, T>
{

    CollectionBuilder add( T v );

    C build( );

    static <T> CollectionBuilder<Collection<T>, T> collection( )
    {
        List<T> l = new ArrayList<>( );
        return new CollectionBuilder<Collection<T>, T>( )
        {
            @Override
            public CollectionBuilder add( T v )
            {
                l.add( v );
                return this;
            }

            @Override
            public List<T> build( )
            {
                return l;
            }
        };
    }

    static <T> CollectionBuilder<List<T>, T> list( )
    {
        List<T> l = new ArrayList<>( );
        return new CollectionBuilder<List<T>, T>( )
        {
            @Override
            public CollectionBuilder add( T v )
            {
                l.add( v );
                return this;
            }

            @Override
            public List<T> build( )
            {
                return l;
            }
        };
    }

    static <T> CollectionBuilder<Set<T>, T> set( )
    {
        Set<T> s = new HashSet<>( );
        return new CollectionBuilder<Set<T>, T>( )
        {
            @Override
            public CollectionBuilder add( T v )
            {
                s.add( v );
                return this;
            }

            @Override
            public Set<T> build( )
            {
                return s;
            }
        };

    }
}
