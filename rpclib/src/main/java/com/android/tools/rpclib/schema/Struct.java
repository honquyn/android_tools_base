/*
 * Copyright (C) 2015 The Android Open Source Project
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
package com.android.tools.rpclib.schema;

import com.android.tools.rpclib.binary.*;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public final class Struct extends Type {
    Entity mEntity;

    public Struct(Entity entity) {
        mEntity = entity;
    }

    public Struct(@NotNull Decoder d, boolean compact) throws IOException {
        mEntity = d.entity(compact);
        if (!compact) {
            d.string();
        }
    }

    @Override
    public boolean equals(Object o) {
        // struct types are all equivalent for the purposes of signature matching
        return (o instanceof Struct);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @NotNull
    @Override
    public String getName() {
        return mEntity.getName();
    }

    public Entity getEntity() {
        return mEntity;
    }

    @Override
    public void encodeValue(@NotNull Encoder e, Object value) throws IOException {
        assert (value instanceof BinaryObject);
        e.value((BinaryObject) value);
    }

    @Override
    public Object decodeValue(@NotNull Decoder d) throws IOException {
        BinaryClass klass = Namespace.lookup(mEntity);
        if (klass == null) {
            throw new IOException("Unknown type: " + mEntity);
        }
        BinaryObject obj = klass.create();
        klass.decode(d, obj);
        return obj;
    }

    @Override
    public void encode(@NotNull Encoder e, boolean compact) throws IOException {
        TypeTag.structTag().encode(e);
        e.entity(mEntity, compact);
        if (!compact) {
            e.string("");
        }
    }
}
