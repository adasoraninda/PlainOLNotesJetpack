package com.adasoraninda.plainolnotes

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.adasoraninda.plainolnotes.data.AppDatabase
import com.adasoraninda.plainolnotes.data.NoteDao
import com.adasoraninda.plainolnotes.data.NoteEntity
import com.adasoraninda.plainolnotes.data.SampleDataProvider
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DatabaseTest {

    private lateinit var dao: NoteDao
    private lateinit var database: AppDatabase

    @Before
    fun createDb() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        database = Room.inMemoryDatabaseBuilder(appContext, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()

        dao = database.noteDao()!!
    }

    @Test
    fun createNotes() {
        dao.insertAll(SampleDataProvider.getNotes())

        val count = dao.getCount()

        assertEquals(count, SampleDataProvider.getNotes().size)
    }

    @Test
    fun insertNode() {
        val expectedId = 1
        val note = NoteEntity()
        note.text = "some text"

        dao.insertNote(note)
        val savedNote = dao.getNoteById(expectedId)

        assertNotNull(savedNote)
        assertEquals(expectedId, savedNote?.id)
    }

    @After
    fun closeDb() {
        database.close()
    }
}