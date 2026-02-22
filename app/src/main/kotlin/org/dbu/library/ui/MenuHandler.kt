package org.dbu.library.ui

import org.dbu.library.model.Book
import org.dbu.library.model.Patron
import org.dbu.library.repository.LibraryRepository
import org.dbu.library.service.BorrowResult
import org.dbu.library.service.LibraryService

fun handleMenuAction(
    choice: String,
    service: LibraryService,
    repository: LibraryRepository
): Boolean {

    return when (choice) {

        "1" -> {
            addBook(service)
            true
        }

        "2" -> {
            registerPatron(repository)
            true
        }

        "3" -> {
            borrowBook(service)
            true
        }

        "4" -> {
            returnBook(service)
            true
        }

        "5" -> {
            search(service)
            true
        }

        "6" -> {
            listAllBooks(repository)
            true
        }

        "0" -> false

        else -> {
            println("Invalid option")
            true
        }
    }
}

fun addBook(service: LibraryService) {
    print("ISBN: ")
    val isbn = readln()
    print("Title: ")
    val title = readln()
    print("Author: ")
    val author = readln()
    print("Year: ")
    val year = readln().toIntOrNull() ?: 0

    val book = Book(isbn, title, author, year)
    if (service.addBook(book)) {
        println("Book added successfully.")
    } else {
        println("Failed to add book.")
    }
}

fun registerPatron(repository: LibraryRepository) {
    print("ID: ")
    val id = readln()
    print("Name: ")
    val name = readln()

    val patron = Patron(id, name)
    if (repository.addPatron(patron)) {
        println("Patron registered.")
    } else {
        println("Failed to register patron.")
    }
}

fun borrowBook(service: LibraryService) {
    print("Patron ID: ")
    val patronId = readln()
    print("ISBN: ")
    val isbn = readln()

    when (service.borrowBook(patronId, isbn)) {
        BorrowResult.SUCCESS -> println("Book borrowed successfully")
        BorrowResult.BOOK_NOT_FOUND -> println("Book not found")
        BorrowResult.PATRON_NOT_FOUND -> println("Patron not found")
        BorrowResult.NOT_AVAILABLE -> println("Book is not available")
        BorrowResult.LIMIT_REACHED -> println("Borrow limit reached")
    }
}

fun returnBook(service: LibraryService) {
    print("Patron ID: ")
    val patronId = readln()
    print("ISBN: ")
    val isbn = readln()

    if (service.returnBook(patronId, isbn)) {
        println("Book returned successfully")
    } else {
        println("Failed to return book")
    }
}

fun search(service: LibraryService) {
    print("Enter search term: ")
    val term = readln()
    val results = service.search(term)

    if (results.isEmpty()) {
        println("No books found.")
    } else {
        results.forEach { println("${it.isbn} | ${it.title} | ${it.author}") }
    }
}

fun listAllBooks(repository: LibraryRepository) {
    val books = repository.getAllBooks()
    if (books.isEmpty()) {
        println("No books in library.")
    } else {
        books.forEach {
            val status = if (it.isAvailable) "Available" else "Borrowed"
            println("${it.isbn} | ${it.title} | $status")
        }
    }
}