package com.neki.android.feature.pose.impl.main

import com.neki.android.core.model.PeopleCount
import com.neki.android.core.model.PoseRelation
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class PoseFilterReducerTest {

    @Test
    fun `관계 태그를 선택하면 인원수가 2인으로 선택된다`() {
        val state = PoseState().toggleRelationTagFilter(PoseRelation.FRIEND)

        assertEquals(PoseRelation.FRIEND, state.selectedRelationTag)
        assertEquals(PeopleCount.TWO, state.selectedPeopleCount)
    }

    @Test
    fun `3인 선택 상태에서 커플을 선택하면 2인으로 바뀐다`() {
        val state = PoseState()
            .togglePeopleCountFilter(PeopleCount.THREE)
            .toggleRelationTagFilter(PoseRelation.COUPLE)

        assertEquals(PoseRelation.COUPLE, state.selectedRelationTag)
        assertEquals(PeopleCount.TWO, state.selectedPeopleCount)
    }

    @Test
    fun `관계 태그를 해제해도 2인 선택은 유지된다`() {
        val state = PoseState()
            .toggleRelationTagFilter(PoseRelation.SSUM)
            .toggleRelationTagFilter(PoseRelation.SSUM)

        assertNull(state.selectedRelationTag)
        assertEquals(PeopleCount.TWO, state.selectedPeopleCount)
    }

    @Test
    fun `다른 관계 태그를 선택하면 관계 태그만 바뀌고 2인은 유지된다`() {
        val state = PoseState()
            .toggleRelationTagFilter(PoseRelation.FRIEND)
            .toggleRelationTagFilter(PoseRelation.COUPLE)

        assertEquals(PoseRelation.COUPLE, state.selectedRelationTag)
        assertEquals(PeopleCount.TWO, state.selectedPeopleCount)
    }

    @Test
    fun `관계 태그 선택 상태에서 2인 외 인원수를 선택하면 관계 태그가 해제된다`() {
        val state = PoseState()
            .toggleRelationTagFilter(PoseRelation.COUPLE)
            .togglePeopleCountFilter(PeopleCount.THREE)

        assertNull(state.selectedRelationTag)
        assertEquals(PeopleCount.THREE, state.selectedPeopleCount)
    }

    @Test
    fun `관계 태그 선택 상태에서 2인을 다시 눌러 해제하면 관계 태그도 해제된다`() {
        val state = PoseState()
            .toggleRelationTagFilter(PoseRelation.FRIEND)
            .togglePeopleCountFilter(PeopleCount.TWO)

        assertNull(state.selectedRelationTag)
        assertNull(state.selectedPeopleCount)
    }

    @Test
    fun `북마크 선택 상태에서 관계 태그를 선택하면 북마크가 해제된다`() {
        val state = PoseState()
            .toggleBookmarkFilter()
            .toggleRelationTagFilter(PoseRelation.FRIEND)

        assertFalse(state.isShowBookmarkedPose)
        assertEquals(PoseRelation.FRIEND, state.selectedRelationTag)
        assertEquals(PeopleCount.TWO, state.selectedPeopleCount)
    }

    @Test
    fun `관계 태그 선택 상태에서 북마크를 선택하면 인원수와 관계 태그가 해제된다`() {
        val state = PoseState()
            .toggleRelationTagFilter(PoseRelation.FRIEND)
            .toggleBookmarkFilter()

        assertTrue(state.isShowBookmarkedPose)
        assertNull(state.selectedRelationTag)
        assertNull(state.selectedPeopleCount)
    }

    @Test
    fun `인원수 미선택과 2인일 때만 관계 태그를 노출한다`() {
        assertTrue(null.isRelationTagVisible())
        assertTrue(PeopleCount.TWO.isRelationTagVisible())
        assertFalse(PeopleCount.ONE.isRelationTagVisible())
        assertFalse(PeopleCount.THREE.isRelationTagVisible())
        assertFalse(PeopleCount.FOUR.isRelationTagVisible())
    }

    @Test
    fun `관계 태그를 선택하면 조회 조건에 2인과 관계 태그가 들어간다`() {
        val query = PoseState().toggleRelationTagFilter(PoseRelation.SSUM).toPoseListQuery()

        assertEquals(PoseListQuery(headCount = PeopleCount.TWO, relation = PoseRelation.SSUM), query)
    }

    @Test
    fun `2인에서 관계 태그만 바꿔도 조회 조건이 바뀐다`() {
        val friend = PoseState().toggleRelationTagFilter(PoseRelation.FRIEND)
        val couple = friend.toggleRelationTagFilter(PoseRelation.COUPLE)

        assertNotEquals(friend.toPoseListQuery(), couple.toPoseListQuery())
    }

    @Test
    fun `관계 태그를 해제하면 2인 전체 조회 조건이 된다`() {
        val query = PoseState()
            .toggleRelationTagFilter(PoseRelation.FRIEND)
            .toggleRelationTagFilter(PoseRelation.FRIEND)
            .toPoseListQuery()

        assertEquals(PoseListQuery(headCount = PeopleCount.TWO), query)
    }

    @Test
    fun `북마크를 켜면 인원수와 관계 태그 없이 북마크 조회 조건이 된다`() {
        val query = PoseState()
            .toggleRelationTagFilter(PoseRelation.COUPLE)
            .toggleBookmarkFilter()
            .toPoseListQuery()

        assertEquals(PoseListQuery(isBookmarkOnly = true), query)
    }

    @Test
    fun `조회와 무관한 상태만 바뀌면 조회 조건은 같다`() {
        val state = PoseState().toggleRelationTagFilter(PoseRelation.FRIEND)
        val changed = state.copy(totalPoseCount = 10, isShowPeopleCountBottomSheet = true)

        assertEquals(state.toPoseListQuery(), changed.toPoseListQuery())
    }
}
