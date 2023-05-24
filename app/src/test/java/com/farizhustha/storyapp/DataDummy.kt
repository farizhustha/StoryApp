package com.farizhustha.storyapp

import com.farizhustha.storyapp.service.remote.response.Story

object DataDummy {
    fun generateDummyStoryResponse(): List<Story> {
        val items: MutableList<Story> = arrayListOf()
        for (i in 0..100) {
            val story = Story(
                i.toString(),
                "https://picsum.photos/id/$i/400",
                "2023-05-24T04:04:22.061Z",
                "fariz$i",
                "description$i"
            )
            items.add(story)
        }
        return items
    }
}