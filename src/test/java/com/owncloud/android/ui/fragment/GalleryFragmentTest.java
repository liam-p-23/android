package com.owncloud.android.ui.fragment;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class GalleryFragmentTest {

    // CS427 Issue link: https://github.com/nextcloud/android/issues/8766
    /**
     * Test to check that the GallerySearchTask constructor correctly handles both cases
     * of the onThisDayTimestamp (i.e. input of 0 => normal gallery fragment and input
     * of timestamp != 0 => 'On this day' gallery fragment).
     */
    @Test
    public void galleryTypeTest() {
        // Test for creating 'On this day' type of GalleryFragment
        GalleryFragment galleryFragmentOnThisDay = new GalleryFragment(true, System.currentTimeMillis());
        assertTrue(galleryFragmentOnThisDay.isOnThisDayGallery());

        // Test for creating normal type of GalleryFragment
        GalleryFragment galleryFragmentNormal = new GalleryFragment(true, 0);
        assertFalse(galleryFragmentNormal.isOnThisDayGallery());
    }
}
