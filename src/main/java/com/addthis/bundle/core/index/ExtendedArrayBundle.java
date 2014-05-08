package com.addthis.bundle.core.index;

import java.util.Iterator;

import com.addthis.bundle.value.ValueArray;
import com.addthis.bundle.value.ValueCustom;
import com.addthis.bundle.value.ValueFactory;
import com.addthis.bundle.value.ValueMap;
import com.addthis.bundle.value.ValueObject;
import com.addthis.bundle.value.ValueString;

import com.google.common.collect.Iterators;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class ExtendedArrayBundle extends ArrayBundle implements ExtIndexBundle {

    public ExtendedArrayBundle(ExtendedArrayFormat format) {
        super(format);
    }

    @Override
    public ValueString string(int index) {
        return value(index).asString();
    }

    @Override
    public void string(int index, ValueString string) {
        value(index, string);
    }

    @Override
    public ValueArray array(int index) {
        return value(index).asArray();
    }

    @Override
    public void array(int index, ValueArray array) {
        value(index, array);
    }

    @Override
    public ValueMap map(int index) {
        return value(index).asMap();
    }

    @Override
    public void map(int index, ValueMap map) {
        value(index, map);
    }

    @Override
    public ByteBuf buf(int index) {
        return Unpooled.wrappedBuffer(value(index).asBytes().getBytes());
    }

    @Override
    public void buf(int index, ByteBuf buf) {
        if (buf.hasArray()) {
            value(index, ValueFactory.create(buf.array()));
        } else {
            throw new UnsupportedOperationException("only heap bufs supported just now due to ValueBytes legacy");
        }
    }

    @Override
    public <T extends ValueCustom> T custom(int index) {
        return (T) value(index).asCustom();
    }

    @Override
    public void custom(int index, ValueCustom custom) {
        value(index, custom);
    }

    @Override
    public long integer(int index) {
        return value(index).asLong().getLong();
    }

    @Override
    public void integer(int index, long integer) {
        value(index, ValueFactory.create(integer));
    }

    @Override
    public double floating(int index) {
        return value(index).asDouble().getDouble();
    }

    @Override
    public void floating(int index, double floating) {
        value(index, ValueFactory.create(floating));
    }

    @Override
    public ExtIndexFormat format() {
        return (ExtIndexFormat) super.format();
    }

    @Override
    public Iterator<ValueObject> iterator() {
        return Iterators.forArray(valueArray);
    }
}
