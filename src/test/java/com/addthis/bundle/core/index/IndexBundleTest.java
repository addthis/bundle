package com.addthis.bundle.core.index;

import java.util.Iterator;

import com.addthis.bundle.core.BundleField;

import com.google.common.collect.Iterators;

import org.junit.Assert;
import org.junit.Test;

public class IndexBundleTest {

    @Test
    public void iterateCorrectly() {
        IndexBundleFormat format = new IndexBundleFormat(5);
        IndexBundle bundle = new IndexBundle(format);

        Assert.assertEquals("Index Bundles should iterate over only columns set for that bundle",
                0, Iterators.size(bundle.iterator()));

        bundle.setValue(0, "hey");
        bundle.setValue(1, "thre");
        bundle.setValue(1, "there");

        Assert.assertEquals("Index Bundle should iterate over only columns set for that bundle",
                2, Iterators.size(bundle.iterator()));

        bundle.removeValue(new IndexBundleField(0));

        Assert.assertEquals("Index Bundle should iterate over only columns set for that bundle",
                1, Iterators.size(bundle.iterator()));

        Iterator<BundleField> iterator = bundle.iterator();

        Assert.assertTrue("Iterator hasNext() should not advance iterator", iterator.hasNext());
        Assert.assertTrue("Iterator hasNext() should not advance iterator", iterator.hasNext());
        Assert.assertNotNull(iterator.next());
        Assert.assertFalse("Iterator hasNext() should not advance iterator", iterator.hasNext());
        Assert.assertFalse("Iterator hasNext() should not advance iterator", iterator.hasNext());

    }

    @Test
    public void countFields() {
        IndexBundleFormat format = new IndexBundleFormat(5);
        IndexBundle bundle = new IndexBundle(format);

        bundle.setValue(0, "hey");
        bundle.setValue(1, "thre");
        bundle.setValue(1, "there");

        Assert.assertEquals("Index Bundle count should be the number of set fields",
                2, bundle.getCount());

        bundle.removeValue(new IndexBundleField(0));

        Assert.assertEquals("Index Bundle count should be the number of set fields",
                1, bundle.getCount());

        bundle.setValue(0, "hiya");

        Assert.assertEquals("Index Bundle count should be the number of set fields",
                2, bundle.getCount());

        bundle.setValue(3, "friend");

        Assert.assertEquals("Index Bundle count should be the number of set fields",
                3, bundle.getCount());
    }

}
