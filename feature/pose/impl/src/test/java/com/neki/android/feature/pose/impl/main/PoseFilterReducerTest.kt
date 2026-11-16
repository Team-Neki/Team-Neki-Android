package com.neki.android.feature.pose.impl.main

import com.neki.android.core.model.PeopleCount
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class PoseFilterReducerTest {

    @Test
    fun `관계 태그를 선택하면 인원수가 2인으로 선택된다`() {
        val state = PoseState().toggleRelationTagFilter(PoseRelationTag.FRIEND)

        assertEquals(PoseRelationTag.FRIEND, state.selectedRelationTag)
        assertEquals(PeopleCount.TWO, state.selectedPeopleCount)
    }

    @Test
    fun `3인 선택 상태에서 커플을 선택하면 2인으로 바뀐다`() {
        val state = PoseState()
            .togglePeopleCountFilter(PeopleCount.THREE)
            .toggleRelationTagFilter(PoseRelationTag.COUPLE)

        assertEquals(PoseRelationTag.COUPLE, state.selectedRelationTag)
        assertEquals(PeopleCount.TWO, state.selectedPeopleCount)
    }

    @Test
    fun `관계 태그를 해제해도 2인 선택은 유지된다`() {
        val state = PoseState()
            .toggleRelationTagFilter(PoseRelationTag.CRUSH)
            .toggleRelationTagFilter(PoseRelationTag.CRUSH)

        assertNull(state.selectedRelationTag)
        assertEquals(PeopleCount.TWO, state.selectedPeopleCount)
    }

    @Test
    fun `다른 관계 태그를 선택하면 관계 태그만 바뀌고 2인은 유지된다`() {
        val state = PoseState()
            .toggleRelationTagFilter(PoseRelationTag.FRIEND)
            .toggleRelationTagFilter(PoseRelationTag.COUPLE)

        assertEquals(PoseRelationTag.COUPLE, state.selectedRelationTag)
        assertEquals(PeopleCount.TWO, state.selectedPeopleCount)
    }

    @Test
    fun `관계 태그 선택 상태에서 2인 외 인원수를 선택하면 관계 태그가 해제된다`() {
        val state = PoseState()
            .toggleRelationTagFilter(PoseRelationTag.COUPLE)
            .togglePeopleCountFilter(PeopleCount.THREE)

        assertNull(state.selectedRelationTag)
        assertEquals(PeopleCount.THREE, state.selectedPeopleCount)
    }

    @Test
    fun `관계 태그 선택 상태에서 2인을 다시 눌러 해제하면 관계 태그도 해제된다`() {
        val state = PoseState()
            .toggleRelationTagFilter(PoseRelationTag.FRIEND)
            .togglePeopleCountFilter(PeopleCount.TWO)

        assertNull(state.selectedRelationTag)
        assertNull(state.selectedPeopleCount)
    }

    @Test
    fun `북마크 선택 상태에서 관계 태그를 선택하면 북마크가 해제된다`() {
        val state = PoseState()
            .toggleBookmarkFilter()
            .toggleRelationTagFilter(PoseRelationTag.FRIEND)

        assertFalse(state.isShowBookmarkedPose)
        assertEquals(PoseRelationTag.FRIEND, state.selectedRelationTag)
        assertEquals(PeopleCount.TWO, state.selectedPeopleCount)
    }

    @Test
    fun `관계 태그 선택 상태에서 북마크를 선택하면 인원수와 관계 태그가 해제된다`() {
        val state = PoseState()
            .toggleRelationTagFilter(PoseRelationTag.FRIEND)
            .toggleBookmarkFilter()

        assertTrue(state.isShowBookmarkedPose)
        assertNull(state.selectedRelationTag)
        assertNull(state.selectedPeopleCount)
    }
}
