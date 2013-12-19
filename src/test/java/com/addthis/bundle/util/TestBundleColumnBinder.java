package com.addthis.bundle.util;

import com.addthis.bundle.core.Bundle;
import com.addthis.bundle.core.list.ListBundle;
import com.addthis.bundle.core.list.ListBundleFormat;
import com.addthis.bundle.value.ValueFactory;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestBundleColumnBinder {

    @Test
    public void testBundleColumnBinder1() {
        Bundle b1 = new ListBundle();
        BundleColumnBinder binder = new BundleColumnBinder(b1);
        b1.setValue(b1.getFormat().getField("abc"), ValueFactory.create("def"));
        b1.setValue(b1.getFormat().getField("ghi"), ValueFactory.create("jkl"));
        b1.setValue(b1.getFormat().getField("mno"), ValueFactory.create("pqr"));
        assertEquals(binder.getColumn(b1, 1), ValueFactory.create("jkl"));
        assertEquals(binder.getColumn(b1, 2), ValueFactory.create("pqr"));
        assertEquals(binder.getColumn(b1, 0), ValueFactory.create("def"));
        assertEquals(binder.getColumn(b1, 3), null);
        Bundle b2 = new ListBundle((ListBundleFormat) b1.getFormat());
        b2.setValue(b2.getFormat().getField("abc"), ValueFactory.create("def"));
        b2.setValue(b2.getFormat().getField("ghi"), ValueFactory.create("jkl"));
        b2.setValue(b2.getFormat().getField("mno"), ValueFactory.create("pqr"));
        assertEquals(binder.getColumn(b2, 1), ValueFactory.create("jkl"));
        assertEquals(binder.getColumn(b2, 2), ValueFactory.create("pqr"));
        assertEquals(binder.getColumn(b2, 0), ValueFactory.create("def"));
        assertEquals(binder.getColumn(b2, 3), null);
    }

    @Test
    public void testBundleColumnBinder2() {
        Bundle b1 = new ListBundle();
        BundleColumnBinder binder = new BundleColumnBinder(b1);
        binder.appendColumn(b1, ValueFactory.create("abc"));
        binder.appendColumn(b1, ValueFactory.create("def"));
        binder.appendColumn(b1, ValueFactory.create("ghi"));
        assertEquals(binder.getColumn(b1, 1), ValueFactory.create("def"));
        assertEquals(binder.getColumn(b1, 2), ValueFactory.create("ghi"));
        assertEquals(binder.getColumn(b1, 0), ValueFactory.create("abc"));
        assertEquals(binder.getColumn(b1, 3), null);
        Bundle b2 = new ListBundle((ListBundleFormat) b1.getFormat());
        binder.appendColumn(b2, ValueFactory.create("abc"));
        binder.appendColumn(b2, ValueFactory.create("def"));
        binder.appendColumn(b2, ValueFactory.create("ghi"));
        binder.appendColumn(b2, ValueFactory.create("jkl"));
        assertEquals(binder.getColumn(b2, 1), ValueFactory.create("def"));
        assertEquals(binder.getColumn(b2, 2), ValueFactory.create("ghi"));
        assertEquals(binder.getColumn(b2, 0), ValueFactory.create("abc"));
        assertEquals(binder.getColumn(b2, 3), ValueFactory.create("jkl"));
        assertEquals(binder.getColumn(b2, 4), null);
    }
}
