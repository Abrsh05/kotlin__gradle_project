package org.dbu.library.service

import org.dbu.library.model.Book
import org.dbu.library.repository.LibraryRepository

class DefaultLibraryService(
    private val repository: LibraryRepository
) : LibraryService {

    override fun addBook(book: Book): Boolean {
        return repository.addBook(book)
    }

    override fun borrowBook(patronId: String, isbn: String): BorrowResult {
        val patron = repository.findPatron(patronId) ?: return BorrowResult.PATRON_NOT_FOUND
        val book = repository.findBook(isbn) ?: return BorrowResult.BOOK_NOT_FOUND

        if (!book.isAvailable) return BorrowResult.NOT_AVAILABLE
        if (patron.borrowedBooks.size >= 3) return BorrowResult.LIMIT_REACHED

        val newPatron = patron.copy(borrowedBooks = patron.borrowedBooks + isbn)
        val newBook = book.copy(isAvailable = false)

        repository.updatePatron(newPatron)
        repository.updateBook(newBook)

        return BorrowResult.SUCCESS
    }

    override fun returnBook(patronId: String, isbn: String): Boolean {
        val patron = repository.findPatron(patronId) ?: return false
        val book = repository.findBook(isbn) ?: return false

        if (!patron.borrowedBooks.contains(isbn)) return false

        val newPatron = patron.copy(borrowedBooks = patron.borrowedBooks - isbn)
        val newBook = book.copy(isAvailable = true)

        repository.updatePatron(newPatron)
        repository.updateBook(newBook)

        return true
    }

    override fun search(query: String): List<Book> {
        val lowerQuery = query.lowercase()
        return repository.getAllBooks().filter {
            it.title.lowercase().contains(lowerQuery) || it.author.lowercase().contains(lowerQuery)
        }
    }
}