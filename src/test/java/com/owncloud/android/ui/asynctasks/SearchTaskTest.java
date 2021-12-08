package com.owncloud.android.ui.asynctasks;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class SearchTaskTest {

    // CS427 Issue link: https://github.com/nextcloud/android/issues/8766
    @Test
    public void gallerySearchConstructorTest() {
        // Test to check if instance is set to onThisDay task type
        GallerySearchTask gallerySearchTaskOnThisDay = new GallerySearchTask(15,
                                                                    null,
                                                                    null,
                                                                    null,
                                                                    null,
                                                                    System.currentTimeMillis());
        assertTrue(gallerySearchTaskOnThisDay.isOnThisDaySearchTask());

        // Test to check if instance is set to normal task type
        GallerySearchTask gallerySearchTaskNormal = new GallerySearchTask(15,
                                                                             null,
                                                                             null,
                                                                             null,
                                                                             null,
                                                                             0);
        assertFalse(gallerySearchTaskNormal.isOnThisDaySearchTask());
    }
}
