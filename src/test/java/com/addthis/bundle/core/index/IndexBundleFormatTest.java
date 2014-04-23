package com.addthis.bundle.core.index;

import com.google.common.collect.Iterators;

import org.junit.Assert;
import org.junit.Test;

public class IndexBundleFormatTest {

    @Test
    public void iterateCorrectly() {
        IndexBundleFormat format = new IndexBundleFormat(5);
        Assert.assertEquals("Index Format should iterate over each column",
                5, Iterators.size(format.iterator()));

        IndexBundleFormat formatStrings = new IndexBundleFormat("one", "two", "merge");
        Assert.assertEquals("Index Format should iterate over each column",
                3, Iterators.size(formatStrings.iterator()));
    }

}
